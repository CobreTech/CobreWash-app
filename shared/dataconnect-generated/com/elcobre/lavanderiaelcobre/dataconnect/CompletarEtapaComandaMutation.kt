
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



public interface CompletarEtapaComandaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CompletarEtapaComandaMutation.Data,
      CompletarEtapaComandaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val comandaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val etapaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val orden: Int,
  
    val estadoComanda: ComandaEstado,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val comandaEtapa_update: ComandaEtapaKey?,
  
    val siguiente: Int,
  
    val comanda_update: ComandaKey?,
  
    val comandaHistorialEstado_insert: ComandaHistorialEstadoKey,
  
    val notificacion: Int?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CompletarEtapaComanda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CompletarEtapaComandaMutation.ref(
  
    comandaId: java.util.UUID,etapaId: java.util.UUID,orden: Int,estadoComanda: ComandaEstado,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CompletarEtapaComandaMutation.Data,
    CompletarEtapaComandaMutation.Variables
  > =
  ref(
    
      CompletarEtapaComandaMutation.Variables(
        comandaId=comandaId,etapaId=etapaId,orden=orden,estadoComanda=estadoComanda,
  
      )
    
  )

public suspend fun CompletarEtapaComandaMutation.execute(

  
    
      comandaId: java.util.UUID,etapaId: java.util.UUID,orden: Int,estadoComanda: ComandaEstado,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CompletarEtapaComandaMutation.Data,
    CompletarEtapaComandaMutation.Variables
  > =
  ref(
    
      comandaId=comandaId,etapaId=etapaId,orden=orden,estadoComanda=estadoComanda,
  
    
  ).execute()


