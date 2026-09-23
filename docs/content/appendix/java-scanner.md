# Anexo B: De Java a Kotlin: equivalencias y `Scanner`

## Introducción

Este curso está pensado para programadores que ya conocen **Java** (o un lenguaje similar de la familia C/C++) y quieren aprender **Kotlin** y **Android moderno**. Aunque ambos lenguajes compilan a *bytecode* de la máquina virtual de Java (JVM) y son 100 % interoperables, Kotlin introduce muchas mejoras de diseño y sintaxis que eliminan el código repetitivo típico de Java.

Este anexo es una **guía rápida de equivalencias**: una tabla de referencia que puedes consultar cuando te preguntes *"¿cómo se escribía esto en Kotlin?"*, seguida de una explicación sobre cómo usar la clase `Scanner` de Java desde Kotlin cuando necesitas control fino sobre la entrada estándar.

## Tabla de equivalencias rápidas

### Variables y tipos básicos

| Concepto | Java | Kotlin |
|---|---|---|
| Variable de solo lectura | `final int x = 10;` | `val x = 10` (o `val x: Int = 10`) |
| Variable reasignable | `int x = 10; x = 20;` | `var x = 10; x = 20` |
| Inferencia de tipos | `var x = 10;` (desde Java 10) | `val x = 10` (estándar en Kotlin) |
| Entero | `int` / `Integer` | `Int` |
| Flotante / Doble | `float` / `double` | `Float` / `Double` |
| Booleano | `boolean` / `Boolean` | `Boolean` |
| Cadena | `String` | `String` |
| Sin valor de retorno | `void` | `Unit` (generalmente se omite) |
| Imprimir en consola | `System.out.println("Hola");` | `println("Hola")` |
| Plantillas de cadena | `"Hola, " + nombre + "!"` | `"Hola, $nombre!"` o `"Total: ${a + b}"` |

En Kotlin no existe la distinción entre tipos primitivos (`int`) y clases envoltorio (`Integer`): el compilador de Kotlin optimiza automáticamente `Int` a primitivo cuando es posible y a objeto cuando es necesario.

### Funciones y métodos

| Concepto | Java | Kotlin |
|---|---|---|
| Método simple | `int sumar(int a, int b) { return a + b; }` | `fun sumar(a: Int, b: Int): Int = a + b` |
| Método con cuerpo | `int mayor(int a, int b) { if (a > b) return a; else return b; }` | `fun mayor(a: Int, b: Int): Int { return if (a > b) a else b }` |
| Parámetros por defecto | *Sobrecarga de métodos* | `fun saludar(nombre: String = "Mundo") { ... }` |
| Parámetros nombrados | *No disponible* | `saludar(nombre = "Ana")` |
| Función de nivel superior | *Debe vivir en una clase pública* | `fun util() { ... }` (directo en el archivo `.kt`) |

### Clases, propiedades y constructores

| Concepto | Java | Kotlin |
|---|---|---|
| Clase básica | `public class Persona { ... }` | `class Persona` (pública y final por defecto) |
| Clase que se puede heredar | `public class Base { ... }` | `open class Base` (cerrada a herencia por defecto) |
| Constructor con propiedades | Constructor + campos privados + getters/setters | `class Persona(val nombre: String, var edad: Int)` |
| Clase de datos (DTO) | `record Persona(String nombre, int edad) {}` (Java 14+) | `data class Persona(val nombre: String, val edad: Int)` |
| Singleton | *Patrón Singleton manual* | `object MiSingleton { ... }` |
| Miembros estáticos | `public static void metodo() { ... }` | `companion object { fun metodo() { ... } }` |
| Instanciar un objeto | `new Persona("Ana", 25)` | `Persona("Ana", 25)` (sin palabra clave `new`) |

### *Null safety*

| Concepto | Java | Kotlin |
|---|---|---|
| Variable que puede ser nula | `String s = null;` (cualquier tipo no primitivo) | `val s: String? = null` (signo `?` explícito) |
| Variable que NUNCA es nula | *Anotaciones opcionales (`@NonNull`)* | `val s: String = "hola"` (garantizado por compilador) |
| Llamada segura | `if (s != null) s.length();` o `Optional` | `s?.length` |
| Operador Elvis / valor por defecto | `s != null ? s : "defecto"` | `s ?: "defecto"` |
| Aserción de no nulidad | *Casting / `Objects.requireNonNull`* | `s!!.length` (¡usar con cuidado!) |

### Estructuras de control

| Concepto | Java | Kotlin |
|---|---|---|
| `if` como expresión | `int max = (a > b) ? a : b;` (operador ternario) | `val max = if (a > b) a else b` |
| `switch` vs. `when` | `switch (x) { case 1: ...; break; default: ...; }` | `when (x) { 1 -> ...; else -> ... }` |
| Bucle `for` por rango | `for (int i = 0; i < 10; i++) { ... }` | `for (i in 0 until 10) { ... }` |
| Bucle `for` inclusivo | `for (int i = 1; i <= 5; i++) { ... }` | `for (i in 1..5) { ... }` |
| Bucle `for-each` | `for (String s : lista) { ... }` | `for (s in lista) { ... }` |
| Comprobar tipo y *cast* | `if (obj instanceof String) { String s = (String) obj; }` | `if (obj is String) { println(obj.length) }` (*smart cast*) |

### Colecciones

| Concepto | Java | Kotlin |
|---|---|---|
| Lista de solo lectura | `List.of("a", "b")` (Java 9+) | `listOf("a", "b")` |
| Lista modificable | `new ArrayList<>(List.of("a", "b"))` | `mutableListOf("a", "b")` |
| Mapa de solo lectura | `Map.of("k", "v")` | `mapOf("k" to "v")` |
| Operaciones funcionales | `lista.stream().map(...).collect(Collectors.toList())` | `lista.map { ... }` (directo, sin Stream) |
| Filtrado | `lista.stream().filter(...).collect(Collectors.toList())` | `lista.filter { ... }` |

---

## Entrada estándar con `Scanner` de Java

En el capítulo 6 aprendiste a leer datos de la consola con `readln()`, que es la forma estándar y directa en Kotlin. Para la gran mayoría de los casos es suficiente.

Sin embargo, a veces necesitas herramientas más específicas de lectura: leer palabra por palabra, buscar números de un tipo concreto en un flujo de texto, o cambiar el delimitador. Como Kotlin es **100 % interoperable con Java**, puedes usar la clase estándar `java.util.Scanner` sin ninguna librería externa.

### ¿Cómo se usa `Scanner` en Kotlin?

Para usarlo, impórtalo al principio del archivo:

```kotlin
import java.util.Scanner
```

Luego, crea una variable `Scanner` conectada a la entrada estándar (`System.`in``):

```kotlin
val scanner = Scanner(System.`in`)
```

> [!NOTE]Nota sobre las comillas invertidas
> En Kotlin, `in` es una palabra reservada (se usa en bucles `for (x in rango)` y comprobaciones de pertenencia). Para acceder al campo estático `System.in` de Java, Kotlin requiere envolverlo en comillas invertidas: ``System.`in` ``.

### Leer datos con `Scanner`

```kotlin
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("Introduce dos números enteros:")
    val num1 = scanner.nextInt() // lee el primer entero
    val num2 = scanner.nextInt() // lee el segundo entero

    println("En orden inverso: $num2, $num1")

    scanner.close() // conviene cerrarlo al terminar
}
```

Métodos principales de lectura:
- `scanner.nextLine()`: lee una línea completa como `String`.
- `scanner.next()`: lee una sola palabra (hasta el próximo espacio en blanco).
- `scanner.nextInt()` / `scanner.nextDouble()` / `scanner.nextBoolean()`: lee el dato directamente convertido al tipo correspondiente.

### Delimitador personalizado

Por defecto, `Scanner` separa los datos por espacios en blanco. Con `useDelimiter()` puedes cambiar ese criterio, incluso al leer de una cadena fija en lugar del teclado:

```kotlin
import java.util.Scanner

fun main() {
    val scanner = Scanner("123_456_789")
    scanner.useDelimiter("_")

    while (scanner.hasNextInt()) {
        println(scanner.nextInt())
    }
    scanner.close()
}
```

Salida:
```text
123
456
789
```

### Comprobar antes de leer: `hasNext`

Si intentas leer un dato cuando el flujo ya no tiene más elementos (o cuando el elemento siguiente no es del tipo esperado, como pedir `nextInt()` ante una letra), `Scanner` lanza una excepción (`NoSuchElementException` o `InputMismatchException`).

Para evitarlo, comprueba primero con la familia `hasNext`:

```kotlin
val scanner = Scanner("¡Hola, Kotlin!")

if (scanner.hasNext()) {
    println(scanner.next()) // ¡Hola,
}
if (scanner.hasNext()) {
    println(scanner.next()) // Kotlin!
}
if (scanner.hasNext()) {
    println(scanner.next()) // no se ejecuta: ya no quedan palabras
}
scanner.close()
```

Variantes útiles:
- `hasNext()`: ¿queda alguna palabra?
- `hasNextInt()`: ¿el próximo dato es un entero válido?
- `hasNextDouble()`: ¿el próximo dato es un número decimal?
- `hasNextLine()`: ¿queda otra línea completa?

## Resumen

- **Kotlin reduce el código repetitivo de Java**: no requiere `new`, infiere tipos, genera *getters* y *setters* automáticamente, y las clases/métodos son públicos y cerrados por defecto.
- **Null safety en el compilador**: los tipos no anulables por defecto (`String`) eliminan los `NullPointerException` imprevistos; los anulables (`String?`) se manejan con llamadas seguras `?.` o el operador Elvis `?:`.
- **Colecciones funcionales directas**: `map`, `filter` y `forEach` operan directamente sobre listas, sin necesidad del `Stream` de Java.
- **Interoperabilidad total**: cualquier clase de Java —como `Scanner`— puede usarse en Kotlin simplemente importándola.
