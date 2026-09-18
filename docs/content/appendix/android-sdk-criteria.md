# Anexo C: Cómo elegir el SDK de un proyecto Android

## Introducción

Cuando creaste tu primer proyecto, en el [capítulo 25](../part06/chapter25.md), Android Studio te pidió un **Minimum SDK** y siguió de largo con un valor razonable. Pero esa pantalla en realidad esconde **tres números** distintos, cada uno con un propósito distinto, y elegirlos bien tiene consecuencias reales: cuántos dispositivos pueden instalar tu app, qué versiones de Android puedes aprovechar y si Google Play te dejará publicarla.

Este anexo explica esos tres números —**`minSdk`**, **`targetSdk`** y **`compileSdk`**— y los criterios para elegir cada uno.

> [!NOTE]Nota
> "SDK" aquí se refiere al **nivel de API** de Android (*API level*), un número entero que identifica una versión de la plataforma (por ejemplo, Android 14 es la API 34). Cada nueva versión de Android agrega funciones nuevas identificadas por su nivel de API.

## `minSdk`: la versión más antigua que soportas

El **`minSdk`** (Minimum SDK) es la versión **más vieja** de Android en la que tu app puede instalarse. Un dispositivo con una versión **anterior** a la que elijas ni siquiera podrá descargar tu app desde la tienda.

Esto es una decisión de **compromiso**:

- Un `minSdk` **bajo** (por ejemplo, API 21) llega a **más dispositivos**, incluidos los más antiguos, pero te obliga a evitar (o a verificar en tiempo de ejecución) las funciones que solo existen en versiones más nuevas de Android.
- Un `minSdk` **alto** te da acceso directo a las funciones más recientes de la plataforma, con menos casos especiales que programar, pero **deja afuera** a los dispositivos más antiguos.

Android Studio te ayuda a decidir: en el asistente de creación de proyecto, junto al selector de **Minimum SDK**, se muestra el **porcentaje de dispositivos activos** que quedarían cubiertos con cada valor, calculado a partir de las estadísticas de Google Play. Antes de publicar una app real, conviene revisar esas estadísticas actualizadas (o el "Android Studio Target and device support" del propio IDE) en lugar de guiarte por un número fijo, porque la distribución de versiones cambia con el tiempo.

Para este curso, usamos **API 24 (Android 7.0)** como piso: es lo bastante moderno para trabajar sin sobresaltos con Jetpack Compose y las bibliotecas de AndroidX, y a la vez cubre a la gran mayoría de los dispositivos en uso.

### ¿Y si necesito una función que no existe en mi `minSdk`?

Puedes seguir usándola, pero debes **protegerla** verificando la versión en tiempo de ejecución con `Build.VERSION.SDK_INT`:

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // código que solo corre en Android 13 (API 33) o superior
} else {
    // alternativa para versiones anteriores
}
```

Muchas bibliotecas de **AndroidX** (incluida Compose) ya resuelven este problema por ti "por detrás", ofreciendo una misma API que funciona en todas las versiones soportadas por tu `minSdk`.

## `targetSdk`: la versión para la que preparaste tu app

El **`targetSdk`** (Target SDK) le indica al sistema **para qué versión de Android probaste y adaptaste** tu app. No limita en qué dispositivos se instala (eso lo hace `minSdk`); en cambio, activa los comportamientos y las políticas de esa versión de Android en tu app.

Cuando Android lanza una versión nueva, suele cambiar comportamientos por defecto (permisos más estrictos, restricciones de batería, etc.). Esos cambios solo se activan en tu app si tu `targetSdk` es **igual o mayor** a la versión en la que se introdujeron; así, una app antigua no se rompe de golpe al instalarse en un teléfono con una versión de Android más nueva.

> [!IMPORTANT]
> Google Play **exige** que las apps nuevas y las actualizaciones apunten a un `targetSdk` reciente (en general, el de la última versión mayor de Android, o como máximo la anterior). Una app con un `targetSdk` demasiado antiguo directamente no se puede publicar ni actualizar.

Por eso, la recomendación práctica es simple: mantén el `targetSdk` en la **última versión estable** de Android disponible al momento de publicar, y revísalo cada vez que salga una nueva.

## `compileSdk`: las herramientas con las que compilas

El **`compileSdk`** (Compile SDK) indica con **qué versión de las APIs de Android** compila tu proyecto: qué clases, métodos y anotaciones tienes disponibles para escribir código. No afecta en qué dispositivos se instala tu app (eso es `minSdk`) ni qué comportamientos se activan en tiempo de ejecución (eso es `targetSdk`); es, sencillamente, la versión de las herramientas con la que **construyes**.

La recomendación aquí es la más sencilla de las tres: usa siempre el **`compileSdk` más reciente disponible** en Android Studio. Compilar contra una versión nueva no le exige nada a tus usuarios y te da acceso a las últimas APIs y a las advertencias del *linter* más actualizadas, aunque tu `minSdk` sea mucho más bajo.

## Relación entre los tres

Como regla general, siempre se cumple:

```text
minSdk ≤ targetSdk ≤ compileSdk
```

Es decir: no puedes apuntar (`target`) a una versión más nueva que aquella con la que compilas, ni soportar (`min`) una versión más nueva que a la que apuntas. En Android Studio, estos tres valores se configuran en el `build.gradle.kts` del módulo `app`:

```kotlin
android {
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        targetSdk = 35
    }
}
```

## ¿Dónde se ven y se configuran estos valores?

La fuente de la verdad es siempre el `build.gradle.kts` del módulo `app`, como en el ejemplo anterior: ahí es donde **defines** los tres valores, y ahí es donde debes ir si quieres cambiarlos.

Si prefieres una vista más visual, Android Studio también los muestra en **File > Project Structure… > Modules**, seleccionando el módulo `app`:

- En la pestaña **Properties** encontrarás el **Compile Sdk Version**.
- En la pestaña **Default Config** encontrarás el **Min SDK Version** y el **Target SDK Version**.

Cambiar un valor ahí actualiza automáticamente el `build.gradle.kts` (y viceversa: son la misma configuración, mostrada de dos formas distintas).

Dos lugares más donde te los vas a encontrar, aunque no los edites ahí:

- El **Merged Manifest** (una pestaña en la parte inferior del editor cuando tienes abierto `AndroidManifest.xml`) muestra el resultado final, incluida la etiqueta `<uses-sdk android:minSdkVersion="..." android:targetSdkVersion="..." />` que Gradle genera a partir de tu configuración.
- En **tiempo de ejecución**, dentro de tu propio código, `Build.VERSION.SDK_INT` te dice la versión de Android del **dispositivo** donde se está ejecutando la app en ese momento; no es un valor del proyecto, sino del teléfono o emulador, y es el que usas en los chequeos como los de la sección anterior.

## Tabla de referencia

Estas son algunas versiones de Android y su nivel de API, como referencia (la lista completa y siempre actualizada está en la documentación oficial de Android):

| Versión de Android | Nivel de API |
| :--- | :--- |
| Android 7.0 (Nougat) | 24 |
| Android 8.0 (Oreo) | 26 |
| Android 10 | 29 |
| Android 12 | 31 |
| Android 13 | 33 |
| Android 14 | 34 |
| Android 15 | 35 |

## Resumen

- El nivel de **API** identifica una versión de Android con un número entero.
- **`minSdk`**: la versión más antigua que tu app soporta; más bajo significa más alcance, pero más casos especiales que manejar. Android Studio te muestra el porcentaje de dispositivos cubiertos al elegirlo.
- **`targetSdk`**: la versión para la que probaste y adaptaste tu app; activa los comportamientos de esa versión de Android. Google Play exige mantenerlo reciente para poder publicar.
- **`compileSdk`**: la versión de las APIs con la que compilas; conviene usar siempre la más reciente disponible, sin que esto afecte a tus usuarios.
- Siempre se cumple `minSdk ≤ targetSdk ≤ compileSdk`.
- Los tres se definen en el `build.gradle.kts` del módulo `app`, y también se pueden ver y editar desde **File > Project Structure… > Modules** en Android Studio.
