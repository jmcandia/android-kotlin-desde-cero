# Capítulo 19: `object`, `companion object` y singletons

- [Introducción](#introducción)
- [El patrón singleton](#el-patrón-singleton)
- [`object`: singletons en Kotlin](#object-singletons-en-kotlin)
- [`companion object`](#companion-object)
- [`object` frente a `companion object`](#object-frente-a-companion-object)
- [Resumen](#resumen)

---

## Introducción

Hasta ahora, a partir de una clase creabas **muchos** objetos: varias personas, varios usuarios. Pero a veces necesitas justo lo contrario: **un único objeto** que exista para todo el programa. Piensa en la configuración de una aplicación, un registro de eventos (*logger*) o un gestor de conexión: tiene sentido que haya **uno solo**, compartido por todos.

También ocurre que quieres definir algo ligado a una **clase** en sí, no a sus objetos: por ejemplo, una constante propia de la clase o una función que sirva para crear instancias.

Kotlin resuelve ambas necesidades con la palabra clave `object` y con el `companion object`. En este capítulo verás qué es el patrón *singleton*, cómo crearlo con `object` y cómo asociar miembros a una clase con `companion object`. Si vienes de Java, esta es la respuesta de Kotlin a la palabra clave `static`.

## El patrón singleton

Un **singleton** ("único") es un patrón de diseño muy común: una clase de la que existe **una sola instancia** en todo el programa, accesible desde cualquier parte.

Se usa cuando tiene sentido que haya un único punto de referencia para algo: la configuración global, un registro compartido, un contador central. En lenguajes como Java, implementarlo a mano requiere cierto cuidado (un constructor privado, una variable estática, etc.). Kotlin lo integra en el propio lenguaje, y por eso crearlo es trivial.

## `object`: singletons en Kotlin

Para declarar un singleton, usas la palabra clave `object` en lugar de `class`:

```kotlin
object Configuracion {
    var idioma = "es"
    val version = "1.0"

    fun mostrar() {
        println("Idioma: $idioma, versión: $version")
    }
}
```

La diferencia clave con una clase es que **no creas** objetos de `Configuracion`: no hay constructor ni se usa `Configuracion()`. El objeto simplemente **existe** (Kotlin lo crea automáticamente la primera vez que lo usas), y accedes a sus miembros directamente a través de su nombre:

```kotlin
println(Configuracion.idioma) // es
Configuracion.idioma = "en"
Configuracion.mostrar()       // Idioma: en, versión: 1.0
```

Como hay una sola instancia, ese cambio de idioma se ve desde cualquier lugar del programa que use `Configuracion`. Un `object` puede tener propiedades y funciones, igual que una clase; lo único que no tiene es constructor, porque no se instancia.

## `companion object`

Ahora, el otro caso: quieres algo ligado a una **clase**, no a sus objetos individuales.

Por ejemplo, imagina una clase `Usuario` y quieres:

- una **constante** propia de la clase, como la edad mínima permitida;
- una **función de fábrica** que cree usuarios de cierta forma.

Estos miembros no pertenecen a un usuario concreto, sino a la clase `Usuario` en general. Para eso, Kotlin ofrece el **`companion object`** ("objeto acompañante"): un objeto único que va **dentro** de la clase y se asocia a ella:

```kotlin
class Usuario(val nombre: String, val edad: Int) {
    companion object {
        const val EDAD_MINIMA = 18

        fun crearInvitado() = Usuario("Invitado", EDAD_MINIMA)
    }
}
```

Accedes a sus miembros a través del **nombre de la clase**, sin crear ningún objeto:

```kotlin
println(Usuario.EDAD_MINIMA)           // 18
val invitado = Usuario.crearInvitado() // crea un Usuario usando la fábrica
println(invitado.nombre)               // Invitado
```

> [!NOTE]
> Si vienes de Java, el `companion object` cumple el papel de los miembros `static`: constantes y funciones que pertenecen a la clase y no a sus instancias. Kotlin no tiene la palabra clave `static`; usa el `companion object` en su lugar.

## `object` frente a `companion object`

Ambos crean un único objeto, pero se usan en situaciones distintas:

- Un **`object`** es un singleton **independiente**, que existe por sí mismo (una configuración, un *logger*). Se accede por su propio nombre.
- Un **`companion object`** vive **dentro de una clase** y agrupa lo que pertenece a la clase en general (constantes, funciones de fábrica). Se accede a través del nombre de la clase.

Una pista: si lo que defines tiene sentido por sí solo, usa `object`; si tiene sentido solo en relación con una clase concreta, usa un `companion object` dentro de ella.

## Resumen

En este capítulo aprendiste a crear objetos únicos:

- Un **singleton** es una clase con una única instancia, compartida por todo el programa.
- La palabra clave **`object`** crea un singleton directamente: no tiene constructor ni se instancia; accedes a sus miembros por su nombre (`Configuracion.idioma`).
- El **`companion object`** es un objeto único dentro de una clase, para miembros que pertenecen a la clase y no a sus instancias (constantes, funciones de fábrica). Se accede por el nombre de la clase (`Usuario.EDAD_MINIMA`).
- El `companion object` es el reemplazo de Kotlin para los miembros `static` de Java.

En el próximo capítulo verás dos herramientas muy útiles para modelar datos con un conjunto limitado de opciones: los `enum` y las `sealed class`.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter18.md">← Anterior</a>
        </td>
        <td style="text-align: center;">
            <a href="/README.md">Ir al índice</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter20.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
