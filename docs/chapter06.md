# Capítulo 6: Entrada y salida estándar y plantillas de cadena

## Introducción

Hasta ahora has aprendido a declarar variables, trabajar con distintos tipos de datos, usar operadores y escribir programas sencillos en Kotlin. Sin embargo, un programa que siempre produce el mismo resultado tiene una utilidad limitada. Para que una aplicación sea realmente útil, debe ser capaz de recibir información, procesarla y generar una respuesta.

En este capítulo aprenderás cómo interactuar con el usuario mediante la **entrada** y la **salida estándar**, dos mecanismos fundamentales presentes en prácticamente cualquier lenguaje de programación. Comenzarás utilizando las funciones `print` y `println` para mostrar mensajes, valores y resultados en la consola. También conocerás las **plantillas de cadenas** de Kotlin, que permiten insertar variables y expresiones dentro de un texto de forma clara y sencilla.

Después, aprenderás a leer datos introducidos por el usuario utilizando la función `readln()`, la forma más habitual y sencilla de trabajar con la entrada estándar en Kotlin. Verás cómo convertir la información leída a distintos tipos de datos, como números enteros, decimales y valores booleanos, además de trabajar con múltiples entradas.

No te preocupes si al principio estos programas parecen sencillos. El objetivo de este capítulo es que aprendas a crear aplicaciones capaces de comunicarse con el usuario, una habilidad esencial que utilizarás continuamente en los siguientes capítulos para resolver problemas cada vez más complejos.

## Salida estándar

La **salida estándar** es la operación básica que muestra información en un dispositivo. Por defecto, la salida estándar muestra los datos en la pantalla, pero es posible redirigirla a un archivo.

### Impresión de texto

Kotlin dispone de dos funciones que envían datos a la salida estándar: `println` y `print`.

La función `println` (*print line*, "imprimir línea") muestra en pantalla una cadena de texto seguida de una nueva línea. Por ejemplo, el fragmento de código siguiente imprime cinco líneas:

```kotlin
println("Yo")
println("sé")
println("programar")
println("en")
println("Kotlin.")
```

**Salida:**

```plaintext
Yo
sé
programar
en
Kotlin.
```

Como puedes ver, todas las cadenas se muestran sin comillas dobles.

También puedes mostrar una línea en blanco:

```kotlin
println("Kotlin es un lenguaje de programación moderno.")
println() // imprime una línea en blanco
println("¡Es usado en todo el mundo!")
```

**Salida:**

```plaintext
Kotlin es un lenguaje de programación moderno.

¡Es usado en todo el mundo!
```

La función `print` muestra un valor y deja el cursor justo después, en la misma línea. Este fragmento muestra todas las cadenas seguidas:

```kotlin
print("Yo ")
print("sé ")
print("programar ")
print("en ")
print("Kotlin.")
```

**Salida:**

```plaintext
Yo sé programar en Kotlin.
```

### Impresión de números y caracteres

Con las funciones `println` y `print`, un programa puede imprimir no solo cadenas, sino también números y caracteres:

```kotlin
print(108)   // muestra un número
print('c')   // muestra un carácter
print("Q")   // muestra una cadena
println('3') // muestra un carácter que representa un dígito

print(22)
print('E')
print(8)
println('1')
```

**Salida:**

```plaintext
108cQ3
22E81
```

Al igual que con las cadenas, no aparecen comillas.

### Plantillas de cadena

Muchas veces querrás mostrar un mensaje que incluya el valor de una variable; por ejemplo, `¡Hola, Alicia!`, donde el nombre viene de una variable. Kotlin ofrece una forma muy cómoda de hacerlo: las **plantillas de cadena**. Dentro de una cadena, el símbolo `$` seguido del nombre de una variable inserta su valor:

```kotlin
val nombre = "Alicia"
println("¡Hola, $nombre!") // ¡Hola, Alicia!
```

También puedes insertar el resultado de una **expresión** (como las que viste en el capítulo de operadores). Para eso, encierra la expresión entre llaves, con `${...}`:

```kotlin
val a = 5
val b = 10
println("La suma de $a y $b es ${a + b}") // La suma de 5 y 10 es 15
```

La regla es sencilla: para una variable simple basta con `$variable`; para una expresión usa `${...}`.

> [!NOTE]
> Una **expresión** es un fragmento de código cuyo resultado es un valor; por ejemplo, `a + b` o `edad >= 18`.

¿Y si necesitas mostrar el propio símbolo `$` dentro del texto? Aquí tienes algunas formas de hacerlo:

```kotlin
val a = 20

// Hay un punto después de $, así que no se interpreta como variable
println("El precio es $a$.") // El precio es 20$.

// El segundo $ inserta la variable; el primero es un símbolo común
println("El precio es $$a.") // El precio es $20.

// \$ toma el símbolo $ de forma literal, sin su interpretación especial
println("El precio es \$a.") // El precio es $a.
```

Así, las plantillas de cadena permiten insertar de forma cómoda y rápida valores de variables y resultados de expresiones directamente dentro de un texto.

## Entrada estándar

La **entrada estándar** es un flujo de datos que se introduce en el programa. Por defecto, la entrada estándar obtiene los datos del teclado, pero también es posible obtenerlos de un archivo.

No todos los programas necesitan utilizar la entrada estándar, pero es probable que la uses con bastante frecuencia. Una forma típica de resolver problemas de programación es la siguiente:

- leer datos de la entrada estándar;
- procesar los datos para obtener un resultado;
- enviar el resultado a la salida estándar.

Antes de escribir programas que hagan algo útil, conviene entender cómo leer datos de la entrada estándar.

### Uso de `readln`

En Kotlin, para leer datos de la entrada estándar se utiliza la función `readln()`, que lee toda la línea como una cadena:

```kotlin
val line = readln()
```

La variable `line` es de tipo `String`, porque la expresión `readln()` devuelve un valor de ese tipo.

Si trabajas con versiones anteriores del lenguaje (antes de Kotlin 1.6), debes usar `readLine()!!` en lugar de `readln()`. Ambas hacen lo mismo, pero `readln()` es más corta y es la recomendada, así que úsala siempre que puedas:

```kotlin
val line = readLine()!! // antes de Kotlin 1.6
```

> [!NOTE]
> Probablemente te llamen la atención los signos de exclamación `!!`. Esta construcción garantiza al compilador que la entrada no está vacía. Hablaremos de ello con más detalle cuando veamos el manejo seguro de valores nulos.

A continuación, un programa sencillo que lee una línea de la entrada estándar y la envía a la salida estándar, usando una plantilla de cadena:

```kotlin
fun main() {
    val name = readln()
    println("Hola, $name")
}
```

Con esta entrada:

```plaintext
Kotlin
```

El resultado sería:

```plaintext
Hola, Kotlin
```

Ahora veamos cómo leer diferentes tipos de datos con `readln()`.

#### Leer valores `Int` y `Long`

A veces necesitamos obtener datos numéricos del usuario; por ejemplo, su edad o un año. Como `readln()` siempre devuelve un `String`, para trabajar con el valor como número debemos convertirlo con `toInt()` (tal como viste en el capítulo de tipos de datos):

```kotlin
println("Introduce cualquier número:")
val number = readln().toInt()
print("Has introducido el número: ")
print(number)
```

Con la entrada `56`, el resultado es:

```plaintext
Introduce cualquier número:
56
Has introducido el número: 56
```

En caso de que necesites procesar un número muy grande (por ejemplo, la cantidad de habitantes de un país), usa la función `toLong()`:

```kotlin
println("Escribe un número muy grande:")
val numeroGrande = readln().toLong()
print("Escribiste: ")
print(numeroGrande)
```

El resultado es:

```plaintext
Escribe un número muy grande:
10000000000
Escribiste: 10000000000
```

#### Leer valores `Double` y `Boolean`

¿Y si necesitas valores con decimales? Por ejemplo, un precio exacto o un promedio. En ese caso no sirven `toInt()` ni `toLong()`; usa la función `toDouble()`:

```kotlin
println("Escribe un número decimal:")
val number = readln().toDouble()
print("Ingresaste el número: ")
print(number)
```

La salida es:

```plaintext
Escribe un número decimal:
0.5673421
Ingresaste el número: 0.5673421
```

La misma lógica se aplica a los `Boolean`: usa la función `toBoolean()`:

```kotlin
println("¿Activar las notificaciones? Escribe true o false:")
val activado = readln().toBoolean()
print("Notificaciones activadas: ")
print(activado)
```

La salida es:

```plaintext
¿Activar las notificaciones? Escribe true o false:
true
Notificaciones activadas: true
```

### Entradas múltiples

¿Es posible recibir y procesar varias entradas? La respuesta es sí: basta con declarar varias variables, cada una con su propia llamada a `readln()`. Si los valores son de tipos distintos, el usuario debe pulsar *Intro* para separar cada uno.

```kotlin
val a = readln()
val b = readln().toInt()
val c = readln()
print(a)
print(" ")
print(b)
print(" ")
print(c)
```

Con esta entrada (un `String`, un `Int` y otro `String`, cada uno en su línea):

```plaintext
¡Has ganado
100
puntos!
```

El resultado es:

```plaintext
¡Has ganado 100 puntos!
```

Como ves, obtener varios valores de distintos tipos no es difícil: solo hay que declarar varias variables, asignarles la conversión adecuada de `readln()` y mostrarlas.

### Lectura de varios valores en una sola línea

Si necesitas leer dos valores escritos en una misma línea (separados por un espacio), puedes usar esta construcción:

```kotlin
val (a, b) = readln().split(" ")
println(a)
println(b)
```

Con esta entrada:

```plaintext
Hola, Kotlin
```

El resultado sería:

```plaintext
Hola,
Kotlin
```

Esta construcción divide la cadena por los espacios y reparte las palabras en las variables `a` y `b`.

> [!NOTE]
> Este ejemplo usa dos herramientas que todavía no hemos estudiado: `split()`, que parte una cadena en varios trozos, y la *desestructuración* (`val (a, b) = ...`), que reparte esos trozos en varias variables. Las veremos en detalle más adelante; por ahora, quédate con la idea de que es una forma cómoda de leer varios valores de una línea.

Del mismo modo, puedes leer más valores por línea. Solo recuerda que el último trozo recoge todo lo que quede:

```kotlin
val (a, b, c, d) = readln().split(" ")
println(a)
println(b)
println(c)
println(d)
```

Con esta entrada:

```plaintext
Que tengas un buen Kotlin
```

El resultado sería:

```plaintext
Que
tengas
un
buen Kotlin
```

> [!NOTE]
> Para tareas de lectura más avanzadas —leer palabra por palabra, usar delimitadores personalizados o comprobar si quedan más datos—, Kotlin también puede apoyarse en la clase `Scanner` de Java. Como es interoperabilidad con Java y requiere conceptos que veremos más adelante (`if` y excepciones), la tratamos aparte en el [Anexo B: Entrada estándar con `Scanner` de Java](appendix-java-scanner.md).

## Resumen

En este capítulo aprendiste a comunicar tu programa con el usuario:

- La **salida estándar** se muestra con `print` (sin salto de línea) y `println` (con salto de línea).
- Las **plantillas de cadena** insertan valores en un texto con `$variable` o `${expresión}`.
- La **entrada estándar** se lee con `readln()`, que devuelve un `String`.
- Para trabajar con otros tipos, conviertes lo leído con `toInt()`, `toDouble()`, `toBoolean()`, etc.
- Para un control más avanzado de la lectura, existe la clase `Scanner` de Java (Anexo B).
