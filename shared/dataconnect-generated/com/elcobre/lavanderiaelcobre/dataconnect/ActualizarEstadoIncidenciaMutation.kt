
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



public interface ActualizarEstadoIncidenciaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ActualizarEstadoIncidenciaMutation.Data,
      ActualizarEstadoIncidenciaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: IncidenciaEstado,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val incidenciaComanda_update: IncidenciaComandaKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ActualizarEstadoIncidencia"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ActualizarEstadoIncidenciaMutation.ref(
  
    id: java.util.UUID,estado: IncidenciaEstado,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ActualizarEstadoIncidenciaMutation.Data,
    ActualizarEstadoIncidenciaMutation.Variables
  > =
  ref(
    
      ActualizarEstadoIncidenciaMutation.Variables(
        id=id,estado=estado,
  
      )
    
  )

public suspend fun ActualizarEstadoIncidenciaMutation.execute(

  
    
      id: java.util.UUID,estado: IncidenciaEstado,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ActualizarEstadoIncidenciaMutation.Data,
    ActualizarEstadoIncidenciaMutation.Variables
  > =
  ref(
    
      id=id,estado=estado,
  
    
  ).execute()


