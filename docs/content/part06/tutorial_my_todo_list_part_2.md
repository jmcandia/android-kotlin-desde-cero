# Tutorial: Mi lista de tareas (parte 2)

## Qué vamos a construir

En la primera parte construiste una lista de tareas funcional con una sola pantalla. Ahora vamos a retomar ese mismo proyecto justo donde lo dejamos: la aplicación ya puede agregar, completar y eliminar tareas, pero todavía tiene dos límites:

- toda la interfaz vive en una única pantalla;
- usa los colores y la tipografía que Android Studio generó por defecto.

En esta segunda parte vas a aplicar lo aprendido en los capítulos 32 y 33 para mejorarla sin volver a empezar. Al terminar, tu app tendrá:

- una pantalla de lista y una pantalla de detalle;
- navegación entre ambas con **Navigation Compose**;
- una barra superior con botón de retroceso en el detalle;
- un esquema de colores propio para los modos claro y oscuro;
- una tipografía coherente con la jerarquía visual de la app;
- el estado de las tareas elevado a un composable contenedor que coordina las pantallas.

> [!NOTE]Nota
> Parte de la aplicación que ya tienes funciona exactamente igual que antes. Conservaremos `Tarea.kt`, el ícono, los textos y la lógica de agregar, completar y eliminar. Solo reorganizaremos la pantalla y añadiremos la navegación y el tema.

## Paso 1: Preparar el proyecto de la parte 1

Abre el proyecto `MiListaDeTareas` que construiste en la primera parte y ejecútalo una vez. Antes de cambiar nada, comprueba que puedes:

1. Agregar una tarea.
2. Marcarla como completada.
3. Eliminarla.

Si algo de ese flujo no funciona, corrígelo primero. Esta parte comienza con una aplicación que ya compila y tiene el modelo `Tarea` creado.

## Paso 2: Agregar Navigation Compose

Navigation Compose no forma parte de todos los proyectos que genera Android Studio. Abre `build.gradle.kts (Module :app)` y agrega la dependencia dentro de `dependencies`:

```kotlin
dependencies {
    // ...las dependencias que ya tiene tu proyecto
    implementation("androidx.navigation:navigation-compose:2.8.9")
}
```

La versión puede ser distinta cuando leas este tutorial. Si Android Studio sugiere una versión más reciente compatible con tu proyecto, usa esa. También puedes declararla en el catálogo de versiones (`gradle/libs.versions.toml`) y añadirla con `implementation(libs.androidx.navigation.compose)`, como viste en el capítulo 25.

Pulsa **Sync Now** y espera a que termine la sincronización. Los imports que usarás después pertenecen principalmente a estos paquetes:

```kotlin
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
```

## Paso 3: Definir los nuevos textos

Como en la primera parte, deja los textos de la interfaz en `strings.xml`. Conserva las entradas que ya tienes y agrega estas:

```xml
<string name="boton_ver_detalle">Ver detalle</string>
<string name="titulo_detalle">Detalle de la tarea</string>
<string name="descripcion_volver">Volver</string>
<string name="tarea_no_encontrada">La tarea ya no existe.</string>
<string name="estado_completada">Completada</string>
<string name="estado_pendiente">Pendiente</string>
```

No es necesario traducir rutas como `"lista"` o `"detalle/{tareaId}"`: son identificadores internos de navegación, no textos que vea el usuario.

## Paso 4: Darle una identidad visual al tema

El proyecto ya tiene una carpeta `ui/theme/` con archivos parecidos a `Color.kt`, `Theme.kt` y `Type.kt`. Vamos a personalizarla con una combinación sobria de verde azulado y coral: el primer color identifica las acciones principales y el segundo sirve como acento.

En `Color.kt`, define los colores del tema:

```kotlin
import androidx.compose.ui.graphics.Color

val VerdeAzulado = Color(0xFF006A6A)
val VerdeAzuladoClaro = Color(0xFF4F9D9D)
val Coral = Color(0xFF9C3F32)
val CoralClaro = Color(0xFFFFB4A8)
val FondoClaro = Color(0xFFF7FAF9)
val FondoOscuro = Color(0xFF101414)
```

En `Theme.kt`, reemplaza los esquemas claro y oscuro usando **roles** de Material, no colores sueltos en cada composable:

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary = VerdeAzuladoClaro,
    secondary = CoralClaro,
    background = FondoOscuro,
    surface = Color(0xFF191C1C),
    onPrimary = Color(0xFF003737),
    onSecondary = Color(0xFF5F160D),
    onBackground = Color(0xFFE0E3E2),
    onSurface = Color(0xFFE0E3E2)
)

private val LightColorScheme = lightColorScheme(
    primary = VerdeAzulado,
    secondary = Coral,
    background = FondoClaro,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF191C1C),
    onSurface = Color(0xFF191C1C)
)
```

Los nombres exactos del tema generado pueden variar. Lo importante es que `lightColorScheme` y `darkColorScheme` sean los esquemas que utiliza el composable del tema.

## Paso 5: Personalizar la tipografía

En `Type.kt` puedes conservar la fuente predeterminada y ajustar los estilos que la aplicación necesita. Por ejemplo, crea una tipografía con títulos algo más destacados:

```kotlin
val MiTipografia = Typography(
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp
    )
)
```

Después, en el `MaterialTheme` de `Theme.kt`, asigna esta tipografía junto con el esquema de colores:

```kotlin
@Composable
fun MiListaDeTareasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MiTipografia, // ← Modifica esa parte
        content = content
    )
}
```

El parámetro `isSystemInDarkTheme()` hace que la app siga la preferencia del dispositivo. Como todos los componentes usan `MaterialTheme.colorScheme` y `MaterialTheme.typography`, el cambio se aplica a toda la interfaz.

> [!WARNING]Advertencia
> No pongas `Color(0xFF...)` directamente en cada `Text`, `Card` o `Button`. Si usas los roles de `MaterialTheme`, el modo oscuro y los cambios futuros de diseño serán mucho más sencillos.

## Paso 6: Elevar el estado al nivel de la app

En la primera parte, `ListaTareasScreen` guardaba `tareas` y `textoNuevaTarea`. Ahora habrá dos pantallas que necesitan consultar o modificar la misma lista. Si cada pantalla tuviera su propia copia, la lista y el detalle podrían quedar desincronizados.

Para mantener cada archivo con una responsabilidad clara, crea un archivo llamado `App.kt` en el mismo paquete donde está `MainActivity.kt`. En la vista **Project** de Android Studio, haz clic derecho sobre ese paquete, elige **New > Kotlin Class/File**, escribe `App` y selecciona **File**. Según la vista del proyecto, la ruta será parecida a `app/src/main/java/com/ejemplo/milistadetareas/App.kt` (o aparecerá dentro de `kotlin+java`). Conserva la misma línea `package ...` que tiene `MainActivity.kt`.

En ese archivo `App.kt`, crea el composable padre `App`, que coordinará la navegación y las tareas:

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()
    var tareas by remember { mutableStateOf(listOf<Tarea>()) }

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            ListaTareasScreen(
                tareas = tareas,
                onAgregar = { texto ->
                    if (texto.isNotBlank()) {
                        tareas = tareas + Tarea(texto = texto)
                    }
                },
                onCambiarCompletada = { id, completada ->
                    tareas = tareas.map { tarea ->
                        if (tarea.id == id) {
                            tarea.copy(completada = completada)
                        } else {
                            tarea
                        }
                    }
                },
                onEliminar = { id ->
                    tareas = tareas.filter { it.id != id }
                },
                onVerDetalle = { id ->
                    navController.navigate("detalle/$id")
                }
            )
        }
    }
}
```

Todavía falta declarar el destino del detalle; lo haremos en el paso 8. Observa mientras tanto el cambio importante: `App` posee la lista y las acciones, y `ListaTareasScreen` recibirá datos y funciones. Es el mismo *state hoisting* del capítulo 31, aplicado ahora a más de una pantalla.

## Paso 7: Convertir la lista en un composable sin estado

En `ListaTareasScreen`, elimina las declaraciones locales de `tareas` y `textoNuevaTarea`. La función recibirá la lista y las acciones mediante parámetros. El estado del texto del formulario sí puede quedarse dentro de esta pantalla, porque ninguna otra pantalla necesita conocerlo:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    tareas: List<Tarea>,
    onAgregar: (String) -> Unit,
    onCambiarCompletada: (String, Boolean) -> Unit,
    onEliminar: (String) -> Unit,
    onVerDetalle: (String) -> Unit
) {
    var textoNuevaTarea by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_pantalla)) }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = textoNuevaTarea,
                    onValueChange = { textoNuevaTarea = it },
                    label = { Text(stringResource(R.string.etiqueta_nueva_tarea)) },
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        if (textoNuevaTarea.isNotBlank()) {
                            onAgregar(textoNuevaTarea)
                            textoNuevaTarea = ""
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(stringResource(R.string.boton_agregar))
                }
            }

            if (tareas.isEmpty()) {
                Text(
                    text = stringResource(R.string.mensaje_lista_vacia),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onCambiarCompletada = onCambiarCompletada,
                            onEliminar = onEliminar,
                            onVerDetalle = onVerDetalle
                        )
                    }
                }
            }
        }
    }
}
```

El componente de cada tarea puede quedar separado para que la pantalla se lea con más facilidad:

```kotlin
@Composable
private fun TareaItem(
    tarea: Tarea,
    onCambiarCompletada: (String, Boolean) -> Unit,
    onEliminar: (String) -> Unit,
    onVerDetalle: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onVerDetalle(tarea.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { marcada ->
                    onCambiarCompletada(tarea.id, marcada)
                }
            )
            Text(
                text = tarea.texto,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { onEliminar(tarea.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.descripcion_eliminar)
                )
            }
        }
    }
}
```

Ahora tocar la tarjeta abre el detalle, mientras que el `Checkbox` y el botón de eliminar siguen ejecutando sus acciones sin que la pantalla conozca cómo se modifica la lista. El uso de `key = { it.id }` ayuda a que `LazyColumn` identifique cada elemento de forma estable.

## Paso 8: Crear la pantalla de detalle

Añade un composable para mostrar una tarea concreta. Recibe la tarea ya encontrada y una función para volver; no necesita conocer el `NavController`.

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleTareaScreen(
    tarea: Tarea,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalle)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.descripcion_volver)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = tarea.texto,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = if (tarea.completada) {
                    stringResource(R.string.estado_completada)
                } else {
                    stringResource(R.string.estado_pendiente)
                },
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
```

Fíjate en que la pantalla usa roles del tema (`headlineSmall`, `titleMedium` y `secondary`) en lugar de decidir tamaños y colores aislados. Por eso la pantalla de detalle también se adapta al diseño que definiste en los pasos 4 y 5.

## Paso 9: Declarar la ruta con argumento

Vuelve a `App.kt` y completa el `NavHost` de `App` con el destino del detalle. La ruta incluye el identificador de la tarea entre llaves, y `navArgument` indica que es un `String`:

```kotlin
NavHost(
    navController = navController,
    startDestination = "lista"
) {
    composable("lista") {
        ListaTareasScreen(
            tareas = tareas,
            onAgregar = { texto ->
                if (texto.isNotBlank()) {
                    tareas = tareas + Tarea(texto = texto)
                }
            },
            onCambiarCompletada = { id, completada ->
                tareas = tareas.map {
                    if (it.id == id) it.copy(completada = completada) else it
                }
            },
            onEliminar = { id ->
                tareas = tareas.filter { it.id != id }
            },
            onVerDetalle = { id ->
                navController.navigate("detalle/$id")
            }
        )
    }
    composable(
        route = "detalle/{tareaId}",
        arguments = listOf(
            navArgument("tareaId") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val tareaId = backStackEntry.arguments?.getString("tareaId")
        val tarea = tareas.firstOrNull { it.id == tareaId }

        if (tarea != null) {
            DetalleTareaScreen(
                tarea = tarea,
                onVolver = { navController.popBackStack() }
            )
        } else {
            Text(stringResource(R.string.tarea_no_encontrada))
        }
    }
}
```

La ruta de declaración (`"detalle/{tareaId}"`) y la ruta real (`"detalle/$id"`) tienen papeles distintos:

- Las llaves declaran el nombre del argumento que la pantalla acepta.
- El signo `$` inserta el valor concreto al navegar.
- `popBackStack()` quita el detalle de la pila y muestra la lista anterior.

El botón de retroceso del sistema también funciona porque Navigation Compose mantiene esa pila automáticamente.

## Paso 10: Conectar todo desde `MainActivity`

Finalmente, `MainActivity` solo debe instalar el tema y mostrar `App`. La actividad ya no necesita conocer las pantallas concretas:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiListaDeTareasTheme {
                App()
            }
        }
    }
}
```

Esta separación es deliberada: `MainActivity` inicia la interfaz, `MiListaDeTareasTheme` define su apariencia y `App` coordina el estado y las rutas.

## Paso 11: Ejecutar y probar

Ejecuta la app en modo claro y, después, activa el modo oscuro del emulador o dispositivo. Comprueba este flujo:

1. Agrega dos tareas.
2. Toca una tarjeta: debe abrirse `DetalleTareaScreen` con su texto y estado.
3. Pulsa la flecha de la barra superior y verifica que vuelves a la lista.
4. Marca una tarea como completada, abre su detalle y comprueba que el estado cambió.
5. Elimina una tarea y verifica que ya no puedes abrir su detalle desde la lista.
6. Cambia entre modo claro y oscuro: los componentes deben conservar contraste y los colores deben cambiar según el esquema.
7. Gira el dispositivo mientras escribes una tarea: el texto debe conservarse gracias a `rememberSaveable`.

## Resumen

En esta segunda parte convertiste la lista de tareas en una app con dos pantallas y una identidad visual propia:

- Agregaste **Navigation Compose** y definiste un `NavHost` con las rutas `lista` y `detalle/{tareaId}`.
- Elevaste la lista de tareas al composable `App`, que ahora es la única fuente de verdad para las dos pantallas.
- Convertiste `ListaTareasScreen` en un composable que recibe estado y eventos, siguiendo *state hoisting*.
- Creaste una pantalla de detalle que recibe una tarea y una función `onVolver`, sin depender directamente de `NavController`.
- Personalizaste el tema con `lightColorScheme`, `darkColorScheme`, roles de color y una tipografía común.
- Usaste `rememberSaveable` para conservar el texto del formulario durante una recreación de la actividad.

La aplicación todavía guarda las tareas solo en memoria: si el proceso termina, se pierden. Eso es intencional. En la Parte VII moverás el estado y la lógica a un `ViewModel` con MVVM, y más adelante podrás conectar una fuente de datos real sin cargar esa responsabilidad en los composables.
