# Capítulo 37: Retrofit: configuración, interfaces y endpoints

## Introducción

En el capítulo anterior entendiste cómo se comunican una app y un servidor con HTTP, REST y JSON. Hacer todo eso a mano —abrir la conexión, construir la petición, esperar la respuesta, interpretar el JSON— sería tedioso y propenso a errores.

Aquí entra **Retrofit**: una biblioteca que hace ese trabajo por ti. Tú describes la API como una **interfaz de Kotlin**, y Retrofit genera automáticamente el código para hacer las peticiones. En este capítulo aprenderás a configurar Retrofit, a declarar los *endpoints* en una interfaz y a hacer tu primera llamada.

> [!NOTE]
> Retrofit es una biblioteca externa; en la siguiente sección la agregamos al proyecto.

## ¿Qué es Retrofit?

**Retrofit** convierte una API REST en una **interfaz de Kotlin**. La idea es elegante: en lugar de escribir código de red, **declaras** qué endpoints existen y qué devuelven, usando una interfaz con **anotaciones**. Retrofit lee esa interfaz y crea, por detrás, la implementación que hace las peticiones reales.

Además, se integra a la perfección con las **coroutines** (puedes declarar los endpoints como funciones `suspend`) y convierte automáticamente el **JSON** de la respuesta en objetos de Kotlin.

## Instalar y configurar Retrofit

Retrofit es una biblioteca externa, así que primero hay que **agregarla al proyecto**. Al momento de escribir este curso, la última versión estable es la **3.0.0**. Necesitarás dos dependencias: **Retrofit** en sí, y un **convertidor** que transforme el JSON en objetos de Kotlin (usaremos el de `kotlinx.serialization`).

Siguiendo el catálogo de versiones que viste al crear el proyecto, primero declaras la versión y las bibliotecas en `libs.versions.toml`:

```toml
[versions]
retrofit = "3.0.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
```

Y luego las agregas al `build.gradle.kts` del módulo `app`:

```kotlin
dependencies {
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    // ...otras dependencias
}
```

Tras sincronizar Gradle, Retrofit queda listo para usar.

> [!NOTE]
> Retrofit requiere como mínimo **Java 8** o **Android API 21+**, algo que cualquier proyecto reciente ya cumple. Es un proyecto de código abierto de Square; su documentación oficial está en [square.github.io/retrofit](https://square.github.io/retrofit/).

> [!NOTE]
> El convertidor de `kotlinx.serialization` necesita, además, el *plugin* de serialización y que tus clases de datos estén marcadas como `@Serializable`. Eso lo completaremos en el próximo capítulo, al hablar de serialización y DTOs.

## Definir la interfaz de la API

El corazón de Retrofit es una interfaz donde **cada método representa un endpoint**. Se anota con el método HTTP y la ruta:

```kotlin
interface ApiService {
    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<Usuario>
}
```

Analicémoslo:

- `@GET("usuarios")` indica que este método hace una petición `GET` al endpoint `usuarios` (relativo a la URL base que configuraremos más abajo).
- `suspend fun` aprovecha las coroutines: la llamada se suspende mientras espera la respuesta, sin bloquear el hilo.
- El tipo de retorno, `List<Usuario>`, es lo que Retrofit te entregará ya convertido: toma el JSON de la respuesta y lo transforma en objetos. Aquí `Usuario` es una `data class` que describe la forma de los datos (algo que detallaremos en el próximo capítulo).

## Endpoints con parámetros

Muchos endpoints necesitan parámetros. Retrofit los maneja con más anotaciones.

Para un valor que va **dentro de la ruta** (como un id), usas `@Path`, y lo referencias entre llaves en la URL:

```kotlin
@GET("usuarios/{id}")
suspend fun obtenerUsuario(@Path("id") id: Int): Usuario
```

Al llamar `obtenerUsuario(42)`, Retrofit construye la petición a `usuarios/42`.

Para un parámetro de **consulta** (los que van después del `?` en la URL), usas `@Query`:

```kotlin
@GET("usuarios")
suspend fun buscarUsuarios(@Query("nombre") nombre: String): List<Usuario>
```

Al llamar `buscarUsuarios("Ana")`, Retrofit genera la petición a `usuarios?nombre=Ana`.

## Construir la instancia de Retrofit

Con la interfaz definida, falta crear la instancia de Retrofit y, a partir de ella, el objeto que implementa tu interfaz. Se usa `Retrofit.Builder`:

```kotlin
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.ejemplo.com/")
    .addConverterFactory(
        Json.asConverterFactory("application/json".toMediaType())
    )
    .build()

val apiService = retrofit.create(ApiService::class.java)
```

Las piezas clave:

- `baseUrl(...)` es la parte común de todas las URLs. Se combina con la ruta de cada endpoint (por ejemplo, `usuarios`) para formar la dirección completa. Debe terminar en `/`.
- `addConverterFactory(...)` le indica a Retrofit cómo **convertir** el JSON en objetos. Aquí usamos el convertidor de `kotlinx.serialization` (`Json.asConverterFactory(...)`) que instalamos más arriba. Los detalles de cómo tus `data class` se conectan con el JSON son el tema del próximo capítulo.
- `retrofit.create(ApiService::class.java)` genera la implementación de tu interfaz. El resultado, `apiService`, ya está listo para usarse.

## Usar la API desde el repositorio

¿Dónde encaja todo esto? En la **capa de datos** que armaste en la parte de arquitectura. El repositorio recibe el `ApiService` y lo usa para obtener los datos:

```kotlin
class DatosRepositoryImpl(
    private val apiService: ApiService
) : DatosRepository {

    override suspend fun obtenerDatos(): List<Usuario> {
        return apiService.obtenerUsuarios()
    }
}
```

Fíjate en lo limpio que queda: el repositorio solo invoca `apiService.obtenerUsuarios()`, una función `suspend`, sin ver ni un detalle de red. Y como el `ViewModel` depende de la interfaz del repositorio, nada más arriba se entera de que los datos vienen de Retrofit. Cada capa cumple su papel, tal como planeamos.

## Resumen

En este capítulo configuraste Retrofit para consumir una API:

- **Retrofit** convierte una API REST en una **interfaz de Kotlin**: declaras los endpoints y Retrofit genera el código que hace las peticiones.
- Cada método de la interfaz es un endpoint, anotado con su método HTTP (`@GET`, `@POST`, …) y su ruta. Al declararlo `suspend`, se integra con las coroutines.
- Los parámetros se pasan con `@Path` (dentro de la ruta) y `@Query` (después del `?`).
- Creas la instancia con `Retrofit.Builder`, indicando la `baseUrl` y un **convertidor** de JSON, y obtienes la implementación con `retrofit.create(...)`.
- El `ApiService` se usa desde el **repositorio**, encajando de forma natural en la arquitectura por capas.

En el próximo capítulo verás la pieza que quedó pendiente: la **serialización**, es decir, cómo se convierte el JSON en tus `data class` mediante los **DTOs**.
