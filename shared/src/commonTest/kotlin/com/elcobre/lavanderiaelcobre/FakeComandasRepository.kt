package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.ComandasRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.model.EtapaProceso
import com.elcobre.lavanderiaelcobre.data.model.EventoHistorial
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.Prioridad
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta
import com.elcobre.lavanderiaelcobre.data.model.TipoComanda
import com.elcobre.lavanderiaelcobre.data.model.TipoEvento

internal fun pedidosFixturePrueba(): List<Pedido> = listOf(
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
                "Operario",
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
    ),
)

class FakeComandasRepository : ComandasRepository {
    val pedidos = pedidosFixturePrueba().toMutableList()
    var desconectado = false
    var falla = false
    var escaneos = 0
    override suspend fun asignadas(): List<Pedido> {
        if (falla) throw OperacionComandaException("Sin conexión")
        return pedidos.toList()
    }
    override suspend fun detalle(id: String) = pedidos.firstOrNull { it.id == id }
    override suspend fun buscarNumero(numero: String) = pedidos.firstOrNull { it.comanda == numero }
    override suspend fun escanear(codigo: String): Pedido? {
        if (falla) throw OperacionComandaException("Asignación rechazada")
        escaneos++
        return pedidos.firstOrNull { it.id == codigo }
    }
    override suspend fun avanzar(pedido: Pedido) {
        if (falla) throw OperacionComandaException("Avance rechazado")
        reemplazar(pedido.avanzarEtapa("10:00", "Test", ""))
    }
    override suspend fun comentar(id: String, texto: String) {
        detalle(id)?.let { reemplazar(it.agregarComentario(texto, "10:00", "Test")) }
    }
    override suspend fun alertar(id: String, motivo: String, texto: String) {
        detalle(id)?.let { reemplazar(it.registrarAlerta(TipoAlerta.RETRASO, texto, "10:00", "Test")) }
    }
    override suspend fun resolver(pedido: Pedido) = reemplazar(pedido.resolverAlerta("10:00", "Test"))
    override fun cerrarSesion() { desconectado = true }
    private fun reemplazar(pedido: Pedido) { pedidos[pedidos.indexOfFirst { it.id == pedido.id }] = pedido }
}
