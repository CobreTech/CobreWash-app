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
    val data = ExampleConnector.instance.getFlotaVehiculos
        .ref()
        .execute(QueryRef.FetchPolicy.SERVER_ONLY)
        .data
    val zona = TimeZone.getTimeZone("America/Santiago")
    val locale = Locale.forLanguageTag("es-CL")
    val formatoFecha = SimpleDateFormat("dd/MM/yyyy", locale).apply { timeZone = zona }
    val formatoHora = SimpleDateFormat("HH:mm", locale).apply { timeZone = zona }

    val salidasVigentes = data.salidas.filter { it.estado.value != SalidaVehiculoEstado.CANCELADA }
    val maxKmPorVehiculo = salidasVigentes.groupBy { it.vehiculo.id.toString() }
        .mapValues { (_, viajes) ->
            viajes.flatMap { it.inspecciones }.maxOfOrNull { it.kilometraje.roundToInt() } ?: 0
        }

    val vehiculos = data.vehiculos.map { item ->
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

    val conductores = data.conductores
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
    fotos.take(MAX_FOTOS_INSPECCION).forEachIndexed { indice, foto ->
        val orden = indice + 1
        val limpia = foto.uri.trim().replace(Regex("\\s+"), "-").take(480)
        val ruta = if (limpia.length >= 5) limpia else "vehiculos/inspecciones/$inspeccionId/foto-$orden.jpg"
        ExampleConnector.instance.adjuntarFotoInspeccionFlota.execute(
            inspeccionId = inspeccionId,
            rutaStorage = ruta,
        ) {
            descripcion = foto.descripcion?.trim()?.takeIf { it.isNotEmpty() }
            this.orden = orden
        }
    }
}

internal actual suspend fun registrarSalidaFirebase(input: SalidaFlotaInput): String = solicitud {
    val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
    val resultado = ExampleConnector.instance.registrarSalidaFlota.execute(
        vehiculoId = UUID.fromString(input.vehiculoId),
        repartidorId = input.repartidorId,
        kilometraje = input.kilometraje.toDouble(),
        estadoVehiculo = mapearCondicionADataConnect(input.estadoVehiculo),
    ) {
        observaciones = obs?.take(300)
        observacionesInspeccion = obs?.take(500)
    }
    adjuntarFotosInspeccion(resultado.data.inspeccion.id, input.fotos)
    resultado.data.salida.id.toString()
}

internal actual suspend fun registrarRetornoFirebase(input: RetornoFlotaInput): Unit = solicitud {
    val obs = input.observaciones?.trim()?.takeIf { it.isNotEmpty() }
    val resultado = ExampleConnector.instance.registrarRetornoFlota.execute(
        salidaId = UUID.fromString(input.salidaId),
        kilometraje = input.kilometraje.toDouble(),
        estadoVehiculo = mapearCondicionADataConnect(input.estadoVehiculo),
    ) {
        observacionesRetorno = obs?.take(300)
        observacionesInspeccion = obs?.take(500)
    }
    adjuntarFotosInspeccion(resultado.data.inspeccion.id, input.fotos)
}
