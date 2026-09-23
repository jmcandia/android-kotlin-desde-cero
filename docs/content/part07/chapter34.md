# Capítulo 34: Formularios y validación en la interfaz

## Introducción

Ya conoces los componentes con estado —`TextField`, `Checkbox`, `Switch`— y sabes elevar el estado al composable padre. Un **formulario** los combina: pide varios datos al usuario, comprueba que sean válidos y explica cómo corregirlos. En este capítulo verás cómo organizarlo para que siga siendo claro a medida que crece.

## Componentes, modelo y validación

Un formulario no es solo una columna de campos. Es un pequeño flujo de datos con tres responsabilidades que conviene distinguir:

1. **Componentes**: `TextField`, `Checkbox`, `Switch`, `Button` y, cuando corresponda, `DropdownMenu` o `RadioButton`.
2. **Modelo**: una clase que representa los datos que el formulario recopila, sin depender de Compose.
3. **Validación**: reglas que determinan si esos datos se pueden enviar y mensajes que explican cómo corregirlos.

Por ejemplo, el modelo de un contacto puede vivir en un archivo normal de Kotlin:

```kotlin
data class ContactoForm(
    val nombre: String = "",
    val correo: String = "",
    val aceptaTerminos: Boolean = false
)
```

El composable puede mantener el estado editable y elevar el resultado al contenedor, siguiendo el *state hoisting* del capítulo 32:

```kotlin
@Composable
fun ContactoForm(
    formulario: ContactoForm,
    errores: Map<String, String>,
    onFormularioChange: (ContactoForm) -> Unit,
    onEnviar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = { onFormularioChange(formulario.copy(nombre = it)) },
            label = { Text("Nombre") },
            isError = errores.containsKey("nombre"),
            supportingText = { errores["nombre"]?.let { Text(it) } }
        )
        OutlinedTextField(
            value = formulario.correo,
            onValueChange = { onFormularioChange(formulario.copy(correo = it)) },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errores.containsKey("correo"),
            supportingText = { errores["correo"]?.let { Text(it) } }
        )
        Button(
            onClick = onEnviar,
            enabled = errores.isEmpty() && formulario.aceptaTerminos
        ) {
            Text("Guardar")
        }
    }
}
```

La validación no debería depender de que el usuario pulse el botón. Puedes validar al salir de un campo para ofrecer una corrección temprana, y volver a validar al enviar para no confiar únicamente en el estado visual. Una función pura resulta fácil de probar:

```kotlin
fun validarContacto(formulario: ContactoForm): Map<String, String> = buildMap {
    if (formulario.nombre.isBlank()) put("nombre", "Escribe un nombre")
    if (!formulario.correo.contains("@")) put("correo", "Escribe un correo válido")
    if (!formulario.aceptaTerminos) put("terminos", "Debes aceptar los términos")
}
```

En una pantalla real, el contenedor conserva `ContactoForm` y los errores, y decide cuándo llamar a `validarContacto`. El composable del formulario solo muestra valores, errores y eventos. Para formularios largos, usa `rememberSaveable` para conservar lo escrito durante una recreación de la `Activity`; la validación definitiva y el envío deberán pasar después a un `ViewModel`, como se verá en la Parte VIII.

> [!WARNING]Advertencia
> `isError` cambia el aspecto del campo, pero no sustituye al texto del error ni a una validación real. Tampoco valides solo en la interfaz: la capa que guarda o envía los datos debe volver a comprobar sus reglas.

## Resumen

En este capítulo aprendiste a construir formularios:

- Un formulario combina **componentes**, un **modelo independiente de Compose** y una **validación** que produce errores comprensibles.
- El estado se eleva al contenedor; el composable del formulario recibe valores, errores y eventos.
- `isError` y `supportingText` muestran los errores junto a cada campo, pero no reemplazan la validación de la capa que guarda o envía los datos.

En el próximo capítulo aprenderás a moverte entre distintas pantallas de tu app con **Navigation Compose**.
