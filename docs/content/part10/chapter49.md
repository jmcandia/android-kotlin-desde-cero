# Capítulo 49: Configuración del proyecto y permisos

## Introducción

Para construir «Mis Contactos» necesitamos configurar las dependencias del proyecto en Gradle, registrar los permisos en el manifiesto y preparar el punto de entrada de la aplicación. Además, nos enfrentaremos a un requisito fundamental introducido en **Android 17 (API 37)**: el permiso en tiempo de ejecución para acceder a la **red local** (`ACCESS_LOCAL_NETWORK`), indispensable para que el emulador se comunique con la API que corre en tu computador (`10.0.2.2`).

En este capítulo configuraremos el catálogo de versiones, los archivos Gradle, los permisos del sistema, la clase `Application` con Hilt y la `MainActivity`.

## Catálogo de versiones y dependencias

Todas las bibliotecas utilizadas en la app se declaran en el catálogo `gradle/libs.versions.toml`:

```toml
[versions]
agp = "9.4.1"
kotlin = "2.4.20"
ksp = "2.3.12"
coreKtx = "1.19.0"
activityCompose = "1.13.0"
composeBom = "2026.09.00"
lifecycle = "2.11.0"
navigationCompose = "2.10.1"
materialIcons = "1.7.8"
hilt = "2.60.1"
hiltLifecycleViewmodelCompose = "1.4.0"
retrofit = "3.0.0"
okhttp = "4.12.0"
kotlinxSerialization = "1.11.0"
room = "2.8.5"
coil = "3.6.3"
coroutines = "1.11.0"
junit = "4.13.2"
androidxJunit = "1.3.0"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-material3 = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-material-icons-core = { group = "androidx.compose.material", name = "material-icons-core", version.ref = "materialIcons" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycle" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycle" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycle" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
androidx-hilt-lifecycle-viewmodel-compose = { group = "androidx.hilt", name = "hilt-lifecycle-viewmodel-compose", version.ref = "hiltLifecycleViewmodelCompose" }
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlinx-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp-logging-interceptor = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
coil-compose = { group = "io.coil-kt.coil3", name = "coil-compose", version.ref = "coil" }
coil-network-okhttp = { group = "io.coil-kt.coil3", name = "coil-network-okhttp", version.ref = "coil" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
kotlinx-coroutines-test = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-test", version.ref = "coroutines" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "androidxJunit" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
hilt = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
room = { id = "androidx.room", version.ref = "room" }
```

En el archivo `app/build.gradle.kts` activamos los plugins y agregamos las dependencias:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
}

android {
    namespace = "com.ejemplo.miscontactos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.ejemplo.miscontactos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    // Compose y ciclo de vida
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // Inyección de dependencias (Hilt)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    // Red (Retrofit + OkHttp + Serialización)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Persistencia local (Room)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Carga de imágenes remotas (Coil 3)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
```

> [!NOTE]Nota
> Usamos `androidx.hilt:hilt-lifecycle-viewmodel-compose` para la función `hiltViewModel()`, ya que la antigua dependencia `hilt-navigation-compose` quedó obsoleta.

## Manifiesto y seguridad de red

En `app/src/main/AndroidManifest.xml` declaramos la clase de aplicación, el tema, la configuración de red y los dos permisos necesarios:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permiso estándar para realizar peticiones de red -->
    <uses-permission android:name="android.permission.INTERNET" />

    <!-- Android 17 (API 37): necesario para comunicarse con la red local (10.0.2.2) -->
    <uses-permission android:name="android.permission.ACCESS_LOCAL_NETWORK" />

    <application
        android:name=".MisContactosApp"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:networkSecurityConfig="@xml/network_security_config"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.MisContactos">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:theme="@style/Theme.MisContactos"
            android:windowSoftInputMode="adjustResize">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

Observa tres detalles clave:
1. `android:name=".MisContactosApp"` conecta la clase `Application` que inicializa Hilt.
2. `android:windowSoftInputMode="adjustResize"` permite que la pantalla se desplace automáticamente cuando se abre el teclado virtual en los formularios.
3. `android:networkSecurityConfig="@xml/network_security_config"` apunta al archivo `app/src/main/res/xml/network_security_config.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <!-- Permite HTTP sin cifrar solo hacia la API local del curso (10.0.2.2) -->
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="false">10.0.2.2</domain>
    </domain-config>
</network-security-config>
```

## La clase Application con Hilt

Creamos `MisContactosApp.kt` anotada con `@HiltAndroidApp`. Como aprendiste en el capítulo 42, esta clase es el disparador del árbol de dependencias de Hilt a nivel de toda la aplicación:

```kotlin
package com.ejemplo.miscontactos

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Punto de entrada de la aplicación para Hilt.
 * Genera el componente raíz a nivel de SingletonComponent.
 */
@HiltAndroidApp
class MisContactosApp : Application()
```

## Permiso de red local en tiempo de ejecución (Android 17+)

A partir de Android 17 (API 37), el sistema exige que el usuario conceda explícitamente el permiso `ACCESS_LOCAL_NETWORK` para comunicarse con servidores locales (como `10.0.2.2`). Sin él, OkHttp falla con un `SocketTimeoutException` silencioso.

Para manejarlo limpiamente creamos un composable en `ui/componentes/PermisoRedLocal.kt`:

```kotlin
package com.ejemplo.miscontactos.ui.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ejemplo.miscontactos.R

/**
 * En Android 17 (API 37)+, pide el permiso de red local si todavía no fue concedido.
 * En versiones anteriores no hace nada y muestra el contenido directamente.
 */
@Composable
fun PermisoRedLocal(
    contenido: @Composable () -> Unit
) {
    val context = LocalContext.current
    val permiso = Manifest.permission.ACCESS_LOCAL_NETWORK

    val necesitaPermiso = Build.VERSION.SDK_INT >= 37
    var concedido by remember {
        mutableStateOf(
            !necesitaPermiso ||
                ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { resultado ->
        concedido = resultado
    }

    if (concedido) {
        contenido()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.permiso_red_local_texto),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(onClick = { launcher.launch(permiso) }) {
                Text(stringResource(R.string.permiso_red_local_boton))
            }
        }
    }
}
```

¿Cómo funciona este composable?
1. Si el dispositivo ejecuta una versión anterior a Android 17 (`Build.VERSION.SDK_INT < 37`), `necesitaPermiso` es `false`, por lo que `concedido` inicia en `true` y muestra el contenido sin interrumpir al usuario.
2. Si es Android 17 o superior, comprueba si ya fue otorgado con `ContextCompat.checkSelfPermission`.
3. Si no ha sido concedido, muestra una pantalla explicativa con un botón. Al pulsar el botón, `launcher.launch(permiso)` dispara el diálogo nativo del sistema operativo mediante `rememberLauncherForActivityResult`. En cuanto el usuario pulsa «Permitir», `concedido` pasa a `true` y se dibuja el contenido principal.

## La Activity principal

En `MainActivity.kt` anotamos con `@AndroidEntryPoint` para que Hilt pueda inyectar dependencias en cualquier composable dentro de la jerarquía, activamos el soporte de pantalla completa (*edge-to-edge*) y envolvemos la navegación dentro de `PermisoRedLocal`:

```kotlin
package com.ejemplo.miscontactos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ejemplo.miscontactos.ui.componentes.PermisoRedLocal
import com.ejemplo.miscontactos.ui.navigation.ContactosNavHost
import com.ejemplo.miscontactos.ui.theme.MisContactosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisContactosTheme {
                PermisoRedLocal {
                    ContactosNavHost()
                }
            }
        }
    }
}
```

## Resumen

- Centralizamos las versiones y dependencias en `libs.versions.toml` y `app/build.gradle.kts` (Compose BOM, Navigation, Hilt, Retrofit, Room y Coil).
- `MisContactosApp` con `@HiltAndroidApp` inicializa el contenedor de inyección de dependencias en el arranque.
- Configuramos `network_security_config.xml` para habilitar tráfico HTTP sin cifrar exclusivamente hacia la IP especial del emulador (`10.0.2.2`).
- Creamos el composable `PermisoRedLocal` que solicita dinámicamente el permiso de red local en Android 17 (API 37) antes de iniciar la navegación.
- `MainActivity` configura la vista de borde a borde (*edge-to-edge*) y monta `ContactosNavHost`.

En el próximo capítulo implementaremos la capa remota: DTOs, la interfaz de Retrofit y el módulo de red de Hilt.
