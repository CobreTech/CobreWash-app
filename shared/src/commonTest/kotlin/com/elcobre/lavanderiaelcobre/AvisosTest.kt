package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.AvisosRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.PaginaAvisos
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AvisosTest {
    private val sesion = Sesion.DeOperario(Operario("Test", "Operario", "Mañana"))
    private fun datos(n: Int) = (1..n).map { Aviso("$it", "Título $it", "Contenido", "Fecha", "Operarios") }

    @Test fun cargaRealPaginacionYRefrescoSinDuplicados() = runTest {
        val filas = datos(45)
        val repo = AvisosRepository { _, limit -> PaginaAvisos(filas.take(limit), filas.size) }
        val vm = CobreViewModel(FakeComandasRepository(), backgroundScope, repo)
        vm.iniciarSesion(sesion)
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(20, vm.avisos.size)
        assertEquals(45, vm.totalAvisos)
        vm.refrescarAvisos(mas = true)
        testScheduler.runCurrent()
        assertEquals(40, vm.avisos.size)
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(40, vm.avisos.size)
        vm.refrescarAvisos(mas = true)
        testScheduler.runCurrent()
        assertEquals(45, vm.avisos.size)
    }

    @Test fun errorConservaAvisosYPermiteReintentar() = runTest {
        var falla = false
        val repo = AvisosRepository { _, _ ->
            if (falla) throw OperacionComandaException("Sin conexión")
            PaginaAvisos(datos(1), 1)
        }
        val vm = CobreViewModel(FakeComandasRepository(), backgroundScope, repo)
        vm.iniciarSesion(sesion)
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        falla = true
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(1, vm.avisos.size)
        assertNotNull(vm.errorAvisos)
        assertFalse(vm.cargandoAvisos)
        falla = false
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(null, vm.errorAvisos)
        vm.cerrarSesion()
        assertTrue(vm.avisos.isEmpty())
        assertEquals(0, vm.totalAvisos)
    }

    @Test fun sinSesionNoConsultaFirebase() = runTest {
        var consultas = 0
        val repo = AvisosRepository { _, _ -> consultas++; PaginaAvisos(emptyList(), 0) }
        val vm = CobreViewModel(FakeComandasRepository(), backgroundScope, repo)
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(0, consultas)
        assertTrue(vm.avisos.isEmpty())
    }

    @Test fun respuestasTardiasNoReaparecenTrasCerrarSesion() = runTest {
        val respuesta = CompletableDeferred<PaginaAvisos>()
        var consultas = 0
        val repo = AvisosRepository { _, _ ->
            consultas++
            withContext(NonCancellable) { respuesta.await() }
        }
        val vm = CobreViewModel(FakeComandasRepository(), backgroundScope, repo)
        vm.iniciarSesion(sesion)
        vm.refrescarAvisos()
        vm.refrescarAvisos()
        testScheduler.runCurrent()
        assertEquals(1, consultas)
        vm.cerrarSesion()
        respuesta.complete(PaginaAvisos(datos(1), 1))
        testScheduler.runCurrent()
        assertTrue(vm.avisos.isEmpty())
        assertEquals(0, vm.totalAvisos)
        assertFalse(vm.cargandoAvisos)
    }
}
