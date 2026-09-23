# Capítulo 57: Componentes compartidos y tema visual

## Introducción

A lo largo de los capítulos 53 a 55 usamos varios composables sin detenernos en ellos: `AvatarContacto`, `EstadoCargando`, `EstadoError`, `EstadoVacio`, `mensajeDe`. Son piezas **reutilizadas por las tres pantallas** de la aplicación, y vivir en `ui/componentes/` en vez de duplicarse en cada pantalla es lo que evita, por ejemplo, que la lista y el detalle dibujen el avatar de forma ligeramente distinta.

En este capítulo cerramos la interfaz de «Mis Contactos» revisando esos componentes compartidos y el tema visual (`Theme.kt`, `Color.kt`) que los envuelve a todos.

## El avatar: `AvatarContacto`

```kotlin
package com.ejemplo.miscontactos.ui.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ejemplo.miscontactos.model.Contacto

@Composable
fun AvatarContacto(contacto: Contacto, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = contacto.iniciales,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        AsyncImage(
            model = "https://i.pravatar.cc/300?u=contacto-${contacto.id}",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
        )
    }
}
```

`AsyncImage` viene de **Coil 3** (`coil3.compose.AsyncImage`), la biblioteca de carga de imágenes que ya se declaró en las dependencias del capítulo 49. Su trabajo es descargar la foto, guardarla en caché y dibujarla, todo con una sola función composable, sin que tengas que gestionar hilos ni bitmaps manualmente.

Fíjate en el orden de los dos elementos dentro del `Box`: primero el `Text` con las iniciales, **luego** la `AsyncImage` encima. Mientras la imagen todavía no termina de descargarse (o si la URL falla), el texto de iniciales queda visible detrás; en cuanto `AsyncImage` completa la carga, la tapa por completo. Es un *fallback* simple, sin necesidad de observar el estado de carga de Coil explícitamente.

`contentScale = ContentScale.Crop` recorta la imagen para llenar el círculo sin deformarla, igual que harías con un `object-fit: cover` en CSS.

## Estados reutilizables: `Estados.kt`

Las tres pantallas necesitan mostrar, en algún momento, "está cargando", "algo falló" o "no hay nada que mostrar". En lugar de que cada una redibuje su propia versión, `Estados.kt` centraliza estos tres casos:

```kotlin
@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun EstadoError(
    error: ErrorDatos,
    onReintentar: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(mensajeDe(error), textAlign = TextAlign.Center)
        if (onReintentar != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReintentar) { Text(stringResource(R.string.reintentar)) }
        }
    }
}

@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(mensaje, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
```

`EstadoError` recibe `onReintentar: (() -> Unit)?` como **función opcional**: cuando es `null` (como en la pantalla de detalle del capítulo 54, donde reintentar simplemente recargaría de nuevo el mismo `id` sin que tenga mucho sentido exponer un botón separado), el botón de reintentar no se dibuja. Es el mismo principio de "el estado decide qué mostrar" que ya aplicaste con `contactosVisibles` en el capítulo 53, llevado a un parámetro de función en vez de a una propiedad.

`EstadoVacio` recibe el mensaje ya armado como `String`, en lugar de un recurso, precisamente porque —como viste en el capítulo 53— ese mensaje cambia según el contexto (favoritos vacíos, búsqueda sin resultados, lista realmente vacía), y es la pantalla que lo llama quien decide cuál de los tres aplica.

### `mensajeDe`: traducir errores a texto

```kotlin
@Composable
fun mensajeDe(error: ErrorDatos): String = when (error) {
    is ErrorDatos.SinConexion -> stringResource(R.string.error_sin_conexion)
    is ErrorDatos.NoEncontrado -> stringResource(R.string.error_no_encontrado)
    is ErrorDatos.Conflicto -> error.message ?: stringResource(R.string.error_desconocido)
    is ErrorDatos.Validacion -> stringResource(R.string.error_validacion)
    is ErrorDatos.Desconocido -> stringResource(R.string.error_desconocido)
}
```

Esta función ya la mencionamos en el capítulo 46: como `ErrorDatos` es una `sealed class`, el `when` es **exhaustivo** — si en el futuro se agrega una nueva variante de error, el compilador obliga a manejarla aquí también, evitando que un caso quede sin traducción. Se usa tanto en `EstadoError` como directamente en los `Snackbar` de las pantallas de detalle y formulario (capítulos 54 y 55).

## El permiso de red local: `PermisoRedLocal`

Ya viste este composable completo en el capítulo 49, pero vale la pena recordar por qué vive en `ui/componentes/` junto a los demás: aunque solo se usa una vez (envolviendo todo `ContactosNavHost` desde `MainActivity`), es un componente genérico —no conoce contactos ni pantallas específicas— y encaja mejor junto al resto de piezas reutilizables que dentro de una carpeta de una sola pantalla.

## El tema visual: `Theme.kt` y `Color.kt`

```kotlin
// Color.kt
val Azul = Color(0xFF1E5AA8)
val AzulClaro = Color(0xFFB8D0F0)
val Turquesa = Color(0xFF00897B)
val TurquesaClaro = Color(0xFF80CBC4)
val Error = Color(0xFFBA1A1A)
val ErrorClaro = Color(0xFFFFDAD6)
```

```kotlin
// Theme.kt
private val LightColorScheme = lightColorScheme(
    primary = Azul,
    primaryContainer = AzulClaro,
    secondary = Turquesa,
    secondaryContainer = TurquesaClaro,
    error = Error,
    errorContainer = ErrorClaro
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    secondary = TurquesaClaro,
    error = ErrorClaro
)

@Composable
fun MisContactosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = Typography,
        content = content
    )
}
```

Este es el mismo patrón de tema Material 3 que estudiaste en el capítulo 36: una paleta de colores propia de la app (`LightColorScheme`/`DarkColorScheme`, construidas a partir de las constantes de `Color.kt`) y, opcionalmente, **color dinámico** en Android 12 (API 31, `Build.VERSION_CODES.S`) en adelante, que deriva la paleta del fondo de pantalla del usuario en lugar de usar los colores fijos de la marca. Aquí `dynamicColor` tiene su valor por defecto en `false`: «Mis Contactos» prioriza su identidad visual propia (azul y turquesa) sobre adaptarse al *wallpaper* de cada dispositivo, pero deja la puerta abierta a activarlo con un simple cambio de parámetro.

`MainActivity` envuelve toda la interfaz en `MisContactosTheme { ... }`, por lo que cualquier composable de la app —`ContactoItem`, `FichaContacto`, `CampoTexto`, etc.— accede a estos colores simplemente a través de `MaterialTheme.colorScheme`, sin tener que recibirlos como parámetro.

## Resumen

- `ui/componentes/` reúne las piezas que usa más de una pantalla: `AvatarContacto` (foto con Coil 3 sobre iniciales de respaldo), `Estados.kt` (`EstadoCargando`/`EstadoError`/`EstadoVacio`/`mensajeDe`) y `PermisoRedLocal` (capítulo 49).
- `AvatarContacto` superpone `AsyncImage` sobre un `Text` de iniciales: mientras la imagen no carga, las iniciales quedan visibles como *fallback*.
- `EstadoError` acepta un `onReintentar` **opcional**, dejando que cada pantalla decida si ofrece o no un botón de reintento.
- `mensajeDe(error: ErrorDatos)` centraliza, con un `when` exhaustivo, la traducción de cada variante de error a un texto localizado.
- El tema (`Theme.kt`/`Color.kt`) sigue el patrón Material 3 del capítulo 36: paleta propia por defecto, con soporte opcional para color dinámico desde Android 12.

Con esto completamos el recorrido por la interfaz de «Mis Contactos». En el capítulo final cerraremos el proyecto con una revisión general, algunas pruebas manuales sugeridas y los próximos pasos sugeridos para seguir aprendiendo.
