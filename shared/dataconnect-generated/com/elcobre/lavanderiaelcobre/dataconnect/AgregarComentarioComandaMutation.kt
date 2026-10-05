
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



public interface AgregarComentarioComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      AgregarComentarioComandaMutation.Data,
      AgregarComentarioComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val texto: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "AgregarComentarioComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun AgregarComentarioComandaMutation.ref(
  
    comandaId: java.util.UUID,texto: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    AgregarComentarioComandaMutation.Data,
    AgregarComentarioComandaMutation.Variables
  > =
  ref(
    
      AgregarComentarioComandaMutation.Variables(
        comandaId=comandaId,texto=texto,
  
      )
    
  )

public suspend fun AgregarComentarioComandaMutation.execute(

  
    
      comandaId: java.util.UUID,texto: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    AgregarComentarioComandaMutation.Data,
    AgregarComentarioComandaMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,texto=texto,
  
    
  ).execute()


