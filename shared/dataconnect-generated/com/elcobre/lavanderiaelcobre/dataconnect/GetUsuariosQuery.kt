
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


import kotlinx.coroutines.flow.filterNotNull as _flow_filterNotNull
import kotlinx.coroutines.flow.map as _flow_map


public interface GetUsuariosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetUsuariosQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuarios: List<UsuariosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class UsuariosItem(
  
    val id: String,
  
    val rut: String?,
  
    val nombre: String,
  
    val apellido: String?,
  
    val email: String,
  
    val telefono: String?,
  
    val activo: Boolean,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val rol: Rol,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetUsuarios"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetUsuariosQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetUsuariosQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetUsuariosQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetUsuariosQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetUsuariosQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetUsuariosQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

