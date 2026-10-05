package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Pedido
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.IncidenciaEstado
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.elcobre.lavanderiaelcobre.dataconnect.execute
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth

actual suspend fun comentarComandaFirebase(id: String, texto: String): Unit = solicitud<Unit> {
    ExampleConnector.instance.agregarComentarioComanda.execute(UUID.fromString(id), texto.trim())
}

actual suspend fun alertarComandaFirebase(id: String, motivo: String, texto: String): Unit = solicitud<Unit> {
    ExampleConnector.instance.registrarIncidenciaComanda.execute(UUID.fromString(id), motivo) { descripcion = texto }
}

actual suspend fun resolverComandaFirebase(id: String): Unit = solicitud<Unit> {
    val incidencias = ExampleConnector.instance.getComandaDetalleOperario.ref(UUID.fromString(id))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comanda?.incidenciaComandas_on_comanda.orEmpty()
        .filter { it.estado.value != IncidenciaEstado.RESUELTA }
    val usuario = FirebaseAuth.getInstance().currentUser?.uid
    if (incidencias.any { it.reportadaPor.id != usuario }) {
        throw OperacionComandaException("Esta incidencia debe resolverla administración o quien la reportó.")
    }
    for (incidencia in incidencias) ExampleConnector.instance.resolverMiIncidencia.execute(incidencia.id)
}

actual suspend fun buscarNumeroComandaFirebase(numero: String): Pedido? = solicitud {
    val filas = ExampleConnector.instance.getSeguimientoProduccion.ref { buscar = numero.trim(); limit = 100 }
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.comandas
    filas.firstOrNull { it.numeroComanda.equals(numero.trim(), ignoreCase = true) }
        ?.let { getComandaDetalleFirebase(it.id.toString()) }
}
