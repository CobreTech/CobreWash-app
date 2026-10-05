
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



public interface EntregarComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EntregarComandaMutation.Data,
      EntregarComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda_update: ComandaKey?,
  
    val entrega: Int,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
    val comandaNotificacion_insert: ComandaNotificacionKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EntregarComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EntregarComandaMutation.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    EntregarComandaMutation.Data,
    EntregarComandaMutation.Variables
  > =
  ref(
    
      EntregarComandaMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun EntregarComandaMutation.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    EntregarComandaMutation.Data,
    EntregarComandaMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


