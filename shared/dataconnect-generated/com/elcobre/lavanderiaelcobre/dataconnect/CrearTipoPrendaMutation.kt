
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



public interface CrearTipoPrendaMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearTipoPrendaMutation.Data,
      CrearTipoPrendaMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val nombre: String,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val tipoPrenda_insert: TipoPrendaKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearTipoPrenda"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearTipoPrendaMutation.ref(
  
    nombre: String,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CrearTipoPrendaMutation.Data,
    CrearTipoPrendaMutation.Variables
  > =
  ref(
    
      CrearTipoPrendaMutation.Variables(
        nombre=nombre,
  
      )
    
  )

public suspend fun CrearTipoPrendaMutation.execute(

  
    
      nombre: String,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CrearTipoPrendaMutation.Data,
    CrearTipoPrendaMutation.Variables
  > =
  ref(
    
      nombre=nombre,
  
    
  ).execute()


