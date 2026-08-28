# Ejercicios: Parte IV

Estos ejercicios te permitirán aplicar los conceptos vistos en esta parte del curso: clases, herencia, `data class`, `companion object`, `enum`, `sealed class`, genéricos y funciones de extensión. También pondrán en práctica lo aprendido en las partes anteriores: colecciones, operaciones funcionales, null safety, manejo de excepciones, estructuras de control y funciones. Intenta resolverlos por tu cuenta antes de consultar la solución propuesta.

---

## Ejercicio 1: Catálogo de productos

### Descripción

Define una `data class` llamada `Producto` con las propiedades `nombre: String`, `precio: Double` y `categoria: String`. Crea una lista de al menos ocho productos de distintas categorías y, usando operaciones funcionales, produce e imprime:

1. Todos los productos de la categoría `"Electrónica"`, ordenados por precio de menor a mayor.
2. El precio total de todos los productos de la lista.
3. El nombre del producto más caro.

### Requisitos

- Usa `filter`, `sortedBy`, `sumOf`, `maxByOrNull` y `forEach`.
- Muestra cada producto con el formato `"[categoria] nombre — $precio"`.
- No uses bucles `for` ni `while`.

### Ejemplo de ejecución

```plaintext
--- Electrónica (orden por precio) ---
[Electrónica] Auriculares — $29.99
[Electrónica] Teclado — $49.99
[Electrónica] Monitor — $299.99

Precio total del catálogo: $689.93
Producto más caro: Monitor
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    data class Producto(val nombre: String, val precio: Double, val categoria: String)

    fun main() {
        val catalogo = listOf(
            Producto("Auriculares",  29.99,  "Electrónica"),
            Producto("Teclado",      49.99,  "Electrónica"),
            Producto("Monitor",      299.99, "Electrónica"),
            Producto("Camiseta",     19.99,  "Ropa"),
            Producto("Zapatillas",   89.99,  "Ropa"),
            Producto("Novela",       12.50,  "Libros"),
            Producto("Enciclopedia", 45.00,  "Libros"),
            Producto("Mochila",      34.99,  "Accesorios")
        )

        println("--- Electrónica (orden por precio) ---")
        catalogo
            .filter { it.categoria == "Electrónica" }
            .sortedBy { it.precio }
            .forEach { println("[${it.categoria}] ${it.nombre} — $${it.precio}") }

        val total = catalogo.sumOf { it.precio }
        println("\nPrecio total del catálogo: $${"%.2f".format(total)}")

        val masCaro = catalogo.maxByOrNull { it.precio }
        println("Producto más caro: ${masCaro?.nombre}")
    }
    ```

    > [!TIP]Sugerencia
    > `maxByOrNull` devuelve `null` si la lista está vacía, por eso accedemos al nombre con `?.`. Usar el operador de llamada segura aquí es una buena costumbre: aunque sabes que la lista no está vacía, el tipo de retorno es `Producto?`, y el compilador te obliga a manejarlo.

---

## Ejercicio 2: Descripción de estaciones

### Descripción

Define un `enum class EstacionDelAno` con los cuatro valores `PRIMAVERA`, `VERANO`, `OTOÑO` e `INVIERNO`. Añade a cada valor una propiedad `descripcion: String` que contenga una frase corta característica de esa estación.

Escribe una función `consejo(estacion: EstacionDelAno): String` que, usando `when`, devuelva un consejo de vestuario para cada estación. Finalmente, en `main`, recorre todos los valores del enum con `enumValues<EstacionDelAno>()` e imprime la descripción y el consejo de cada uno.

### Requisitos

- El `when` de `consejo` debe cubrir todas las ramas sin usar `else`.
- Recorre los valores del enum con un `for` sobre `enumValues<EstacionDelAno>()`.

### Ejemplo de ejecución

```plaintext
PRIMAVERA — Flores y temperatura suave.
  Consejo: Lleva una chaqueta ligera.

VERANO — Calor intenso y días largos.
  Consejo: Usa ropa fresca y protector solar.

OTOÑO — Hojas de colores y lluvia.
  Consejo: Un impermeable nunca falla.

INVIERNO — Frío, nieve y noches largas.
  Consejo: Abrígate bien con capas.
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    enum class EstacionDelAno(val descripcion: String) {
        PRIMAVERA("Flores y temperatura suave."),
        VERANO("Calor intenso y días largos."),
        OTOÑO("Hojas de colores y lluvia."),
        INVIERNO("Frío, nieve y noches largas.")
    }

    fun consejo(estacion: EstacionDelAno): String = when (estacion) {
        EstacionDelAno.PRIMAVERA -> "Lleva una chaqueta ligera."
        EstacionDelAno.VERANO   -> "Usa ropa fresca y protector solar."
        EstacionDelAno.OTOÑO    -> "Un impermeable nunca falla."
        EstacionDelAno.INVIERNO -> "Abrígate bien con capas."
    }

    fun main() {
        for (estacion in enumValues<EstacionDelAno>()) {
            println("${estacion.name} — ${estacion.descripcion}")
            println("  Consejo: ${consejo(estacion)}")
            println()
        }
    }
    ```

    > [!NOTE]Nota
    > Al cubrir **todos** los valores del enum en el `when`, no hace falta la rama `else`. Y si mañana añades una quinta estación al enum, el compilador te avisará de inmediato de que este `when` ya no está completo, obligándote a actualizarlo. Esa protección automática es una de las grandes ventajas de usar `enum` en lugar de cadenas o números.

---

## Ejercicio 3: Jerarquía de figuras geométricas

### Descripción

Crea una jerarquía de clases para representar figuras geométricas:

- Una clase `abstract` llamada `Figura` con una propiedad `nombre: String` y dos métodos abstractos: `area(): Double` y `perimetro(): Double`. Añade un método no abstracto `describir()` que imprima el nombre, el área y el perímetro de la figura con dos decimales.
- Una clase `Rectangulo(ancho: Double, alto: Double)` que herede de `Figura` e implemente los cálculos correspondientes.
- Una clase `Circulo(radio: Double)` que herede de `Figura` e implemente los cálculos usando `Math.PI`.
- Una clase `TrianguloRectangulo(base: Double, altura: Double)` que herede de `Figura` e implemente los cálculos (para el perímetro, aplica el teorema de Pitágoras para la hipotenusa: `√(base² + altura²)`).

Crea una lista con al menos dos instancias de cada figura y, usando polimorfismo y operaciones funcionales:

1. Llama a `describir()` sobre cada figura.
2. Muestra el nombre de la figura con **mayor área**.
3. Muestra el número de figuras con un área superior a `20.0`.

### Requisitos

- La clase `Figura` debe ser `abstract` y `open`.
- Usa `companion object` en cada subclase para ofrecer una función `crear()` que construya un ejemplo con valores predeterminados.
- Usa `forEach`, `maxByOrNull` y `count` sobre la lista.

### Ejemplo de ejecución

```plaintext
Rectángulo — Área: 15,00 | Perímetro: 16,00
Rectángulo — Área: 50,00 | Perímetro: 30,00
Círculo — Área: 28,27 | Perímetro: 18,85
Círculo — Área: 78,54 | Perímetro: 31,42
Triángulo rectángulo — Área: 6,00 | Perímetro: 12,00
Triángulo rectángulo — Área: 24,00 | Perímetro: 19,21

Figura con mayor área: Círculo
Figuras con área > 20: 3
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    import kotlin.math.PI
    import kotlin.math.sqrt

    abstract class Figura(val nombre: String) {
        abstract fun area(): Double
        abstract fun perimetro(): Double

        fun describir() {
            println("$nombre — Área: ${"%.2f".format(area())} | Perímetro: ${"%.2f".format(perimetro())}")
        }
    }

    class Rectangulo(val ancho: Double, val alto: Double) : Figura("Rectángulo") {
        override fun area() = ancho * alto
        override fun perimetro() = 2 * (ancho + alto)

        companion object {
            fun crear() = Rectangulo(4.0, 5.0)
        }
    }

    class Circulo(val radio: Double) : Figura("Círculo") {
        override fun area() = PI * radio * radio
        override fun perimetro() = 2 * PI * radio

        companion object {
            fun crear() = Circulo(3.0)
        }
    }

    class TrianguloRectangulo(val base: Double, val altura: Double) : Figura("Triángulo rectángulo") {
        override fun area() = base * altura / 2
        override fun perimetro() = base + altura + sqrt(base * base + altura * altura)

        companion object {
            fun crear() = TrianguloRectangulo(3.0, 4.0)
        }
    }

    fun main() {
        val figuras: List<Figura> = listOf(
            Rectangulo(3.0, 5.0),
            Rectangulo(5.0, 10.0),
            Circulo(3.0),
            Circulo(5.0),
            TrianguloRectangulo(3.0, 4.0),
            TrianguloRectangulo(6.0, 8.0)
        )

        figuras.forEach { it.describir() }

        println()
        val mayor = figuras.maxByOrNull { it.area() }
        println("Figura con mayor área: ${mayor?.nombre}")

        val cantidad = figuras.count { it.area() > 20.0 }
        println("Figuras con área > 20: $cantidad")
    }
    ```

    > [!TIP]Sugerencia
    > Fíjate en que la lista es `List<Figura>`, aunque contiene objetos de tres tipos distintos. Eso es el **polimorfismo** en acción: `forEach { it.describir() }` llama al `describir()` correcto para cada objeto, sin necesidad de saber de qué subclase es. Cada clase sabe cómo calcular su propia área y perímetro; la lista solo sabe que todos son `Figura`.

---

## Ejercicio 4: Sistema de evaluación con `sealed class`

### Descripción

Imagina que procesas las notas de un grupo de alumnos. Algunos tienen nota registrada, otros aún no han sido evaluados, y algunos tienen una entrada inválida (texto en lugar de número).

Define las siguientes clases:

- Una `data class Alumno(val nombre: String, val notaTexto: String?)` que representa la entrada en bruto de cada alumno (la nota puede no existir o puede ser un texto inválido).
- Una `sealed class ResultadoEvaluacion` con tres casos:
    - `data class Aprobado(val nombre: String, val nota: Double)` — nota ≥ 5.0.
    - `data class Reprobado(val nombre: String, val nota: Double)` — nota < 5.0.
    - `data class SinEvaluar(val nombre: String)` — nota ausente o inválida.

Escribe una función `evaluar(alumno: Alumno): ResultadoEvaluacion` que intente convertir `notaTexto` a `Double` usando `try/catch` y devuelva el resultado correspondiente.

En `main`, aplica `evaluar` a una lista de alumnos con `map`, y luego muestra:

1. La lista completa de resultados usando `when`.
2. El número de aprobados, reprobados y sin evaluar.

### Requisitos

- Usa `try/catch` dentro de `evaluar` para manejar entradas inválidas.
- Usa `when` sobre cada `ResultadoEvaluacion` para el mensaje, sin rama `else`.
- Usa `count` con una lambda para contar cada tipo de resultado.

### Ejemplo de ejecución

```plaintext
--- Resultados ---
✓ Ana López: APROBADA con 7.5
✗ Carlos Ruiz: REPROBADO con 3.0
? María Torres: SIN EVALUAR
✓ Diego Soto: APROBADO con 9.0
? Luis Vera: SIN EVALUAR (entrada inválida: "diez")
✗ Sofía Mendez: REPROBADA con 4.5

--- Resumen ---
Aprobados:    2
Reprobados:   2
Sin evaluar:  2
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    data class Alumno(val nombre: String, val notaTexto: String?)

    sealed class ResultadoEvaluacion {
        data class Aprobado(val nombre: String, val nota: Double) : ResultadoEvaluacion()
        data class Reprobado(val nombre: String, val nota: Double) : ResultadoEvaluacion()
        data class SinEvaluar(val nombre: String) : ResultadoEvaluacion()
    }

    fun evaluar(alumno: Alumno): ResultadoEvaluacion {
        val notaTexto = alumno.notaTexto ?: return ResultadoEvaluacion.SinEvaluar(alumno.nombre)
        return try {
            val nota = notaTexto.toDouble()
            if (nota >= 5.0) ResultadoEvaluacion.Aprobado(alumno.nombre, nota)
            else             ResultadoEvaluacion.Reprobado(alumno.nombre, nota)
        } catch (e: NumberFormatException) {
            ResultadoEvaluacion.SinEvaluar(alumno.nombre)
        }
    }

    fun main() {
        val alumnos = listOf(
            Alumno("Ana López",    "7.5"),
            Alumno("Carlos Ruiz",  "3.0"),
            Alumno("María Torres", null),
            Alumno("Diego Soto",   "9.0"),
            Alumno("Luis Vera",    "diez"),
            Alumno("Sofía Mendez", "4.5")
        )

        val resultados = alumnos.map { evaluar(it) }

        println("--- Resultados ---")
        resultados.forEach { resultado ->
            val mensaje = when (resultado) {
                is ResultadoEvaluacion.Aprobado   -> "✓ ${resultado.nombre}: APROBADO con ${resultado.nota}"
                is ResultadoEvaluacion.Reprobado  -> "✗ ${resultado.nombre}: REPROBADO con ${resultado.nota}"
                is ResultadoEvaluacion.SinEvaluar -> "? ${resultado.nombre}: SIN EVALUAR"
            }
            println(mensaje)
        }

        println()
        println("--- Resumen ---")
        println("Aprobados:    ${resultados.count { it is ResultadoEvaluacion.Aprobado }}")
        println("Reprobados:   ${resultados.count { it is ResultadoEvaluacion.Reprobado }}")
        println("Sin evaluar:  ${resultados.count { it is ResultadoEvaluacion.SinEvaluar }}")
    }
    ```

    > [!TIP]Sugerencia
    > Observa cómo `evaluar` devuelve antes de llegar al `try/catch` si la nota es `null`, usando el operador Elvis (`?:`) con `return`. Esto se llama *early return* y evita anidar condiciones: en cuanto detectas que no hay nota, sales de la función con el resultado correspondiente. El resto de la función puede asumir que `notaTexto` es un `String` real.

    > [!NOTE]Nota
    > Dentro del `when` se usa `is` para hacer una **comprobación de tipo** (*smart cast*): al escribir `is ResultadoEvaluacion.Aprobado`, Kotlin sabe que dentro de esa rama `resultado` es de ese tipo concreto, y por eso puedes acceder directamente a `resultado.nota` sin ninguna conversión adicional.
