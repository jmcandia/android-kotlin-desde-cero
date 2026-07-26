# Capítulo 3: Conceptos básicos de Kotlin

- [Introducción](#introducción)
- [Literales básicos: números, cadenas y caracteres](#literales-básicos-números-cadenas-y-caracteres)
  - [Números enteros](#números-enteros)
  - [Caracteres](#caracteres)
  - [Cadenas de caracteres (*Strings*)](#cadenas-de-caracteres-strings)
- [Variables y constantes](#variables-y-constantes)
  - [Declaración de variables](#declaración-de-variables)
  - [`val` frente a `var`](#val-frente-a-var)
  - [Una variable `var` conserva su tipo](#una-variable-var-conserva-su-tipo)
  - [Constantes: la palabra clave `const`](#constantes-la-palabra-clave-const)
  - [`val` no significa inmutable](#val-no-significa-inmutable)
  - [Convenciones de nomenclatura](#convenciones-de-nomenclatura)
  - [Números mágicos](#números-mágicos)
- [Comentarios](#comentarios)
  - [Comentarios de fin de línea](#comentarios-de-fin-de-línea)
  - [Comentarios de varias líneas](#comentarios-de-varias-líneas)
  - [Comentarios de documentación (comentarios "doc")](#comentarios-de-documentación-comentarios-doc)
- [Guía de estilo de Kotlin](#guía-de-estilo-de-kotlin)
- [Escribiendo nuestro primer programa](#escribiendo-nuestro-primer-programa)
  - [Hola, mundo](#hola-mundo)
  - [Terminología básica](#terminología-básica)
  - [El programa "Hola, mundo" al detalle](#el-programa-hola-mundo-al-detalle)
  - [Programas con varias instrucciones](#programas-con-varias-instrucciones)

---

## Introducción

En el capítulo anterior conociste qué es Kotlin, cuál es su historia y por qué se ha convertido en uno de los lenguajes más utilizados para el desarrollo de aplicaciones Android. Ahora ha llegado el momento de comenzar a escribir nuestros primeros programas y aprender los conceptos fundamentales que utilizarás a lo largo de todo el curso.

En este capítulo descubrirás cómo representar información mediante números, caracteres y cadenas de texto, cómo declarar variables y constantes, y cuáles son las diferencias entre `val`, `var` y `const`. También aprenderás a escribir comentarios, seguir las convenciones de estilo de Kotlin y aplicar buenas prácticas de programación desde el comienzo.

Finalmente, escribirás y analizarás el clásico programa **"¡Hola, mundo!"**, que te permitirá comprender la estructura básica de un programa en Kotlin y familiarizarte con algunos de los conceptos y términos que utilizarás de forma permanente durante el desarrollo de aplicaciones.

No te preocupes si algunos conceptos resultan nuevos al principio. El objetivo de este capítulo es construir una base sólida sobre la que desarrollarás tus conocimientos en los siguientes capítulos, incorporando gradualmente nuevas herramientas y características del lenguaje.

## Literales básicos: números, cadenas y caracteres

Independientemente de su complejidad, todos los programas realizan, en esencia, operaciones con números, cadenas de caracteres y otros valores. Estos valores se denominan **literales**, es decir, el sentido o significado más básico del símbolo. Antes de empezar a escribir nuestros primeros programas, veamos cuáles son los literales básicos en Kotlin: números enteros, caracteres y cadenas de caracteres. Estos literales se pueden encontrar en todas partes en la vida cotidiana.

### Números enteros

Utilizamos los números enteros para contar cosas en el mundo real. También los usaremos a menudo en Kotlin.

A continuación se muestran varios ejemplos de literales de números enteros válidos separados por comas: `0`, `1`, `2`, `10`, `11`, `100`.

Si un valor entero contiene muchos dígitos, podemos añadir guiones bajos (`_`) para dividir los dígitos en bloques y hacer que el número sea más legible: por ejemplo, `1_000_000` es mucho más fácil de leer que `1000000`.

Puedes añadir tantos guiones bajos como quieras: `1__000_000`, `1_2_3`. Recuerda que los guiones bajos no pueden aparecer al principio ni al final del número. Si escribes `_10` o `100_`, obtendrás un error.

### Caracteres

Un solo carácter puede representar un dígito, una letra u otro símbolo. Para escribir un solo carácter, se coloca el símbolo entre **comillas simples**, de la siguiente manera: `'A'`, `'B'`, `'C'`, `'x'`, `'y'`, `'z'`, `'0'`, `'1'`, `'2'`, `'9'`. Los **literales de caracteres** pueden representar letras del alfabeto, dígitos del `'0'` al `'9'`, espacios en blanco (`' '`) u otros símbolos (por ejemplo, `'$'`).

No hay que confundir los caracteres que representan números (por ejemplo, `'9'`) con los propios números (por ejemplo, `9`).

Un carácter no puede incluir dos o más dígitos o letras, ya que representa un único símbolo. Los dos ejemplos siguientes son **incorrectos**: `'abc'`, `'543'`, ya que estos literales tienen demasiados caracteres.

### Cadenas de caracteres (*Strings*)

Las cadenas de caracteres o *strings* representan información de texto, como el texto de un anuncio, la dirección de una página web o los datos de inicio de sesión en un sitio web. Una cadena es una secuencia de cualquier tipo de caracteres.

Para escribir cadenas, se colocan los caracteres entre **comillas dobles** en lugar de comillas simples. Estos son algunos ejemplos válidos: `"texto"`, `"Quiero aprender Kotlin"`, `"123456"`, `"e-mail@gmail.com"`. Así pues, las cadenas pueden incluir letras, dígitos, espacios en blanco y otros caracteres.

Una cadena también puede contener un solo carácter, como `"A"`. No lo confundas con el carácter `'A'`, que no es una cadena.

## Variables y constantes

Una variable es un espacio de almacenamiento para un valor, que puede ser una cadena, un número o cualquier otra cosa. Cada variable tiene un nombre (o identificador) que la distingue de las demás, y mediante ese nombre accedemos a su valor. Son uno de los elementos más utilizados en cualquier programa.

En esta sección veremos cómo declarar variables con `val` y `var`, en qué se diferencian, cómo declarar constantes con `const`, qué significa realmente la mutabilidad en Kotlin y cómo nombrar todo esto siguiendo las convenciones del lenguaje.

### Declaración de variables

Antes de poder utilizar una variable, debes declararla. Para ello, Kotlin ofrece dos palabras clave principales, más un modificador:

- **`val`** (de *value*) declara una variable de **solo lectura**: una vez inicializada, no se puede reasignar.
- **`var`** (de *variable*) declara una variable **mutable**, que se puede reasignar tantas veces como necesites.
- **`const`** se usa junto a `val` para declarar constantes cuyo valor se conoce en tiempo de compilación (lo veremos más adelante).

> [!NOTE]
> Dos términos que usaremos seguido: **mutable** significa que algo *puede cambiar* después de creado, e **inmutable** significa que *no puede cambiar*. Aquí lo aplicaremos a las variables: una `var` es mutable (su valor se puede reasignar) y una `val`, en ese sentido, es inmutable (no se puede reasignar). Más adelante veremos que esa palabra tiene un matiz interesante.

Al declarar una variable, escribes su nombre después de la palabra clave. Ten cuidado: el nombre no puede empezar por un dígito y conviene que sea significativo y legible. Para asignarle un valor, usamos el operador de asignación `=`.

Declaremos una variable de solo lectura llamada `language` e inicialicémosla con la cadena `"Kotlin"`:

```kotlin
val language = "Kotlin"
println(language) // muestra "Kotlin" sin comillas
```

> [!IMPORTANT]
> Los nombres distinguen entre mayúsculas y minúsculas: `language` no es lo mismo que `Language`.

Ahora declaremos una variable mutable llamada `dayOfWeek` e imprimamos su valor antes y después de modificarla:

```kotlin
var dayOfWeek = "Monday"
println(dayOfWeek) // imprime Monday

dayOfWeek = "Tuesday"
println(dayOfWeek) // imprime Tuesday
```

Primero inicializamos `dayOfWeek` con `"Monday"` y mostramos su valor; luego lo cambiamos a `"Tuesday"` y volvemos a mostrarlo.

> [!NOTE]
> No es necesario volver a declarar una variable para cambiar su valor. Basta con asignarle un nuevo valor con el operador `=`.

### `val` frente a `var`

Ya viste ambas palabras clave en acción (`val language` y `var dayOfWeek`), así que dejemos la diferencia lo más clara posible, porque es una decisión que tomarás en casi cada línea que escribas.

La regla, en una frase:

- Una variable **`val`** se asigna **una sola vez**; después, su nombre queda amarrado a ese valor y **no** puedes reasignarlo.
- Una variable **`var`** se puede **reasignar** tantas veces como necesites.

Una forma fácil de recordarlo: un `val` es como tu fecha de nacimiento, se fija una vez y ya no cambia; un `var` es como tu edad, que va cambiando con el tiempo.

Míralas lado a lado. Lo único que cambia entre un bloque y el otro es la palabra clave:

```kotlin
// Con val: solo se puede asignar una vez
val nombre = "Ana"
nombre = "Beatriz" // error de compilación: Val cannot be reassigned

// Con var: se puede reasignar cuantas veces quieras
var ciudad = "Madrid"
ciudad = "Lima" // correcto
```

Ambas se declaran e inicializan igual; la diferencia aparece cuando intentas **cambiar** el valor. Con `val`, el compilador detiene la compilación con el mensaje `Val cannot be reassigned`; con `var`, la reasignación funciona sin problemas.

El siguiente cuadro resume el contraste:

| | `val` | `var` |
| :--- | :--- | :--- |
| ¿Se puede reasignar? | No | Sí |
| ¿Cuántas veces se le asigna un valor? | Una sola vez | Las que necesites |
| Equivalente aproximado en Java | `final` | variable normal |
| ¿Cuándo usarla? | Por defecto | Solo cuando el valor deba cambiar |

> [!TIP]
> Empieza siempre con `val`. Si más adelante el código realmente necesita cambiar el valor, recién ahí conviértela en `var`. Mientras menos variables mutables tengas, más fácil será leer y entender tu programa: cada `var` que aparece es una señal de "ojo, esto cambia".

### Una variable `var` conserva su tipo

Una variable puede almacenar distintos tipos de valores: números, cadenas, caracteres y otros que veremos en el próximo capítulo.

```kotlin
val ten = 10
val greeting = "Hola"
val firstLetter = 'A'

println(ten)         // 10
println(greeting)    // Hola
println(firstLetter) // A
```

Ahora bien, cuando una variable `var` ya tiene un valor, al reasignarla solo puedes usar valores **del mismo tipo** que el inicial. Por eso el siguiente código no compila:

```kotlin
var number = 10
number = 11     // correcto
number = "doce" // ¡aquí hay un error!
```

Esto también aplica al copiar valores: puedes copiar el valor de un `val` a un `var` sin problema; lo que no puedes es reasignar el `val`.

```kotlin
val count = 10
var cnt = count
cnt = 20 // correcto: cnt es var, no una constante
```

### Constantes: la palabra clave `const`

Un `val` es una variable de solo lectura: su valor se fija una sola vez, aunque ese valor puede calcularse mientras el programa se ejecuta (por ejemplo, a partir de datos que ingresa el usuario).

A veces necesitas algo más estricto: un valor que se conozca **en tiempo de compilación**, es decir, antes de que el programa siquiera se ejecute. Para eso, Kotlin ofrece el modificador `const`, que se antepone a `val`:

```kotlin
const val MY_STRING = "Esta es una cadena constante"
```

Como su valor debe conocerse al compilar, no puede provenir de una función ni de la entrada del usuario:

```kotlin
const val MY_STRING = readln() // ¡¡¡No es una constante en tiempo de compilación!!!
```

Existen algunas restricciones para usar `const`. En primer lugar, solo se puede aplicar a variables de tipo `String` o de tipo primitivo:

```kotlin
const val CONST_INT = 127
const val CONST_DOUBLE = 3.14
const val CONST_CHAR = 'c'
const val CONST_STRING = "Soy una constante"
const val CONST_ARRAY = arrayOf(1, 2, 3) // error: tipo no permitido
```

Además, las variables `const` deben declararse en el nivel superior, fuera de cualquier función:

```kotlin
const val MY_INT_1 = 1024 // línea correcta

fun main() {
    const val MY_INT_2 = 2048 // error: no es aplicable a una variable local
}
```

### `val` no significa inmutable

Hay un matiz importante: que un `val` no se pueda **reasignar** no quiere decir que su contenido sea **inmutable**. Veámoslo con una `MutableList`, una lista ordenada de elementos del mismo tipo (si quieres adelantarte puedes investigarla, pero no es imprescindible por ahora).

No puedes reasignar la variable:

```kotlin
val myMutableList = mutableListOf(1, 2, 3, 4, 5)
myMutableList = mutableListOf(1, 2, 3, 4, 5, 6) // línea de error
```

Pero sí puedes modificar su **estado interno**:

```kotlin
val myMutableList = mutableListOf(1, 2, 3, 4, 5)
myMutableList.add(6)   // funciona
println(myMutableList) // [1, 2, 3, 4, 5, 6]
```

Al invocar `add()` no modificamos la variable en sí (sigue apuntando a la misma lista), sino el contenido de esa lista.

> [!NOTE]
> Si vienes de Java, puedes pensar en un `val` de Kotlin como una variable `final`: en ambos casos no puedes reasignar la variable, pero sí puedes modificar el estado interno del objeto al que apunta.

### Convenciones de nomenclatura

Elegir buenos nombres para tus variables parece trivial, pero no lo es: pasarás más tiempo leyendo código (tuyo y de otros) que escribiéndolo, y los nombres claros hacen toda la diferencia. Hay dos conjuntos de reglas: las **obligatorias**, que el compilador exige, y las **convenciones**, que son recomendaciones de estilo.

**Reglas obligatorias**:

- Los nombres distinguen entre mayúsculas y minúsculas (`number` no es lo mismo que `Number`).
- Un nombre solo puede incluir letras, dígitos y guiones bajos.
- Un nombre no puede empezar por un dígito.
- Un nombre no puede ser una palabra clave (`val`, `var`, `fun`, etc.).

Por lo tanto, no se permiten espacios en los nombres, salvo que uses comillas invertidas:

```kotlin
val `buen nombre` = 5
val mal nombre = 2 // no funcionará
```

> [!WARNING]
> Si incumples estas reglas, tu programa no compilará.

Ejemplos de nombres **válidos**:

```kotlin
score, level, fruitType, i, j, abc, _cost, number1, `nombre con espacio`
```

Ejemplos de nombres **incorrectos**:

```kotlin
@pple, $dollar, 1number, !ab, val, var, _, nombre con espacio
```

**Convenciones de estilo**:

- Para variables `val` y `var`, usa `lowerCamelCase`: la primera palabra en minúscula y las siguientes con mayúscula inicial (`numberOfCoins`). Si es una sola palabra, va en minúsculas (`number`, `value`).
- Para variables `const`, usa `SCREAMING_SNAKE_CASE`: todo en mayúsculas y con guiones bajos separando palabras (`MAX_USER_COUNT`).
- Elige nombres significativos: `score` comunica mucho más que `s`, aunque ambos sean válidos.

```kotlin
val numberOfWheels: Int
val isConnectionAvailable: Boolean
val userFirstName: String

const val MAX_USER_COUNT = 50
const val COMPANY_NAME = "TechCorp"
```

### Números mágicos

A veces necesitas un valor fijo en tu código: por ejemplo, los días de la semana, que siempre son 7. Sin embargo, escribirlo directamente no le dice nada a quien lo lee (ni a ti mismo tiempo después):

```kotlin
println(7)
```

¿Qué es ese `7`? ¿Por qué necesitamos que se imprima? Ni idea. A estos valores sueltos, sin nombre ni contexto, se les llama **números mágicos**, y conviene evitarlos.

En su lugar, guárdalos en una constante con un nombre significativo, declarada como `const val` fuera de cualquier función:

```kotlin
const val DAYS_OF_THE_WEEK = 7

fun main() {
    // ...
    println(DAYS_OF_THE_WEEK) // 7
    // ...
}
```

Así el código se explica solo. Compara un mal nombre con uno bueno:

```kotlin
const val s = 4        // ¿qué es "s"?
const val SEASONS = 4  // mucho más claro
```

## Comentarios

Entonces, ¿qué son exactamente esos comentarios y para qué los necesitamos? Básicamente, es un texto especial que el compilador ignora. Los comentarios te permiten aclarar una parte del código o excluirla del proceso de compilación (es decir, desactivarla). A lo largo de este curso, utilizaremos comentarios para explicar cómo y por qué funciona nuestro código.

Hay tres tipos de comentarios en Kotlin. Echemos un vistazo a ellos.

### Comentarios de fin de línea

Después de `//`, el compilador ignora cualquier texto restante en la línea.

```kotlin
fun main() {
    // La línea siguiente se ignorará
    // println("Hola, Mundo")

    // Esto imprime la cadena "Hola, Kotlin"
    println("Hola, Kotlin")  // Aquí puede haber cualquier comentario
    /// Comentario válido de una sola línea
}
```

### Comentarios de varias líneas

El compilador ignora cualquier texto que se encuentre entre los símbolos `/*` y `*/`. Puedes utilizarlos para escribir tanto comentarios de una sola línea como de varias líneas:

```kotlin
fun main() {
    /* Este es un comentario de una sola línea */
    /*  Este es un ejemplo de
        un comentario de varias líneas */

    /*** Comentario de varias líneas válido
        println("Hola")
        println("Mundo")
    **/
}
```

También puedes incluir comentarios dentro de otros comentarios. Al escribir comentarios de varias líneas anidados, asegúrate de que los símbolos de apertura `/*` y de cierre `*/` formen pares.

```kotlin
fun main() {
    /*
    System.out.println("Hola")  // imprime "Hola"
    System.out.println("Kotlin") /* imprime "Kotlin" */
    */
}
```

### Comentarios de documentación (comentarios "doc")

El compilador ignora cualquier texto situado entre los signos `/**` y `*/`, del mismo modo que ignora todo el texto de los comentarios de varias líneas. Este tipo de comentarios se denominan comentarios de documentación (comentarios "doc"). La documentación es un texto que ayuda a otros a comprender qué hace tu código. Puedes utilizar este tipo de comentarios para generar automáticamente documentación sobre tu código fuente mediante una herramienta especial.

Normalmente, estos comentarios se colocan encima de las declaraciones de los respectivos elementos del programa. Además, en este caso, se utilizan algunas etiquetas especiales, como `@param`, `@return` y otras, para controlar la herramienta.

Echemos un vistazo al ejemplo siguiente.

```kotlin
/**
 * La función `main` acepta argumentos de tipo cadena desde el exterior.
 *
 * @param args argumentos de la línea de comandos.
 */
fun main(args: Array<String>) {
    // hacer algo
}
```

## Guía de estilo de Kotlin

Además de los nombres, existen **convenciones de estilo** que estandarizan el código y lo hacen más fácil de leer, sobre todo cuando se trabaja en equipo. Son recomendaciones, no reglas estrictas, pero conviene seguirlas desde el principio. No hace falta memorizarlas todas de una vez; puedes consultarlas a medida que avanzas. Estas son algunas de las más habituales en Kotlin:

1. Usa **4 espacios** para la sangría, en lugar de tabulaciones (una tabulación no siempre equivale a cuatro espacios según el sistema o el IDE).
2. Omite el punto y coma (`;`). Kotlin no lo necesita.
3. Coloca la llave de apertura al final de la línea.
4. Coloca la llave de cierre al principio de la línea siguiente.

Por ejemplo, este programa sigue esas reglas:

```kotlin
fun main() { // llave de apertura al final de la línea
    println("Hola") // sangría de 4 espacios y sin `;` al final
} // llave de cierre al inicio de su propia línea
```

También es buena práctica dejar un espacio antes de la llave de apertura tras `main()`.

## Escribiendo nuestro primer programa

En esta lección, escribirás tu primer programa en Kotlin, que muestra el mensaje `"¡Hola, mundo!"`. Este es el primer paso que todo principiante debe dar. Aunque el programa en sí es muy sencillo, se trata de un programa que funciona y muestra la sintaxis básica del lenguaje de programación.

### Hola, mundo

Aquí lo tienes. A continuación se muestra el código fuente de este programa:

```kotlin
fun main() {
    println("¡Hola, mundo!")
}
```

Si ya tienes instalado un entorno de programación en tu computador, puedes ejecutar el programa desde allí. Si no es así, no te preocupes. Volveremos sobre este tema más adelante.

### Terminología básica

Ahora, aprendamos la terminología básica y luego intentemos comprender nuestro programa.

- Un **programa** es una secuencia de instrucciones (denominadas **sentencias**), que se ejecutan una tras otra de forma predecible. El flujo secuencial es la situación más habitual y sencilla, en la que las sentencias se ejecutan en el orden en que están escritas, es decir, de arriba abajo, una tras otra;
- Una **sentencia** (o **sentencia de programación**) es un comando único que debe ejecutarse (como imprimir un texto);
- Una **expresión** es un fragmento de código que produce un único valor (por ejemplo, `2*2` es una expresión);
- Un **bloque** es un grupo de cero o más instrucciones encerradas entre un **par de llaves** `{...}`;
- Una **palabra clave** es una palabra que tiene un significado especial en el lenguaje de programación. Los nombres de las palabras clave no se pueden cambiar;
- Un **identificador** (o **nombre**) es una palabra escrita por el programador para identificar algo;
- Un **comentario** es un fragmento de texto que se ignora al ejecutar el programa; simplemente explica una parte del código. Los comentarios comienzan por `//`;
- Un **espacio en blanco** es un espacio, una tabulación o un salto de línea; se utiliza para separar palabras en el programa y mejorar la legibilidad.

### El programa "Hola, mundo" al detalle

El programa **"Hola, mundo"** ilustra el uso de los elementos básicos de cualquier programa en Kotlin. Por ahora, nos centraremos únicamente en los más importantes.

**El punto de entrada:**

La palabra clave `fun` define una función que contiene un fragmento de código que se va a ejecutar. Esta función tiene un nombre especial: `main`. Indica el punto de entrada de un programa en Kotlin. El cuerpo de la función está entre llaves `{...}`.

```kotlin
fun main() {
    // ...
}
```

> [!NOTE]
> No te preocupes por las funciones, ya que hablaremos de ellas en profundidad más adelante.

El nombre de esta función debe ser siempre el mismo: `main`. Si la llamas `Main`, `MAIN` o cualquier otra cosa, el programa no se iniciará.

> [!NOTE]
> El texto que aparece después de `//` es solo un comentario, no forma parte del código. Más adelante aprenderemos a escribir comentarios.

**Imprimir `"¡Hola, mundo!"`:**

El cuerpo de esta función está formado por instrucciones de programación que definen lo que debe hacer el programa. Nuestro programa imprime la cadena `"¡Hola, mundo!"` mediante la siguiente instrucción:

```kotlin
println("¡Hola, mundo!")
```

Esta es una de las cosas más importantes que hay que entender sobre el programa **"Hola, mundo"**. Llamamos a la función `println` para mostrar una cadena seguida de una nueva línea en la pantalla. A menudo utilizaremos este método para mostrar algo en pantalla.

> [!IMPORTANT]
> Recuerda que `"¡Hola, mundo!"` no es una palabra clave ni un nombre. Es simplemente una cadena literal que se muestra en pantalla.

### Programas con varias instrucciones

Por regla general, un programa contiene varias instrucciones. Debes empezar una nueva línea para escribir cada instrucción. Por ejemplo, el programa siguiente tiene dos instrucciones:

```kotlin
fun main() {
    println("Hola")
    println("Mundo")
}
```

Si ejecutas el programa, verás que muestra lo siguiente:

```plaintext
Hola
Mundo
```

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter02.md">← Anterior</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter04.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
