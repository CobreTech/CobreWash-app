
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



public interface RegistrarseComoClienteMutation :
    com.google.firebase.dataconnect.generated.GeneratedMutation<
      ExampleConnector,
      RegistrarseComoClienteMutation.Data,
      RegistrarseComoClienteMutation.Variables
    >
{
  
    @kotlinx.serialization.Serializable
  public data class Variables(
  
    val rut: String,
  
    val nombre: String,
  
    val apellido: String,
  
    val telefono: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val email: String,
  
    val direccion: com.google.firebase.dataconnect.OptionalVariable<String?>,
  
    val tipoCliente: TipoCliente,
  
  ) {
    
    
      
      @kotlin.DslMarker public annotation class BuilderDsl

      @BuilderDsl
      public interface Builder {
        public var rut: String
        public var nombre: String
        public var apellido: String
        public var telefono: String?
        public var email: String
        public var direccion: String?
        public var tipoCliente: TipoCliente
        
      }

      public companion object {
        @Suppress("NAME_SHADOWING")
        public fun build(
          rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,
          block_: Builder.() -> Unit
        ): Variables {
          var rut= rut
            var nombre= nombre
            var apellido= apellido
            var telefono: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var email= email
            var direccion: com.google.firebase.dataconnect.OptionalVariable<String?> =
                com.google.firebase.dataconnect.OptionalVariable.Undefined
            var tipoCliente= tipoCliente
            

          return object : Builder {
            override var rut: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { rut = value_ }
              
            override var nombre: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { nombre = value_ }
              
            override var apellido: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { apellido = value_ }
              
            override var telefono: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { telefono = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var email: String
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { email = value_ }
              
            override var direccion: String?
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { direccion = com.google.firebase.dataconnect.OptionalVariable.Value(value_) }
              
            override var tipoCliente: TipoCliente
              get() = throw UnsupportedOperationException("getting builder values is not supported")
              set(value_) { tipoCliente = value_ }
              
            
          }.apply(block_)
          .let {
            Variables(
              rut=rut,nombre=nombre,apellido=apellido,telefono=telefono,email=email,direccion=direccion,tipoCliente=tipoCliente,
            )
          }
        }
      }
    
  }
  

  
    @kotlinx.serialization.Serializable
  public data class Data(
  
    val usuario_insert: UsuarioKey,
  
    val cliente_insert: ClienteKey,
  
  ) {
    
    
  }
  

  public companion object {
    public val operationName: String = "RegistrarseComoCliente"

    public val dataDeserializer: kotlinx.serialization.DeserializationStrategy<Data> =
      kotlinx.serialization.serializer()

    public val variablesSerializer: kotlinx.serialization.SerializationStrategy<Variables> =
      kotlinx.serialization.serializer()
  }
}

public fun RegistrarseComoClienteMutation.ref(
  
    rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,

  
    block_: RegistrarseComoClienteMutation.Variables.Builder.() -> Unit = {}
  
): com.google.firebase.dataconnect.MutationRef<
    RegistrarseComoClienteMutation.Data,
    RegistrarseComoClienteMutation.Variables
  > =
  ref(
    
      RegistrarseComoClienteMutation.Variables.build(
        rut=rut,nombre=nombre,apellido=apellido,email=email,tipoCliente=tipoCliente,
  
    block_
      )
    
  )

public suspend fun RegistrarseComoClienteMutation.execute(

  
    
      rut: String,nombre: String,apellido: String,email: String,tipoCliente: TipoCliente,

  
    block_: RegistrarseComoClienteMutation.Variables.Builder.() -> Unit = {}

  ): com.google.firebase.dataconnect.MutationResult<
    RegistrarseComoClienteMutation.Data,
    RegistrarseComoClienteMutation.Variables
  > =
  ref(
    
      rut=rut,nombre=nombre,apellido=apellido,email=email,tipoCliente=tipoCliente,
  
    block_
    
  ).execute()


