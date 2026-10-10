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

??? tip "Pista"
    Lee cada dato con `readln()` y conviértelo al tipo adecuado solo cuando vayas a usarlo: `.toInt()` para la edad y `.toDouble()` para la altura. Para los años restantes hasta 100, piensa qué operación aritmética relaciona la edad actual con ese objetivo. Repasa el capítulo de variables, tipos de datos y plantillas de cadena.

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

??? tip "Pista"
    Traslada cada fórmula literalmente a Kotlin: el área multiplica ancho por alto, y el perímetro suma ambos lados antes de multiplicar por 2. Cuidado con la prioridad de los operadores y con convertir los valores leídos con `readln()` al tipo numérico adecuado. Revisa cómo interpolar variables y unidades en una plantilla de cadena.

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

---

## Hito: comprueba tu avance (Parte I)

Marca cada afirmación que puedas hacer **sin mirar los apuntes**. Si alguna te resulta difícil, repasa el capítulo indicado antes de continuar.

- [ ] Escribo y ejecuto un programa mínimo en Kotlin. *(caps. 1–2)*
- [ ] Declaro variables con `val` y `var` y elijo la correcta. *(cap. 3)*
- [ ] Uso los tipos básicos (`Int`, `Double`, `Boolean`, `String`, `Char`) sin confundirlos. *(cap. 4)*
- [ ] Aplico operadores aritméticos, de comparación y lógicos. *(cap. 5)*
- [ ] Leo datos con `readln()` y compongo cadenas con plantillas `$`. *(cap. 6)*
- [ ] Trabajo con la longitud, la concatenación y `repeat` de cadenas. *(cap. 7)*
