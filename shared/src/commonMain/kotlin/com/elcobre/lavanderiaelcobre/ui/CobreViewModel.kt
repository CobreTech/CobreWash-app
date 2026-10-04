package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.mock.RelojMock
import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.AlertaStock
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.Sesion
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.ui.theme.ModoTema

/** Destinos de nivel superior: navegar a uno de estos reemplaza la pila en vez de apilar (semántica de bottom-nav). */
private val TABS: Set<Pantalla> = setOf(
    Pantalla.Dashboard,
    Pantalla.Avisos,
    Pantalla.Insumos,
    Pantalla.Configuracion,
    Pantalla.Vehiculos,
)

/**
 * Dueño del estado de la app del Operario: pedidos, avisos, alertas de stock,
 * sesión y navegación son responsabilidades cohesivas de un mismo flujo, así que
 * superan el umbral por defecto de funciones por clase a propósito (el estado de
 * vehículos, que pertenece a una sesión de rol distinto, vive aparte en
 * [VehiculosViewModel]). Los modelos de dominio ([Pedido]) son inmutables; este
 * holder mantiene la lista observable y reemplaza la instancia afectada por su
 * copia actualizada en cada operación.
 */
@Suppress("TooManyFunctions")
class CobreViewModel : ViewModel() {

    private val _pedidos: SnapshotStateList<Pedido> =
        mutableStateListOf<Pedido>().apply { addAll(MockData.pedidosIniciales()) }

    val pedidos: List<Pedido> get() = _pedidos

    /** RF-AN04 — solo lectura en el prototipo. */
    val avisos: List<Aviso> = MockData.avisosIniciales()

    private val _alertasStock: SnapshotStateList<AlertaStock> =
        mutableStateListOf<AlertaStock>().apply { addAll(MockData.alertasStockIniciales()) }

    /** Alertas de stock reportadas por el operario (RF-AN05), más reciente primero. */
    val alertasStock: List<AlertaStock> get() = _alertasStock

    // Contador monotónico independiente de la posición en la lista: a diferencia
    // de derivar el id de `lista.size`, no colisiona si algún día se agrega borrado.
    private var alertaStockContador = _alertasStock.size

    var sesion by mutableStateOf<Sesion?>(null)
        private set

    var modoTema by mutableStateOf(ModoTema.CLARO)
        private set

    fun cambiarModoTema(modo: ModoTema) { modoTema = modo }

    fun alternarTema(esOscuroActual: Boolean) {
        modoTema = if (esOscuroActual) ModoTema.CLARO else ModoTema.OSCURO
    }

    // Pila de navegación real (no solo la pantalla actual): vive en el ViewModel para
    // sobrevivir a la recreación de la Activity en cambios de configuración, y permite
    // que `volver()` regrese al destino anterior real en vez de uno fijo hardcodeado.
    private val _backStack: SnapshotStateList<Pantalla> = mutableStateListOf(Pantalla.Login)

    internal val pantalla: Pantalla get() = _backStack.last()

    val operario: Operario? get() = (sesion as? Sesion.DeOperario)?.operario
    val administrador: Administrador? get() = (sesion as? Sesion.DeAdministrador)?.administrador

    /** Los destinos de [TABS] reemplazan la pila (como tabs de bottom-nav); el resto se apila. */
    internal fun navegar(destino: Pantalla) {
        if (destino in TABS) {
            _backStack.clear()
            _backStack.add(destino)
        } else {
            _backStack.add(destino)
        }
    }

    val puedeVolver: Boolean get() = _backStack.size > 1

    /** Regresa al destino ,anterior real de la pila; no hace nada si ya está en la raíz. */
    internal fun volver() {
        if (_backStack.size > 1) _backStack.removeAt(_backStack.lastIndex)
    }

    fun iniciarSesion(s: Sesion) {
        sesion = s
        val destino = when (s) {
            is Sesion.DeOperario -> Pantalla.Dashboard
            is Sesion.DeAdministrador -> Pantalla.Vehiculos
        }
        _backStack.clear()
        _backStack.add(destino)
    }

    fun cerrarSesion() {
        sesion = null
        _backStack.clear()
        _backStack.add(Pantalla.Login)
    }

    fun pedido(id: String): Pedido? = _pedidos.firstOrNull { it.id == id }

    fun avanzarEtapa(id: String, comentario: String) =
        _pedidos.reemplazarPrimero({ it.id == id }) { it.avanzarEtapa(RelojMock.ahora(), autor(), comentario) }

    fun agregarComentario(id: String, texto: String) =
        _pedidos.reemplazarPrimero({ it.id == id }) { it.agregarComentario(texto, RelojMock.ahora(), autor()) }

    fun registrarAlerta(id: String, tipo: TipoAlerta, comentario: String) =
        _pedidos.reemplazarPrimero({ it.id == id }) { it.registrarAlerta(tipo, comentario, RelojMock.ahora(), autor()) }

    fun resolverAlerta(id: String) =
        _pedidos.reemplazarPrimero({ it.id == id }) { it.resolverAlerta(RelojMock.ahora(), autor()) }

    /** RF-AN05: inserta al frente (más reciente primero). */
    fun crearAlertaStock(insumo: Insumo, severidad: SeveridadStock, nota: String) {
        alertaStockContador += 1
        _alertasStock.add(
            0,
            AlertaStock(
                id = "AS${alertaStockContador.toString().padStart(2, '0')}",
                insumo = insumo,
                severidad = severidad,
                nota = nota.trim(),
                hora = RelojMock.ahora(),
                autor = autor(),
            ),
        )
    }

    fun pedidoPorComanda(codigo: String): Pedido? {
        val q = codigo.trim()
        if (q.isEmpty()) return null
        return _pedidos.firstOrNull { it.comanda.equals(q, ignoreCase = true) }
    }

    /** Usada por el escaneo simulado: prioriza una activa sin bloqueo, luego cualquiera no finalizada. */
    fun siguientePendiente(): Pedido? =
        _pedidos.firstOrNull { !it.esFinal && !it.estaBloqueado }
            ?: _pedidos.firstOrNull { !it.esFinal }
            ?: _pedidos.firstOrNull()

    private fun autor(): String = operario?.nombre ?: administrador?.nombre ?: MockData.operario.nombre
}
