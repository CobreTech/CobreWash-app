
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



public interface AnularComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AnularComandaMutation.Data,
      AnularComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val motivoAnulacion: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comanda_update: ComandaKey?,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AnularComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AnularComandaMutation.ref(
  
    id: java.util.UUID,motivoAnulacion: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AnularComandaMutation.Data,
    AnularComandaMutation.Variables
  > =
  ref(
    
      AnularComandaMutation.Variables(
        id=id,motivoAnulacion=motivoAnulacion,
  
      )
    
  )

public suspend fun AnularComandaMutation.execute(

  
    
      id: java.util.UUID,motivoAnulacion: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AnularComandaMutation.Data,
    AnularComandaMutation.Variables
  > =
  ref(
    
      id=id,motivoAnulacion=motivoAnulacion,
  
    
  ).execute()


