# Ejercicios: Parte IX

Estos ejercicios te permitirán aplicar lo aprendido en la Parte IX: HTTP/REST, serialización JSON, Retrofit, estados de red y persistencia local con Room. El repaso acumulativo combina todo lo anterior.

---

## Repaso acumulativo (Partes I–IX): el clima *offline-first*

Este reto reúne `Result` (Parte III), tipos sellados y `data class` (Parte IV), corrutinas y `Flow` (Parte V), inyección de dependencias (Parte VIII) y red + Room (Parte IX). Intenta diseñarlo antes de mirar la solución.

### Enunciado

Diseña el repositorio de una app del clima con estrategia ***offline-first***: al pedir el clima de una ciudad, intenta primero la API remota; si la petición **no llega al servidor** (por ejemplo, porque no hay conexión), devuelve la última copia guardada en la base de datos local; cuando la red responde, actualiza la caché. El repositorio también debe exponer un `Flow` con el clima guardado, ya convertido al modelo de dominio `Clima`, para que la interfaz se actualice sola.

### Requisitos

- Modela la respuesta de la API con un DTO `@Serializable` y renombra una propiedad con `@SerialName`.
- Declara la interfaz Retrofit `ClimaApi` con un `@GET` que reciba la ciudad por `@Query` y sea `suspend`.
- Declara la entidad Room `ClimaEntity` (con `@PrimaryKey`), el modelo de dominio `Clima` y las conversiones `ClimaDto.aDominio()` (capítulo 49), `ClimaDto.aEntity()` y `ClimaEntity.aDominio()` (capítulo 50).
- Declara un `ClimaDao` con:
  - una consulta `suspend fun buscar(ciudad): ClimaEntity?` para el respaldo,
  - una consulta `fun observar(ciudad): Flow<ClimaEntity?>` para observar cambios,
  - un `@Insert(onConflict = OnConflictStrategy.REPLACE)` para guardar.
- Implementa `ClimaRepository` (con inyección por constructor, como en el capítulo 45) que exponga:
  - `fun observar(ciudad: String): Flow<Clima?>` que **mapee** la entidad al modelo de dominio,
  - `suspend fun obtener(ciudad: String): Result<Clima>` que siga la estrategia descrita.
- Centraliza el manejo de excepciones en un ayudante `llamarApi` genérico que **vuelva a lanzar `CancellationException`** y traduzca el resto con el ayudante `aErrorDatos` (capítulo 49).
- No acoples el repositorio a Android: que sea una clase de Kotlin normal **que pueda probarse de forma aislada**.

### Pistas

??? tip "Pista 1 — El flujo de decisión del repositorio"
    La estrategia *offline-first* es un árbol de decisiones: intenta la red; si tiene éxito, **guarda** y devuelve; **si la petición no llegó al servidor**, lee la caché; y si tampoco hay caché, devuelve el fallo original.

    Dos detalles que marcan la diferencia:
    - El respaldo a la caché se dispara solo con el error de "no llegó al servidor" (`ErrorDatos.SinConexion`, que en el capítulo 49 se produce a partir de `IOException`). Un error del servidor (por ejemplo, un `500`) llegó hasta él: mostrar la caché ahí ocultaría que el servidor está fallando.
    - `buscar` puede devolver `null`: eso es un caso válido que debes tratar, no una excepción.

??? tip "Pista 2 — Firmas de las piezas (sin implementar)"
    ```kotlin
    @Serializable
    data class ClimaDto(@SerialName("temp_c") val temperatura: Double, val ciudad: String)

    interface ClimaApi {
        @GET("clima") suspend fun obtener(@Query("ciudad") ciudad: String): ClimaDto
    }

    data class Clima(val ciudad: String, val temperatura: Double)

    fun ClimaDto.aDominio(): Clima = /* ... */
    fun ClimaDto.aEntity(): ClimaEntity = /* ... */
    fun ClimaEntity.aDominio(): Clima = /* ... */

    @Dao
    interface ClimaDao {
        @Query("SELECT * FROM clima WHERE ciudad = :ciudad")
        fun observar(ciudad: String): Flow<ClimaEntity?>

        @Query("SELECT * FROM clima WHERE ciudad = :ciudad")
        suspend fun buscar(ciudad: String): ClimaEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun guardar(entidad: ClimaEntity)
    }

    class ClimaRepository(private val api: ClimaApi, private val dao: ClimaDao, private val json: Json) {
        fun observar(ciudad: String): Flow<Clima?> = /* mapea la entidad a dominio */

        suspend fun obtener(ciudad: String): Result<Clima> = /* estrategia offline-first */

        private suspend fun <T> llamarApi(bloque: suspend () -> T): Result<T> = /* ... */
    }
    ```

??? success "Ver solución comentada"
    ```kotlin
    import javax.inject.Inject
    import javax.inject.Singleton
    import kotlin.coroutines.cancellation.CancellationException
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.map
    import kotlinx.serialization.SerialName
    import kotlinx.serialization.Serializable
    import kotlinx.serialization.json.Json
    import androidx.room.*

    @Serializable
    data class ClimaDto(
        @SerialName("temp_c") val temperatura: Double,
        val ciudad: String
    )

    interface ClimaApi {
        @GET("clima")
        suspend fun obtener(@Query("ciudad") ciudad: String): ClimaDto
    }

    @Entity(tableName = "clima")
    data class ClimaEntity(
        @PrimaryKey val ciudad: String,
        val temperatura: Double
    )

    data class Clima(val ciudad: String, val temperatura: Double)

    fun ClimaDto.aDominio(): Clima = Clima(ciudad = ciudad, temperatura = temperatura)
    fun ClimaDto.aEntity(): ClimaEntity = ClimaEntity(ciudad = ciudad, temperatura = temperatura)
    fun ClimaEntity.aDominio(): Clima = Clima(ciudad = ciudad, temperatura = temperatura)

    @Dao
    interface ClimaDao {
        @Query("SELECT * FROM clima WHERE ciudad = :ciudad")
        fun observar(ciudad: String): Flow<ClimaEntity?>

        @Query("SELECT * FROM clima WHERE ciudad = :ciudad")
        suspend fun buscar(ciudad: String): ClimaEntity?

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun guardar(entidad: ClimaEntity)
    }

    @Singleton
    class ClimaRepository @Inject constructor(
        private val api: ClimaApi,
        private val dao: ClimaDao,
        private val json: Json
    ) {
        fun observar(ciudad: String): Flow<Clima?> =
            dao.observar(ciudad).map { entidad -> entidad?.aDominio() }

        suspend fun obtener(ciudad: String): Result<Clima> {
            val resultado = llamarApi { api.obtener(ciudad) }
                .onSuccess { dto -> dao.guardar(dto.aEntity()) }
                .map { dto -> dto.aDominio() }

            if (resultado.exceptionOrNull() is ErrorDatos.SinConexion) {
                val cache = dao.buscar(ciudad)
                if (cache != null) {
                    return Result.success(cache.aDominio())
                }
            }
            return resultado
        }

        private suspend fun <T> llamarApi(bloque: suspend () -> T): Result<T> =
            try {
                Result.success(bloque())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e.aErrorDatos(json))
            }
    }
    ```

    **Por qué funciona.** `llamarApi` envuelve la llamada de red y concentra el manejo de errores (capítulo 49): primero **vuelve a lanzar** la `CancellationException` —que no es un fallo, sino la señal de que la coroutine debe detenerse— y solo después traduce cualquier otra excepción con `aErrorDatos(json)`, el mismo ayudante del capítulo 49. Así, un `IOException` (la petición **no llegó** al servidor) se convierte en `ErrorDatos.SinConexion`, mientras que un error HTTP (`HttpException`) se traduce a su caso correspondiente y **nunca** cae en `SinConexion`. Gracias a que `onSuccess` y `map` son funciones *inline*, dentro de `obtener` (que es `suspend`) se puede llamar a `dao.guardar`. La red va primero; si falla por falta de conexión, el resultado es un `Result.failure` con `ErrorDatos.SinConexion`, y solo entonces se consulta la caché. Devolver `null` es un caso legítimo, así que se comprueba con `if (cache != null)`. Guardar con `REPLACE` deja siempre la última versión en Room, y `observar` **mapea la entidad a `Clima`** —igual que en el capítulo 50— para que la interfaz no conozca el detalle de Room. El repositorio recibe sus dependencias por constructor (`@Inject`, `@Singleton`), así que Hilt lo construye y se puede probar con una API y un DAO falsos.

    **Errores comunes.**
    - Atrapar `CancellationException` dentro del `catch (e: Exception)`: la coroutine cancelada seguiría ejecutándose en lugar de detenerse. Se relanza **antes** de cualquier otro `catch`, y nunca se usa `runCatching` alrededor de una función `suspend` (capítulo 49).
    - Activar el respaldo ante **cualquier** fallo: solo debe dispararse con `ErrorDatos.SinConexion`. Un `500` significa que el servidor respondió; mostrar la caché como si todo fuera bien esconde el problema.
    - Volver a traducir las excepciones a mano (`catch (e: IOException)`, `catch (e: Exception)`) en vez de reutilizar `aErrorDatos(json)`: es fácil clasificar por error un fallo del servidor como `SinConexion` y disparar el respaldo de caché cuando no corresponde (capítulo 49).
    - Hacer que `observar` devuelva `ClimaEntity` en vez de `Clima`: filtra el detalle de Room hacia la interfaz y rompe la separación entre capas (capítulo 50).
    - Olvidar `onConflict = OnConflictStrategy.REPLACE`: guardar la misma ciudad lanzaría un conflicto de clave primaria.
    - Intercambiar los papeles del DAO: la consulta que devuelve `Flow` **no** es `suspend`, y la operación puntual de respaldo **sí** lo es (capítulo 50).

    **Para ir más allá.** Añade un parámetro `forzarRecarga: Boolean = false`: si es `true`, salta la caché e intenta siempre la red (útil para un botón "actualizar"). Pregúntate qué devolvería el repositorio si la red falla *y* la caché está vacía, y cómo podría la interfaz distinguir unos datos recién descargados de unos recuperados de la caché (en la app final, el capítulo 55 resuelve esto con un indicador `desdeCache`).

---

## Hito: comprueba tu avance (Parte IX)

Marca cada afirmación que puedas hacer **sin mirar los apuntes**. Si alguna te resulta difícil, repasa el capítulo indicado.

- [ ] Distingo el método HTTP y el código de estado adecuados para una operación REST. *(cap. 46)*
- [ ] Modelo un DTO con `@Serializable`/`@SerialName` y trato campos opcionales. *(cap. 47)*
- [ ] Declaro una interfaz Retrofit con `@GET`/`@POST`, `@Path`, `@Query` y `@Body`. *(cap. 48)*
- [ ] Modelo los estados de red (cargando, éxito, error) de forma exhaustiva. *(cap. 49)*
- [ ] Explico por qué nunca debo atrapar una `CancellationException`. *(cap. 49)*
- [ ] Persisto y observo datos con Room usando `Flow`. *(cap. 50)*
- [ ] Combino red y caché en un repositorio con `Result` e inyección de dependencias. *(Partes VIII–IX)*

Si puedes con todo, tienes las piezas para construir la app final «Mis Contactos» en la Parte X.
