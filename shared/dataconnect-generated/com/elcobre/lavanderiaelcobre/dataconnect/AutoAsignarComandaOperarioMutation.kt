
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



public interface AutoAsignarComandaOperarioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AutoAsignarComandaOperarioMutation.Data,
      AutoAsignarComandaOperarioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaEtapa_updateMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AutoAsignarComandaOperario"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AutoAsignarComandaOperarioMutation.ref(
  
    comandaId: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AutoAsignarComandaOperarioMutation.Data,
    AutoAsignarComandaOperarioMutation.Variables
  > =
  ref(
    
      AutoAsignarComandaOperarioMutation.Variables(
        comandaId=comandaId,
  
      )
    
  )

public suspend fun AutoAsignarComandaOperarioMutation.execute(

  
    
      comandaId: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AutoAsignarComandaOperarioMutation.Data,
    AutoAsignarComandaOperarioMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,
  
    
  ).execute()


