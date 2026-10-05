
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



public interface ResolverMiIncidenciaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ResolverMiIncidenciaMutation.Data,
      ResolverMiIncidenciaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val incidenciaComanda_update: IncidenciaComandaKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ResolverMiIncidencia"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ResolverMiIncidenciaMutation.ref(
  
    id: java.util.UUID,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ResolverMiIncidenciaMutation.Data,
    ResolverMiIncidenciaMutation.Variables
  > =
  ref(
    
      ResolverMiIncidenciaMutation.Variables(
        id=id,
  
      )
    
  )

public suspend fun ResolverMiIncidenciaMutation.execute(

  
    
      id: java.util.UUID,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ResolverMiIncidenciaMutation.Data,
    ResolverMiIncidenciaMutation.Variables
  > =
  ref(
    
      id=id,
  
    
  ).execute()


