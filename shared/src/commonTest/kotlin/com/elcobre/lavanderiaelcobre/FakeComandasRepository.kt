package com.elcobre.lavanderiaelcobre

import com.elcobre.lavanderiaelcobre.data.ComandasRepository
import com.elcobre.lavanderiaelcobre.data.OperacionComandaException
import com.elcobre.lavanderiaelcobre.data.mock.MockData
import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.data.model.TipoAlerta

class FakeComandasRepository : ComandasRepository {
    val pedidos = MockData.pedidosIniciales().toMutableList()
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
