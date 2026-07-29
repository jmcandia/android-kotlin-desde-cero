# Capítulo 3: Conceptos básicos de Kotlin

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

Las variables son uno de los elementos más utilizados en cualquier programa. En esta sección las conoceremos paso a paso: primero qué son y cómo se crean, luego cómo declarar una variable cuyo valor no cambia (`val`) y otra cuyo valor sí puede cambiar (`var`), cuándo conviene cada una, y por último cómo declarar constantes y cómo nombrar todo esto siguiendo las convenciones de Kotlin.

### ¿Qué es una variable?

Cuando un programa se ejecuta, necesita guardar valores para usarlos más adelante: un nombre, un número, un mensaje. Una **variable** es precisamente eso: un espacio con nombre donde el programa guarda un valor.

Piensa en una variable como una **caja etiquetada**: la etiqueta es el nombre y, dentro, está el valor que guardaste. Cuando necesitas ese valor, no tienes que recordar dónde quedó; basta con nombrar la caja.

Gracias a ese nombre, podemos guardar un valor una vez y reutilizarlo tantas veces como queramos a lo largo del programa.

### Declaración de variables

Antes de usar una variable hay que **declararla**, es decir, crearla. En Kotlin, declarar una variable tiene esta forma: una palabra clave, el nombre que le damos, el signo `=` y el valor que queremos guardar.

Empecemos por la palabra clave más recomendable (más adelante verás por qué): `val`. Declaremos una variable llamada `language` que guarde el texto `"Kotlin"`:

```kotlin
val language = "Kotlin"
```

Para leer el valor guardado, usamos el nombre de la variable. Por ejemplo, para mostrarlo en pantalla:

```kotlin
println(language) // muestra: Kotlin
```

Al elegir el nombre, ten en cuenta un par de cosas: no puede empezar por un dígito y conviene que describa lo que guarda (`language` se entiende mucho mejor que `x`). Volveremos sobre las reglas de nomenclatura al final de esta sección.

> [!IMPORTANT]
> Los nombres distinguen entre mayúsculas y minúsculas: `language` no es lo mismo que `Language`.

### Uso de `val`

La palabra clave `val` (de *value*, "valor") crea una variable de **solo lectura**: le asignas un valor **una sola vez** y, a partir de ahí, puedes leerlo cuantas veces quieras, pero no cambiarlo.

```kotlin
val pi = 3.1415
val saludo = "Hola"

println(pi)     // 3.1415
println(saludo) // Hola
```

En la mayoría de los programas, muchos datos no necesitan cambiar una vez definidos, así que `val` será tu opción por defecto.

#### ¿Qué ocurre cuando intento cambiar un `val`?

Como un `val` es de solo lectura, si intentas asignarle un nuevo valor, el programa **no compilará**. Veámoslo:

```kotlin
val language = "Kotlin"
language = "Java" // error de compilación: Val cannot be reassigned
```

El compilador detiene la compilación y muestra el mensaje `Val cannot be reassigned` ("no se puede reasignar un val"). Lejos de ser una molestia, esto es una **protección**: te avisa de inmediato si por error intentas modificar un valor que debía permanecer fijo.

### Variables `var`

Hasta ahora, los valores que guardamos no cambiaban. Pero en muchos programas hay datos que **sí cambian mientras el programa se ejecuta**. Por ejemplo:

- el **puntaje** de un jugador, que sube a medida que avanza;
- la **edad** de una persona, que aumenta con el tiempo;
- la **cantidad** de productos en un carrito de compras;
- la **página actual** que se está leyendo en una aplicación.

Para todos estos casos, `val` se queda corto: apenas intentáramos actualizar el valor, obtendríamos el error `Val cannot be reassigned`. Necesitamos una variable que **sí** podamos modificar, y para eso existe `var` (de *variable*).

Veamos un puntaje que va cambiando:

```kotlin
var puntaje = 0
puntaje = 10
puntaje = 25
println(puntaje) // 25
```

A una variable que puede cambiar de valor se le llama **mutable**; a una que no cambia (como `val`), **inmutable**.

> [!NOTE]
> No hace falta volver a declarar la variable para cambiar su valor. Basta con asignarle uno nuevo con el operador `=`.

#### ¿Cuándo debo usar `var`?

La respuesta es simple: usa `var` **solo cuando el valor realmente necesite cambiar** durante la ejecución del programa.

Ante cada variable, hazte una pregunta: *¿este dato va a cambiar mientras el programa corre?* Si la respuesta es sí (un puntaje, un contador, una edad), usa `var`. Si es no (el número pi, un nombre fijo, un mensaje), usa `val`.

### Comparación entre `val` y `var`

Pongamos las dos, una al lado de la otra. Fíjate en que los ejemplos son casi idénticos: lo **único** que cambia es la palabra clave.

Con `val`, el valor no se puede reemplazar:

```kotlin
val a = 1
a = 2 // error de compilación: Val cannot be reassigned
```

Con `var`, sí se puede:

```kotlin
var b = 1
b = 2 // correcto
```

Para recordarlo, volvamos a la caja etiquetada: con `val`, una vez que pones algo dentro, la caja queda **sellada**; puedes mirar el contenido cuantas veces quieras, pero no reemplazarlo. Con `var`, la caja queda **abierta**: puedes sacar lo que hay y poner otra cosa en su lugar.

Este cuadro resume la diferencia:

| | `val` | `var` |
| :--- | :--- | :--- |
| ¿Se puede reasignar? | No | Sí |
| ¿Cuántas veces se le asigna un valor? | Una sola vez | Las que necesites |
| Equivalente aproximado en Java | `final` | variable normal |
| ¿Cuándo usarla? | Por defecto | Cuando el valor deba cambiar |

### Regla práctica: usar `val` por defecto

> [!TIP]
> La recomendación oficial en el ecosistema Kotlin es empezar siempre con `val` y cambiar a `var` solo cuando de verdad necesites modificar el valor. Preferir `val` produce código más **seguro**, más **predecible** y más **fácil de mantener**, porque limita la cantidad de datos que pueden cambiar y, además, cada `var` que aparece funciona como una señal de "ojo, esto cambia".

### Conservación del tipo de una variable

Cuando guardas un valor en una variable, Kotlin **recuerda su tipo** (un número, un texto, un carácter…). A partir de ese momento, esa variable solo puede contener valores de ese mismo tipo. Esto importa sobre todo con `var`, que es la que reasignamos:

```kotlin
var numero = 10
numero = 11     // correcto: sigue siendo un número
numero = "doce" // error: no puedes guardar texto donde había un número
```

En cambio, sí puedes **copiar** el valor de un `val` a un `var` sin problema; lo que no puedes es reasignar el `val`:

```kotlin
val base = 10
var contador = base
contador = 20 // correcto: contador es var, no una variable de solo lectura
```

### Constantes (`const`)

Ya sabes que un `val` no se puede reasignar. Sin embargo, su valor todavía puede **calcularse mientras el programa se ejecuta** (por ejemplo, a partir de un dato que escribe el usuario). A veces necesitas algo distinto: un valor que se conozca **antes** de ejecutar el programa, en lo que se denomina *tiempo de compilación*. Para eso, Kotlin ofrece el modificador `const`, que se antepone a `val`:

```kotlin
const val MY_STRING = "Esta es una cadena constante"
```

> [!NOTE]
> No confundas ambas ideas: `val` controla **si** una variable se puede reasignar (no se puede); `const` controla **cuándo** se conoce su valor (al compilar). Por eso `const` siempre acompaña a `val`; no se trata de "un `val` más estricto", sino de algo diferente.

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

<!-- NOTA INTERNA (autor): aquí iba la subsección "`val` no significa inmutable", que muestra que un `val` puede tener un estado interno modificable (ejemplo con MutableList y .add()). Se pospone a propósito porque depende de objetos, referencias y colecciones mutables que el estudiante todavía no conoce. Reincorporarla en un capítulo posterior, una vez introducidos objetos / referencias / listas / colecciones mutables. -->

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

¿Qué es ese `7`? ¿Por qué debemos imprimirla? Ni idea. A estos valores sueltos, sin nombre ni contexto, se les llama **números mágicos**, y conviene evitarlos.

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

## Resumen

En este capítulo diste tus primeros pasos escribiendo código Kotlin:

- Los **literales** básicos son números, caracteres (comillas simples) y cadenas (comillas dobles).
- Una **variable** guarda un valor: `val` es de solo lectura (no se reasigna) y `var` es mutable.
- Prefiere `val` por defecto y usa `var` solo cuando el valor deba cambiar.
- `const val` declara constantes cuyo valor se conoce en tiempo de compilación.
- Sigue las **convenciones de nomenclatura** (`camelCase` para variables, `SCREAMING_SNAKE_CASE` para constantes) y evita los números mágicos.
- Escribiste y analizaste el programa **"¡Hola, mundo!"**.
