# Anexo E: Glosario de términos

## Introducción

A lo largo del curso aparecen muchos términos técnicos de Kotlin, Android y arquitectura de aplicaciones. Este anexo los reúne en un solo lugar, ordenados alfabéticamente, para que puedas **consultarlos rápidamente** cuando te encuentres con uno que no recuerdas.

Cada entrada incluye una definición breve y un enlace al capítulo donde el término se introduce por primera vez y se explica en detalle.

> [!TIP]¿Cuándo usar este anexo?
> Úsalo como un punto de referencia rápido, no como un reemplazo del capítulo correspondiente. Si un término te resulta nuevo o quieres entenderlo a fondo, sigue el enlace al capítulo: ahí encontrarás el contexto, los ejemplos y las advertencias que aquí se resumen en una o dos líneas.

## Términos

**`@Composable`** — Anotación que marca una función como un *composable*, es decir, una pieza de interfaz de Jetpack Compose que describe qué mostrar en función de su estado. Ver el [capítulo 29](../part07/chapter29.md).

**`@Preview`** — Anotación que permite visualizar un composable en Android Studio sin ejecutar la app en un dispositivo. Ver el [capítulo 29](../part07/chapter29.md).

**`Activity`** — Componente de Android que representa una pantalla con la que el usuario interactúa. Tiene un ciclo de vida propio (creación, pausa, reanudación, destrucción) que debes respetar. Ver el [capítulo 27](../part06/chapter27.md).

**`async` / `await`** — Constructor de corrutinas que lanza una tarea concurrente y devuelve un `Deferred`, cuyo resultado se obtiene con `await`. Se usa cuando necesitas un valor de vuelta desde la corrutina. Ver el [capítulo 24](../part05/chapter24.md).

**Corrutina (*coroutine*)** — Mecanismo de Kotlin para escribir código asíncrono de forma secuencial y legible, sin bloquear el hilo principal. Es la base de toda la asincronía en Android moderno. Ver el [capítulo 23](../part05/chapter23.md).

**`data class`** — Clase pensada para contener datos; el compilador genera automáticamente `equals`, `hashCode`, `toString`, `copy` y la desestructuración. Ver el [capítulo 18](../part04/chapter18.md).

**DAO (*Data Access Object*)** — Interfaz de Room que declara las operaciones de acceso a la base de datos (consultas, inserciones, borrados) mediante anotaciones. Ver el [capítulo 50](../part09/chapter50.md).

**Delegación (`by`)** — Mecanismo de Kotlin para delegar la implementación de una propiedad o interfaz a otro objeto, evitando código repetitivo. Ver el [capítulo 22](../part04/chapter22.md).

**Dispatcher** — Componente que decide en qué hilo o grupo de hilos se ejecuta una corrutina (por ejemplo, `Dispatchers.IO` para operaciones de red o disco, `Dispatchers.Main` para la interfaz). Ver el [capítulo 24](../part05/chapter24.md).

**DTO (*Data Transfer Object*)** — Objeto que modela la forma exacta de los datos que se intercambian con una API, separado de los modelos internos de la app. Ver el [capítulo 47](../part09/chapter47.md).

**Endpoint** — Dirección concreta de una API REST (una URL y un método HTTP) que expone una operación, como obtener o crear un recurso. Ver el [capítulo 48](../part09/chapter48.md).

**`enum class`** — Tipo que representa un conjunto fijo y conocido de valores posibles (por ejemplo, los días de la semana). Ver el [capítulo 20](../part04/chapter20.md).

**Estado y recomposición** — En Compose, el *estado* son los datos que la interfaz observa; cuando el estado cambia, Compose vuelve a ejecutar (recompone) las funciones afectadas para reflejar el nuevo valor. Ver el [capítulo 33](../part07/chapter33.md).

**`Flow`** — Flujo asíncrono de Kotlin que emite una secuencia de valores a lo largo del tiempo, de forma similar a una lista que se produce poco a poco. Ver el [capítulo 25](../part05/chapter25.md).

**Función de alcance (*scope function*)** — Funciones como `let`, `run`, `with`, `apply` y `also` que ejecutan un bloque en el contexto de un objeto, útiles para encadenar operaciones de forma concisa. Ver el [capítulo 22](../part04/chapter22.md).

**Función de extensión** — Función que agrega comportamiento a un tipo existente sin modificar su código fuente ni heredar de él. Ver el [capítulo 21](../part04/chapter21.md).

**Genéricos** — Mecanismo para escribir clases y funciones que trabajan con distintos tipos de forma segura, parametrizando el tipo (por ejemplo, `List<T>`). Ver el [capítulo 21](../part04/chapter21.md).

**HTTP / REST** — HTTP es el protocolo con el que las apps se comunican con un servidor; REST es un estilo de diseño de APIs sobre HTTP basado en recursos y métodos (`GET`, `POST`, etc.). Ver el [capítulo 46](../part09/chapter46.md).

**Hilt** — Biblioteca de inyección de dependencias para Android, construida sobre Dagger, que reduce el código necesario para proveer y conectar dependencias. Ver el [capítulo 45](../part08/chapter45.md).

**Inyección de dependencias** — Patrón en el que un objeto recibe las dependencias que necesita desde fuera, en lugar de crearlas internamente, lo que facilita las pruebas y el desacoplamiento. Ver el [capítulo 45](../part08/chapter45.md).

**JSON** — Formato de texto ligero para representar datos estructurados; es el formato habitual de intercambio entre una app y una API REST. Ver el [capítulo 46](../part09/chapter46.md).

**`LaunchedEffect`** — Efecto de Compose que ejecuta código suspendido (por ejemplo, cargar datos o mostrar un mensaje puntual) ligado al ciclo de vida de un composable. Ver el [capítulo 43](../part08/chapter43.md).

**`launch`** — Constructor de corrutinas que inicia una tarea concurrente sin devolver un resultado; se usa para trabajo de tipo "dispara y olvida". Ver el [capítulo 24](../part05/chapter24.md).

**`LazyColumn`** — Lista vertical de Compose que solo compone los elementos visibles, ideal para colecciones largas. Ver el [capítulo 34](../part07/chapter34.md).

**Material 3** — Sistema de diseño de Google que ofrece componentes de interfaz listos para usar y una guía de color, tipografía y forma coherente. Ver el [capítulo 32](../part07/chapter32.md) (componentes) y el [capítulo 39](../part07/chapter39.md) (tema).

**`Modifier`** — Objeto de Compose que ajusta la apariencia y el comportamiento de un composable (tamaño, relleno, fondo, clics, etc.) mediante llamadas encadenadas. Ver el [capítulo 30](../part07/chapter30.md).

**MVVM (*Model-View-ViewModel*)** — Patrón de arquitectura que separa la interfaz (*View*) de la lógica de presentación (*ViewModel*) y de los datos (*Model*). Ver el [capítulo 41](../part08/chapter41.md).

**Navigation Compose (`NavController` / `NavHost`)** — Biblioteca para navegar entre pantallas en Compose; el `NavController` gestiona la pila de navegación y el `NavHost` define las rutas disponibles. Ver el [capítulo 36](../part07/chapter36.md).

**Null safety** — Sistema de tipos de Kotlin que distingue entre valores que pueden ser nulos (`T?`) y los que no (`T`), evitando en tiempo de compilación muchos errores por referencias nulas. Ver el [capítulo 13](../part03/chapter13.md).

**Offline-first** — Estrategia en la que la app funciona con datos guardados localmente y sincroniza con el servidor cuando hay red, ofreciendo una experiencia fluida sin conexión. Ver el [capítulo 55](../part10/chapter55.md).

**Operaciones funcionales (`map`, `filter`, `forEach`)** — Funciones de orden superior sobre colecciones que transforman, seleccionan o recorren elementos sin bucles explícitos. Ver el [capítulo 12](../part03/chapter12.md).

**`object` / `companion object`** — `object` declara un singleton (una única instancia); `companion object` asocia miembros a una clase sin necesitar una instancia, similar a los miembros estáticos de Java. Ver el [capítulo 19](../part04/chapter19.md).

**`remember` / `mutableStateOf`** — En Compose, `mutableStateOf` crea un estado observable y `remember` lo conserva entre recomposiciones para que no se reinicie en cada ejecución. Ver el [capítulo 33](../part07/chapter33.md).

**Repositorio** — Capa que centraliza el acceso a los datos (red, base de datos local) y oculta al resto de la app de dónde provienen. Ver el [capítulo 44](../part08/chapter44.md).

**`Result`** — Tipo de Kotlin que representa el resultado de una operación que puede tener éxito o fallar, como alternativa a lanzar excepciones. Ver el [capítulo 15](../part03/chapter15.md).

**Retrofit** — Biblioteca que convierte una interfaz de Kotlin en un cliente HTTP para consumir una API REST, gestionando la serialización de las respuestas. Ver el [capítulo 48](../part09/chapter48.md).

**Room** — Biblioteca de persistencia de Android que ofrece una capa sobre SQLite con verificación de consultas en tiempo de compilación. Ver el [capítulo 50](../part09/chapter50.md).

**Ruta tipada** — Enfoque de navegación en el que cada destino se representa con un tipo de Kotlin en lugar de una cadena de texto, aprovechando el compilador para evitar errores. Ver el [capítulo 59](../part10/chapter59.md).

**`Scaffold`** — Composable que provee la estructura básica de una pantalla Material (barra superior, contenido, barra inferior, botón flotante). Ver el [capítulo 34](../part07/chapter34.md).

**`sealed class`** — Clase que restringe sus subtipos a un conjunto conocido y cerrado, lo que permite que `when` verifique de forma exhaustiva todos los casos. Ver el [capítulo 20](../part04/chapter20.md).

**Serialización JSON** — Proceso de convertir objetos de Kotlin a texto JSON y viceversa, necesario para enviar y recibir datos de una API. Ver el [capítulo 47](../part09/chapter47.md).

**State hoisting (elevación de estado)** — Técnica de Compose que consiste en mover el estado fuera de un composable hacia su llamador, dejando el composable sin estado y más reutilizable y comprobable. Ver el [capítulo 33](../part07/chapter33.md).

**`StateFlow`** — Variante de `Flow` que siempre tiene un valor actual y emite las actualizaciones a sus observadores; se usa habitualmente para exponer el estado de la interfaz desde un `ViewModel`. Ver el [capítulo 25](../part05/chapter25.md).

**`suspend`** — Modificador que marca una función como suspendible: puede pausar su ejecución y reanudarla más tarde sin bloquear el hilo. Solo puede llamarse desde una corrutina u otra función `suspend`. Ver el [capítulo 24](../part05/chapter24.md).

**`UiState`** — Objeto que representa, en un solo lugar, todo el estado que necesita una pantalla para dibujarse (datos, carga, errores). Ver el [capítulo 42](../part08/chapter42.md).

**`ViewModel`** — Componente que conserva y gestiona el estado de la interfaz, sobrevive a los cambios de configuración (como girar la pantalla) y mantiene la lógica de presentación fuera de la vista. Ver el [capítulo 42](../part08/chapter42.md).

**Window Size Classes** — Categorías (compacto, medio, expandido) que clasifican el tamaño de la ventana disponible para adaptar el diseño a teléfonos, tablets y pantallas grandes. Ver el [capítulo 40](../part07/chapter40.md).
