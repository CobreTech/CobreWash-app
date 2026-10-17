package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.EditarVehiculoInput
import com.elcobre.lavanderiaelcobre.data.EstadoFlotaRemota
import com.elcobre.lavanderiaelcobre.data.NuevoVehiculoInput
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.RetornoFlotaInput
import com.elcobre.lavanderiaelcobre.data.SalidaFlotaInput
import com.elcobre.lavanderiaelcobre.data.VehiculosRepository
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.ConductorFlota
import com.elcobre.lavanderiaelcobre.data.model.EstadoCondicionVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FiltroFlota
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo
import com.elcobre.lavanderiaelcobre.data.model.esPatenteChilenaValida
import com.elcobre.lavanderiaelcobre.data.model.normalizarPatenteChilena
import com.elcobre.lavanderiaelcobre.ui.VehiculosViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests de [VehiculosViewModel] y sincronización de flota con Firebase (`RF-VE01` a `RF-VE04`). */
@OptIn(ExperimentalCoroutinesApi::class)
@Suppress("TooManyFunctions")
class VehiculosViewModelTest {

    private class FakeVehiculosRepository : VehiculosRepository {
        var falla = false
        val vehiculos = mutableListOf(
            Vehiculo(
                id = "00000000-0000-4000-8000-000000000001",
                patente = "HLRT-24",
                marca = "Suzuki",
                modelo = "APV Cargo",
                anio = 2021,
                kilometrajeActual = 58_320,
                activo = true,
            ),
            Vehiculo(
                id = "00000000-0000-4000-8000-000000000002",
                patente = "FXYP-87",
                marca = "Chevrolet",
                modelo = "N300 Max",
                anio = 2022,
                kilometrajeActual = 31_450,
                activo = true,
            ),
        )
        val conductores = mutableListOf(
            ConductorFlota(id = "cond-1", nombreCompleto = "Mauricio Aguilera", rut = "15.234.567-8", rol = "operario"),
            ConductorFlota(id = "cond-2", nombreCompleto = "Camila Riquelme", rut = "17.890.123-4", rol = "admin"),
        )
        val chequeos = mutableListOf(
            ChequeoVehiculo(
                id = "00000000-0000-4000-8000-000000000010",
                vehiculoId = "00000000-0000-4000-8000-000000000001",
                fecha = "17/10/2026",
                responsable = "Mauricio Aguilera",
                repartidorId = "cond-1",
                estadoSalida = EstadoCondicionVehiculo.APTO,
                kmSalida = 58_320,
                horaSalida = "08:05",
            ),
        )

        override suspend fun consultarFlota(): EstadoFlotaRemota {
            if (falla) throw OperacionComandaException("Sin conexión con Firebase")
            return EstadoFlotaRemota(
                vehiculos = vehiculos.toList(),
                conductores = conductores.toList(),
                chequeos = chequeos.toList(),
            )
        }

        override suspend fun registrarVehiculo(input: NuevoVehiculoInput): String {
            if (falla) throw OperacionComandaException("No se pudo registrar el vehículo")
            val nuevoId = "00000000-0000-4000-8000-00000000000${vehiculos.size + 1}"
            vehiculos.add(
                Vehiculo(
                    id = nuevoId,
                    patente = input.patente,
                    marca = input.marca,
                    modelo = input.modelo,
                    anio = input.anio,
                    descripcion = input.descripcion,
                    kilometrajeActual = 0,
                    activo = true,
                ),
            )
            return nuevoId
        }

        override suspend fun editarVehiculo(input: EditarVehiculoInput) {
            if (falla) throw OperacionComandaException("No se pudo actualizar el vehículo")
            val idx = vehiculos.indexOfFirst { it.id == input.id }
            if (idx >= 0) {
                vehiculos[idx] = vehiculos[idx].copy(
                    patente = input.patente,
                    marca = input.marca,
                    modelo = input.modelo,
                    anio = input.anio,
                    descripcion = input.descripcion,
                    activo = input.activo,
                )
            }
        }

        override suspend fun registrarSalida(input: SalidaFlotaInput): String {
            if (falla) throw OperacionComandaException("No se pudo registrar la salida")
            val idSalida = "salida-${chequeos.size + 1}"
            val conductor = conductores.firstOrNull { it.id == input.repartidorId }?.nombreCompleto ?: "Repartidor"
            chequeos.add(
                0,
                ChequeoVehiculo(
                    id = idSalida,
                    vehiculoId = input.vehiculoId,
                    fecha = "17/10/2026",
                    responsable = conductor,
                    repartidorId = input.repartidorId,
                    estadoSalida = input.estadoVehiculo,
                    kmSalida = input.kilometraje,
                    fotosSalida = input.fotos,
                    observacionesSalida = input.observaciones,
                    horaSalida = "09:30",
                ),
            )
            val idx = vehiculos.indexOfFirst { it.id == input.vehiculoId }
            if (idx >= 0) {
                vehiculos[idx] = vehiculos[idx].copy(
                    kilometrajeActual = maxOf(vehiculos[idx].kilometrajeActual, input.kilometraje),
                )
            }
            return idSalida
        }

        override suspend fun registrarRetorno(input: RetornoFlotaInput) {
            if (falla) throw OperacionComandaException("No se pudo registrar el retorno")
            val idx = chequeos.indexOfFirst { it.id == input.salidaId }
            if (idx >= 0) {
                val previo = chequeos[idx]
                chequeos[idx] = previo.registrarEntrada(
                    km = input.kilometraje,
                    fotos = input.fotos,
                    observaciones = input.observaciones,
                    hora = "13:15",
                    estado = input.estadoVehiculo,
                )
                val vIdx = vehiculos.indexOfFirst { it.id == previo.vehiculoId }
                if (vIdx >= 0) {
                    vehiculos[vIdx] = vehiculos[vIdx].copy(kilometrajeActual = input.kilometraje)
                }
            }
        }
    }

    @Test
    fun chequeoEnRuta_devuelveElChequeoAbiertoDelVehiculo() {
        val vm = VehiculosViewModel()
        assertNotNull(vm.chequeoEnRuta("V01"))
        assertNull(vm.chequeoEnRuta("V02"))
    }

    @Test
    fun registrarSalidaVehiculo_abreUnChequeoNuevo() {
        val vm = VehiculosViewModel()
        val previos = vm.chequeosVehiculo.size

        vm.registrarSalidaVehiculo("V02", km = 31_500, fotos = emptyList(), observaciones = null, responsable = "Ana")

        assertEquals(previos + 1, vm.chequeosVehiculo.size)
        val chequeo = vm.chequeoEnRuta("V02")
        assertNotNull(chequeo)
        assertTrue(chequeo.estaEnRuta)
        assertEquals(31_500, chequeo.kmSalida)
    }

    @Test
    fun registrarEntradaVehiculo_cierraElChequeoYActualizaKilometraje() {
        val vm = VehiculosViewModel()

        vm.registrarEntradaVehiculo("V01", km = 58_400, fotos = emptyList(), observaciones = "Sin novedad")

        assertNull(vm.chequeoEnRuta("V01"))
        val vehiculo = vm.vehiculos.first { it.id == "V01" }
        assertEquals(58_400, vehiculo.kilometrajeActual)
    }

    @Test
    fun registrarEntradaVehiculo_sinChequeoAbierto_noHaceNada() {
        val vm = VehiculosViewModel()
        val kmOriginal = vm.vehiculos.first { it.id == "V02" }.kilometrajeActual

        vm.registrarEntradaVehiculo("V02", km = 40_000, fotos = emptyList(), observaciones = null)

        assertEquals(kmOriginal, vm.vehiculos.first { it.id == "V02" }.kilometrajeActual)
    }

    @Test
    fun registrarSalidaVehiculo_generaIdsMonotonicosSinColisionar() {
        val vm = VehiculosViewModel()
        vm.registrarEntradaVehiculo("V01", km = 58_400, fotos = emptyList(), observaciones = null)

        vm.registrarSalidaVehiculo("V01", km = 58_400, fotos = emptyList(), observaciones = null, responsable = "Ana")
        vm.registrarSalidaVehiculo("V02", km = 31_450, fotos = emptyList(), observaciones = null, responsable = "Ana")

        val ids = vm.chequeosVehiculo.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun registrarVehiculo_agregaUnoNuevoALaFlota() {
        val vm = VehiculosViewModel()
        val previos = vm.vehiculos.size

        vm.registrarVehiculo(patente = "xyzw12", modelo = "Kia Bongo 2020", kilometrajeInicial = 1_000)

        assertEquals(previos + 1, vm.vehiculos.size)
        val nuevo = vm.vehiculos.last()
        assertEquals("XYZW-12", nuevo.patente)
        assertEquals("Kia Bongo 2020", nuevo.modelo)
        assertEquals(1_000, nuevo.kilometrajeActual)
    }

    @Test
    fun historialVehiculo_devuelveSoloLosChequeosDeEseVehiculo() {
        val vm = VehiculosViewModel()
        vm.registrarEntradaVehiculo("V01", km = 58_400, fotos = emptyList(), observaciones = null)
        vm.registrarSalidaVehiculo("V02", km = 31_450, fotos = emptyList(), observaciones = null, responsable = "Ana")

        val historialV01 = vm.historialVehiculo("V01")
        assertEquals(1, historialV01.size)
        assertTrue(historialV01.all { it.vehiculoId == "V01" })

        val historialSinRegistros = vm.historialVehiculo("V-INEXISTENTE")
        assertTrue(historialSinRegistros.isEmpty())
    }

    @Test
    fun normalizacionYValidacionDePatentesChilenasCoincideConLaWeb() {
        assertEquals("HLRT-24", normalizarPatenteChilena("hlrt24"))
        assertEquals("AB-1234", normalizarPatenteChilena("ab 1234"))
        assertEquals("BCD-123", normalizarPatenteChilena("bcd123"))
        assertTrue(esPatenteChilenaValida("hlrt24"))
        assertTrue(esPatenteChilenaValida("AB-1234"))
        assertFalse(esPatenteChilenaValida("123"))
        assertFalse(esPatenteChilenaValida("PATENTE-LARGA-99"))
    }

    @Test
    fun sincronizacionFirebaseCargaFlotaCalculaKpisYFiltra() = runTest {
        val repo = FakeVehiculosRepository()
        val vm = VehiculosViewModel(repository = repo, scopeOverride = backgroundScope)
        vm.cambiarSesion(true)
        vm.refrescarFlota()
        testScheduler.runCurrent()

        assertEquals(2, vm.kpis.total)
        assertEquals(1, vm.kpis.enRuta)
        assertEquals(1, vm.kpis.disponibles)
        assertEquals(0, vm.kpis.inactivos)

        vm.seleccionarFiltro(FiltroFlota.EN_RUTA)
        assertEquals(1, vm.vehiculosFiltrados.size)
        assertEquals("HLRT-24", vm.vehiculosFiltrados.single().patente)

        vm.seleccionarFiltro(FiltroFlota.TODOS)
        vm.actualizarBusqueda("Mauricio")
        assertEquals(1, vm.vehiculosFiltrados.size)
    }

    @Test
    fun validacionesDeNegocioBloqueanSalidaNoAptoOdometroMenorYDesactivacionEnRuta() = runTest {
        val repo = FakeVehiculosRepository()
        val vm = VehiculosViewModel(repository = repo, scopeOverride = backgroundScope)
        vm.cambiarSesion(true)
        vm.refrescarFlota()
        testScheduler.runCurrent()

        val idEnRuta = repo.vehiculos[0].id
        val idDisponible = repo.vehiculos[1].id

        vm.cambiarEstadoVehiculo(idEnRuta, activo = false)
        assertNotNull(vm.error)
        assertTrue(vm.vehiculos.first { it.id == idEnRuta }.activo)

        vm.limpiarMensajes()
        vm.registrarSalidaVehiculo(
            vehiculoId = idDisponible,
            km = 31_500,
            fotos = emptyList(),
            observaciones = "Falla de frenos",
            responsable = "Mauricio Aguilera",
            repartidorId = "cond-1",
            estadoVehiculo = EstadoCondicionVehiculo.NO_APTO,
        )
        assertNotNull(vm.error)
        assertNull(vm.chequeoEnRuta(idDisponible))

        vm.limpiarMensajes()
        vm.registrarSalidaVehiculo(
            vehiculoId = idDisponible,
            km = 30_000,
            fotos = emptyList(),
            observaciones = null,
            responsable = "Mauricio Aguilera",
            repartidorId = "cond-1",
        )
        assertNotNull(vm.error)

        vm.limpiarMensajes()
        vm.registrarEntradaVehiculo(
            vehiculoId = idEnRuta,
            km = 58_000,
            fotos = emptyList(),
            observaciones = null,
        )
        assertNotNull(vm.error)
        assertNotNull(vm.chequeoEnRuta(idEnRuta))
    }

    @Test
    fun flujoCompletoFirebaseRegistraSalidaConFotosYRetornoActualizandoOdometro() = runTest {
        val repo = FakeVehiculosRepository()
        val vm = VehiculosViewModel(repository = repo, scopeOverride = backgroundScope)
        vm.cambiarSesion(true)
        vm.refrescarFlota()
        testScheduler.runCurrent()

        val idDisponible = repo.vehiculos[1].id
        val fotosSalida = listOf(FotoChequeo(id = "F1", uri = "vehiculos/frente.jpg", descripcion = "Frente", orden = 1))

        vm.registrarSalidaVehiculo(
            vehiculoId = idDisponible,
            km = 31_500,
            fotos = fotosSalida,
            observaciones = "Salida turno mañana",
            responsable = "Camila Riquelme",
            repartidorId = "cond-2",
            estadoVehiculo = EstadoCondicionVehiculo.CON_OBSERVACIONES,
        )
        testScheduler.runCurrent()

        val abierto = vm.chequeoEnRuta(idDisponible)
        assertNotNull(abierto)
        assertEquals(31_500, abierto.kmSalida)
        assertEquals(1, abierto.fotosSalida.size)
        assertEquals(EstadoCondicionVehiculo.CON_OBSERVACIONES, abierto.estadoSalida)

        val fotosRetorno = listOf(FotoChequeo(id = "F2", uri = "vehiculos/tablero.jpg", descripcion = "Tablero", orden = 1))
        vm.registrarEntradaVehiculo(
            vehiculoId = idDisponible,
            km = 31_620,
            fotos = fotosRetorno,
            observaciones = "Regreso conforme",
            estadoVehiculo = EstadoCondicionVehiculo.APTO,
        )
        testScheduler.runCurrent()

        assertNull(vm.chequeoEnRuta(idDisponible))
        assertEquals(31_620, vm.vehiculos.first { it.id == idDisponible }.kilometrajeActual)
        val cerrado = vm.historialVehiculo(idDisponible).first()
        assertEquals(120, cerrado.kilometrosRecorridos)
        assertEquals(1, cerrado.fotosEntrada.size)
    }

    @Test
    fun errorConservaDatosYRespuestasTardiasDeOtraSesionSeDescartan() = runTest {
        val repo = FakeVehiculosRepository()
        val vm = VehiculosViewModel(repository = repo, scopeOverride = backgroundScope)
        vm.cambiarSesion(true)
        vm.refrescarFlota()
        testScheduler.runCurrent()
        assertEquals(2, vm.vehiculos.size)

        repo.falla = true
        vm.refrescarFlota()
        testScheduler.runCurrent()
        assertEquals(2, vm.vehiculos.size)
        assertNotNull(vm.error)

        val diferido = CompletableDeferred<EstadoFlotaRemota>()
        val repoLento = object : VehiculosRepository by repo {
            override suspend fun consultarFlota(): EstadoFlotaRemota = withContext(NonCancellable) { diferido.await() }
        }
        val vmLento = VehiculosViewModel(repository = repoLento, scopeOverride = backgroundScope)
        vmLento.cambiarSesion(true)
        vmLento.refrescarFlota()
        testScheduler.runCurrent()
        vmLento.cambiarSesion(false)
        diferido.complete(EstadoFlotaRemota(repo.vehiculos, repo.conductores, repo.chequeos))
        testScheduler.runCurrent()
        assertTrue(vmLento.vehiculos.isEmpty())
        assertFalse(vmLento.cargando)
    }
}
