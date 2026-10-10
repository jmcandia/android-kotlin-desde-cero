# Ejercicios: Parte V

Estos ejercicios te permitirán aplicar los conceptos vistos en esta parte del curso: hilos, coroutines, funciones `suspend`, `launch`, `async`, scopes, dispatchers y `Flow`. Intenta resolverlos por tu cuenta antes de consultar la solución propuesta — la práctica de recordar y aplicar es lo que fija los conceptos.

---

## Ejercicio 1: Descarga simulada, en serie y en paralelo

### Descripción

Escribe un programa que simule la descarga de tres archivos, donde cada descarga tarda un tiempo distinto (por ejemplo, 1000 ms, 1500 ms y 800 ms) usando `delay`. El programa debe:

1. Descargar los tres **en serie** (uno tras otro) y medir el tiempo total.
2. Descargar los tres **en paralelo** (concurrentemente) y medir el tiempo total.
3. Mostrar ambos tiempos y compararlos.

### Requisitos

- Usa `runBlocking` como scope para el `main` de consola.
- Modela cada descarga como una función `suspend` que recibe el nombre del archivo y el tiempo de espera.
- En la versión en paralelo, usa `async` + `await`.
- Mide el tiempo con `System.currentTimeMillis()` o `measureTimeMillis { }`.

### Ejemplo de ejecución

```plaintext
Descargando en serie...
  foto.png descargada (1000 ms)
  video.mp4 descargada (1500 ms)
  doc.pdf descargada (800 ms)
Tiempo en serie: 3302 ms

Descargando en paralelo...
Tiempo en paralelo: 1504 ms

¡En paralelo fue 1.8 veces más rápido!
```

??? tip "Pista"
    En serie, un `forEach` que llame a la función `suspend` una descarga tras otra. En paralelo, lanza cada descarga con `async { }` y guarda los `Deferred`; luego `await()` a todos para esperar al conjunto. Mide cada bloque con `measureTimeMillis { }`.

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    import kotlinx.coroutines.*
    import kotlin.system.measureTimeMillis

    suspend fun descargar(nombre: String, duracionMs: Long) {
        delay(duracionMs)
        println("  $nombre descargada ($duracionMs ms)")
    }

    fun main() = runBlocking {
        val archivos = listOf(
            "foto.png" to 1000L,
            "video.mp4" to 1500L,
            "doc.pdf" to 800L
        )

        println("Descargando en serie...")
        val tiempoSerie = measureTimeMillis {
            archivos.forEach { (nombre, duracion) -> descargar(nombre, duracion) }
        }
        println("Tiempo en serie: $tiempoSerie ms")

        println()
        println("Descargando en paralelo...")
        val tiempoParalelo = measureTimeMillis {
            val deferreds = archivos.map { (nombre, duracion) ->
                async { descargar(nombre, duracion) }
            }
            deferreds.forEach { it.await() }
        }
        println("Tiempo en paralelo: $tiempoParalelo ms")

        println()
        println("¡En paralelo fue ${tiempoSerie / tiempoParalelo} veces más rápido!")
    }
    ```

    > [!NOTE]Nota
    > En la versión en serie, los `delay` se **suman** (3300 ms aproximados), porque la coroutine espera cada uno sin adelantar trabajo. En la versión en paralelo, los tres `delay` ocurren **a la vez**, así que el tiempo total es el del más lento (1500 ms). Esa es exactamente la diferencia entre trabajo secuencial y concurrente.

---

## Ejercicio 2: Contador con Flow de segundo en segundo

### Descripción

Crea un `Flow<Int>` que emita los números del 1 al 5, con un segundo de espera entre cada emisión, y recolecta sus valores en un `main`. Después, modifica el programa para que el flujo emita números **infinitos** y demuestra cómo detener la recolección con `take(n)`.

### Requisitos

- Usa el constructor `flow { }` con `emit()` y `delay()`.
- Recolecta los valores con `collect { }` dentro de `runBlocking`.
- En la segunda versión, usa `take(5)` sobre el flujo infinito.
- Muestra cada número con el formato `"Recibido: N"`.

### Ejemplo de ejecución

```plaintext
Recibido: 1
Recibido: 2
Recibido: 3
Recibido: 4
Recibido: 5
Fin del flujo
```

??? tip "Pista"
    El constructor `flow { }` deja que `emit(i)` emita valores y `delay(1000)` los espacie. Para la versión infinita, usa un `while (true)` dentro del `flow { }` y encadena `take(5)` antes de `collect` para detener la recolección.

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    import kotlinx.coroutines.*
    import kotlinx.coroutines.flow.*

    fun contadorHastaCinco(): Flow<Int> = flow {
        for (i in 1..5) {
            emit(i)
            delay(1000)
        }
    }

    fun contadorInfinito(): Flow<Int> = flow {
        var i = 1
        while (true) {
            emit(i)
            i++
            delay(1000)
        }
    }

    fun main() = runBlocking {
        println("--- Flujo finito ---")
        contadorHastaCinco().collect { valor ->
            println("Recibido: $valor")
        }
        println("Fin del flujo")

        println()
        println("--- Flujo infinito con take(5) ---")
        contadorInfinito()
            .take(5)
            .collect { valor ->
                println("Recibido: $valor")
            }
        println("Fin del flujo")
    }
    ```

    > [!NOTE]Nota
    > El flujo infinito no es un problema: `take(5)` **cancela** la recolección después del quinto valor, y la coroutine del flujo se detiene con él. Esta es la base de los flujos de estado que verás en los próximos capítulos: emiten indefinidamente y es el observador quien decide cuándo deja de escuchar.

---

## Ejercicio 3: Elegir el dispatcher correcto

### Descripción

Escribe un programa que ejecute tres tareas distintas y muestre **en qué hilo** corre cada una usando `Thread.currentThread().name`:

1. Una tarea de interfaz simulada (debe correr en el hilo principal).
2. Una tarea de red simulada (con `delay`).
3. Una tarea de cálculo intensivo (un bucle con muchas iteraciones).

Cada tarea debe lanzarse con el **dispatcher** que le corresponde según su tipo.

### Requisitos

- Usa `Dispatchers.Main`, `Dispatchers.IO` y `Dispatchers.Default` (si no tienes entorno Android, `Main` puede sustituirse por el scope del `runBlocking`).
- Lanza cada tarea con `launch(dispatcher) { ... }`.
- Imprime el nombre del hilo dentro de cada tarea.

??? tip "Pista"
    Asocia cada tarea a su dispatcher: cálculo de CPU con `Dispatchers.Default`, red o disco con `Dispatchers.IO` y la interfaz con `Dispatchers.Main` (en consola puedes omitirlo y heredar el contexto). Lanza cada tarea con `launch(dispatcher) { ... }` e imprime `Thread.currentThread().name` dentro.

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    import kotlinx.coroutines.*

    fun main() = runBlocking {
        launch { // sin dispatcher: hereda el contexto del runBlocking
            println("Interfaz simulada en: ${Thread.currentThread().name}")
        }

        launch(Dispatchers.IO) {
            println("Red simulada en: ${Thread.currentThread().name}")
            delay(500)
            println("Respuesta de red en: ${Thread.currentThread().name}")
        }

        launch(Dispatchers.Default) {
            println("Cálculo en: ${Thread.currentThread().name}")
            var suma = 0L
            repeat(10_000_000) { suma += it }
            println("Resultado del cálculo en: ${Thread.currentThread().name}")
        }
    }
    ```

    > [!NOTE]Nota
    > `Dispatchers.IO` está pensado para operaciones que **esperan** (red, disco) y puede usar muchos hilos; `Dispatchers.Default` está pensado para trabajo **de CPU** y usa tantos hilos como núcleos. En una app Android real, `Dispatchers.Main` es el hilo de la interfaz: nunca lances ahí trabajo pesado.

---

## Repaso acumulativo (Partes I–V): el buscador de ciudades

Este reto no es de una sola parte: reúne **colecciones** y `String` (Parte III), una **jerarquía sellada** y `data class` (Parte IV) y **corrutinas con `Flow`** (Parte V). Intenta resolverlo sin mirar la solución.

### Enunciado

Escribe un programa de consola que simule el buscador de ciudades de una app de viajes. Al buscar debe mostrar un estado de **carga** mientras "consulta al servidor" y, al terminar, distinguir tres desenlaces: **resultados** (que pueden estar vacíos) y **fallo** (por ejemplo, si la consulta viene vacía).

### Requisitos

- Modela cada ciudad con una `data class Ciudad(nombre, pais)`.
- Modela el estado con una `sealed interface EstadoBusqueda` y tres casos: `Cargando`, `Resultados(ciudades)` y `Fallo(mensaje)`.
- Implementa `buscar(consulta: String): Flow<EstadoBusqueda>` con el constructor `flow { }`: emite `Cargando`, espera con `delay` y luego emite el desenlace.
- Filtra un catálogo en memoria con `filter` y `contains(..., ignoreCase = true)`.
- Consume el flujo con `collect` dentro de `runBlocking` y trata los tres estados con un `when` **exhaustivo** (sin `else`).
- Prueba al menos una consulta que coincida, una vacía y una sin resultados.

### Ejemplo de ejecución

```plaintext
>> Buscar: "mad"
   Buscando…
   Madrid, España
   Madrid, Estados Unidos

>> Buscar: ""
   Buscando…
   Error: La búsqueda no puede estar vacía

>> Buscar: "zzz"
   Buscando…
   Sin resultados
```

### Pistas

??? tip "Pista 1 — Qué concepto usar en cada parte"
    - **Parte III**: `filter` para quedarte con las coincidencias, `isEmpty()` para el caso "sin resultados" y funciones de `String` como `isBlank()` y `contains(..., ignoreCase = true)`.
    - **Parte IV**: un `sealed interface` con tres implementaciones te obliga a cubrir los tres casos en el `when`; la `data class` te da `toString` y comparación sin escribirlos.
    - **Parte V**: un `Flow` puede emitir **varios** valores a lo largo del tiempo, así que emites `Cargando` primero y el desenlace después. El constructor `flow { }`, `emit` y `delay` viven en `kotlinx.coroutines.flow`.

??? tip "Pista 2 — Estructura del programa (fragmento parcial)"
    ```kotlin
    sealed interface EstadoBusqueda {
        // Cargando
        // Resultados(ciudades)
        // Fallo(mensaje)
    }

    fun buscar(consulta: String): Flow<EstadoBusqueda> = flow {
        // 1) emite el estado de carga
        // 2) simula la espera de red con delay
        // 3) si la consulta está en blanco, emite Fallo y termina el flujo
        // 4) si no, filtra el catálogo y emite Resultados
    }

    fun main() = runBlocking {
        // para cada consulta de prueba: collect y un when exhaustivo
    }
    ```

??? success "Ver solución comentada"
    ```kotlin
    import kotlinx.coroutines.*
    import kotlinx.coroutines.flow.*

    data class Ciudad(val nombre: String, val pais: String)

    sealed interface EstadoBusqueda {
        data object Cargando : EstadoBusqueda
        data class Resultados(val ciudades: List<Ciudad>) : EstadoBusqueda
        data class Fallo(val mensaje: String) : EstadoBusqueda
    }

    val catalogo = listOf(
        Ciudad("Madrid", "España"),
        Ciudad("Medellín", "Colombia"),
        Ciudad("Morelia", "México"),
        Ciudad("Madrid", "Estados Unidos")
    )

    fun buscar(consulta: String): Flow<EstadoBusqueda> = flow {
        emit(EstadoBusqueda.Cargando)
        delay(1200)
        if (consulta.isBlank()) {
            emit(EstadoBusqueda.Fallo("La búsqueda no puede estar vacía"))
            return@flow
        }
        val encontradas = catalogo.filter {
            it.nombre.contains(consulta, ignoreCase = true)
        }
        emit(EstadoBusqueda.Resultados(encontradas))
    }

    fun main() = runBlocking {
        for (consulta in listOf("mad", "", "zzz")) {
            println(">> Buscar: \"$consulta\"")
            buscar(consulta).collect { estado ->
                when (estado) {
                    EstadoBusqueda.Cargando -> println("   Buscando…")
                    is EstadoBusqueda.Resultados ->
                        if (estado.ciudades.isEmpty()) {
                            println("   Sin resultados")
                        } else {
                            estado.ciudades.forEach {
                                println("   ${it.nombre}, ${it.pais}")
                            }
                        }
                    is EstadoBusqueda.Fallo ->
                        println("   Error: ${estado.mensaje}")
                }
            }
            println()
        }
    }
    ```

    **Por qué funciona.** El `Flow` expresa el paso del tiempo: emites `Cargando`, esperas con `delay` y después emites el desenlace. Como `EstadoBusqueda` es sellado, el `when` exhaustivo obliga a tratar los tres casos y el compilador avisa si olvidas uno (Parte IV). El filtrado reutiliza colecciones y funciones de `String` (Parte III) y `runBlocking` (Parte V) permite consumir el flujo desde un `main` de consola.

    **Errores comunes.**
    - Devolver un valor único en lugar de un `Flow`: pierdes el estado `Cargando`, justo lo que una pantalla necesita para mostrar el indicador de progreso.
    - Olvidar el `return@flow` tras emitir `Fallo`: el flujo continuaría y emitiría también resultados.
    - Comparar con `==` en vez de `contains(..., ignoreCase = true)`: una búsqueda parcial ("mad") no encontraría "Madrid".
    - Usar `else` en el `when`: funciona, pero anula la ventaja del tipo sellado; deja que el compilador te exija cubrir cada caso.

    **Para ir más allá.** Sustituye el `delay(1200)` por una lista que simule errores de red (por ejemplo, emite `Fallo` si la consulta es "error") y añade un caso `Vacio` separado de `Resultados`. Pregúntate cuándo conviene un caso propio para "sin resultados" y cuándo basta con una lista vacía.

---

## Hito: comprueba tu avance (Parte V)

Marca cada afirmación que puedas hacer **sin mirar los apuntes**. Si alguna te resulta difícil, repasa el capítulo indicado antes de continuar.

- [ ] Explico por qué una operación lenta en el hilo principal congela la interfaz. *(cap. 23)*
- [ ] Distingo cuándo usar `launch` y cuándo `async`/`await`. *(cap. 24)*
- [ ] Elijo el dispatcher adecuado para red, cálculo y trabajo de interfaz. *(cap. 24)*
- [ ] Describo la diferencia entre un `Flow` frío y un `StateFlow`. *(cap. 25)*
- [ ] Combino corrutinas con colecciones, tipos sellados y `when` exhaustivo para modelar el estado de una pantalla. *(Partes III–V)*

Si puedes con todo, ya tienes las bases para llevar este mismo modelo de estado a un `ViewModel` en la Parte VI.
