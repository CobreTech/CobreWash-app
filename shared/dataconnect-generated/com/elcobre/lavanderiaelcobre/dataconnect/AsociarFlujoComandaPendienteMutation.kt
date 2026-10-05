
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



public interface AsociarFlujoComandaPendienteMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AsociarFlujoComandaPendienteMutation.Data,
      AsociarFlujoComandaPendienteMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaEtapa_insertMany: List<ComandaEtapaKey>,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AsociarFlujoComandaPendiente"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AsociarFlujoComandaPendienteMutation.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AsociarFlujoComandaPendienteMutation.Data,
    AsociarFlujoComandaPendienteMutation.Variables
  > =
  ref(
    
      AsociarFlujoComandaPendienteMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun AsociarFlujoComandaPendienteMutation.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AsociarFlujoComandaPendienteMutation.Data,
    AsociarFlujoComandaPendienteMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


