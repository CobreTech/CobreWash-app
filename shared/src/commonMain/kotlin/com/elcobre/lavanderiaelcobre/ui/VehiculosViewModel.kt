package com.elcobre.lavanderiaelcobre.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.mock.RelojMock
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo

/**
 * Estado de la flota de reparto (RF-VE), separado de [CobreViewModel] porque lo
 * usa exclusivamente la sesión de Administrador — un rol distinto al Operario
 * que gobierna pedidos/avisos/alertas, sin solapamiento de datos entre ambos.
 */
class VehiculosViewModel : ViewModel() {

    private val _vehiculos: SnapshotStateList<Vehiculo> =
        mutableStateListOf<Vehiculo>().apply { addAll(MockData.vehiculosIniciales()) }

    val vehiculos: List<Vehiculo> get() = _vehiculos

    private val _chequeosVehiculo: SnapshotStateList<ChequeoVehiculo> =
        mutableStateListOf<ChequeoVehiculo>().apply { addAll(MockData.chequeosVehiculoIniciales()) }

    /** Chequeos de vehículo (check-in/checkout), más reciente primero. */
    val chequeosVehiculo: List<ChequeoVehiculo> get() = _chequeosVehiculo

    // Contadores monotónicos independientes de la posición en la lista: a diferencia
    // de derivar el id de `lista.size`, no colisionan si algún día se agrega borrado.
    private var chequeoContador = _chequeosVehiculo.size
    private var vehiculoContador = _vehiculos.size

    /** RF-VE01: agrega un vehículo nuevo a la flota. */
    fun registrarVehiculo(patente: String, modelo: String, kilometrajeInicial: Int) {
        vehiculoContador += 1
        _vehiculos.add(
            Vehiculo(
                id = "V${vehiculoContador.toString().padStart(2, '0')}",
                patente = patente.trim().uppercase(),
                modelo = modelo.trim(),
                kilometrajeActual = kilometrajeInicial,
            ),
        )
    }

    fun chequeoEnRuta(vehiculoId: String): ChequeoVehiculo? =
        _chequeosVehiculo.firstOrNull { it.vehiculoId == vehiculoId && it.estaEnRuta }

    /** RF-VE04: historial de salidas/regresos de un vehículo, más reciente primero. */
    fun historialVehiculo(vehiculoId: String): List<ChequeoVehiculo> =
        _chequeosVehiculo.filter { it.vehiculoId == vehiculoId }

    fun registrarSalidaVehiculo(
        vehiculoId: String,
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        responsable: String,
    ) {
        chequeoContador += 1
        _chequeosVehiculo.add(
            0,
            ChequeoVehiculo(
                id = "CV${chequeoContador.toString().padStart(2, '0')}",
                vehiculoId = vehiculoId,
                fecha = "Hoy",
                responsable = responsable,
            ).registrarSalida(km, fotos, observaciones, RelojMock.ahora()),
        )
    }

    fun registrarEntradaVehiculo(vehiculoId: String, km: Int, fotos: List<FotoChequeo>, observaciones: String?) {
        if (chequeoEnRuta(vehiculoId) == null) return // sin chequeo abierto, no hay entrada que registrar
        val hora = RelojMock.ahora()
        _chequeosVehiculo.reemplazarPrimero({ it.vehiculoId == vehiculoId && it.estaEnRuta }) {
            it.registrarEntrada(km, fotos, observaciones, hora)
        }
        _vehiculos.reemplazarPrimero({ it.id == vehiculoId }) { it.copy(kilometrajeActual = km) }
    }
}
