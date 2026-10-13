package com.elcobre.lavanderiaelcobre.data

import com.elcobre.lavanderiaelcobre.dataconnect.ExampleConnector
import com.elcobre.lavanderiaelcobre.dataconnect.GetInsumosOperarioQuery
import com.elcobre.lavanderiaelcobre.dataconnect.instance
import com.elcobre.lavanderiaelcobre.dataconnect.ref
import com.google.firebase.dataconnect.QueryRef
import java.util.UUID

internal actual suspend fun catalogoInsumosFirebase(): List<InsumoInventario> = solicitud {
    val resultado = mutableListOf<InsumoInventario>()
    var pagina: List<GetInsumosOperarioQuery.Data.InsumosItem>
    var offset = 0
    do {
        pagina = ExampleConnector.instance.getInsumosOperario.ref {
            limit = 50
            this.offset = offset
        }.execute(QueryRef.FetchPolicy.SERVER_ONLY).data.insumos
        resultado.addAll(pagina.map {
            InsumoInventario(it.id.toString(), it.codigoQr.toString(), it.nombre, it.unidadMedida,
                it.stockActual, it.stockMinimo, it.activo)
        })
        offset += pagina.size
    } while (pagina.size == 50)
    resultado.distinctBy { it.id }
}

internal actual suspend fun insumoPorQrFirebase(codigo: String): InsumoInventario? = solicitud {
    ExampleConnector.instance.getInsumoPorQr.ref(UUID.fromString(codigo))
        .execute(QueryRef.FetchPolicy.SERVER_ONLY).data.insumo?.let {
            InsumoInventario(it.id.toString(), it.codigoQr.toString(), it.nombre, it.unidadMedida,
                it.stockActual, it.stockMinimo, it.activo)
        }
}
