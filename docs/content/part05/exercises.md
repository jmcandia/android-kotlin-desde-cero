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
