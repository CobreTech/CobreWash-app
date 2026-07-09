package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.ui.VehiculosViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests de [VehiculosViewModel]: check-in/checkout de vehículos y generación de ids. */
class VehiculosViewModelTest {

    @Test
    fun chequeoEnRuta_devuelveElChequeoAbiertoDelVehiculo() {
        val vm = VehiculosViewModel()
        // V01 nace con un chequeo abierto (solo salida) en MockData.
        assertNotNull(vm.chequeoEnRuta("V01"))
        // V02 no tiene chequeo alguno.
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

        assertNull(vm.chequeoEnRuta("V01")) // ya no está en ruta
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

        vm.registrarVehiculo(patente = "xyzw-12", modelo = "Kia Bongo 2020", kilometrajeInicial = 1_000)

        assertEquals(previos + 1, vm.vehiculos.size)
        val nuevo = vm.vehiculos.last()
        assertEquals("XYZW-12", nuevo.patente) // normalizada a mayúsculas
        assertEquals("Kia Bongo 2020", nuevo.modelo)
        assertEquals(1_000, nuevo.kilometrajeActual)
    }

    @Test
    fun historialVehiculo_devuelveSoloLosChequeosDeEseVehiculo() {
        val vm = VehiculosViewModel()
        vm.registrarEntradaVehiculo("V01", km = 58_400, fotos = emptyList(), observaciones = null)
        vm.registrarSalidaVehiculo("V02", km = 31_450, fotos = emptyList(), observaciones = null, responsable = "Ana")

        val historialV01 = vm.historialVehiculo("V01")
        assertEquals(1, historialV01.size) // el chequeo sembrado en MockData, ya cerrado
        assertTrue(historialV01.all { it.vehiculoId == "V01" })

        val historialSinRegistros = vm.historialVehiculo("V-INEXISTENTE")
        assertTrue(historialSinRegistros.isEmpty())
    }
}
