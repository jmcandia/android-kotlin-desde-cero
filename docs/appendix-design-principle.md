# Anexo: Principios de diseño (SOLID, DRY y KISS)

## Introducción

A medida que tus programas crecen, escribir código que *funcione* deja de ser suficiente: también quieres que sea fácil de leer, de mantener y de modificar. Con los años, la comunidad de desarrollo ha destilado varios principios que ayudan a lograrlo.

En este anexo veremos los más conocidos y útiles: dos principios generales, **DRY** y **KISS**, y el conjunto **SOLID**, cinco principios para organizar el código orientado a objetos. Ninguno es una regla rígida: son guías de sentido común que te acompañarán durante todo el curso.

Los mencionamos por primera vez en el capítulo de funciones, porque las funciones son la primera herramienta que tienes para ponerlos en práctica.

## DRY: no te repitas

El problema: si copias y pegas el mismo bloque de código en varios sitios, el día que necesites corregirlo o cambiarlo tendrás que hacerlo en todos ellos, y es fácil olvidar alguno.

El principio **DRY** (*Don't Repeat Yourself*, "no te repitas") resuelve esto con una idea simple: **cada pieza de lógica debería vivir en un solo lugar**. Las funciones son tu principal herramienta para lograrlo: extraes la lógica repetida a una función y la invocas donde la necesites.

Código repetido:

```kotlin
println("Bienvenido, Ana. Que tengas un buen día.")
// ... más adelante, en otra parte del programa ...
println("Bienvenido, Diego. Que tengas un buen día.")
```

Aplicando DRY:

```kotlin
fun darBienvenida(nombre: String) {
    println("Bienvenido, $nombre. Que tengas un buen día.")
}

darBienvenida("Ana")
darBienvenida("Diego")
```

Ahora el mensaje existe en un único lugar. Si algún día cambias el saludo, lo cambias una sola vez y el cambio se refleja en todas partes.

La misma idea aplica a las **clases**. Si dos clases repiten la misma lógica:

```kotlin
class Empleado(val nombre: String) {
    fun saludo() = "Hola, soy $nombre"
}

class Cliente(val nombre: String) {
    fun saludo() = "Hola, soy $nombre" // ¡lógica duplicada!
}
```

puedes llevar esa lógica común a una clase base y hacer que ambas la hereden:

```kotlin
open class Persona(val nombre: String) {
    fun saludo() = "Hola, soy $nombre"
}

class Empleado(nombre: String) : Persona(nombre)
class Cliente(nombre: String) : Persona(nombre)
```

Ahora `saludo()` vive en un solo lugar.

> [!NOTE]
> DRY no significa "nunca escribas dos líneas parecidas". Se trata de no duplicar *lógica* o *conocimiento* importante. A veces, un poco de repetición trivial es más clara que una abstracción forzada.

## KISS: mantenlo simple

El problema: es tentador escribir soluciones "ingeniosas" o llenas de opciones "por si acaso", pero ese código termina siendo difícil de leer, de corregir y de explicar.

El principio **KISS** (*Keep It Simple, Stupid*, "mantenlo simple") aconseja **preferir siempre la solución más sencilla que resuelva el problema** y desconfiar de la complejidad innecesaria. En la práctica, significa cosas como: no agregar abstracciones que aún no necesitas, elegir nombres claros y resistir la tentación de complicar algo que se resuelve de forma directa. Un código simple es más fácil de mantener y de entender para otra persona (o para tu yo del futuro).

Veámoslo con un caso típico. Supongamos que queremos una función que indique si un número es par. Una primera versión, más enrevesada de lo necesario:

```kotlin
fun esPar(numero: Int): Boolean {
    if (numero % 2 == 0) {
        return true
    } else {
        return false
    }
}
```

Funciona, pero da un rodeo: la condición `numero % 2 == 0` ya es un valor `Boolean` (`true` o `false`), así que podemos devolverla directamente. Aplicando KISS:

```kotlin
fun esPar(numero: Int) = numero % 2 == 0
```

El resultado es idéntico, pero la segunda versión es más corta, más clara y más fácil de leer.

## SOLID

SOLID es un acrónimo que agrupa **cinco principios** para organizar el código orientado a objetos —el que trabaja con **clases** e **interfaces**— de manera que sea fácil de mantener y de ampliar. Cada letra corresponde a un principio: **S**RP, **O**CP, **L**SP, **I**SP y **D**IP. Veamos cada uno partiendo del problema que resuelve.

### SRP: responsabilidad única

El problema: una función (o una clase) que hace demasiadas cosas es difícil de entender, de probar y de reutilizar; y cualquier cambio en una de sus tareas puede romper las otras sin querer.

El principio **SRP** (*Single Responsibility Principle*, "responsabilidad única") dice que cada unidad de código debería tener **una sola responsabilidad**, es decir, una única razón para cambiar.

Una función que hace de todo:

```kotlin
fun procesarPedido() {
    // valida los datos
    // calcula el total
    // guarda el pedido
    // envía el correo de confirmación
}
```

Separada por responsabilidades:

```kotlin
fun validarPedido() { /* ... */ }
fun calcularTotal() { /* ... */ }
fun guardarPedido() { /* ... */ }
fun enviarConfirmacion() { /* ... */ }
```

En la segunda versión, cada función tiene un propósito claro. Si mañana cambia la forma de calcular el total, solo tocas `calcularTotal`, sin arriesgarte a romper el envío del correo.

El mismo principio se aplica a las **clases**. En lugar de una clase `Pedido` que se encargue de todo —calcular su total, guardarse en la base de datos y enviar correos—, cada responsabilidad vive en su propia clase:

```kotlin
class Pedido {
    fun calcularTotal() { /* ... */ } // solo lo propio del pedido
}

class RepositorioPedido {
    fun guardar(pedido: Pedido) { /* ... */ } // se ocupa de la persistencia
}

class ServicioNotificacion {
    fun enviarConfirmacion(pedido: Pedido) { /* ... */ } // se ocupa de avisar
}
```

Así, si cambia la forma de guardar los pedidos, solo tocas `RepositorioPedido`, sin afectar al cálculo del total ni al envío de las notificaciones.

### OCP: abierto a la extensión

El problema: tienes una función con un `when` enorme que decide algo según un tipo (por ejemplo, el área según la figura). Cada vez que aparece un caso nuevo, debes abrir esa función y modificarla, arriesgándote a romper lo que ya funcionaba.

El principio **OCP** (*Open/Closed Principle*, "abierto/cerrado") dice que el código debería estar **abierto a la extensión, pero cerrado a la modificación**: deberías poder añadir comportamiento nuevo **sin** tocar el código que ya funciona.

¿Cómo se logra? Definiendo una abstracción —por ejemplo, algo que represente "una figura que sabe calcular su área"— de modo que cada figura nueva aporte su propio cálculo. Así, agregar una figura no obliga a modificar el código anterior: el sistema queda abierto a crecer, pero cerrado a que lo alteren.

Con un `when`, cada figura nueva obliga a modificar la misma función:

```kotlin
fun area(tipo: String, medida: Double) = when (tipo) {
    "cuadrado" -> medida * medida
    "circulo" -> 3.1416 * medida * medida
    else -> 0.0
}
```

Con OCP, defines una interfaz y cada figura la implementa por su cuenta:

```kotlin
interface Figura {
    fun area(): Double
}

class Cuadrado(val lado: Double) : Figura {
    override fun area() = lado * lado
}

class Circulo(val radio: Double) : Figura {
    override fun area() = 3.1416 * radio * radio
}
```

Para agregar un triángulo, creas una clase nueva `Triangulo : Figura` sin tocar ni una línea de las anteriores.

### LSP: sustitución de Liskov

El problema: creas una clase hija para reutilizar a su clase madre, pero la hija no se comporta como se espera de la madre, y el programa falla al usarla en su lugar.

El principio **LSP** (*Liskov Substitution Principle*, "sustitución de Liskov") dice que deberías poder usar un objeto de la clase hija **en cualquier lugar donde se espere la madre**, sin que el programa se comporte de forma incorrecta.

El ejemplo clásico: una clase `Ave` con un método `volar()` y una hija `Pingüino`. Como un pingüino no vuela, al usarlo donde el programa espera un `Ave` capaz de volar, algo se romperá. La lección es que una subclase no debe romper las expectativas de su clase base; si no puede cumplirlas, quizá esa herencia no es la adecuada.

Esta jerarquía **viola** el principio:

```kotlin
open class Ave {
    open fun volar() {
        println("Estoy volando")
    }
}

class Pinguino : Ave() {
    override fun volar() {
        throw Exception("¡Un pingüino no puede volar!") // rompe la expectativa
    }
}
```

Una solución es no obligar a todas las aves a volar. Separamos la capacidad de volar en su propia rama, y `Pinguino` simplemente no la hereda:

```kotlin
open class Ave { /* comportamiento común: comer, caminar...*/ }

open class AveVoladora : Ave() {
    fun volar() {
        println("Estoy volando")
    }
}

class Aguila : AveVoladora() // vuela, y está bien
class Pinguino : Ave()       // no vuela, y también está bien
```

### ISP: segregación de interfaces

El problema: una interfaz demasiado grande obliga a las clases a implementar métodos que no necesitan.

El principio **ISP** (*Interface Segregation Principle*, "segregación de interfaces") recomienda tener **varias interfaces pequeñas y específicas** en lugar de una sola gigante.

Imagina una interfaz `MáquinaDeOficina` con los métodos `imprimir()`, `escanear()` y `enviarFax()`. Una impresora sencilla, que solo imprime, quedaría obligada a implementar también `escanear()` y `enviarFax()`, aunque no haga ninguna de las dos cosas. Siguiendo ISP, divides esa interfaz en otras más pequeñas (`Impresora`, `Escáner`, `Fax`), y cada aparato implementa solo las que le corresponden.

Una sola interfaz que lo abarca todo obliga a implementar métodos que sobran:

```kotlin
interface MaquinaDeOficina {
    fun imprimir()
    fun escanear()
    fun enviarFax()
}

class ImpresoraBasica : MaquinaDeOficina {
    override fun imprimir() { /* ... */ }
    override fun escanear() { /* no hace nada, pero está obligada */ }
    override fun enviarFax() { /* no hace nada, pero está obligada */ }
}
```

Con interfaces pequeñas, cada clase implementa solo lo que usa:

```kotlin
interface Impresora { fun imprimir() }
interface Escaner { fun escanear() }
interface Fax { fun enviarFax() }

class ImpresoraBasica : Impresora {
    override fun imprimir() { /* ... */ }
}
```

Una máquina multifunción, en cambio, puede implementar las tres: `class Multifuncion : Impresora, Escaner, Fax`.

### DIP: inversión de dependencias

El problema: si tu lógica principal depende directamente de un detalle concreto (por ejemplo, una base de datos específica), cambiar ese detalle te obliga a modificar la lógica, y probarla se vuelve difícil.

El principio **DIP** (*Dependency Inversion Principle*, "inversión de dependencias") dice que los componentes importantes no deberían depender de detalles concretos, sino de **abstracciones**.

En la práctica, tu lógica de negocio dependería de una interfaz que describa qué operaciones necesita (algo como un "repositorio"), no de una base de datos concreta. Así puedes cambiar la base de datos —o usar una versión falsa para las pruebas— sin tocar la lógica. Este principio es la base de la **inyección de dependencias**, una técnica que usaremos al construir la arquitectura de la aplicación con el patrón MVVM.

Sin DIP, la lógica depende directamente de una base de datos concreta:

```kotlin
class BaseDeDatosMySQL {
    fun guardar(dato: String) { /* ... */ }
}

class ServicioUsuario {
    private val db = BaseDeDatosMySQL() // dependencia concreta
    fun registrar(nombre: String) {
        db.guardar(nombre)
    }
}
```

Con DIP, la lógica depende de una abstracción, que recibe desde fuera:

```kotlin
interface Repositorio {
    fun guardar(dato: String)
}

class ServicioUsuario(private val repositorio: Repositorio) {
    fun registrar(nombre: String) {
        repositorio.guardar(nombre)
    }
}
```

Ahora `ServicioUsuario` no sabe (ni le importa) si el repositorio guarda en MySQL, en un archivo o en memoria. Fíjate en que el repositorio se recibe por el constructor: eso, precisamente, es **inyección de dependencias**.

## En resumen

Principios generales:

- **DRY** — no repitas la misma lógica; extráela a una función.
- **KISS** — prefiere la solución más simple que funcione.

Principios **SOLID** (diseño orientado a objetos):

- **SRP** — una sola responsabilidad por unidad de código.
- **OCP** — abierto a la extensión, cerrado a la modificación.
- **LSP** — una subclase debe poder sustituir a su clase base sin romper nada.
- **ISP** — muchas interfaces pequeñas y específicas, en vez de una gigante.
- **DIP** — depende de abstracciones, no de detalles concretos.

Estos principios se refuerzan entre sí y te acompañarán durante todo el curso. Volverás a encontrarte con varios de ellos —en especial SRP y DIP— cuando trabajemos con clases y con la arquitectura de la aplicación, ya que son la base de un código bien organizado.
