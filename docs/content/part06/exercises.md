# Ejercicios: Parte VI

Estos ejercicios te permitirán aplicar los conceptos vistos en esta parte del curso: la anatomía de un proyecto Android, el ciclo de vida de una `Activity` y la separación de responsabilidades entre capas. A diferencia de las partes anteriores, aquí el foco es **conceptual**: antes de escribir más código, conviene que puedas razonar *por qué* las apps se organizan como se organizan. Intenta responder por tu cuenta antes de consultar la solución propuesta.

---

## Ejercicio 1: El ciclo de vida, paso a paso

### Descripción

Para cada una de las siguientes situaciones, escribe la **secuencia de callbacks del ciclo de vida** que Android invoca sobre la `Activity` (por ejemplo: `onCreate → onStart → onResume`) y responde las preguntas adicionales:

1. El usuario abre la app por primera vez.
2. El usuario pulsa el botón de inicio (*home*) y la app queda en segundo plano.
3. El usuario vuelve a la app desde la lista de recientes.
4. El usuario gira el teléfono mientras la app está en primer plano.
5. El usuario pulsa "atrás" hasta salir de la app.

Además, responde:

- ¿En qué callback guardarías datos que deben sobrevivir al giro de la pantalla, y por qué `onCreate` **no** es el lugar adecuado para el estado de la interfaz tras una rotación?
- ¿Por qué una operación costosa en `onResume` puede hacer que la app "se sienta pesada" al volver del segundo plano?

### Solución propuesta

??? example "Ver solución"
    1. **Apertura inicial**: `onCreate → onStart → onResume`.
    2. **Botón home**: `onPause → onStop`. La `Activity` sigue viva en memoria, pero deja de ser visible; **no** se llama a `onDestroy`.
    3. **Volver desde recientes**: `onRestart → onStart → onResume`. Como la instancia seguía viva, no se repite `onCreate`.
    4. **Girar el teléfono**: `onPause → onStop → onDestroy → onCreate → onStart → onResume`. Android **destruye y recrea** la `Activity`, con una instancia nueva: cualquier estado guardado en propiedades de la actividad se pierde si no lo conservas (por ejemplo, con un `ViewModel`, que sobrevive a esta destrucción).
    5. **Botón atrás (salir)**: `onPause → onStop → onDestroy`.

    **Preguntas adicionales:**

    - Los datos que deben sobrevivir al giro se guardan en `onStop` (o en `onPause`, según el recurso). `onCreate` no es el lugar para *recuperar* el estado de la interfaz tras una rotación porque, al recrearse la actividad, se llama de nuevo con una instancia nueva: lo que guardaste en propiedades de la actividad anterior ya no existe. La recuperación debe venir de un objeto que sobreviva a la recreación (`ViewModel` o estado guardado, *saved state*), no de la propia actividad.
    - `onResume` se ejecuta **cada vez** que la pantalla vuelve al primer plano: cada vez que desbloqueas el teléfono o cambias de app y vuelves. Si ahí pones una operación costosa (una descarga, un cálculo, una pausa visible), el usuario percibe un congelamiento justo cuando quiere interactuar. `onResume` debe ser ligero: solo lo necesario para reactivar la interacción.

    > [!NOTE]Nota
    > La regla práctica que salió de este ejercicio: los métodos del ciclo de vida se llaman muchas veces y en un orden que **tú no controlas**. Nunca asumas que la actividad vivirá para siempre ni que se destruirá solo cuando el usuario lo pida: escribe el código de modo que una recreación en cualquier momento no pierda nada importante.

---

## Ejercicio 2: Repartir los cinco trabajos

### Descripción

El capítulo 27 identifica cinco trabajos en cualquier app: **mostrar**, **reaccionar**, **decidir**, **obtener** y **guardar**. Para cada fragmento de código siguiente, indica a cuál de esos trabajos pertenece y en qué **capa** debería vivir (interfaz, lógica de presentación o datos):

```kotlin
// Fragmento A
if (nombreTarea.isBlank()) {
    mostrarError("La tarea no puede estar vacía")
    return
}

// Fragmento B
Column {
    Text(text = "Tareas pendientes: $pendientes")
    LazyColumn { /* ... */ }
}

// Fragmento C
suspend fun obtenerTareas(): List<Tarea> = tareaDao.todas().first()

// Fragmento D
Button(onClick = { viewModel.agregarTarea(texto) }) {
    Text("Agregar")
}

// Fragmento E
@Insert
suspend fun guardar(tarea: Tarea)
```

Después responde: ¿cuáles de estos fragmentos podrías reutilizar sin cambios si la app migrara de teléfono a tableta? ¿Por qué?

### Solución propuesta

??? example "Ver solución"
    | Fragmento | Trabajo | Capa |
    |---|---|---|
    | A | **Decidir** | Lógica de presentación (la regla "una tarea vacía no se agrega" es una regla de negocio, no de interfaz) |
    | B | **Mostrar** | Interfaz |
    | C | **Obtener** | Datos (repositorio) |
    | D | **Reaccionar** | Interfaz (detecta el toque; la decisión misma vive en el `ViewModel`) |
    | E | **Guardar** | Datos (capa de persistencia, p. ej. Room) |

    Los fragmentos **A, C y E** sobreviven sin cambios a una migración a tableta: las reglas de negocio y el acceso a datos no dependen del tamaño de la pantalla. Los fragmentos **B y D**, en cambio, probablemente cambien: una tableta puede mostrar la lista y el detalle a la vez, con un diseño distinto (lo verás con las *Window Size Classes*).

    > [!NOTE]Nota
    > Fíjate en el fragmento D: la interfaz *reacciona*, pero no *decide*. El botón detecta el toque y delega en `viewModel.agregarTarea(texto)`; la validación (fragmento A) ocurre en la capa de lógica. Si la validación viviera en el `onClick`, tendrías que duplicarla en cada botón, en cada pantalla y en cada diseño adaptado que agregues.

---

## Ejercicio 3: Diagnosticar una app monolítica

### Descripción

Una `Activity` contiene todo esto dentro de `onCreate`:

```kotlin
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Lee el archivo de configuración del disco
        val config = File(filesDir, "config.json").readText()

        // 2. Guarda las tareas en una propiedad mutable de la actividad
        var tareas = mutableListOf<Tarea>()

        // 3. Hace una petición de red sincrónica
        val resp = URL("https://api.ejemplo.com/tareas").readText()
        tareas = parsear(resp)

        // 4. Dibuja la interfaz mezclada con lógica
        setContentView(...Texto con "Tienes ${tareas.size} tareas"...)

        // 5. Valida la entrada del usuario en el mismo lugar
        if (tareaNueva.isBlank()) { /* muestra error */ }
    }
}
```

Enumera **todos los problemas** que este código tiene respecto a: (a) el hilo principal, (b) el ciclo de vida y el giro de pantalla, (c) la separación de responsabilidades y (d) la testabilidad. Para cada problema, propón la corrección (basta con nombrar la herramienta o capa adecuada, sin escribir el código completo).

### Solución propuesta

??? example "Ver solución"
    **(a) Hilo principal**

    - La lectura del archivo (1) y la petición de red (3) son operaciones bloqueantes en el hilo principal: la app se congela durante la descarga y, si tarda demasiado, Android la cierra (*ANR*).
    - **Corrección**: mover ambas a funciones `suspend` y ejecutarlas en `Dispatchers.IO` con coroutines, observando el resultado desde la interfaz.

    **(b) Ciclo de vida y giro de pantalla**

    - `tareas` (2) vive en la `Activity`: al girar el teléfono la actividad se destruye y **se pierde la lista**; el usuario ve la pantalla vaciarse (y probablemente se re-descargan los datos).
    - **Corrección**: mover el estado de la pantalla a un `ViewModel`, que sobrevive a los cambios de configuración, y observar sus cambios (por ejemplo, con un `StateFlow`).

    **(c) Separación de responsabilidades**

    - Los cinco trabajos del capítulo 27 (mostrar, reaccionar, decidir, obtener, guardar) están todos en `onCreate`: la validación (5), la red (3), el disco (1) y el dibujado (4) comparten un mismo método. Cualquier cambio de diseño obliga a tocar código de datos, y viceversa.
    - **Corrección**: dividir en capas: la interfaz solo dibuja y reporta eventos; el `ViewModel` valida y decide; un repositorio obtiene y guarda (con Room para el disco y Retrofit para la red, como verás en la Parte IX).

    **(d) Testabilidad**

    - Para probar la regla de validación (5) tendrías que instanciar una `Activity` de Android, con su ciclo de vida y su framework completo. Con la red embebida (3) no puedes probar el dibujado sin un servidor de prueba.
    - **Corrección**: con la lógica en el `ViewModel` y los datos tras un repositorio (que recibe sus fuentes por constructor), puedes probar las reglas con pruebas unitarias puras de Kotlin, sin emulador, inyectando fuentes falsas (*fakes*).

    > [!NOTE]Nota
    > Este es el diagnóstico completo en una sola frase: la actividad hace **los cinco trabajos a la vez, en el hilo equivocado, guardando el estado en el objeto equivocado**. Las tres partes siguientes del curso (Compose, MVVM y la capa de datos) existen precisamente para resolver cada uno de esos puntos, uno por uno.
