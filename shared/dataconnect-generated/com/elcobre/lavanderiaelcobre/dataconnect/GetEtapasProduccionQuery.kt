
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


public interface GetEtapasProduccionQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetEtapasProduccionQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val etapaProduccions: List<EtapaProduccionsItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class EtapaProduccionsItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val orden: Int,
  
    val descripcion: String?,
  
    val tiempoEstimadoMin: Int?,
  
  ) {
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetEtapasProduccion"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetEtapasProduccionQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetEtapasProduccionQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetEtapasProduccionQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetEtapasProduccionQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetEtapasProduccionQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetEtapasProduccionQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

