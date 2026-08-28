# Ejercicios: Parte I

Estos ejercicios te permitirán aplicar los conceptos que aprendiste a lo largo de la primera parte del curso: variables, tipos de datos, operadores, entrada/salida y operaciones con cadenas. Intenta resolverlos por tu cuenta antes de consultar la solución propuesta.

---

## Ejercicio 1: Ficha personal

### Descripción

Escribe un programa que solicite al usuario su **nombre**, su **edad** y su **altura en metros**, y que luego muestre un resumen con esa información. Además, el programa debe calcular e indicar en cuántos años cumplirá 100 años.

### Requisitos

- Usa `readln()` para leer los datos introducidos por el usuario.
- Declara las variables con el tipo más adecuado para cada dato.
- Muestra el resultado usando **plantillas de cadena**.
- Realiza el cálculo de los años restantes para llegar a 100 usando un operador aritmético.

### Ejemplo de ejecución

```plaintext
Introduce tu nombre: Laura
Introduce tu edad: 28
Introduce tu altura en metros (por ejemplo, 1.70): 1.65

--- Tu ficha personal ---
Nombre: Laura
Edad: 28 años
Altura: 1.65 m
En 72 años cumplirás 100 años.
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun main() {
        print("Introduce tu nombre: ")
        val nombre = readln()

        print("Introduce tu edad: ")
        val edad = readln().toInt()

        print("Introduce tu altura en metros (por ejemplo, 1.70): ")
        val altura = readln().toDouble()

        val anosRestantes = 100 - edad

        println()
        println("--- Tu ficha personal ---")
        println("Nombre: $nombre")
        println("Edad: $edad años")
        println("Altura: $altura m")
        println("En $anosRestantes años cumplirás 100 años.")
    }
    ```

    > [!TIP]Sugerencia
    > Presta atención al tipo de cada variable: el nombre es un `String` (y no necesita conversión), la edad es un `Int` (usa `.toInt()`) y la altura es un `Double` (usa `.toDouble()`). Asignar el tipo equivocado provocaría un error de incompatibilidad de tipos en tiempo de compilación.

---

## Ejercicio 2: Calculadora de área y perímetro

### Descripción

Escribe un programa que pida al usuario el **ancho** y el **alto** de un rectángulo (en centímetros) y que calcule e imprima su **área** y su **perímetro**.

Recuerda que:

- El **área** de un rectángulo es: `ancho × alto`
- El **perímetro** de un rectángulo es: `2 × (ancho + alto)`

### Requisitos

- Lee los valores del teclado con `readln()` y conviértelos al tipo adecuado.
- Utiliza variables con nombres descriptivos para los resultados.
- Muestra los resultados con plantillas de cadena, incluyendo la unidad de medida correspondiente (`cm` para el perímetro, `cm²` para el área).

### Ejemplo de ejecución

```plaintext
Introduce el ancho del rectángulo (en cm): 15
Introduce el alto del rectángulo (en cm): 8

--- Resultados ---
Área: 120 cm²
Perímetro: 46 cm
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun main() {
        print("Introduce el ancho del rectángulo (en cm): ")
        val ancho = readln().toInt()

        print("Introduce el alto del rectángulo (en cm): ")
        val alto = readln().toInt()

        val area = ancho * alto
        val perimetro = 2 * (ancho + alto)

        println()
        println("--- Resultados ---")
        println("Área: $area cm²")
        println("Perímetro: $perimetro cm")
    }
    ```

    > [!NOTE]Nota
    > Si introduces valores decimales como `15.5`, el programa fallará porque `.toInt()` no sabe cómo convertir ese texto. Para admitir decimales, bastaría con cambiar el tipo a `Double` y usar `.toDouble()` en la lectura. ¿Te animas a probar esa variante?
