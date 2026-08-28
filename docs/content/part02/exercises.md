# Ejercicios: Parte II

Estos ejercicios te permitirán aplicar los conceptos vistos en esta parte del curso: control de flujo con `if` y `when`, bucles con `for` y `while`, y definición de funciones. También pondrán en práctica lo aprendido en la Parte I: variables, tipos de datos, operadores y entrada/salida estándar. Intenta resolverlos por tu cuenta antes de consultar la solución propuesta.

---

## Ejercicio 1: Clasificador de notas

### Descripción

Escribe un programa que solicite al usuario una **nota numérica** (de 0 a 100) y muestre en pantalla la calificación correspondiente según la siguiente escala:

| Rango | Calificación |
| :--- | :--- |
| 90 – 100 | Excelente |
| 70 – 89 | Notable |
| 60 – 69 | Aprobado |
| 0 – 59 | Reprobado |

Si el valor introducido está fuera del rango 0–100, muestra el mensaje `"Nota fuera de rango."`.

### Requisitos

- Lee la nota con `readln()` y conviértela al tipo adecuado.
- Usa `when` con rangos (`in`) para clasificar la nota.
- Muestra el resultado con una plantilla de cadena.

### Ejemplo de ejecución

```plaintext
Introduce tu nota (0-100): 75

Tu nota es 75 → Notable
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun main() {
        print("Introduce tu nota (0-100): ")
        val nota = readln().toInt()

        val calificacion = when (nota) {
            in 90..100 -> "Excelente"
            in 70..89  -> "Notable"
            in 60..69  -> "Aprobado"
            in 0..59   -> "Reprobado"
            else       -> "Nota fuera de rango."
        }

        println()
        if (nota in 0..100) {
            println("Tu nota es $nota → $calificacion")
        } else {
            println(calificacion)
        }
    }
    ```

    > [!TIP]Sugerencia
    > Fíjate en que `when` puede usarse como **expresión**: devuelve el texto de la rama que corresponde y lo guardamos directamente en `calificacion`. Esto es mucho más limpio que una cadena de `if`/`else if`. Recuerda que cuando `when` se usa como expresión, el bloque `else` es obligatorio.

---

## Ejercicio 2: Tabla de multiplicar

### Descripción

Escribe un programa que pida al usuario un **número entero** y muestre su tabla de multiplicar del 1 al 10.

### Requisitos

- Lee el número con `readln()` y conviértelo al tipo adecuado.
- Usa un bucle `for` con un rango para generar las diez multiplicaciones.
- Muestra cada línea con una plantilla de cadena con el formato del ejemplo.

### Ejemplo de ejecución

```plaintext
Introduce un número: 7

Tabla de multiplicar del 7:
7 × 1  = 7
7 × 2  = 14
7 × 3  = 21
7 × 4  = 28
7 × 5  = 35
7 × 6  = 42
7 × 7  = 49
7 × 8  = 56
7 × 9  = 63
7 × 10 = 70
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun main() {
        print("Introduce un número: ")
        val numero = readln().toInt()

        println()
        println("Tabla de multiplicar del $numero:")
        for (i in 1..10) {
            println("$numero × $i = ${numero * i}")
        }
    }
    ```

    > [!NOTE]Nota
    > Dentro de la plantilla de cadena, `${numero * i}` usa llaves porque contiene una **expresión** (una multiplicación), no solo el nombre de una variable. Cuando insertas un único nombre de variable, basta con `$variable`; cuando insertas una operación, necesitas `${expresión}`.

---

## Ejercicio 3: Estadísticas de una serie de números

### Descripción

Escribe un programa que pida al usuario una serie de **números enteros positivos**, uno a uno. El programa debe seguir solicitando números mientras el usuario no introduzca `0` (que actúa como señal de fin). Una vez finalizada la entrada, el programa mostrará:

- La **cantidad** de números introducidos (sin contar el 0).
- La **suma** de todos los números.
- El **promedio** (como número decimal).
- El **valor máximo** y el **valor mínimo** de la serie.

Si el usuario no introduce ningún número (pulsa 0 directamente), muestra el mensaje `"No introdujiste ningún número."` y termina.

Organiza el código usando **funciones**: al menos una para calcular el promedio y otra para mostrar el resumen final.

### Requisitos

- Usa un bucle `while` para leer los números uno a uno.
- Declara las funciones fuera de `main` y llama a cada una desde donde corresponda.
- La función de promedio debe recibir la suma y la cantidad como parámetros y devolver un `Double`.
- La función de resumen debe recibir todos los resultados y mostrarlos; no necesita devolver nada.

### Ejemplo de ejecución

```plaintext
Introduce números positivos (0 para terminar):
> 12
> 5
> 30
> 8
> 0

--- Estadísticas ---
Cantidad de números: 4
Suma total:         55
Promedio:           13.75
Máximo:             30
Mínimo:             5
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun calcularPromedio(suma: Int, cantidad: Int): Double {
        return suma.toDouble() / cantidad
    }

    fun mostrarResumen(cantidad: Int, suma: Int, maximo: Int, minimo: Int) {
        println()
        println("--- Estadísticas ---")
        println("Cantidad de números: $cantidad")
        println("Suma total:         $suma")
        println("Promedio:           ${calcularPromedio(suma, cantidad)}")
        println("Máximo:             $maximo")
        println("Mínimo:             $minimo")
    }

    fun main() {
        println("Introduce números positivos (0 para terminar):")

        var suma = 0
        var cantidad = 0
        var maximo = Int.MIN_VALUE
        var minimo = Int.MAX_VALUE

        print("> ")
        var numero = readln().toInt()

        while (numero != 0) {
            suma += numero
            cantidad++
            if (numero > maximo) maximo = numero
            if (numero < minimo) minimo = numero

            print("> ")
            numero = readln().toInt()
        }

        if (cantidad == 0) {
            println("No introdujiste ningún número.")
        } else {
            mostrarResumen(cantidad, suma, maximo, minimo)
        }
    }
    ```

    > [!TIP]Sugerencia
    > `Int.MIN_VALUE` e `Int.MAX_VALUE` son constantes que representan el número entero más pequeño y el más grande que puede guardar un `Int`. Inicializar `maximo` con el valor más pequeño posible garantiza que cualquier número real que leas sea mayor que él, y viceversa para `minimo`. Es una técnica habitual para buscar máximos y mínimos sin asumir nada sobre los datos que llegará a recibir el programa.

    > [!NOTE]Nota
    > Observa que `calcularPromedio` se invoca **desde dentro** de `mostrarResumen`. Esto es perfectamente válido: una función puede llamar a otra, siempre que ambas estén definidas en el mismo ámbito. También podrías haberla llamado antes y pasado el resultado como parámetro; cualquiera de las dos opciones es correcta.
