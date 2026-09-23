# Capítulo 55: Pantalla de formulario: validación local y del servidor

## Introducción

La pantalla de formulario cumple dos roles con la misma interfaz: **crear** un contacto nuevo y **editar** uno existente. Es también la pantalla con más responsabilidades de validación: revisa los datos localmente antes de enviarlos (para dar una respuesta inmediata) y, si el servidor rechaza igualmente la petición, traduce esos errores de vuelta a los campos correspondientes.

En este capítulo veremos `ContactoForm` (el modelo del formulario y sus reglas de validación), `FormularioContactoViewModel` y `FormularioContactoScreen`.

## El modelo del formulario: `ContactoForm.kt`

A diferencia de `Contacto` y `DatosContacto` (capítulo 48), el formulario necesita que **todos** sus campos sean `String`, incluso los numéricos o los opcionales, porque así es como llegan desde un `TextField`. Un campo vacío se representa como `""`, no como `null`:

```kotlin
package com.ejemplo.miscontactos.ui.formulario

data class ContactoForm(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val ciudad: String = ""
)
```

### El enum `Campo`

```kotlin
enum class Campo(val nombreApi: String) {
    NOMBRE("nombre"),
    APELLIDO("apellido"),
    EMAIL("email"),
    TELEFONO("telefono"),
    DIRECCION("direccion"),
    CIUDAD("ciudad")
}
```

Cada valor del `enum` (capítulo 19) lleva asociado su `nombreApi`: el nombre exacto que usa el campo en el JSON que envía y responde el servidor. Este puente entre el nombre de la propiedad en Kotlin y el nombre del campo en la API es lo que permite, más adelante, traducir automáticamente un mensaje de error del servidor ("email: formato no válido") al campo `Campo.EMAIL` de la interfaz.

### Errores de campo: `ErrorCampo`

```kotlin
sealed interface ErrorCampo {
    data object Obligatorio : ErrorCampo
    data class Longitud(val minimo: Int) : ErrorCampo
    data class LargoMaximo(val maximo: Int) : ErrorCampo
    data object EmailInvalido : ErrorCampo
    data class Servidor(val mensaje: String) : ErrorCampo
}
```

Modelar los errores como una `sealed interface` (en vez de simples cadenas de texto) permite que la Screen decida **qué texto mostrar en cada idioma** a partir del tipo de error, en lugar de que el `ViewModel` tenga que conocer cadenas de recursos (`stringResource`), algo que no está disponible fuera de un composable. `Servidor` es el único caso que sí lleva un mensaje en texto plano, porque ese mensaje ya viene redactado por la API.

### Validación local: `validarContacto`

```kotlin
private val PATRON_EMAIL = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

fun validarContacto(form: ContactoForm): Map<Campo, ErrorCampo> {
    val errores = mutableMapOf<Campo, ErrorCampo>()

    if (form.nombre.isBlank()) {
        errores[Campo.NOMBRE] = ErrorCampo.Obligatorio
    } else if (form.nombre.length < LARGO_MINIMO_NOMBRE) {
        errores[Campo.NOMBRE] = ErrorCampo.Longitud(LARGO_MINIMO_NOMBRE)
    }

    if (form.apellido.isBlank()) {
        errores[Campo.APELLIDO] = ErrorCampo.Obligatorio
    } else if (form.apellido.length < LARGO_MINIMO_APELLIDO) {
        errores[Campo.APELLIDO] = ErrorCampo.Longitud(LARGO_MINIMO_APELLIDO)
    }

    if (form.email.isBlank()) {
        errores[Campo.EMAIL] = ErrorCampo.Obligatorio
    } else if (!PATRON_EMAIL.matches(form.email)) {
        errores[Campo.EMAIL] = ErrorCampo.EmailInvalido
    }

    if (form.direccion.length > LARGO_MAXIMO_DIRECCION) {
        errores[Campo.DIRECCION] = ErrorCampo.LargoMaximo(LARGO_MAXIMO_DIRECCION)
    }

    return errores
}
```

`validarContacto` es una **función pura**: recibe un `ContactoForm` y devuelve un `Map<Campo, ErrorCampo>` sin efectos secundarios ni dependencias externas, lo que la hace trivial de probar de forma aislada, sin necesidad de un `ViewModel` ni de Android. Solo se agrega una entrada al mapa cuando un campo falla su validación; un formulario válido produce un mapa vacío.

### Errores del servidor: `erroresDelServidor`

```kotlin
fun erroresDelServidor(errores: List<String>): Map<Campo, ErrorCampo> {
    val porCampo = mutableMapOf<Campo, ErrorCampo>()
    for (error in errores) {
        val (nombreCampo, mensaje) = error.split(":", limit = 2)
            .map { it.trim() }
            .let { it[0] to it.getOrElse(1) { it[0] } }
        val campo = Campo.entries.find { it.nombreApi == nombreCampo }
        if (campo != null) {
            porCampo[campo] = ErrorCampo.Servidor(mensaje)
        }
    }
    return porCampo
}
```

Recuerda de `ErrorDatos` (capítulo 46) que `ErrorDatos.Validacion` trae una `List<String>` con mensajes en formato `"campo: descripción"`. Esta función separa cada cadena en su nombre de campo y su mensaje, busca el `Campo` de Kotlin cuyo `nombreApi` coincide, y arma el mismo tipo de mapa que produce `validarContacto`, para que la Screen los pueda mostrar de forma idéntica sin importar si el error vino del cliente o del servidor.

### Mapeos entre formulario y dominio

```kotlin
fun ContactoForm.aDatos(): DatosContacto = DatosContacto(
    nombre = nombre.trim(),
    apellido = apellido.trim(),
    email = email.trim(),
    telefono = telefono.trim().ifBlank { null },
    direccion = direccion.trim().ifBlank { null },
    ciudad = ciudad.trim().ifBlank { null }
)

fun Contacto.aFormulario(): ContactoForm = ContactoForm(
    nombre = nombre,
    apellido = apellido,
    email = email,
    telefono = telefono.orEmpty(),
    direccion = direccion.orEmpty(),
    ciudad = ciudad.orEmpty()
)
```

`aDatos()` convierte el formulario (todo `String`) al modelo de dominio `DatosContacto` (capítulo 48), donde los campos opcionales sí son `String?`: un campo en blanco se transforma en `null` con `ifBlank { null }`. `aFormulario()` hace el camino inverso al editar un contacto existente, usando `orEmpty()` para convertir un `String?` en `""` si no hay valor.

## El ViewModel: `FormularioContactoViewModel`

```kotlin
data class FormularioContactoUiState(
    val formulario: ContactoForm = ContactoForm(),
    val errores: Map<Campo, ErrorCampo> = emptyMap(),
    val esEdicion: Boolean = false,
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val errorGeneral: ErrorDatos? = null
)

@HiltViewModel
class FormularioContactoViewModel @Inject constructor(
    private val repository: ContactosRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Int? = savedStateHandle.toRoute<FormularioContactoRuta>().id

    private val _uiState = MutableStateFlow(
        FormularioContactoUiState(esEdicion = id != null, cargando = id != null)
    )
    val uiState: StateFlow<FormularioContactoUiState> = _uiState.asStateFlow()

    init {
        if (id != null) cargarContacto(id)
    }

    // ... (cargarContacto, cambiarCampo, guardar)
}
```

El `id` vuelve a leerse desde la ruta tipada, pero esta vez es **anulable** (`Int?`): la misma pantalla sirve para crear (`id == null`) y para editar (`id != null`). Ese único valor determina tanto `esEdicion` como si hay que mostrar un indicador de carga inicial mientras se trae el contacto a editar.

### Cargar el contacto a editar

```kotlin
private fun cargarContacto(id: Int) {
    viewModelScope.launch {
        repository.obtenerContacto(id)
            .onSuccess { contacto ->
                _uiState.update { it.copy(formulario = contacto.aFormulario(), cargando = false) }
            }
            .onFailure { error ->
                _uiState.update { it.copy(cargando = false, errorGeneral = error.comoErrorDatos()) }
            }
    }
}
```

Cuando se crea un contacto nuevo, no hace falta esta llamada: el formulario ya nace con `ContactoForm()` (todos los campos vacíos). Solo al editar se pide el contacto por su `id` y se convierte a `ContactoForm` con `aFormulario()`.

### Cambiar un campo y limpiar su error

```kotlin
fun cambiarCampo(campo: Campo, valor: String) {
    _uiState.update { estado ->
        val formulario = when (campo) {
            Campo.NOMBRE -> estado.formulario.copy(nombre = valor)
            Campo.APELLIDO -> estado.formulario.copy(apellido = valor)
            Campo.EMAIL -> estado.formulario.copy(email = valor)
            Campo.TELEFONO -> estado.formulario.copy(telefono = valor)
            Campo.DIRECCION -> estado.formulario.copy(direccion = valor)
            Campo.CIUDAD -> estado.formulario.copy(ciudad = valor)
        }
        estado.copy(formulario = formulario, errores = estado.errores - campo)
    }
}
```

Cada vez que el usuario modifica un campo, además de actualizar su valor, **se quita ese campo del mapa de errores** (`estado.errores - campo`, operador de colecciones del capítulo 11). Así, si el usuario ya vio el error "correo inválido" y empieza a corregirlo, el mensaje desaparece apenas empieza a escribir, en lugar de esperar a un nuevo intento de guardado.

### Guardar: validar antes de llamar a la red

```kotlin
fun guardar() {
    val formulario = _uiState.value.formulario
    val errores = validarContacto(formulario)
    if (errores.isNotEmpty()) {
        _uiState.update { it.copy(errores = errores) }
        return
    }

    viewModelScope.launch {
        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        val datos = formulario.aDatos()
        val resultado = if (id != null) repository.actualizar(id, datos) else repository.crear(datos)

        resultado
            .onSuccess {
                _uiState.update { it.copy(guardando = false, guardado = true) }
            }
            .onFailure { excepcion ->
                val error = excepcion.comoErrorDatos()
                when (error) {
                    is ErrorDatos.Validacion ->
                        _uiState.update { it.copy(guardando = false, errores = erroresDelServidor(error.errores)) }
                    is ErrorDatos.Conflicto ->
                        _uiState.update {
                            it.copy(guardando = false, errores = mapOf(Campo.EMAIL to ErrorCampo.Servidor(error.message.orEmpty())))
                        }
                    else ->
                        _uiState.update { it.copy(guardando = false, errorGeneral = error) }
                }
            }
    }
}
```

`guardar()` tiene dos líneas de defensa:

1. **Validación local primero**: llama a `validarContacto(formulario)` de forma síncrona, sin coroutine. Si hay errores, los publica en el estado y **retorna de inmediato**, sin llamar jamás a la red con datos que ya sabemos inválidos.
2. **Validación del servidor como respaldo**: si la validación local pasó pero el servidor igual rechaza los datos (por ejemplo, un correo que ya existe, algo que solo el servidor puede saber), el `when` distingue tres casos:
   - `ErrorDatos.Validacion`: errores por campo, traducidos con `erroresDelServidor`.
   - `ErrorDatos.Conflicto`: un caso específico (correo duplicado) que se asigna directamente al campo `EMAIL`.
   - Cualquier otro error (por ejemplo, `SinConexion`): se guarda en `errorGeneral`, porque no corresponde a ningún campo en particular.

`id != null` decide, en una sola línea, si la operación es `actualizar` o `crear` — el mismo `id` opcional que ya vimos determinar `esEdicion` al construir el estado inicial.

## La pantalla: `FormularioContactoScreen`

```kotlin
@Composable
fun FormularioContactoScreen(
    onVolver: () -> Unit,
    viewModel: FormularioContactoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.guardado) {
        if (uiState.guardado) onVolver()
    }

    FormularioContactoContent(
        uiState = uiState,
        onCampoChange = viewModel::cambiarCampo,
        onGuardar = viewModel::guardar,
        onVolver = onVolver
    )
}
```

Igual que en la pantalla de detalle, guardar con éxito es un evento (`guardado: Boolean`) observado con `LaunchedEffect(uiState.guardado)` para volver atrás — el mismo patrón, reutilizado por tercera vez en esta parte del curso.

### El contenido: un campo por dato, con su error

```kotlin
@Composable
fun FormularioContactoContent(
    uiState: FormularioContactoUiState,
    onCampoChange: (Campo, String) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorGeneral = uiState.errorGeneral
    LaunchedEffect(errorGeneral) {
        if (errorGeneral != null) snackbarHostState.showSnackbar(mensajeDe(errorGeneral))
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(
                            if (uiState.esEdicion) R.string.titulo_editar else R.string.titulo_nuevo
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.cargando) {
            EstadoCargando(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            CampoTexto(
                etiqueta = stringResource(R.string.campo_nombre),
                valor = uiState.formulario.nombre,
                error = uiState.errores[Campo.NOMBRE],
                onValueChange = { onCampoChange(Campo.NOMBRE, it) },
                capitalizarPalabras = true
            )
            CampoTexto(
                etiqueta = stringResource(R.string.campo_apellido),
                valor = uiState.formulario.apellido,
                error = uiState.errores[Campo.APELLIDO],
                onValueChange = { onCampoChange(Campo.APELLIDO, it) },
                capitalizarPalabras = true
            )
            CampoTexto(
                etiqueta = stringResource(R.string.campo_email),
                valor = uiState.formulario.email,
                error = uiState.errores[Campo.EMAIL],
                onValueChange = { onCampoChange(Campo.EMAIL, it) },
                tipoTeclado = KeyboardType.Email
            )
            CampoTexto(
                etiqueta = stringResource(R.string.campo_telefono),
                valor = uiState.formulario.telefono,
                error = uiState.errores[Campo.TELEFONO],
                onValueChange = { onCampoChange(Campo.TELEFONO, it) },
                tipoTeclado = KeyboardType.Phone
            )
            CampoTexto(
                etiqueta = stringResource(R.string.campo_direccion),
                valor = uiState.formulario.direccion,
                error = uiState.errores[Campo.DIRECCION],
                onValueChange = { onCampoChange(Campo.DIRECCION, it) },
                capitalizarPalabras = true
            )
            CampoTexto(
                etiqueta = stringResource(R.string.campo_ciudad),
                valor = uiState.formulario.ciudad,
                error = uiState.errores[Campo.CIUDAD],
                onValueChange = { onCampoChange(Campo.CIUDAD, it) },
                capitalizarPalabras = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onGuardar, enabled = !uiState.guardando, modifier = Modifier.fillMaxWidth()) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.guardar))
                }
            }
        }
    }
}
```

Cada `CampoTexto` recibe su propio error potencial con `uiState.errores[Campo.X]` — como `errores` es un `Map`, un campo sin problemas simplemente no tiene entrada y el acceso devuelve `null`. El botón de guardar se **deshabilita** (`enabled = !uiState.guardando`) mientras la operación está en curso, y muestra un `CircularProgressIndicator` pequeño en lugar del texto, evitando que el usuario dispare guardados duplicados con toques repetidos.

### `CampoTexto`: un `OutlinedTextField` configurable

```kotlin
@Composable
private fun CampoTexto(
    etiqueta: String,
    valor: String,
    error: ErrorCampo?,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizarPalabras: Boolean = false
) {
    Column(modifier = modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = valor,
            onValueChange = onValueChange,
            label = { Text(etiqueta) },
            isError = error != null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = tipoTeclado,
                capitalization = if (capitalizarPalabras) KeyboardCapitalization.Words else KeyboardCapitalization.None
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = mensajeDeCampo(error),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
private fun mensajeDeCampo(error: ErrorCampo): String = when (error) {
    ErrorCampo.Obligatorio -> stringResource(R.string.error_obligatorio)
    is ErrorCampo.Longitud -> stringResource(R.string.error_longitud_minima, error.minimo)
    is ErrorCampo.LargoMaximo -> stringResource(R.string.error_largo_maximo, error.maximo)
    ErrorCampo.EmailInvalido -> stringResource(R.string.error_email_invalido)
    is ErrorCampo.Servidor -> error.mensaje
}
```

`keyboardOptions` es lo que le indica al teclado del sistema qué mostrar: `KeyboardType.Email` habilita la tecla `@` para el correo, `KeyboardType.Phone` cambia a un teclado numérico para el teléfono, y `KeyboardCapitalization.Words` pone en mayúscula automática la primera letra de cada palabra en campos como nombre o dirección.

`mensajeDeCampo` es el `when` exhaustivo (capítulo 19) que traduce cada variante de `ErrorCampo` a un texto localizado — el mismo principio que `mensajeDe(error: ErrorDatos)` del capítulo 46: los datos deciden qué pasó, la interfaz decide cómo decirlo.

## Resumen

- `ContactoForm` guarda todos los campos como `String` (incluso los opcionales), porque así llegan desde un `TextField`; se convierte a `DatosContacto` con `aDatos()` al guardar, y desde `Contacto` con `aFormulario()` al editar.
- `validarContacto` es una función pura que revisa el formulario localmente; `erroresDelServidor` traduce los mensajes `"campo: descripción"` de `ErrorDatos.Validacion` al mismo formato `Map<Campo, ErrorCampo>`, para que la interfaz no tenga que distinguir el origen del error.
- `guardar()` valida localmente primero (sin red) y, si el servidor igual rechaza los datos, distingue `Validacion` (por campo), `Conflicto` (correo duplicado, asignado a `EMAIL`) y cualquier otro error (a `errorGeneral`).
- `FormularioContactoScreen` reutiliza, por tercera vez en esta parte, el patrón "evento como estado": `guardado: Boolean` + `LaunchedEffect` para volver atrás tras guardar con éxito.
- `CampoTexto` centraliza la configuración del teclado (`KeyboardType`, `KeyboardCapitalization`) y la presentación del error de cada campo.

En el próximo capítulo veremos cómo estas tres pantallas se conectan mediante **rutas de navegación tipadas**, en lugar del patrón de cadenas de texto que usaste en el capítulo 35.
