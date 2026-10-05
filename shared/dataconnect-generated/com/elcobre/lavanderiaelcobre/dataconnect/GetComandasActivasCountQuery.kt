
@file:Suppress(
  "KotlinRedundantDiagnosticSuppress",
  "PropertyName",
  "MayBeConstant",
  "RedundantVisibilityModifier",
  "RedundantCompanionReference",
  "RemoveEmptyClassBody",
  "SpellCheckingInspection",
  "unused",
)

package com.elcobre.lavanderiaelcobre.dataconnect


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetComandasActivasCountQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetComandasActivasCountQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val pendientes: List<PendientesItem>,
  
    val enProceso: List<EnProcesoItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class PendientesItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class EnProcesoItem(
  
    val _count: Int,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetComandasActivasCount"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetComandasActivasCountQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetComandasActivasCountQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetComandasActivasCountQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetComandasActivasCountQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetComandasActivasCountQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetComandasActivasCountQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

