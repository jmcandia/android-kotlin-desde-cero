# Capítulo 45: Retrofit: configuración, interfaces y endpoints

## Introducción

Ya conoces la API de contactos desde fuera (capítulo 43) y sabes convertir su JSON en objetos de Kotlin (capítulo 44). Falta el paso del medio: **hacer la petición** desde la app. Hacerlo a mano —abrir la conexión, construir la petición, esperar la respuesta, leer el cuerpo— sería tedioso y propenso a errores.

Aquí entra **Retrofit**: una biblioteca que hace ese trabajo por ti. Tú describes la API como una **interfaz de Kotlin**, y Retrofit genera el código que hace las peticiones. En este capítulo aprenderás a configurarlo, a declarar los cinco *endpoints* de la API de contactos, a crear la instancia con Hilt y a resolver los tres obstáculos que aparecen al conectarse a una API que corre en tu propio computador.

## ¿Qué es Retrofit?

**Retrofit** convierte una API REST en una **interfaz de Kotlin**. En lugar de escribir código de red, **declaras** qué *endpoints* existen y qué devuelven, con **anotaciones**. Retrofit lee esa interfaz y crea, por detrás, la implementación que hace las peticiones reales.

Además, se integra con las **coroutines** (los *endpoints* se declaran como funciones `suspend`) y usa `kotlinx.serialization` para convertir el JSON en tus DTOs. Por debajo, las conexiones las hace **OkHttp**, otra biblioteca del mismo autor (Square).

## Instalar Retrofit

Necesitarás tres bibliotecas: **Retrofit**, su **convertidor** para `kotlinx.serialization` y un **interceptor de registro** de OkHttp, que muestra cada petición en Logcat y es muy útil mientras desarrollas. En el catálogo `gradle/libs.versions.toml`:

```toml
[versions]
retrofit = "3.0.0"
okhttp = "4.12.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
```

Y en el `build.gradle.kts` del módulo `app`:

```kotlin
dependencies {
    // ... las que ya tenías
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
}
```

El convertidor necesita que el proyecto ya tenga el plugin y la biblioteca de `kotlinx.serialization`, que configuraste en el capítulo anterior.

> [!NOTE]Nota
> Retrofit es un proyecto de código abierto de Square; su documentación oficial está en [square.github.io/retrofit](https://square.github.io/retrofit/). Las versiones de este capítulo son las que se usaron para probar el código del curso.

## Definir la interfaz de la API

El corazón de Retrofit es una interfaz donde **cada función representa un *endpoint***. Empecemos por el más simple, obtener un contacto:

```kotlin
interface ContactApi {

    @GET("api/contact/{id}")
    suspend fun obtenerContacto(@Path("id") id: Int): ContactDto
}
```

- `@GET("api/contact/{id}")` indica que la función hace un `GET` a esa ruta, relativa a la URL base que configurarás más abajo. La ruta **no empieza con `/`**.
- `{id}` es un hueco en la ruta, y `@Path("id")` indica qué parámetro lo llena. Al llamar a `obtenerContacto(42)`, Retrofit pide `api/contact/42`.
- `suspend fun` hace que la llamada se suspenda mientras espera la respuesta, sin bloquear el hilo principal.
- El tipo de retorno, `ContactDto`, es lo que Retrofit entrega **ya convertido**. Si la respuesta es un `200`, deserializa el cuerpo con `kotlinx.serialization`.

### Parámetros de consulta: `@Query`

La lista usa los parámetros de consulta que probaste en Swagger:

```kotlin
@GET("api/contact")
suspend fun obtenerContactos(
    @Query("page") pagina: Int,
    @Query("size") tamano: Int,
    @Query("search") busqueda: String? = null,
    @Query("sort") orden: String = "firstName,asc"
): ContactPageDto
```

Cada `@Query` agrega un parámetro después del `?`. Al llamar a `obtenerContactos(pagina = 0, tamano = 20)`, Retrofit pide `api/contact?page=0&size=20&sort=firstName%2Casc` (la coma se codifica como `%2C`). Dos detalles útiles:

- Si un parámetro `@Query` vale **`null`**, Retrofit lo **omite**. Por eso `busqueda` es anulable: sin búsqueda, la URL no lleva `search`.
- Los parámetros pueden tener **valores por defecto**, como en cualquier función de Kotlin. El orden alfabético por nombre queda fijo sin que quien llama tenga que recordarlo.

### Enviar datos: `@POST`, `@PUT` y `@Body`

Para crear y modificar, el contacto viaja en el **cuerpo** de la petición. Se indica con `@Body`:

```kotlin
@POST("api/contact")
suspend fun crearContacto(@Body contacto: ContactRequestDto): ContactDto

@PUT("api/contact/{id}")
suspend fun actualizarContacto(
    @Path("id") id: Int,
    @Body contacto: ContactRequestDto
): ContactDto
```

Retrofit **serializa** el `ContactRequestDto` con `kotlinx.serialization` (el `encodeToString` del capítulo anterior) y lo envía como JSON. Las dos funciones devuelven el contacto tal como quedó en el servidor, con su `id`.

### Sin respuesta: `@DELETE`

```kotlin
@DELETE("api/contact/{id}")
suspend fun eliminarContacto(@Path("id") id: Int)
```

La API responde `204 No Content`, sin cuerpo, así que la función no declara tipo de retorno (devuelve `Unit`). Si termina sin lanzar una excepción, el contacto se eliminó.

Con eso, la interfaz completa queda así:

```kotlin
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

Fíjate en que es una interfaz pequeña y fiel a la API: una función por *endpoint*, con DTOs en la entrada y en la salida. No mapea ni decide nada; eso lo hará el repositorio.

### ¿Y si la respuesta es un error?

Si el servidor responde con un código que no es `2xx` (un `404`, un `409`), la función **lanza una excepción** `HttpException`, que trae el código y el cuerpo del error. Si ni siquiera se puede conectar (servidor apagado, sin red), lanza una `IOException`. Distinguir y tratar esos casos es el tema del próximo capítulo.

## Construir la instancia de Retrofit

Con la interfaz definida, falta crear el objeto que la implementa. Se hace con `Retrofit.Builder`:

```kotlin
val json = Json { ignoreUnknownKeys = true }

val cliente = OkHttpClient.Builder()
    .addInterceptor(
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
    )
    .build()

val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080/")
    .client(cliente)
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .build()

val api: ContactApi = retrofit.create(ContactApi::class.java)
```

- **`baseUrl`** es la parte común de todas las URLs. Se combina con la ruta de cada *endpoint* (`api/contact`) para formar la dirección completa. **Debe terminar en `/`**, o Retrofit lanza un error al crearse. Enseguida verás por qué es `10.0.2.2` y no `localhost`.
- **`client`** es el cliente de OkHttp que hará las conexiones. Aquí se le agrega el interceptor de registro con nivel `BASIC`, que escribe en Logcat una línea por petición y otra por respuesta.
- **`addConverterFactory`** le indica a Retrofit cómo convertir los cuerpos. Recibe **la misma instancia de `Json`** configurada en el capítulo anterior, con `ignoreUnknownKeys`. Si usaras el `Json` sin configurar, el `_links` de cada respuesta provocaría un error.
- **`retrofit.create(...)`** genera la implementación de la interfaz. `ContactApi::class.java` es la referencia a la clase que viste en el capítulo 21.

Los imports de este bloque son:

```kotlin
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
```

## Crear Retrofit con Hilt: `@Provides`

Crear Retrofit es costoso, y la app necesita **una sola instancia**, compartida por todos los repositorios. Es un trabajo para Hilt. Pero hay un problema: en el capítulo 42 le enseñaste a Hilt a crear clases **marcando su constructor** con `@Inject`, y `Retrofit` no es una clase tuya: se construye con `Retrofit.Builder`. No puedes agregarle anotaciones.

Para estos casos existe **`@Provides`**: una función, dentro de un módulo, que le enseña a Hilt **cómo construir** un objeto. Crea `di/NetworkModule.kt`:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /** Desde el emulador, 10.0.2.2 es el computador donde corre la API. */
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
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
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

Léelo de abajo hacia arriba, como lo hace Hilt:

- Cuando alguien pide un **`ContactApi`**, Hilt llama a `provideContactApi`. Esa función necesita un `Retrofit`, así que Hilt busca quién lo provee.
- **`provideRetrofit`** lo construye, pero necesita un `OkHttpClient` y un `Json`: Hilt llama a las dos funciones de arriba y le pasa los resultados.
- **`@Singleton`** hace que cada objeto se cree **una sola vez** y se reutilice.
- El módulo es un **`object`** (y no una `abstract class` como el `DataModule` del capítulo 42) porque sus funciones tienen cuerpo y no necesitan estado.

Los **parámetros** de una función `@Provides` son sus dependencias. Tú nunca llamas a estas funciones: Hilt las encadena solo, en el orden correcto. Ahora cualquier clase puede pedir un `ContactApi` en su constructor.

> [!NOTE]Nota
> `explicitNulls = false` hace que, al leer, una clave que falta se trate como `null` aunque la propiedad no tenga valor por defecto, y que, al escribir, no se envíen las propiedades `null`. Es una red de seguridad adicional; los valores por defecto del capítulo anterior siguen siendo la forma clara de decir qué claves son opcionales.

> [!TIP]Sugerencia
> Usa `@Binds` cuando la clase es **tuya** y solo hay que decir qué implementación usar para una interfaz. Usa `@Provides` cuando el objeto se **construye con código** (un *builder*, una función de fábrica) o viene de una biblioteca.

## Conectarse a la API de tu computador

Si ejecutas la app ahora, la petición fallará. Hay tres obstáculos, y los tres aparecen solo porque la API corre en tu propio computador.

### 1. `10.0.2.2`, no `localhost`

El emulador de Android es un **dispositivo aparte**, con su propia red. Dentro del emulador, `localhost` es el propio emulador, no tu computador. Para llegar a tu computador, el emulador ofrece una dirección especial: **`10.0.2.2`**. Por eso la URL base es `http://10.0.2.2:8080/`.

Si pruebas en un **teléfono físico**, `10.0.2.2` no existe: usa la dirección IP de tu computador en la red Wi-Fi (por ejemplo, `http://192.168.1.20:8080/`), con el teléfono conectado a la misma red.

### 2. El permiso de internet

Una app de Android no puede usar la red a menos que lo declare. En `AndroidManifest.xml`, antes de la etiqueta `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Sin este permiso, OkHttp falla apenas intenta conectarse, con un error que habla de un permiso denegado (`EPERM` o `Permission denied`).

### 3. HTTP sin cifrar (*cleartext*)

La API local usa `http://`, sin cifrar. Desde Android 9, las apps **bloquean por defecto** el tráfico HTTP sin cifrar y solo permiten `https://`. Es una buena protección en producción, pero impide hablar con tu API local; el error dice `CLEARTEXT communication to 10.0.2.2 not permitted`.

La solución correcta es permitir HTTP **solo hacia esa dirección**, con un archivo de configuración de seguridad de red. Crea `res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Permite HTTP sin cifrar solo hacia la API local del curso (el equipo anfitrión visto desde el emulador). -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

Y actívalo desde la etiqueta `<application>` del manifiesto:

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
```

> [!WARNING]Advertencia
> En internet verás la solución rápida: `android:usesCleartextTraffic="true"` en el manifiesto. Permite HTTP sin cifrar hacia **cualquier** servidor, y es fácil olvidarla al publicar la app. Con el archivo de configuración, la excepción queda limitada a `10.0.2.2`.

### Android 17: el permiso de red local

Desde **Android 17 (API 37)**, conectarse a una dirección de la **red local** —y `10.0.2.2` lo es— exige un permiso más: `ACCESS_LOCAL_NETWORK`. Se declara en el manifiesto, junto al de internet:

```xml
<uses-permission android:name="android.permission.ACCESS_LOCAL_NETWORK" />
```

A diferencia de `INTERNET`, es un **permiso de ejecución**: declararlo no basta, el usuario tiene que concederlo mientras la app está en uso, como el de la cámara o la ubicación. Si no está concedido, la conexión no llega a establecerse y OkHttp termina con una `SocketTimeoutException` después de esperar varios segundos, un error que no da ninguna pista de la causa real.

En el proyecto final (capítulo 49) escribirás un composable que pide este permiso al abrir la app. Mientras tanto, para probar los ejemplos de este capítulo en un emulador con API 37, concédelo a mano: mantén presionado el ícono de la app, elige **Información de la app** → **Permisos** y activa el de red local. En emuladores con una API anterior a la 37, este permiso no existe y no hace falta.

## Usar la API desde el repositorio

La interfaz de Retrofit encaja en la capa de datos que construiste en la Parte VIII. El repositorio la recibe por su constructor, gracias al `@Provides`, y traduce los DTOs a modelos de dominio con los mapeos del capítulo anterior:

```kotlin
class ContactosRepositoryImpl @Inject constructor(
    private val api: ContactApi
) : ContactosRepository {

    override suspend fun obtenerContactos(): List<Contacto> =
        api.obtenerContactos(pagina = 0, tamano = 20)
            .embedded?.contacts.orEmpty()
            .map { it.aDominio() }
}
```

La cadena es: pedir la primera página, sacar la lista de `embedded` (que puede ser `null` si no hay contactos, de ahí el `?.` y el `orEmpty()` del capítulo 13) y convertir cada `ContactDto` en un `Contacto`. El repositorio no ve una sola URL ni una cabecera, y hacia arriba solo entrega `Contacto`. Como el `ViewModel` depende de la interfaz `ContactosRepository`, nada más arriba se entera de que los datos vienen de Retrofit.

Con la API en ejecución y el permiso concedido, abre **Logcat** y filtra por `okhttp`. Por cada petición verás dos líneas parecidas a estas:

```text
--> GET http://10.0.2.2:8080/api/contact?page=0&size=20&sort=firstName%2Casc
<-- 200 http://10.0.2.2:8080/api/contact?page=0&size=20&sort=firstName%2Casc (38ms, unknown-length body)
```

La primera es la petición; la segunda, el código de la respuesta y cuánto tardó. Cuando algo falle, lo primero es mirar aquí: si no aparece la línea `<--`, la petición no llegó al servidor.

> [!TIP]Sugerencia
> Cambia el nivel del interceptor a `HttpLoggingInterceptor.Level.BODY` para ver también las cabeceras y el cuerpo completo de cada petición y respuesta. Es ideal para depurar, pero genera mucho texto: vuelve a `BASIC` cuando termines.

## Resumen

- **Retrofit** convierte una API REST en una **interfaz de Kotlin**: declaras los *endpoints* con anotaciones y Retrofit genera el código que hace las peticiones, sobre OkHttp.
- Cada función de la interfaz es un *endpoint*: `@GET`, `@POST`, `@PUT` o `@DELETE` con su ruta relativa. Se declaran `suspend`.
- **`@Path`** llena un hueco de la ruta; **`@Query`** agrega un parámetro de consulta (y se omite si vale `null`); **`@Body`** envía un objeto serializado como JSON. Una función sin tipo de retorno sirve para respuestas sin cuerpo, como el `204` de `DELETE`.
- Un código que no es `2xx` lanza una **`HttpException`**; la falta de conexión, una **`IOException`**.
- La instancia se crea con `Retrofit.Builder`: `baseUrl` (terminada en `/`), un cliente de OkHttp con interceptor de registro y el convertidor con **tu** instancia de `Json`.
- Con Hilt, los objetos que se construyen con código se proveen con funciones **`@Provides`** en un módulo `object`, marcadas con `@Singleton` para compartir una sola instancia.
- Para llegar a la API local desde el emulador: la dirección **`10.0.2.2`**, el permiso **`INTERNET`**, un **`network_security_config.xml`** que permita HTTP solo hacia esa dirección y, desde Android 17, el permiso de ejecución **`ACCESS_LOCAL_NETWORK`**.

En el próximo capítulo verás qué hacer cuando la petición **tarda** y cuando **falla**: los estados de red y los tipos de error.
