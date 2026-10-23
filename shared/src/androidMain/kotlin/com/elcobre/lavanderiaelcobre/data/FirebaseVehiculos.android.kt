package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.esRolConductorORepartidor
import com.elcobre.lavanderiaelcobre.data.model.extraerMarcaYModelo
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.dataconnect.EstadoVehiculo
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetFlotaVehiculosQuery
import com.elcobre.lavanderiaelcobre.dataconnect.MomentoInspeccion
import com.elcobre.lavanderiaelcobre.dataconnect.SalidaVehiculoEstado
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import kotlin.math.roundToInt

private const val MAX_FOTOS_INSPECCION = 20
private val salidasPendientes = java.util.concurrent.ConcurrentHashMap<String, Pair<UUID, UUID>>()
private val retornosPendientes = java.util.concurrent.ConcurrentHashMap<String, UUID>()

internal actual fun descartarReintentosFlotaFirebase() {
    salidasPendientes.clear()
    retornosPendientes.clear()
}

private fun mapearCondicionADataConnect(estado: EstadoCondicionVehiculo): EstadoVehiculo = when (estado) {
    EstadoCondicionVehiculo.APTO -> EstadoVehiculo.APTO
    EstadoCondicionVehiculo.CON_OBSERVACIONES -> EstadoVehiculo.CON_OBSERVACIONES
    EstadoCondicionVehiculo.NO_APTO -> EstadoVehiculo.NO_APTO
}

private fun mapearFotosInspeccion(
    inspeccion: GetFlotaVehiculosQuery.Data.SalidasItem.InspeccionesItem?,
): List<FotoChequeo> = inspeccion?.fotos.orEmpty().map {
    FotoChequeo(
        id = it.id.toString(),
        uri = it.rutaStorage,
        descripcion = it.descripcion,
        orden = it.orden,
    )
}

private fun mapearSalidaAChequeo(
    salida: GetFlotaVehiculosQuery.Data.SalidasItem,
    formatoFecha: SimpleDateFormat,
    formatoHora: SimpleDateFormat,
): ChequeoVehiculo {
    val antes = salida.inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.ANTES }
    val despues = salida.inspecciones.firstOrNull { it.momento.value == MomentoInspeccion.DESPUES }
    val finalizada = salida.estado.value == SalidaVehiculoEstado.FINALIZADA
    val kmSalida = antes?.kilometraje?.roundToInt() ?: 0
    val kmEntrada = if (finalizada) (despues?.kilometraje?.roundToInt() ?: kmSalida) else null
    val instanteBase = salida.fechaSalida ?: antes?.registradoEn ?: salida.creadoEn
    val instanteRetorno = salida.fechaRetorno ?: despues?.registradoEn
    val responsable = listOfNotNull(salida.repartidor.nombre, salida.repartidor.apellido)
        .joinToString(" ")
        .trim()
        .ifEmpty { salida.repartidor.email }

    return ChequeoVehiculo(
        id = salida.id.toString(),
        vehiculoId = salida.vehiculo.id.toString(),
        fecha = formatoFecha.format(instanteBase.toDate()),
        responsable = responsable,
        repartidorId = salida.repartidor.id,
        estadoSalida = EstadoCondicionVehiculo.desdeCodigo(antes?.estadoVehiculo?.value?.name),
        estadoEntrada = despues?.let { EstadoCondicionVehiculo.desdeCodigo(it.estadoVehiculo.value?.name) },
        kmSalida = kmSalida,
        fotosSalida = mapearFotosInspeccion(antes),
        observacionesSalida = antes?.observaciones ?: salida.observaciones,
        horaSalida = formatoHora.format(instanteBase.toDate()),
        kmEntrada = kmEntrada,
        fotosEntrada = mapearFotosInspeccion(despues),
        observacionesEntrada = despues?.observaciones ?: salida.observaciones.takeIf { finalizada },
        horaEntrada = instanteRetorno?.let { formatoHora.format(it.toDate()) },
    )
}

internal actual suspend fun consultarFlotaFirebase(): EstadoFlotaRemota = solicitud {
    var data: GetFlotaVehiculosQuery.Data? = null
    val salidas = mutableListOf<GetFlotaVehiculosQuery.Data.SalidasItem>()
    var offset = 0
    do {
        val pagina = ExampleConnector.instance.getFlotaVehiculos.ref { limit = 200; this.offset = offset }
            .execute(QueryRef.FetchPolicy.SERVER_ONLY).data
        data = pagina
        salidas.addAll(pagina.salidas)
        offset += pagina.salidas.size
    } while (pagina.salidas.size == 200)
    val completa = requireNotNull(data).copy(salidas = salidas.distinctBy { it.id })
    val zona = TimeZone.getTimeZone("America/Santiago")
    val locale = Locale.forLanguageTag("es-CL")
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", locale).apply { timeZone = zona }
    val formatoHora = SimpleDateFormat("HH:mm", locale).apply { timeZone = zona }

    val salidasVigentes = completa.salidas.filter { it.estado.value != SalidaVehiculoEstado.CANCELADA }
    val maxKmPorVehiculo = salidasVigentes.groupBy { it.vehiculo.id.toString() }
        .mapValues { (_, viajes) ->
            viajes.flatMap { it.inspecciones }.maxOfOrNull { it.kilometraje.roundToInt() } ?: 0
        }

    val vehiculos = completa.vehiculos.map { item ->
        val id = item.id.toString()
        Vehiculo(
            id = id,
            patente = item.patente,
            marca = item.marca,
            modelo = item.modelo,
            anio = item.anio,
            descripcion = item.descripcion,
            activo = item.activo,
            kilometrajeActual = maxKmPorVehiculo[id] ?: 0,
        )
    }

    val conductores = completa.conductores
        .filter { esRolConductorORepartidor(it.rol.nombre) }
        .map { item ->
            val nombreCompleto = listOfNotNull(item.nombre, item.apellido)
                .joinToString(" ")
                .trim()
                .ifEmpty { item.email }
            ConductorFlota(
                id = item.id,
                nombreCompleto = nombreCompleto,
                rut = item.rut,
                rol = item.rol.nombre,
            )
        }

    EstadoFlotaRemota(
        vehiculos = vehiculos,
        conductores = conductores,
        chequeos = salidasVigentes.map { mapearSalidaAChequeo(it, formatoFecha, formatoHora) },
    )
}

internal actual suspend fun registrarVehiculoFirebase(input: NuevoVehiculoInput): String = solicitud {
    val patente = normalizarPatenteChilena(input.patente)
    val (marca, modelo) = extraerMarcaYModelo(input.marca, input.modelo)
    val resultado = ExampleConnector.instance.registrarVehiculoFlota.execute(
        patente = patente,
        marca = marca,
        modelo = modelo,
    ) {
        anio = input.anio
        descripcion = input.descripcion?.trim()?.takeIf { it.isNotEmpty() }
    }
    resultado.data.vehiculo_insert.id.toString()
}

internal actual suspend fun editarVehiculoFirebase(input: EditarVehiculoInput): Unit = solicitud {
    val patente = normalizarPatenteChilena(input.patente)
    val (marca, modelo) = extraerMarcaYModelo(input.marca, input.modelo)
    ExampleConnector.instance.editarVehiculoFlota.execute(
        id = UUID.fromString(input.id),
        patente = patente,
        marca = marca,
        modelo = modelo,
        activo = input.activo,
    ) {
        anio = input.anio
        descripcion = input.descripcion?.trim()?.takeIf { it.isNotEmpty() }
    }
}

private suspend fun adjuntarFotosInspeccion(inspeccionId: UUID, fotos: List<FotoChequeo>) {
    require(fotos.size <= MAX_FOTOS_INSPECCION) { "Máximo 20 fotografías por inspección." }
    for ((indice, foto) in fotos.withIndex()) subirFotoInspeccion(inspeccionId.toString(), foto, indice + 1)
}

internal actual suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String = solicitud {
    validarArchivosEvidencia(input.fotos)
    val clave = "${com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid}|${input.copy(fotos = emptyList())}"
    val ids = salidasPendientes.getOrPut(clave) { UUID.randomUUID() to UUID.randomUUID() }
    val previa = ExampleConnector.instance.getMovimientoFlotaPorInspeccion.ref(ids.second)
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.inspeccionVehiculo
    val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
    val salidaId = if (previa != null) previa.salidaId else {
        ExampleConnector.instance.registrarSalidaFlota.execute(
            vehiculoId = UUID.fromString(input.vehiculoId), repartidorId = input.repartidorId,
            kilometraje = input.kilometraje.toDouble(), estadoVehiculo = mapearCondicionADataConnect(input.estadoVehiculo),
        ) {
            salidaId = ids.first
            inspeccionId = ids.second
            observaciones = obs?.take(300)
            observacionesInspeccion = obs?.take(500)
        }.data.salida.id
    }
    try { adjuntarFotosInspeccion(ids.second, input.fotos) }
    catch (error: OperacionComandaException) {
        throw OperacionComandaException(
            "La salida ya se guardó. Hay fotografías pendientes: reintenta con los mismos datos. " + error.message, error,
        )
    }
    salidasPendientes.remove(clave)
    salidaId.toString()
}

internal actual suspend fun registrarRetornoFirebase(input: RetornoFlotaInput): Unit = solicitud {
    validarArchivosEvidencia(input.fotos)
    val clave = "${com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid}|${input.copy(fotos = emptyList())}"
    val id = retornosPendientes.getOrPut(clave) { UUID.randomUUID() }
    val previa = ExampleConnector.instance.getMovimientoFlotaPorInspeccion.ref(id)
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.inspeccionVehiculo
    if (previa == null) {
        val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
        ExampleConnector.instance.registrarRetornoFlota.execute(
            salidaId = UUID.fromString(input.salidaId), kilometraje = input.kilometraje.toDouble(),
            estadoVehiculo = mapearCondicionADataConnect(input.estadoVehiculo),
        ) {
            inspeccionId = id
            observacionesRetorno = obs?.take(300)
            observacionesInspeccion = obs?.take(500)
        }
    }
    try { adjuntarFotosInspeccion(id, input.fotos) }
    catch (error: OperacionComandaException) {
        throw OperacionComandaException(
            "El retorno ya se guardó. Hay fotografías pendientes: reintenta con los mismos datos. " + error.message, error,
        )
    }
    retornosPendientes.remove(clave)
}
