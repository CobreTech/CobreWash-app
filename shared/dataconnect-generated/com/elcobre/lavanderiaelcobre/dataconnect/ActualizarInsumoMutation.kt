
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



public interface ActualizarInsumoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      ActualizarInsumoMutation.Data,
      ActualizarInsumoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockMinimo: Double,
  
    val activo: Boolean,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val insumo_update: InsumoKey?,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "ActualizarInsumo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun ActualizarInsumoMutation.ref(
  
    id: java.util.UUID,nombre: String,unidadMedida: String,stockMinimo: Double,activo: Boolean,

  
  
): com.google.firebase.dataconnect.MutationRef<
    ActualizarInsumoMutation.Data,
    ActualizarInsumoMutation.Variables
  > =
  ref(
    
      ActualizarInsumoMutation.Variables(
        id=id,nombre=nombre,unidadMedida=unidadMedida,stockMinimo=stockMinimo,activo=activo,
  
      )
    
  )

public suspend fun ActualizarInsumoMutation.execute(

  
    
      id: java.util.UUID,nombre: String,unidadMedida: String,stockMinimo: Double,activo: Boolean,

  

  ): com.google.firebase.dataconnect.MutationResult<
    ActualizarInsumoMutation.Data,
    ActualizarInsumoMutation.Variables
  > =
  ref(
    
      id=id,nombre=nombre,unidadMedida=unidadMedida,stockMinimo=stockMinimo,activo=activo,
  
    
  ).execute()


