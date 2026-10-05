
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



public interface CrearInsumoMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      CrearInsumoMutation.Data,
      CrearInsumoMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val nombre: String,
  
    val unidadMedida: String,
  
    val stockInicial: Double,
  
    val stockMinimo: Double,
  
  ) {
    
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val insumo_insert: InsumoKey,
  
    val movimientoInventario_insert: MovimientoInventarioKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "CrearInsumo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun CrearInsumoMutation.ref(
  
    nombre: String,unidadMedida: String,stockInicial: Double,stockMinimo: Double,

  
  
): com.google.firebase.dataconnect.MutationRef<
    CrearInsumoMutation.Data,
    CrearInsumoMutation.Variables
  > =
  ref(
    
      CrearInsumoMutation.Variables(
        nombre=nombre,unidadMedida=unidadMedida,stockInicial=stockInicial,stockMinimo=stockMinimo,
  
      )
    
  )

public suspend fun CrearInsumoMutation.execute(

  
    
      nombre: String,unidadMedida: String,stockInicial: Double,stockMinimo: Double,

  

  ): com.google.firebase.dataconnect.MutationResult<
    CrearInsumoMutation.Data,
    CrearInsumoMutation.Variables
  > =
  ref(
    
      nombre=nombre,unidadMedida=unidadMedida,stockInicial=stockInicial,stockMinimo=stockMinimo,
  
    
  ).execute()


