package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.CobreViewModel
import com.elcobre.lavanderiaelcobre.ui.Pantalla
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Tests de [CobreViewModel]: sesión, pila de navegación y mutaciones de pedidos/alertas. */
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CobreViewModelTest {
    private fun crearViewModel(): CobreViewModel {
        val vm = CobreViewModel(FakeComandasRepository(),
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.test.UnconfinedTestDispatcher()))
        vm.refrescarComandas()
        return vm
    }

    private val operario = Sesion.DeOperario(Operario("Test", "Operario", "Mañana"))
    private val administrador = Sesion.DeAdministrador(Administrador("Test Admin"))

    @Test
    fun iniciarSesion_operario_navegaADashboard() {
        val vm = crearViewModel()
        vm.iniciarSesion(operario)
        assertEquals(Pantalla.Dashboard, vm.pantalla)
    }

    @Test
    fun iniciarSesion_administrador_navegaAVehiculos() {
        val vm = crearViewModel()
        vm.iniciarSesion(administrador)
        assertEquals(Pantalla.Vehiculos, vm.pantalla)
    }

    @Test
    fun cerrarSesion_vuelveALogin() {
        val vm = crearViewModel()
        vm.iniciarSesion(operario)
        vm.cerrarSesion()
        assertEquals(Pantalla.Login, vm.pantalla)
        assertNull(vm.sesion)
    }

    @Test
    fun navegar_aDestinoDeTab_reemplazaLaPilaEnVezDeApilar() {
        val vm = crearViewModel()
        vm.iniciarSesion(operario)
        vm.navegar(Pantalla.Avisos)
        vm.navegar(Pantalla.Insumos)
        assertEquals(Pantalla.Insumos, vm.pantalla)

        // Los tabs no se apilan entre sí: cambiar de tab reemplaza la pila, así que
        // "volver" desde un tab no hace nada (ya está en la raíz).
        vm.volver()
        assertEquals(Pantalla.Insumos, vm.pantalla)
    }

    @Test
    fun navegar_aDetalle_apilaYVolverRegresaAlDestinoAnteriorReal() {
        val vm = crearViewModel()
        vm.iniciarSesion(operario)
        vm.navegar(Pantalla.Avisos)
        vm.navegar(Pantalla.Detalle("P001"))
        assertEquals(Pantalla.Detalle("P001"), vm.pantalla)

        vm.volver()

        // Antes de la pila real, "volver" desde Detalle siempre iba a Dashboard;
        // ahora regresa a Avisos, el destino real desde el que se navegó.
        assertEquals(Pantalla.Avisos, vm.pantalla)
    }

    @Test
    fun volver_enLaRaiz_noHaceNada() {
        val vm = crearViewModel()
        vm.iniciarSesion(operario)
        vm.volver()
        assertEquals(Pantalla.Dashboard, vm.pantalla)
    }

    @Test
    fun pedido_existente_seEncuentraPorId() {
        val vm = crearViewModel()
        assertNotNull(vm.pedido("P001"))
        assertNull(vm.pedido("NO-EXISTE"))
    }

    @Test
    fun avanzarEtapa_actualizaSoloElPedidoAfectado() {
        val vm = crearViewModel()
        val etapaOriginalP003 = vm.pedido("P003")!!.etapaActual

        vm.avanzarEtapa("P003", "avance de prueba")

        assertTrue(vm.pedido("P003")!!.etapaActual != etapaOriginalP003)
        // P001 no debe verse afectado por avanzar P003.
        assertEquals(EtapaProceso.LAVADO, vm.pedido("P001")!!.etapaActual)
    }

    @Test
    fun avanzarEtapa_conAlertaActiva_noAvanza() {
        val vm = crearViewModel()
        // P002 nace con una alerta INSUMO_CRITICO activa (ver MockData).
        val etapaOriginal = vm.pedido("P002")!!.etapaActual

        vm.avanzarEtapa("P002", "")

        assertEquals(etapaOriginal, vm.pedido("P002")!!.etapaActual)
    }

    @Test
    fun registrarAlerta_yResolverAlerta_desbloqueanElPedido() {
        val vm = crearViewModel()
        vm.registrarAlerta("P001", TipoAlerta.RETRASO, "demora en lavado")
        assertTrue(vm.pedido("P001")!!.estaBloqueado)

        vm.resolverAlerta("P001")
        assertFalse(vm.pedido("P001")!!.estaBloqueado)
    }

    @Test
    fun crearAlertaStock_generaIdsMonotonicosSinColisionar() {
        val vm = crearViewModel()
        val previos = vm.alertasStock.size

        vm.crearAlertaStock(Insumo.CLORO, SeveridadStock.AGOTADO, "sin cloro")
        vm.crearAlertaStock(Insumo.BOLSAS, SeveridadStock.BAJO, "quedan pocas")

        assertEquals(previos + 2, vm.alertasStock.size)
        val ids = vm.alertasStock.map { it.id }
        assertEquals(ids.size, ids.toSet().size) // todos distintos
    }

    @Test
    fun pedidoPorComanda_buscaSinDistinguirMayusculas() {
        val vm = crearViewModel()
        assertNotNull(vm.pedidoPorComanda("cmd-1042"))
        assertNull(vm.pedidoPorComanda(""))
        assertNull(vm.pedidoPorComanda("no-existe"))
    }

    @Test
    fun siguientePendiente_priorizaActivaSinBloqueo() {
        val vm = crearViewModel()
        val siguiente = vm.siguientePendiente()
        assertNotNull(siguiente)
        // No debe elegir P002 (bloqueada) ni P005 (ya finalizada) mientras haya otra opción.
        assertFalse(siguiente.esFinal)
        assertFalse(siguiente.estaBloqueado)
    }
}
