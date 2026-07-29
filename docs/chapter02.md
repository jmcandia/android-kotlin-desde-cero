# Capítulo 2: Un primer vistazo a Kotlin

## Introducción

En el capítulo anterior conociste el entorno de desarrollo que utilizarás durante el curso y realizaste la configuración necesaria para comenzar a programar. Ahora es momento de dar el siguiente paso: aprender el lenguaje que utilizarás para desarrollar aplicaciones móviles modernas para Android.

En este capítulo descubrirás qué es **Kotlin**, por qué se ha convertido en uno de los lenguajes más populares para el desarrollo de aplicaciones y cuáles son las principales características que lo diferencian de otros lenguajes. También conocerás brevemente su historia, las distintas plataformas donde puede utilizarse y verás tus primeras líneas de código en Kotlin.

El objetivo de este capítulo no es que domines el lenguaje de inmediato, sino que comprendas su propósito, sus fortalezas y el ecosistema en el que se desarrolla. Estos conceptos servirán como base para los siguientes capítulos, donde comenzarás a escribir programas cada vez más complejos y a construir tus primeras aplicaciones móviles para Android.

## ¿Qué es Kotlin?

Kotlin es un lenguaje de programación moderno y muy eficaz desarrollado por JetBrains. Cuenta con una sintaxis muy clara y concisa, lo que hace que el código sea fácil de leer.

Kotlin se utiliza ampliamente en todo el mundo y su popularidad entre los desarrolladores no deja de crecer. Muchos desarrolladores que utilizan Kotlin señalan que les permite trabajar más rápido y de forma más cómoda.

La **sintaxis básica** de Kotlin es similar a la de Java, pero presenta numerosas diferencias importantes. Una de estas características son las funciones de extensión, que ofrecen a los desarrolladores la posibilidad de ampliar la funcionalidad de las clases sin tener que recurrir a la herencia. Además, Kotlin ofrece **inferencia de tipos**, lo que permite al compilador determinar el tipo de una variable en función del contexto, lo que simplifica la programación y reduce el número de errores en programas complejos.

## Breve historia de Kotlin

En julio de 2011, JetBrains presentó el Proyecto Kotlin, un nuevo lenguaje para la plataforma Java que llevaba un año en desarrollo. El nombre proviene de la isla de Kotlin, situada cerca de San Petersburgo, en Rusia. El objetivo principal de este proyecto era ofrecer una alternativa más segura y concisa a Java en todos los contextos en los que se utiliza actualmente este lenguaje.

En 2016 se lanzó la primera versión estable oficial (Kotlin v1.0). La comunidad de desarrolladores ya mostraba interés en utilizar este lenguaje, especialmente en Android.

En la conferencia **Google I/O 2017**, Google anunció soporte oficial de primer nivel para Kotlin en el desarrollo de Android e invitó a la comunidad a adoptarlo. Dos años más tarde, en **Google I/O 2019**, fue un paso más allá y declaró Android como *"Kotlin-first"*: a partir de ese momento, Kotlin pasó a ser el lenguaje preferido y recomendado para crear aplicaciones Android.

En la actualidad, Kotlin se considera un lenguaje de propósito general para numerosas plataformas, no solo para Android. El lenguaje cuenta con varias versiones al año. La última versión se puede encontrar en la [página web oficial](https://kotlinlang.org).

## Una función de ejemplo en Kotlin

Aquí tienes un ejemplo de un programa sencillo en el lenguaje de programación Kotlin que muestra el mensaje "¡Hola, Kotlin!".

```kotlin
fun main() {
    println("¡Hola, Kotlin!")
}
```

Por ahora, no hace falta que entiendas cómo funciona este código, ¡simplemente disfrútalo!

## Plataformas de aplicación para Kotlin: JVM, Android, JS, nativo

Kotlin se puede utilizar en diversas plataformas de aplicación, como JVM (Máquina Virtual de Java), Android, JavaScript y nativo. Destaca por su flexibilidad y facilidad de uso a la hora de desarrollar software para diferentes plataformas.

Por ejemplo, los desarrolladores que estén familiarizados con Java pueden aprender fácilmente a utilizar Kotlin en dispositivos Android. Lo mismo ocurre con los desarrolladores que estén familiarizados con JavaScript y quieran desarrollar aplicaciones web utilizando Kotlin.

- [JVM](https://docs.oracle.com/javase/specs/jvms/se8/html/): Kotlin es totalmente interoperable con Java, lo que significa que funciona a la perfección con todo el código fuente y las bibliotecas de Java existentes. Además, permite a las empresas realizar una migración gradual de Java a Kotlin, ya que el código Java también puede acceder al código Kotlin. Al mismo tiempo, los desarrolladores pueden utilizar Kotlin como único lenguaje para sus proyectos, sin necesidad de recurrir a Java en absoluto.
- [Android](https://www.android.com/): utilizando la sintaxis de Kotlin, puedes crear aplicaciones móviles para Android, el sistema operativo más utilizado del mundo.
- [JS](https://kotlinlang.org/docs/js-overview.html): Kotlin también es compatible con JavaScript, lo que permite desarrollar aplicaciones web del lado del cliente y ejecutarlas en un navegador.
- [Nativo](https://kotlinlang.org/docs/native-overview.html): Kotlin/Native es una tecnología que permite compilar código Kotlin en binarios nativos que pueden ejecutarse en cualquier sistema operativo, como Windows, Linux, iOS y macOS.
- [Multiplataforma](https://kotlinlang.org/docs/multiplatform.html): con Kotlin Multiplatform, puedes crear aplicaciones multiplataforma que compartan código entre proyectos de Android e iOS para implementar funciones de red, almacenamiento y validación de datos, análisis, cálculos y otra lógica de aplicación.

Entre todas estas oportunidades, los programadores actuales prefieren el desarrollo para dispositivos móviles y el desarrollo del lado del servidor, pero otras áreas también están ganando popularidad.

> [!NOTE]
> La **interoperabilidad con Java** es una de las razones por las que Kotlin se adoptó tan rápido en el mundo profesional. Si ya sabes Java, puedes seguir usando tus librerías favoritas e incluso mezclar clases de Java y Kotlin en el mismo proyecto. Esto hace que la transición desde Java sea muy suave, algo que aprovecharemos a lo largo del curso.

## Características: programación funcional, programación orientada a objetos y mucho más

Kotlin está diseñado como un lenguaje pragmático, lo que significa que su objetivo principal es resolver problemas del mundo real, más que servir a fines de investigación.

También es importante destacar que Kotlin admite múltiples paradigmas de programación, como la programación imperativa, la programación orientada a objetos, la programación genérica, la programación funcional y muchos más. Kotlin también ofrece herramientas como las funciones anónimas y las funciones de orden superior, que permiten a los desarrolladores crear fácilmente abstracciones sobre el código existente.

Por último, pero no por ello menos importante, Kotlin es un lenguaje compatible con numerosas herramientas, lo que significa que todos los tipos de herramientas de desarrollo más populares, como IntelliJ IDEA, Eclipse y Android Studio, son compatibles con él.

## Resumen

En este capítulo conociste el lenguaje que usarás durante todo el curso:

- **Kotlin** es un lenguaje moderno, conciso y seguro, desarrollado por JetBrains.
- Nació en 2011 y hoy es el lenguaje preferido para el desarrollo Android (*Kotlin-first*, desde 2019).
- Es **interoperable con Java**, lo que facilita mucho la transición desde ese lenguaje.
- Funciona en múltiples plataformas (JVM, Android, JavaScript, nativo y multiplataforma) y admite varios paradigmas de programación.
