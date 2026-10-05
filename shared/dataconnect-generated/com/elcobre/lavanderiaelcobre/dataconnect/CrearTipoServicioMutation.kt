
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



public interface CrearTipoServicioMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearTipoServicioMutation.Data,
      CrearTipoServicioMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val nombre: String,
  
    val precioBase: Double,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tipoServicio_insert: TipoServicioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearTipoServicio"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearTipoServicioMutation.ref(
  
    nombre: String,precioBase: Double,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CrearTipoServicioMutation.Data,
    CrearTipoServicioMutation.Variables
  > =
  ref(
    
      CrearTipoServicioMutation.Variables(
        nombre=nombre,precioBase=precioBase,
  
      )
    
  )

public suspend fun CrearTipoServicioMutation.execute(

  
    
      nombre: String,precioBase: Double,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CrearTipoServicioMutation.Data,
    CrearTipoServicioMutation.Variables
  > =
  ref(
    
      nombre=nombre,precioBase=precioBase,
  
    
  ).execute()


