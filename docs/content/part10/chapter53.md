# Capítulo 53: La capa remota: DTOs, Retrofit y módulo de red

> [!TIP]Antes de empezar, recupera lo que ya sabes
> Responde de memoria; no importa acertar, se trata de activar lo que ya sabes. Vuelve a estas preguntas al terminar el capítulo.
>
> 1. ¿Por qué conviene usar DTOs en lugar de serializar directamente el modelo de dominio?
> 2. ¿Qué anotación de Retrofit marca el cuerpo de una petición y cuál un parámetro de la URL?
> 3. ¿Para qué sirve `@InstallIn(SingletonComponent::class)` en un módulo de Hilt?

## Introducción

La aplicación «Mis Contactos» se comunica con la API REST a través de la capa remota. Esta capa se encarga de convertir las respuestas JSON del servidor en objetos de transferencia de datos (DTOs), ofrecer una interfaz tipada mediante **Retrofit** y traducir cualquier fallo de red en un `ErrorDatos` que el resto de la aplicación pueda entender sin depender de bibliotecas externas.

En este capítulo construiremos los DTOs, la interfaz `ContactApi`, las funciones de mapeo hacia el dominio, el traductor de errores y el módulo de inyección de dependencias `NetworkModule`.

## Los modelos de transferencia de datos (DTOs)

En `data/remote/dto/ContactDto.kt` definimos las estructuras serializables con `kotlinx.serialization` que reflejan el formato exacto que produce y consume la API Spring Boot con formato HAL:

```kotlin
package com.ejemplo.miscontactos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Respuesta de la API para un contacto individual. */
@Serializable
data class ContactDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null
)

/** Respuesta paginada con formato HAL de Spring HATEOAS. */
@Serializable
data class ContactPageDto(
    @SerialName("_embedded")
    val embedded: ContactEmbeddedDto? = null,
    val page: PageMetadataDto? = null
)

@Serializable
data class ContactEmbeddedDto(
    @SerialName("contactResponseList")
    val contacts: List<ContactDto> = emptyList()
)

@Serializable
data class PageMetadataDto(
    val size: Int = 20,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val number: Int = 0
)

/** Cuerpo JSON que enviamos para crear (POST) o actualizar (PUT) un contacto. */
@Serializable
data class ContactRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null
)

/** Estructura estándar de error devuelta por la API ante un código HTTP no exitoso. */
@Serializable
data class ApiErrorDto(
    val status: Int = 0,
    val error: String? = null,
    val message: String? = null,
    val errors: List<String> = emptyList()
)
```

Fíjate en cómo manejamos las particularidades del JSON:
- `@SerialName("_embedded")` y `@SerialName("contactResponseList")` mapean los nombres de propiedades con guiones o convenciones de Spring Boot a nombres limpios en Kotlin.
- Los campos opcionales tienen valores por defecto (`= null` o `= emptyList()`), evitando excepciones de deserialización si el servidor omite alguna clave (por ejemplo, cuando una búsqueda no arroja resultados y la clave `_embedded` no viene en la respuesta).

## La interfaz de Retrofit: `ContactApi`

En `data/remote/ContactApi.kt` declaramos las cinco operaciones REST como funciones `suspend`:

```kotlin
package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ContactDto
import com.ejemplo.miscontactos.data.remote.dto.ContactPageDto
import com.ejemplo.miscontactos.data.remote.dto.ContactRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactApi {

    @GET("api/contact")
    suspend fun obtenerContactos(
        @Query("page") pagina: Int,
        @Query("size") tamano: Int,
        @Query("search") busqueda: String? = null,
        @Query("sort") orden: String = "firstName,asc"
    ): ContactPageDto

    @GET("api/contact/{id}")
    suspend fun obtenerContacto(@Path("id") id: Int): ContactDto

    @POST("api/contact")
    suspend fun crearContacto(@Body contacto: ContactRequestDto): ContactDto

    @PUT("api/contact/{id}")
    suspend fun actualizarContacto(
        @Path("id") id: Int,
        @Body contacto: ContactRequestDto
    ): ContactDto

    @DELETE("api/contact/{id}")
    suspend fun eliminarContacto(@Path("id") id: Int)
}
```

- `@Query("search") busqueda: String? = null`: si `busqueda` es `null`, Retrofit omite el parámetro en la URL generada.
- `@DELETE`: no declara tipo de retorno (devuelve `Unit`), ya que la API responde con un código `204 No Content` sin cuerpo.

## Mapeo entre DTOs y modelos de dominio

En `data/remote/Mappers.kt` creamos funciones de extensión para transformar los DTOs en modelos de dominio (`Contacto` y `DatosContacto`):

```kotlin
package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ContactDto
import com.ejemplo.miscontactos.data.remote.dto.ContactRequestDto
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.DatosContacto

fun ContactDto.aDominio(esFavorito: Boolean = false): Contacto = Contacto(
    id = id,
    nombre = firstName,
    apellido = lastName,
    email = email,
    telefono = phone,
    direccion = address,
    ciudad = city,
    esFavorito = esFavorito
)

fun DatosContacto.aRequest(): ContactRequestDto = ContactRequestDto(
    firstName = nombre,
    lastName = apellido,
    email = email,
    phone = telefono,
    address = direccion,
    city = ciudad
)
```

Gracias a estas funciones, los campos de la API (`firstName`, `lastName`, `phone`) quedan aislados en la capa remota. El resto de la app utiliza nombres en español (`nombre`, `apellido`, `telefono`).

## Traducción centralizada de errores

En `data/remote/Errores.kt` traducimos cualquier excepción generada por OkHttp o Retrofit a una instancia de `ErrorDatos`:

```kotlin
package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ApiErrorDto
import com.ejemplo.miscontactos.model.ErrorDatos
import java.io.IOException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

/**
 * Traduce excepciones de red (IOException, HttpException) a un [ErrorDatos]
 * de la capa de dominio.
 */
fun Throwable.aErrorDatos(json: Json): ErrorDatos = when (this) {
    is ErrorDatos -> this
    is IOException -> ErrorDatos.SinConexion()
    is HttpException -> {
        val cuerpo = response()?.errorBody()?.string()
        val error = cuerpo?.let {
            runCatching { json.decodeFromString<ApiErrorDto>(it) }.getOrNull()
        }
        when (code()) {
            400 -> ErrorDatos.Validacion(error?.errors.orEmpty())
            404 -> ErrorDatos.NoEncontrado()
            409 -> ErrorDatos.Conflicto(error?.message ?: "Conflicto")
            else -> ErrorDatos.Desconocido(error?.message ?: "HTTP ${code()}")
        }
    }
    else -> ErrorDatos.Desconocido(message ?: "Error desconocido")
}
```

- **`IOException`**: incluye falta de conexión a internet, servidor apagado o `SocketTimeoutException` por falta de permisos. Se traduce a `ErrorDatos.SinConexion()`.
- **`HttpException`**: lee el código HTTP y el JSON de error del cuerpo:
  - `400`: extrae la lista `errors` enviada por las validaciones de Spring Boot.
  - `404`: recurso inexistente.
  - `409`: conflicto (por ejemplo, correo ya registrado).
  - Cualquier otro código: error desconocido con el mensaje del servidor o el código de estado.

## El módulo de inyección: `NetworkModule`

En `di/NetworkModule.kt` le enseñamos a Hilt cómo construir `Json`, `OkHttpClient`, `Retrofit` y `ContactApi`:

```kotlin
package com.ejemplo.miscontactos.di

import com.ejemplo.miscontactos.data.remote.ContactApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Desde el emulador Android, 10.0.2.2 es la dirección del computador anfitrión. */
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideContactApi(retrofit: Retrofit): ContactApi =
        retrofit.create(ContactApi::class.java)
}
```

Cada método `@Provides` tiene ámbito `@Singleton`, garantizando que en toda la aplicación exista un único cliente HTTP y un único convertidor JSON reutilizable.

## Resumen

- Los DTOs en `ContactDto.kt` modelan las respuestas HAL del backend con `@SerialName` y valores por defecto para claves ausentes.
- `ContactApi` expone los cinco endpoints CRUD de la API con funciones `suspend` y anotaciones `@GET`, `@POST`, `@PUT`, `@DELETE`, `@Path`, `@Query` y `@Body`.
- Las funciones de extensión en `Mappers.kt` aíslan los DTOs de red del modelo de dominio `Contacto`.
- `aErrorDatos` convierte excepciones de red y códigos HTTP en variantes de la clase sellada `ErrorDatos`.
- `NetworkModule` provee las dependencias de red (`Json`, `OkHttpClient`, `Retrofit`, `ContactApi`) mediante `@Provides` y `@Singleton`.

## Para cerrar

Quieres añadir a `ContactApi` una función que busque contactos solo por ciudad, reutilizando el endpoint existente `GET api/contact` con un parámetro de consulta `city`. Completa la firma `suspend`:

```kotlin
@GET("api/contact")
suspend fun obtenerPorCiudad( ??? ): ContactPageDto
```

Responde antes de mirar la respuesta.

??? success "Ver respuesta y razonamiento"
    ```kotlin
    @GET("api/contact")
    suspend fun obtenerPorCiudad(@Query("city") ciudad: String): ContactPageDto
    ```

    Un parámetro que forma parte de la cadena de consulta (`?city=...`) se declara con `@Query`, igual que `search`, `page` o `sort` en `obtenerContactos`. `@Path` se reserva para los segmentos de la ruta, como el `{id}` de `obtenerContacto`. Error común: usar `@Path` para lo que en realidad es un `@Query` (o al revés), lo que produce una URL con el formato equivocado.

En el próximo capítulo construiremos la capa local con **Room** para almacenar contactos en caché y guardar la lista de favoritos.
