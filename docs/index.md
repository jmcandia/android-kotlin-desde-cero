# Android con Kotlin desde cero

Bienvenido a **Android con Kotlin desde cero**: un recorrido que te lleva desde los fundamentos del lenguaje Kotlin hasta construir una aplicación Android completa con Jetpack Compose.

## ¿Para quién es este curso?

Este curso está pensado para personas que quieren aprender **Kotlin** y el **desarrollo Android moderno** empezando por los cimientos del lenguaje. Cada concepto se apoya en el anterior, con ejemplos claros y sin dar nada por sabido. No necesitas experiencia previa con Java, Android, Kotlin ni Git: todo se introduce cuando hace falta.

**Conocimientos previos recomendados:**

- Manejo básico de una computadora: crear carpetas, descargar e instalar programas.
- Lógica de programación a nivel inicial (muy recomendable, no obligatorio): qué es una variable, un condicional, un bucle y una función. Si nunca programaste, la **Ruta A** está diseñada para ti.
- Algo de inglés para leer mensajes de error y documentación. Todo el material del curso está en español.

Si además ya programas en **Java**, el [Anexo B](content/appendix/java-scanner.md) traduce el vocabulario de un lenguaje al otro y te permitirá avanzar más rápido.

## ¿Por dónde empiezo? Tres rutas

No todos llegamos con el mismo punto de partida. Estas tres rutas cubren los casos más comunes; elige la que mejor describa tu situación después de hacer la [autoevaluación inicial](#autoevaluacion-inicial-sin-nota).

- **Ruta A · Empiezo desde cero** — Nunca programaste (o casi nunca). Recorre las Partes I a X en orden, sin saltarte ejercicios. Dedica tiempo a la Parte I: la comodidad con las variables, los tipos y la entrada/salida es la base de todo lo que viene después.
- **Ruta B · Ya programo en otro lenguaje** — Conoces variables, condicionales, bucles, funciones, colecciones y clases en Java, Python, C# u otro lenguaje. Puedes *hojear* las Partes I a IV como repaso (leyendo sobre todo lo específico de Kotlin: `val`/`var`, *null safety*, `data class`), apoyarte en el [Anexo B](content/appendix/java-scanner.md), y concentrar tu esfuerzo en las Partes V a X.
- **Ruta C · Solo necesito Kotlin** — No vienes por Android, sino por el lenguaje. Recorre las Partes I a V (Kotlin, colecciones, orientación a objetos y coroutines) y los anexos [A](content/appendix/design-principle.md), [B](content/appendix/java-scanner.md) y [D](content/appendix/string-format.md). La Parte V es el puente natural hacia Android; si en algún momento te interesa, continúa con la Parte VI.

> [!NOTE]Recomendación
> Si dudas entre la Ruta A y la B, empieza por la Parte I y decide sobre la marcha. Avanzar rápido por temas que ya dominas cuesta menos que rellenar una base que falta.

## Autoevaluación inicial (sin nota)

Antes de elegir ruta, responde mentalmente estas preguntas. **No hay respuestas correctas ni incorrectas**: solo sirven para situarte. Si una respuesta te deja en duda, revisa el tema en la ruta correspondiente.

1. ¿Has escrito algún programa, en cualquier lenguaje, que use variables, condicionales y bucles?
2. ¿Conoces los tipos de datos básicos (números, texto, booleanos) y las colecciones (listas, conjuntos, mapas) de algún lenguaje?
3. ¿Sabes qué es una clase, la herencia y una interfaz?
4. ¿Has usado alguna vez una función lambda o has pasado una función como argumento de otra?
5. ¿Te manejas con una terminal o línea de comandos y con Git (aunque sea lo básico)?
6. ¿Has creado alguna aplicación (web, de escritorio o móvil), aunque sea mínima?

**Cómo leer tus respuestas:**

- **«No» o «no estoy seguro» en las preguntas 1 a 4** → **Ruta A**. El curso empieza exactamente desde ahí.
- **«Sí» en las preguntas 1 a 4, pero no en la 5 ni la 6** → **Ruta B**. Ya conoces la programación; el curso te dará el vocabulario de Kotlin y luego Android.
- **«Sí» en casi todas, y solo te falta Kotlin o repasarlo** → **Ruta C**. Puedes incluso empezar por el Anexo B y saltar directamente a los capítulos de Kotlin que menos domines.

## ¿Qué construiremos?

El curso cierra con **«Mis Contactos»**, una app de gestión de contactos que consume una API REST con operaciones **CRUD** completas: listar contactos (con búsqueda, paginación, *pull-to-refresh* y favoritos), ver su detalle, y crearlos, editarlos y eliminarlos, con **caché local para funcionar sin conexión**. Está construida con **MVVM**, **Jetpack Compose**, **Retrofit**, **Room** y **Material 3**.

## ¿Cómo está organizado?

El curso avanza por partes, de lo más básico a lo más avanzado. Al inicio de cada parte encontrarás un recuadro **«Lo que aprenderás»** con los resultados de aprendizaje, y al cierre un recuadro **«Comprueba tu avance»** con un hito de autoevaluación:

- **Parte I · Fundamentos de Kotlin** — variables, tipos, operadores, entrada/salida y cadenas.
- **Parte II · Estructuras de control y funciones** — `if`, `when`, bucles y funciones.
- **Parte III · Colecciones y código robusto** — listas, mapas, operaciones funcionales, *null safety* y excepciones.
- **Parte IV · Programación orientada a objetos** — clases, herencia, `data class`, `object`, `sealed class`, genéricos y lambdas.
- **Parte V · Asincronía con Coroutines** — hilos, `suspend`, `launch`, `Flow` y `StateFlow`.
- **Parte VI · Android y arquitectura de aplicaciones** — Android Studio, ciclo de vida de una `Activity`, cómo se organiza una app, arquitectura MVVM.
- **Parte VII · Jetpack Compose y construcción de interfaces** — composables, `Modifier`, Material 3, estado, `Scaffold`/`LazyColumn`, formularios y navegación.
- **Parte VIII · Estado y arquitectura con MVVM** — `ViewModel`, `UiState`, efectos y eventos, repositorio, inyección de dependencias.
- **Parte IX · Datos: API REST y persistencia local** — HTTP y JSON, Retrofit, estados de red y persistencia local con Room.
- **Parte X · Proyecto final: contactos** — «Mis Contactos» de principio a fin: arquitectura, capa remota y local, repositorio *offline-first*, las tres pantallas, navegación tipada y componentes compartidos.

Al final encontrarás los **anexos** con material de referencia (principios de diseño, equivalencias Java→Kotlin y entrada estándar con `Scanner`, criterios para elegir el SDK de un proyecto Android y formato de cadenas con `String.format`).

## Cómo aprovechar el curso

- **Intenta antes de mirar.** Casi todos los capítulos abren con unas preguntas de recuperación («Antes de empezar») y cierran con una actividad breve («Para cerrar») que se resuelve sin copiar el ejemplo. Las soluciones están plegadas: intenta primero y consulta después.
- **No te saltes los ejercicios de repaso.** Al final de cada parte hay ejercicios con ayudas graduadas (pregunta guía → pista → fragmento → solución explicada). Son la mejor forma de detectar qué no entendiste del todo.
- **Comprueba tu avance por partes.** Los recuadros «Comprueba tu avance» te piden explicar ideas con tus palabras y resolver algo sin mirar el material; si no puedes, vuelve al capítulo correspondiente.

> [!TIP]Sugerencia
> Si es tu primera vez, empieza por el [Capítulo 1](content/part01/chapter01.md) y avanza en orden: cada capítulo asume lo aprendido en los anteriores. Usa el menú lateral para desplazarte entre los capítulos.
