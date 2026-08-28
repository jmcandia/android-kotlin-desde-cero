# Ejercicios: Parte III

Estos ejercicios te permitirán aplicar los conceptos vistos en esta parte del curso: colecciones, operaciones funcionales, null safety y manejo de excepciones. También pondrán en práctica lo aprendido en las partes anteriores: variables, tipos de datos, operadores, estructuras de control, entrada/salida y funciones. Intenta resolverlos por tu cuenta antes de consultar la solución propuesta.

---

## Ejercicio 1: Filtrar y transformar una lista

### Descripción

Tienes la siguiente lista de palabras:

```kotlin
val palabras = listOf("kotlin", "android", "sol", "app", "desarrollo", "código", "IDE", "bug")
```

Escribe un programa que, **sin modificar la lista original**, produzca e imprima en pantalla una nueva lista que contenga únicamente las palabras con **más de 4 caracteres**, transformadas a **mayúsculas**.

### Requisitos

- No uses bucles `for` ni `while`: resuélvelo encadenando operaciones funcionales sobre la colección.
- Muestra la lista resultante con `forEach`.

### Ejemplo de ejecución

```plaintext
Palabras filtradas y en mayúsculas:
KOTLIN
ANDROID
DESARROLLO
CÓDIGO
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun main() {
        val palabras = listOf("kotlin", "android", "sol", "app", "desarrollo", "código", "IDE", "bug")

        val resultado = palabras
            .filter { it.length > 4 }
            .map { it.uppercase() }

        println("Palabras filtradas y en mayúsculas:")
        resultado.forEach { println(it) }
    }
    ```

    > [!TIP]Sugerencia
    > Fíjate en que `filter` y `map` se **encadenan**: cada operación toma como entrada el resultado de la anterior. Esto se llama *encadenamiento de operaciones* y es uno de los patrones más habituales cuando se trabaja con colecciones en Kotlin. La lista original `palabras` no se modifica en ningún momento.

---

## Ejercicio 2: Agenda de contactos

### Descripción

Escribe un programa que implemente una **agenda de contactos** sencilla. La agenda se almacena en un mapa que relaciona el nombre de cada contacto con su número de teléfono. Algunos contactos pueden no tener teléfono registrado (su valor será `null`).

La agenda debe estar precargada con estos datos:

| Nombre | Teléfono |
| :--- | :--- |
| Ana García | 612 345 678 |
| Luis Martínez | `null` |
| Marta Sánchez | 699 111 222 |
| Carlos López | 655 987 654 |
| Sofía Ruiz | `null` |

El programa debe pedir al usuario un nombre y mostrar:

- El número de teléfono, si existe.
- `"Sin teléfono registrado."`, si el contacto existe pero su teléfono es `null`.
- `"Contacto no encontrado."`, si el nombre no está en la agenda.

El programa debe seguir preguntando hasta que el usuario escriba `"salir"`.

### Requisitos

- Usa `Map<String, String?>` para la agenda (los valores pueden ser `null`).
- Usa `?.` y `?:` para manejar los valores nulos.
- Extrae la lógica de búsqueda en una función separada.

### Ejemplo de ejecución

```plaintext
=== Agenda de contactos ===
Escribe un nombre (o "salir" para terminar): Ana García
Teléfono: 612 345 678

Escribe un nombre (o "salir" para terminar): Luis Martínez
Sin teléfono registrado.

Escribe un nombre (o "salir" para terminar): Pedro
Contacto no encontrado.

Escribe un nombre (o "salir" para terminar): salir
¡Hasta luego!
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun buscarContacto(agenda: Map<String, String?>, nombre: String): String {
        val telefono = agenda[nombre]
        return when {
            nombre !in agenda       -> "Contacto no encontrado."
            telefono != null        -> "Teléfono: $telefono"
            else                    -> "Sin teléfono registrado."
        }
    }

    fun main() {
        val agenda: Map<String, String?> = mapOf(
            "Ana García"     to "612 345 678",
            "Luis Martínez"  to null,
            "Marta Sánchez"  to "699 111 222",
            "Carlos López"   to "655 987 654",
            "Sofía Ruiz"     to null
        )

        println("=== Agenda de contactos ===")
        while (true) {
            print("Escribe un nombre (o \"salir\" para terminar): ")
            val entrada = readln()
            if (entrada == "salir") break
            println(buscarContacto(agenda, entrada))
            println()
        }
        println("¡Hasta luego!")
    }
    ```

    > [!NOTE]Nota
    > La comprobación `nombre !in agenda` es importante: si solo hicieras `agenda[nombre]?.let { ... }`, no podrías distinguir entre un contacto sin teléfono (`null`) y un nombre que directamente no existe (que también devuelve `null`). El `when` con las tres ramas resuelve esa ambigüedad con claridad.

---

## Ejercicio 3: Lector tolerante a errores

### Descripción

Escribe un programa que pida al usuario números enteros uno a uno. El programa debe seguir leyendo mientras el usuario no escriba `"fin"`. Si el usuario introduce algo que **no es un número válido** (como `"abc"` o `"3.5"`), el programa debe mostrar un aviso y continuar pidiendo, **sin detenerse**.

Una vez que el usuario escriba `"fin"`, el programa mostrará, usando operaciones funcionales sobre la lista acumulada:

- La cantidad de números válidos introducidos.
- La **suma** de todos.
- El **promedio** (con dos decimales).
- Los números **pares** de la lista.
- Los números ordenados de **mayor a menor**.

Si no se introdujo ningún número válido, muestra el mensaje `"No se introdujo ningún número válido."` y termina.

### Requisitos

- Usa `try/catch` para atrapar la excepción que lanza `toInt()` ante una entrada inválida.
- Acumula los números válidos en una `MutableList<Int>`.
- Usa `filter`, `map`, `sum()` y `sortedDescending()` para los cálculos finales.
- Muestra el promedio con `String.format("%.2f", ...)`.

### Ejemplo de ejecución

```plaintext
Introduce números enteros ("fin" para terminar):
> 14
> hola
  ⚠ "hola" no es un número válido. Inténtalo de nuevo.
> 3
> 27
> 8
> abc
  ⚠ "abc" no es un número válido. Inténtalo de nuevo.
> 10
> fin

--- Resultados ---
Cantidad de números:  5
Suma:                 62
Promedio:             12,40
Números pares:        [14, 8, 10]
De mayor a menor:     [27, 14, 10, 8, 3]
```

### Solución propuesta

??? example "Ver solución"
    ```kotlin
    fun mostrarResultados(numeros: List<Int>) {
        val suma = numeros.sum()
        val promedio = suma.toDouble() / numeros.size
        val pares = numeros.filter { it % 2 == 0 }
        val ordenados = numeros.sortedDescending()

        println()
        println("--- Resultados ---")
        println("Cantidad de números:  ${numeros.size}")
        println("Suma:                 $suma")
        println("Promedio:             ${String.format("%.2f", promedio)}")
        println("Números pares:        $pares")
        println("De mayor a menor:     $ordenados")
    }

    fun main() {
        val numeros = mutableListOf<Int>()

        println("Introduce números enteros (\"fin\" para terminar):")
        while (true) {
            print("> ")
            val entrada = readln()
            if (entrada == "fin") break

            try {
                numeros.add(entrada.toInt())
            } catch (e: NumberFormatException) {
                println("  ⚠ \"$entrada\" no es un número válido. Inténtalo de nuevo.")
            }
        }

        if (numeros.isEmpty()) {
            println("No se introdujo ningún número válido.")
        } else {
            mostrarResultados(numeros)
        }
    }
    ```

    > [!TIP]Sugerencia
    > Observa que se captura `NumberFormatException` en lugar del tipo genérico `Exception`. Ser específico con el tipo de excepción es una buena práctica: solo interceptas el error que realmente esperas (una entrada que no es un número), y cualquier otro problema inesperado puede seguir propagándose y avisarte de que algo más grave ocurrió.

    > [!NOTE]Nota
    > `sortedDescending()` devuelve una **lista nueva** con los elementos ordenados de mayor a menor; no modifica la lista original. Si quisieras ordenar la lista en su sitio (sin crear una copia), usarías `sortDescending()` sobre una `MutableList`.
