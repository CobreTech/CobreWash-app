package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.data.model.Aviso
import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal actual suspend fun consultarAvisosFirebase(offset: Int, limit: Int): PaginaAvisos = solicitud {
    val data = ExampleConnector.instance.getAvisosParaEquipo.ref("operario") {
        this.offset = offset
        this.limit = limit
    }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-CL")).apply {
        timeZone = TimeZone.getTimeZone("America/Santiago")
    }
    PaginaAvisos(data.avisos.map {
        Aviso(
            id = it.id.toString(), titulo = it.titulo, mensaje = it.contenido,
            hora = formato.format(it.fechaPublicacion.toDate()),
            dirigidoA = if (it.rolDestinatario == null) "Todos los equipos" else "Operarios",
            autor = listOfNotNull(it.autor.nombre, it.autor.apellido).joinToString(" ").trim(),
        )
    }, data.total.firstOrNull()?._count ?: 0)
}
