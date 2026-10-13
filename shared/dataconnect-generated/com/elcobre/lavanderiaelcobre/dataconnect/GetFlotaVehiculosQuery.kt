
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


public interface GetFlotaVehiculosQuery :
    com.google.firebase.dataconnect.generated.GeneratedQuery<
      ExampleConnector,
      GetFlotaVehiculosQuery.Data,
      Unit
    >
{
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val vehiculos: List<VehiculosItem>,
  
    val conductores: List<ConductoresItem>,
  
    val salidas: List<SalidasItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class VehiculosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: Int?,
  
    val descripcion: String?,
  
    val activo: Boolean,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class ConductoresItem(
  
    val id: String,
  
    val rut: String?,
  
    val nombre: String,
  
    val apellido: String?,
  
    val email: String,
  
    val telefono: String?,
  
    val rol: Rol,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class SalidasItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val estado: @kotlinx.serialization.Serializable(with = SalidaVehiculoEstado.EnumValueSerializer::class) EnumValue<SalidaVehiculoEstado>,
  
    val fechaSalida: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val fechaRetorno: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp?,
  
    val observaciones: String?,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val vehiculo: Vehiculo,
  
    val repartidor: Repartidor,
  
    val inspecciones: List<InspeccionesItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Vehiculo(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val patente: String,
  
    val marca: String,
  
    val modelo: String,
  
    val anio: Int?,
  
    val activo: Boolean,
  
  ) {
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class Repartidor(
  
    val id: String,
  
    val rut: String?,
  
    val nombre: String,
  
    val apellido: String?,
  
    val email: String,
  
    val telefono: String?,
  
    val rol: Rol,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class Rol(
  
    val nombre: String,
  
  ) {
    
    
  }
      
    
    
  }
      
        @kotlinx.serialization.Serializable
  public data class InspeccionesItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val momento: @kotlinx.serialization.Serializable(with = MomentoInspeccion.EnumValueSerializer::class) EnumValue<MomentoInspeccion>,
  
    val estadoVehiculo: @kotlinx.serialization.Serializable(with = EstadoVehiculo.EnumValueSerializer::class) EnumValue<EstadoVehiculo>,
  
    val kilometraje: Double,
  
    val observaciones: String?,
  
    val registradoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
    val fotos: List<FotosItem>,
  
  ) {
    
      
        @kotlinx.serialization.Serializable
  public data class FotosItem(
  
    val id: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.UUIDSerializer::class) java.util.UUID,
  
    val rutaStorage: String,
  
    val descripcion: String?,
  
    val orden: Int,
  
    val creadoEn: @kotlinx.serialization.Serializable(with = com.google.firebase.dataconnect.serializers.TimestampSerializer::class) com.google.firebase.Timestamp,
  
  ) {
    
    
  }
      
    
    
  }
      
    
    
  }
      
    
    
  }
  

  public companion object {
    public val operationName: String = "GetFlotaVehiculos"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Unit> =
      kotlinx.serialization.serializer()
  }
}

public fun GetFlotaVehiculosQuery.ref(
  
): com.google.firebase.dataconnect.QueryRef<
    GetFlotaVehiculosQuery.Data,
    Unit
  > =
  ref(
    
      Unit
    
  )

public suspend fun GetFlotaVehiculosQuery.execute(

  

  ): com.google.firebase.dataconnect.QueryResult<
    GetFlotaVehiculosQuery.Data,
    Unit
  > =
  ref(
    
  ).execute()


  public fun GetFlotaVehiculosQuery.flow(
    
    ): kotlinx.coroutines.flow.Flow<GetFlotaVehiculosQuery.Data> =
    ref(
        
      ).subscribe()
      .flow
      ._flow_map { querySubscriptionResult -> querySubscriptionResult.result.getOrNull() }
      ._flow_filterNotNull()
      ._flow_map { it.data }

