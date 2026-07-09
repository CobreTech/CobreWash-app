package com.elcobre.lavanderiaelcobre.data.model

/** Vehículo de la flota de reparto ([TipoComanda.DOMICILIO]); check-in/checkout diario en [ChequeoVehiculo]. */
data class Vehiculo(
    val id: String,
    val patente: String,
    val modelo: String,
    val kilometrajeActual: Int,
)

/** Foto adjunta a un chequeo de vehículo; en el prototipo, `uri` apunta a un recurso mock. */
data class FotoChequeo(
    val id: String,
    val uri: String,
    val descripcion: String? = null,
)

/**
 * Registro de salida/entrada diaria de un [Vehiculo]: nace al dar la salida
 * (`kmSalida` no nulo) y se cierra al registrar la entrada (`kmEntrada` no nulo).
 */
data class ChequeoVehiculo(
    val id: String,
    val vehiculoId: String,
    val fecha: String,
    val responsable: String,
    val kmSalida: Int? = null,
    val fotosSalida: List<FotoChequeo> = emptyList(),
    val observacionesSalida: String? = null,
    val horaSalida: String? = null,
    val kmEntrada: Int? = null,
    val fotosEntrada: List<FotoChequeo> = emptyList(),
    val observacionesEntrada: String? = null,
    val horaEntrada: String? = null,
) {
    /** `true` si ya se dio la salida pero el vehículo no ha vuelto. */
    val estaEnRuta: Boolean get() = kmSalida != null && kmEntrada == null

    /** `true` si el chequeo tiene tanto salida como entrada registradas. */
    val estaCerrado: Boolean get() = kmSalida != null && kmEntrada != null

    /** Kilómetros recorridos en la jornada, o `null` si el chequeo no está cerrado. */
    val kilometrosRecorridos: Int?
        get() = if (estaCerrado) kmEntrada!! - kmSalida!! else null

    fun registrarSalida(
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        hora: String,
    ): ChequeoVehiculo = copy(
        kmSalida = km,
        fotosSalida = fotos,
        observacionesSalida = observaciones,
        horaSalida = hora,
    )

    fun registrarEntrada(
        km: Int,
        fotos: List<FotoChequeo>,
        observaciones: String?,
        hora: String,
    ): ChequeoVehiculo = copy(
        kmEntrada = km,
        fotosEntrada = fotos,
        observacionesEntrada = observaciones,
        horaEntrada = hora,
    )
}
