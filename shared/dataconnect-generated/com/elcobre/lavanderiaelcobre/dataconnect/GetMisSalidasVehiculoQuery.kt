
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


public interface GetMisSalidasVehiculoQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetMisSalidasVehiculoQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val salidaVehiculos: List<SalidaVehiculosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class SalidaVehiculosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = SalidaVehiculoEstado.EnumValueSerializer::class) EnumValue<SalidaVehiculoEstado>,
  
    val fechaSalida: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaRetorno: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val observaciones: String?,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val vehiculo: Vehiculo,
  
    val inspeccionVehiculos_on_salida: List<InspeccionVehiculosOnSalidaItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Vehiculo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InspeccionVehiculosOnSalidaItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val momento: @kotlinx.serialization.Serializable(with = MomentoInspeccion.EnumValueSerializer::class) EnumValue<MomentoInspeccion>,
  
    val estadoVehiculo: @kotlinx.serialization.Serializable(with = EstadoVehiculo.EnumValueSerializer::class) EnumValue<EstadoVehiculo>,
  
    val kilometraje: Double,
  
    val observaciones: String?,
  
    val registradoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fotoInspeccionVehiculos_on_inspeccion: List<FotoInspeccionVehiculosOnInspeccionItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FotoInspeccionVehiculosOnInspeccionItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rutaStorage: String,
  
    val descripcion: String?,
  
    val orden: Int,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetMisSalidasVehiculo"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetMisSalidasVehiculoQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetMisSalidasVehiculoQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetMisSalidasVehiculoQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetMisSalidasVehiculoQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetMisSalidasVehiculoQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetMisSalidasVehiculoQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

