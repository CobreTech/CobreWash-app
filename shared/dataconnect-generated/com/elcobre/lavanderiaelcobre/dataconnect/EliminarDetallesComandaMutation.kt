
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



public interface EliminarDetallesComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      EliminarDetallesComandaMutation.Data,
      EliminarDetallesComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaDetalle_deleteMany: Int,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "EliminarDetallesComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun EliminarDetallesComandaMutation.ref(
  
    comandaId: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    EliminarDetallesComandaMutation.Data,
    EliminarDetallesComandaMutation.Variables
  > =
  ref(
    
      EliminarDetallesComandaMutation.Variables(
        comandaId=comandaId,
  
      )
    
  )

public suspend fun EliminarDetallesComandaMutation.execute(

  
    
      comandaId: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    EliminarDetallesComandaMutation.Data,
    EliminarDetallesComandaMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,
  
    
  ).execute()


