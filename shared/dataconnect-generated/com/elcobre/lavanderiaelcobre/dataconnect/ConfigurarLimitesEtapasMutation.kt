
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



public interface ConfigurarLimitesEtapasMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ConfigurarLimitesEtapasMutation.Data,
      ConfigurarLimitesEtapasMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val recepcion: Int,
  
    val lavado: Int,
  
    val secado: Int,
  
    val planchado: Int,
  
    val entrega: Int,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val recepcion: EtapaProduccionKey?,
  
    val lavado: EtapaProduccionKey?,
  
    val secado: EtapaProduccionKey?,
  
    val planchado: EtapaProduccionKey?,
  
    val entrega: EtapaProduccionKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ConfigurarLimitesEtapas"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ConfigurarLimitesEtapasMutation.ref(
  
    recepcion: Int,lavado: Int,secado: Int,planchado: Int,entrega: Int,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ConfigurarLimitesEtapasMutation.Data,
    ConfigurarLimitesEtapasMutation.Variables
  > =
  ref(
    
      ConfigurarLimitesEtapasMutation.Variables(
        recepcion=recepcion,lavado=lavado,secado=secado,planchado=planchado,entrega=entrega,
  
      )
    
  )

public suspend fun ConfigurarLimitesEtapasMutation.execute(

  
    
      recepcion: Int,lavado: Int,secado: Int,planchado: Int,entrega: Int,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ConfigurarLimitesEtapasMutation.Data,
    ConfigurarLimitesEtapasMutation.Variables
  > =
  ref(
    
      recepcion=recepcion,lavado=lavado,secado=secado,planchado=planchado,entrega=entrega,
  
    
  ).execute()


