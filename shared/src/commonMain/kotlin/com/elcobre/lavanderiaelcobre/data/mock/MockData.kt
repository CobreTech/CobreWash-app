package com.elcobre.lavanderiaelcobre.data.mock

import com.elcobre.lavanderiaelcobre.data.model.Administrador
import com.elcobre.lavanderiaelcobre.data.model.AlertaStock
import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.data.model.ChequeoVehiculo
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.EventoHistorial
import com.elcobre.lavanderiaelcobre.data.model.FotoChequeo
import com.elcobre.lavanderiaelcobre.data.model.Insumo
import com.elcobre.lavanderiaelcobre.data.model.Operario
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.data.model.SeveridadStock
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento
import com.elcobre.lavanderiaelcobre.data.model.Vehiculo

/**
 * Datos mockeados en memoria. No hay backend ni persistencia: al reiniciar la
 * app se regeneran estos pedidos.
 */
object MockData {

    val operario = Operario(
        nombre = "Camila Riquelme",
        rol = "Operaria de planta",
        turno = "Turno mañana",
    )

    /** Credenciales fijas sugeridas; cualquier par no vacío también entra. */
    const val USUARIO_DEMO = "operario"
    const val PASSWORD_DEMO = "1234"

    val administrador = Administrador(nombre = "Mauricio Aguilera")

    /** Credenciales fijas sugeridas para el rol Administrador; cualquier par no vacío también entra. */
    const val USUARIO_ADMIN_DEMO = "admin"
    const val PASSWORD_ADMIN_DEMO = "1234"

    // Datos de demostración en memoria; se eliminarán al conectar el repositorio real.
    @Suppress("LongMethod")
    fun pedidosIniciales(): List<Pedido> = listOf(
        Pedido(
            id = "P001",
            comanda = "CMD-1042",
            cliente = "Hotel Boutique Los Andes",
            tipo = TipoComanda.DOMICILIO,
            tipoServicio = "Ropa de cama · Lavado y planchado",
            prioridad = Prioridad.ALTA,
            piezas = 48,
            plazoEntrega = "Hoy 18:00",
            etapaActual = EtapaProceso.LAVADO,
            historial = listOf(
                EventoHistorial(
                    TipoEvento.CAMBIO_ETAPA,
                    "Recepción en planta → Lavado",
                    "Carga pesada e ingresada a lavadora industrial 3.",
                    "09:15",
                    "Camila Riquelme",
                ),
                EventoHistorial(
                    TipoEvento.COMENTARIO,
                    "Comentario",
                    "Cliente pide especial cuidado con manteles de lino.",
                    "08:58",
                    "Recepción",
                ),
            ),
        ),
        Pedido(
            id = "P002",
            comanda = "CMD-1043",
            cliente = "Restaurante El Fogón",
            tipo = TipoComanda.EN_TIENDA,
            tipoServicio = "Manteles y servilletas · Lavado en seco",
            prioridad = Prioridad.MEDIA,
            piezas = 32,
            plazoEntrega = "Mañana 12:00",
            etapaActual = EtapaProceso.RECEPCION,
            historial = listOf(
                EventoHistorial(
                    TipoEvento.COMENTARIO,
                    "Comentario",
                    "Ingreso registrado en recepción, pendiente de clasificar.",
                    "10:05",
                    "Recepción",
                ),
            ),
        ).registrarAlerta(
            TipoAlerta.INSUMO_CRITICO,
            "Falta detergente industrial biodegradable.",
            "14:30",
            "Sistema",
        ),
        Pedido(
            id = "P003",
            comanda = "CMD-1039",
            cliente = "Clínica Santa Marta",
            tipo = TipoComanda.EN_TIENDA,
            tipoServicio = "Uniformes clínicos · Lavado sanitizado",
            prioridad = Prioridad.ALTA,
            piezas = 76,
            plazoEntrega = "Hoy 16:00",
            etapaActual = EtapaProceso.LAVADO,
            historial = listOf(
                EventoHistorial(
                    TipoEvento.CAMBIO_ETAPA,
                    "Recepción → Lavado",
                    "Ciclo sanitizado en curso, temperatura verificada.",
                    "11:20",
                    "Camila Riquelme",
                ),
            ),
        ),
        Pedido(
            id = "P004",
            comanda = "CMD-1051",
            cliente = "Gimnasio Pulso Fit",
            tipo = TipoComanda.DOMICILIO,
            tipoServicio = "Toallas deportivas · Lavado estándar",
            prioridad = Prioridad.BAJA,
            piezas = 120,
            plazoEntrega = "Mañana 10:00",
            etapaActual = EtapaProceso.PLANCHADO,
            historial = listOf(
                EventoHistorial(
                    TipoEvento.CAMBIO_ETAPA,
                    "Secado → Planchado",
                    "Planchado y doblado en curso en mesa 2.",
                    "12:40",
                    "Camila Riquelme",
                ),
            ),
        ),
        Pedido(
            id = "P005",
            comanda = "CMD-1028",
            cliente = "Spa Aguas Claras",
            tipo = TipoComanda.EN_TIENDA,
            tipoServicio = "Batas y toallas · Lavado premium",
            prioridad = Prioridad.MEDIA,
            piezas = 54,
            plazoEntrega = "Hoy 14:30",
            etapaActual = EtapaProceso.ENTREGA,
            historial = listOf(
                EventoHistorial(
                    TipoEvento.CAMBIO_ETAPA,
                    "Planchado → Entrega",
                    "Control de calidad aprobado. Empaquetado y etiquetado, esperando retiro del cliente.",
                    "13:10",
                    "Camila Riquelme",
                ),
            ),
        ),
    )

    /** Avisos de administración visibles al operario (RF-AN04 / RF-CI). */
    fun avisosIniciales(): List<Aviso> = listOf(
        Aviso(
            id = "AV01",
            titulo = "Mantención lavadora industrial 2",
            mensaje = "La lavadora 2 estará fuera de servicio hasta las 15:00 por " +
                "mantención preventiva. Redistribuir las cargas a las lavadoras 1 y 3.",
            hora = "08:30",
            dirigidoA = "Todos los operarios",
            destacado = true,
        ),
        Aviso(
            id = "AV02",
            titulo = "Ciclo sanitizado para ropa clínica",
            mensaje = "Usar siempre el ciclo sanitizado en los uniformes de Clínica " +
                "Santa Marta. No mezclar con otras cargas.",
            hora = "09:10",
            dirigidoA = "Turno mañana",
        ),
        Aviso(
            id = "AV03",
            titulo = "Coordinación de fin de turno 13:00",
            mensaje = "Breve reunión en oficina para revisar pendientes del día. " +
                "Asistencia de todo el personal de planta.",
            hora = "11:45",
            dirigidoA = "Todos los operarios",
        ),
    )

    /** Alertas de stock ya reportadas (semilla para la demo del reporte de insumos). */
    fun alertasStockIniciales(): List<AlertaStock> = listOf(
        AlertaStock(
            id = "AS01",
            insumo = Insumo.DETERGENTE,
            severidad = SeveridadStock.BAJO,
            nota = "Queda menos de un bidón para el turno tarde.",
            hora = "10:20",
            autor = "Camila Riquelme",
        ),
    )

    /** Flota de reparto (semilla para la demo del check-in de vehículos). */
    fun vehiculosIniciales(): List<Vehiculo> = listOf(
        Vehiculo(id = "V01", patente = "HLRT-24", modelo = "Suzuki APV 2019", kilometrajeActual = 58_320),
        Vehiculo(id = "V02", patente = "FXYP-87", modelo = "Chevrolet N300 2021", kilometrajeActual = 31_450),
    )

    /** Chequeos de vehículos ya registrados (semilla para la demo). */
    fun chequeosVehiculoIniciales(): List<ChequeoVehiculo> = listOf(
        ChequeoVehiculo(
            id = "CV01",
            vehiculoId = "V01",
            fecha = "Hoy",
            responsable = "Mauricio Aguilera",
        ).registrarSalida(
            km = 58_320,
            fotos = listOf(
                FotoChequeo(id = "F01", uri = "mock://vehiculo/v01/salida-frontal.jpg"),
            ),
            observaciones = "Sin daños visibles. Rayón leve preexistente en parachoques trasero.",
            hora = "08:05",
        ),
    )
}
