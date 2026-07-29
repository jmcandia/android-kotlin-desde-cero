# Capítulo 5: Operadores

## Introducción

En los capítulos anteriores aprendiste a declarar variables y conociste los distintos tipos de datos que ofrece Kotlin. Pero un programa no se limita a guardar valores: los **combina**, los **calcula** y los **compara**. Para todo eso existen los **operadores**.

Un operador es un símbolo que realiza una operación sobre uno o más valores. Por ejemplo, el símbolo `+` suma dos números, y el símbolo `>` indica si un número es mayor que otro. A los valores sobre los que actúa un operador se les llama **operandos**.

En este capítulo veremos, paso a paso, tres grandes familias de operadores: los **aritméticos** (para hacer cálculos), los de **comparación** (para comparar valores) y los **lógicos** (para combinar condiciones). Al terminar, tendrás las piezas necesarias para escribir **expresiones**, que serán la base de las estructuras de control que estudiaremos más adelante.

## Operadores aritméticos

Muchos programas necesitan hacer cuentas: sumar los productos de un carrito, calcular un promedio, repartir una cantidad. Para eso están los **operadores aritméticos**, los mismos que ya conoces de las matemáticas:

| Operador | Nombre | Ejemplo | Resultado |
| :---: | :--- | :--- | :--- |
| `+` | suma | `7 + 3` | `10` |
| `-` | resta | `7 - 3` | `4` |
| `*` | multiplicación | `7 * 3` | `21` |
| `/` | división | `7 / 3` | `2` |
| `%` | resto (módulo) | `7 % 3` | `1` |

Veámoslos aplicados a variables:

```kotlin
val precio = 1200
val cantidad = 3

val total = precio * cantidad
println(total) // 3600
```

El signo `-` también sirve para indicar un número negativo (lo que se llama *menos unario*):

```kotlin
val temperatura = -8
val bajoCero = -temperatura
println(bajoCero) // 8
```

Dos de estos operadores merecen una explicación aparte: la división y el módulo.

### División entera y división decimal

Aquí hay un detalle que sorprende a quien recién empieza. Si divides dos **números enteros**, el resultado también es un número entero, y la parte decimal simplemente **se descarta**:

```kotlin
val resultado = 7 / 2
println(resultado) // 3, ¡no 3.5!
```

Esto ocurre porque ambos operandos son `Int`, así que Kotlin calcula una **división entera**. Si necesitas el resultado con decimales, al menos uno de los operandos debe ser un número decimal (`Double`):

```kotlin
val a = 7.0 / 2   // 3.5
val b = 7 / 2.0   // 3.5
println(a)        // 3.5
println(b)        // 3.5
```

Si los valores están en variables `Int`, puedes convertir uno a `Double` (como viste en el capítulo anterior):

```kotlin
val puntos = 7
val jugadores = 2
val promedio = puntos.toDouble() / jugadores
println(promedio) // 3.5
```

> [!WARNING]
> Dividir un número entero por cero (`7 / 0`) detiene el programa con un error. Antes de dividir, asegúrate de que el divisor no pueda ser cero.

### El operador módulo (`%`)

El operador `%` devuelve el **resto** de una división entera. Por ejemplo, `7 % 3` es `1`, porque 7 dividido por 3 da 2 con un resto de 1:

```kotlin
println(10 % 3) // 1
println(10 % 5) // 0
println(9 % 2)  // 1
```

Aunque parezca un detalle menor, el módulo es muy útil. Un uso clásico es saber si un número es **par**: un número es par cuando el resto de dividirlo por 2 es cero.

```kotlin
val numero = 8
val esPar = numero % 2 == 0
println(esPar) // true
```

(No te preocupes por el `==`: lo veremos en unos minutos, cuando lleguemos a los operadores de comparación.)

### Orden de las operaciones

Cuando una expresión combina varios operadores, Kotlin respeta el mismo orden que en matemáticas: primero `*`, `/` y `%`, y después `+` y `-`.

```kotlin
val resultado = 2 + 3 * 4
println(resultado) // 14, no 20 (primero 3 * 4, luego + 2)
```

Si quieres cambiar ese orden, o simplemente dejar más claras tus intenciones, usa **paréntesis**:

```kotlin
val resultado = (2 + 3) * 4
println(resultado) // 20
```

> [!TIP]
> Ante la duda, usa paréntesis. No cuesta nada y hacen que la expresión se lea sin ambigüedades, tanto para ti como para quien lea tu código más adelante.

## Operadores de asignación

Ya conoces el operador de asignación `=`, que guarda un valor en una variable:

```kotlin
var contador = 0
```

A partir de él, Kotlin ofrece dos comodidades muy frecuentes: la asignación compuesta y los operadores de incremento y decremento. Ambas trabajan sobre variables `var`, porque modifican el valor guardado.

### Asignación compuesta

Es muy común querer actualizar una variable usando su propio valor. Por ejemplo, aumentar un contador:

```kotlin
var contador = 0
contador = contador + 1
println(contador) // 1
```

Escribir `contador = contador + 1` funciona, pero es repetitivo. Kotlin ofrece una forma más breve, la **asignación compuesta**, que combina una operación aritmética con la asignación:

```kotlin
var contador = 0
contador += 1  // equivale a: contador = contador + 1
println(contador) // 1
```

Existe una versión para cada operador aritmético:

| Operador | Ejemplo | Equivale a |
| :---: | :--- | :--- |
| `+=` | `x += 5` | `x = x + 5` |
| `-=` | `x -= 5` | `x = x - 5` |
| `*=` | `x *= 5` | `x = x * 5` |
| `/=` | `x /= 5` | `x = x / 5` |
| `%=` | `x %= 5` | `x = x % 5` |

### Incremento y decremento

Sumar o restar 1 es tan habitual (contar elementos, avanzar de página, quitar una vida) que Kotlin le dedica dos operadores propios: `++` (incremento) suma 1, y `--` (decremento) resta 1.

```kotlin
var vidas = 3
vidas--          // ahora vale 2
println(vidas)   // 2

var pagina = 1
pagina++         // ahora vale 2
println(pagina)  // 2
```

## Operadores de comparación

A menudo necesitas **comparar** dos valores: ¿la edad es mayor o igual a 18? ¿el stock llegó a cero? ¿estos dos textos son iguales? Los **operadores de comparación** responden ese tipo de preguntas, y el resultado siempre es un valor `Boolean` (`true` o `false`), ese tipo que conociste en el capítulo anterior.

| Operador | Significado | Ejemplo | Resultado |
| :---: | :--- | :--- | :--- |
| `==` | igual a | `5 == 5` | `true` |
| `!=` | distinto de | `5 != 3` | `true` |
| `>` | mayor que | `5 > 3` | `true` |
| `<` | menor que | `5 < 3` | `false` |
| `>=` | mayor o igual que | `5 >= 5` | `true` |
| `<=` | menor o igual que | `3 <= 5` | `true` |

Fíjate en que la igualdad se escribe con **dos** signos igual (`==`). Un solo `=` es asignación (guardar un valor); dos `==` es comparación (preguntar si son iguales). Confundirlos es un error muy común al principio.

Veámoslos guardando el resultado en una variable:

```kotlin
val edad = 20
val esMayorDeEdad = edad >= 18
println(esMayorDeEdad) // true

val stock = 0
val hayStock = stock > 0
println(hayStock) // false
```

Estos operadores también funcionan con texto para saber si dos cadenas son iguales:

```kotlin
val clave = "kotlin"
val coincide = clave == "kotlin"
println(coincide) // true
```

## Operadores lógicos

A veces una sola comparación no basta. Quieres saber si se cumplen **dos condiciones a la vez** (que la persona sea mayor de edad **y** tenga entrada), o si se cumple **al menos una**, o si una condición **no** se cumple. Para combinar valores `Boolean` existen los **operadores lógicos**:

- `&&` (**Y** lógico): es `true` solo si **ambos** operandos son `true`.
- `||` (**O** lógico): es `true` si **al menos uno** de los operandos es `true`.
- `!` (**NO** lógico): invierte el valor, convierte `true` en `false` y viceversa.

```kotlin
val esMayorDeEdad = true
val tieneEntrada = true
val puedeEntrar = esMayorDeEdad && tieneEntrada
println(puedeEntrar) // true

val esFinDeSemana = false
val esFeriado = true
val hayDescanso = esFinDeSemana || esFeriado
println(hayDescanso) // true

val disponible = false
println(!disponible) // true
```

### Tablas de verdad

Una **tabla de verdad** muestra el resultado de un operador lógico para todas las combinaciones posibles de sus operandos.

Operador `&&` (verdadero solo si ambos lo son):

| `a` | `b` | `a && b` |
| :---: | :---: | :---: |
| `true` | `true` | `true` |
| `true` | `false` | `false` |
| `false` | `true` | `false` |
| `false` | `false` | `false` |

Operador `||` (verdadero si al menos uno lo es):

| `a` | `b` | `a \|\| b` |
| :---: | :---: | :---: |
| `true` | `true` | `true` |
| `true` | `false` | `true` |
| `false` | `true` | `true` |
| `false` | `false` | `false` |

Operador `!` (invierte el valor):

| `a` | `!a` |
| :---: | :---: |
| `true` | `false` |
| `false` | `true` |

### Evaluación en cortocircuito

Los operadores `&&` y `||` son "perezosos": evalúan solo lo necesario. En `a && b`, si `a` ya es `false`, el resultado será `false` sin importar `b`, así que Kotlin ni siquiera evalúa `b`. Del mismo modo, en `a || b`, si `a` ya es `true`, no evalúa `b`. A esto se le llama **evaluación en cortocircuito**, y más adelante te ayudará a escribir condiciones más seguras y eficientes.

## Precedencia de los operadores

Cuando una expresión mezcla operadores de distintas familias, Kotlin los evalúa en un orden establecido (su **precedencia**), de mayor a menor prioridad:

| Prioridad | Operadores |
| :---: | :--- |
| 1 (más alta) | `!`, `-` (menos unario) |
| 2 | `*`, `/`, `%` |
| 3 | `+`, `-` |
| 4 | `<`, `<=`, `>`, `>=` |
| 5 | `==`, `!=` |
| 6 | `&&` |
| 7 | `\|\|` |
| 8 (más baja) | `=`, `+=`, `-=`, `*=`, `/=`, `%=` |

Así, en una expresión como `2 + 3 > 4 && 1 < 5`, primero se resuelven las operaciones aritméticas, luego las comparaciones y, por último, el `&&`. Pero no necesitas memorizar toda esta tabla: en la práctica, basta con recordar la regla de oro.

> [!TIP]
> No confíes en la memoria para casos complejos: usa paréntesis para agrupar las partes de una expresión. El código queda más claro y evitas errores difíciles de detectar.

## Resumen

En este capítulo conociste las tres grandes familias de operadores de Kotlin:

- Los **aritméticos** (`+`, `-`, `*`, `/`, `%`) hacen cálculos. Recuerda que la división entre dos enteros descarta los decimales.
- Los de **asignación** (`=`, `+=`, `++`, `--`…) actualizan el valor de una variable de forma breve.
- Los de **comparación** (`==`, `!=`, `>`, `<`, `>=`, `<=`) comparan valores y producen un `Boolean`.
- Los **lógicos** (`&&`, `||`, `!`) combinan valores `Boolean` para expresar condiciones más ricas.

Los operadores de comparación y lógicos serán especialmente importantes en el próximo tema, porque son la base de las **estructuras de control** (`if`, `when`), que le permiten a un programa **tomar decisiones**.
