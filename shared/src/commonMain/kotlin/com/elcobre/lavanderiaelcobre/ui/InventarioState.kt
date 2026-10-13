package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.elcobre.lavanderiaelcobre.data.ConteoFisico
import com.elcobre.lavanderiaelcobre.data.InsumoInventario
import com.elcobre.lavanderiaelcobre.data.InventarioRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.ui.screens.escaner.extraerQrInsumo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class InventarioState(private val scope: CoroutineScope, private val repository: InventarioRepository) {
    var insumos by mutableStateOf<List<InsumoInventario>>(emptyList())
        private set
    var seleccionado by mutableStateOf<InsumoInventario?>(null)
        private set
    var conteos by mutableStateOf<Map<String, ConteoFisico>>(emptyMap())
        private set
    var cargando by mutableStateOf(false)
        private set
    var buscando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private var generacion = 0
    private var habilitado = false
    private var revision = 0

    fun cambiarSesion(activo: Boolean) {
        generacion++
        habilitado = activo
        insumos = emptyList()
        seleccionado = null
        conteos = emptyMap()
        cargando = false
        buscando = false
        error = null
    }

    fun seleccionar(insumo: InsumoInventario?) { seleccionado = insumo }

    fun conteoDe(insumoId: String): ConteoFisico? = conteos[insumoId]

    fun registrarConteoFisico(insumoId: String, cantidadContada: Double): Boolean {
        if (!habilitado) return false
        if (!cantidadContada.isFinite() || cantidadContada < 0.0) {
            error = "La cantidad contada debe ser cero o mayor."
            return false
        }
        val insumo = insumos.firstOrNull { it.id == insumoId }
            ?: seleccionado?.takeIf { it.id == insumoId }
        if (insumo == null) {
            error = "El insumo no está disponible para registrar conteo."
            return false
        }
        error = null
        conteos = conteos + (insumoId to ConteoFisico(insumoId, cantidadContada, insumo.stockActual))
        return true
    }

    fun refrescar() {
        if (!habilitado || cargando) return
        val inicio = generacion
        val antes = revision
        cargando = true
        error = null
        scope.launch {
            try {
                val resultado = repository.catalogo()
                if (inicio == generacion && antes == revision) {
                    insumos = resultado.filter { it.activo }.distinctBy { it.id }
                    seleccionado = seleccionado?.let { previo -> insumos.firstOrNull { it.id == previo.id } }
                }
            } catch (causa: OperacionComandaException) {
                if (inicio == generacion) error = "No se pudo cargar el inventario: ${causa.message}"
            } finally {
                if (inicio == generacion) cargando = false
            }
        }
    }

    fun escanear(raw: String, onResultado: (String?) -> Unit) {
        if (!habilitado || buscando) return
        val codigo = extraerQrInsumo(raw)
        if (codigo == null) {
            onResultado("El QR no contiene un código válido de insumo.")
            return
        }
        val inicio = generacion
        buscando = true
        error = null
        revision++
        scope.launch {
            try {
                val insumo = repository.porQr(codigo)
                    ?: throw OperacionComandaException("No existe un insumo activo con este QR.")
                if (!insumo.activo) throw OperacionComandaException("El insumo está inactivo.")
                if (inicio == generacion) {
                    revision++
                    seleccionado = insumo
                    insumos = (insumos.filterNot { it.id == insumo.id } + insumo).sortedBy { it.nombre }
                    onResultado(null)
                }
            } catch (causa: OperacionComandaException) {
                if (inicio == generacion) {
                    error = causa.message
                    onResultado(causa.message)
                }
            } finally {
                if (inicio == generacion) buscando = false
            }
        }
    }
}
