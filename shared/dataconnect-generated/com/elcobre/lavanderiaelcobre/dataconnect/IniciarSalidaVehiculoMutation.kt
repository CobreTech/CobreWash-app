
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



public interface IniciarSalidaVehiculoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      IniciarSalidaVehiculoMutation.Data,
      IniciarSalidaVehiculoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val salidaId: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val salidaVehiculo_update: SalidaVehiculoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "IniciarSalidaVehiculo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun IniciarSalidaVehiculoMutation.ref(
  
    salidaId: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    IniciarSalidaVehiculoMutation.Data,
    IniciarSalidaVehiculoMutation.Variables
  > =
  ref(
    
      IniciarSalidaVehiculoMutation.Variables(
        salidaId=salidaId,
  
      )
    
  )

public suspend fun IniciarSalidaVehiculoMutation.execute(

  
    
      salidaId: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    IniciarSalidaVehiculoMutation.Data,
    IniciarSalidaVehiculoMutation.Variables
  > =
  ref(
    
      salidaId=salidaId,
  
    
  ).execute()


