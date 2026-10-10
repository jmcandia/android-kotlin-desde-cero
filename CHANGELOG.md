# Changelog

Registro de los hitos más relevantes del curso **"Android con Kotlin desde cero"**. Sigue, de forma simplificada, el formato de [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/), en orden cronológico descendente.

Este archivo resume los cambios de alto nivel del contenido del curso. Para el detalle sesión a sesión de las decisiones de rediseño, consulta `CONTEXT.md`.

## 2026-10-10

### Añadido

- **Capítulo 51 «Preferencias con DataStore»** (Parte IX), capítulo nuevo tras Room: elección entre Room y DataStore, SharedPreferences frente a DataStore, configuración Gradle, delegado `preferencesDataStore`, lectura con `Flow`/`catch`/`map`, escritura con `edit`, repositorio con Hilt y pantalla de ajustes. La Parte X se renumeró en cascada de 51–61 a **52–62**.
- **Sección «Animar cambios de estado»** en el capítulo 34: `animateColorAsState`, `animateDpAsState`, `AnimatedVisibility` y un ejemplo completo, con foco en animar a partir de cambios de estado.

### Cambiado

- Renumeración en cascada de la Parte X (capítulos 51–61 → **52–62**), con H1, `nav` de `mkdocs.yml` y todas las referencias cruzadas actualizadas.
- Glosario: nueva entrada «DataStore»; «Lo que aprenderás» y el hito «Comprueba tu avance» de la Parte IX mencionan DataStore; blurb de la Parte IX en `docs/index.md` actualizado.
- `AGENTS.md`: el rango de capítulos globales pasa de 1–61 a 1–62.

## 2026-10-10

### Añadido

- **Orientación de entrada**: `docs/index.md` reescrito con perfil del estudiante, requisitos previos, tres rutas (desde cero / ya programo / repaso de Kotlin) y una autoevaluación inicial sin calificación.
- **Recuperación activa y cierre por capítulo**: los 61 capítulos incorporan un recuadro «Antes de empezar, recupera lo que ya sabes» y una sección «Para cerrar» con una tarea de aplicación y su respuesta razonada (plegable).
- **Repasos acumulativos** al final de las Partes III, V, VII y IX, con retos que combinan partes anteriores, pistas graduadas y solución comentada; se crearon los archivos de ejercicios de las Partes VII y VIII (esta última con dos ejercicios de repaso).
- **Ayudas graduadas**: se añadió una pista previa a los ejercicios que no la tenían en las Partes I, II, III, IV, V, VI y VIII. Los ejercicios más difíciles y los repasos acumulativos llevan ayuda de dos niveles (pista conceptual + fragmento parcial de estructura).
- **Puntos de decisión** en los cinco tutoriales de «Mi lista de tareas», para que el estudiante anticipe una decisión de diseño antes de leer la solución.
- **Hitos «Comprueba tu avance»** al final de las 10 partes (listas de comprobación con punteros a capítulo) y una **reflexión final** en el cierre del curso (flujo de datos, casos límite y cómo probar una funcionalidad nueva).

### Cambiado

- En `mkdocs.yml` se movió «Ejercicios de repaso» de la Parte VI para que quede después del capítulo 29 (antes estaba entre el 28 y el 29), y se añadieron las entradas de los nuevos archivos de ejercicios de las Partes VII, VIII y IX.
- Corregidas tres referencias desactualizadas en el ejercicio de cierre de la Parte X (pista «El orden de las capas»).
- Reescrito el ejercicio de repaso de la Parte IX para alinearlo con el patrón *offline-first* del curso: usaba el ayudante `llamarApi`/`ErrorDatos` (relanzando `CancellationException`), respaldo a Room solo ante `ErrorDatos.SinConexion`, `observar` como `Flow<Clima?>` con mapeo entidad↔dominio e inyección con Hilt.

## 2026-10-01

### Añadido

- Ejercicios de repaso de fin de bloque para las Partes V, VI y X para integrar andamiaje decreciente (*scaffolding fading*).

### Cambiado

- Se eliminaron los sufijos con letras introduciendo una numeración estrictamente secuencial en cascada, llevando el total del curso a 61 capítulos.
- Se dividieron capítulos densos (Excepciones y Result; Navegación Compose) en capítulos separados más manejables.
- El capítulo conceptual de MVVM (antes 41, al final de la Parte VII) se trasladó a la Parte VI como capítulo 29, justo después de «Cómo se organiza una app», e incorpora el árbol de carpetas propuesto del proyecto con una referencia al capítulo futuro donde se construye cada parte. Los capítulos 29–40 de la Parte VII se renumeraron a 30–41 para dar espacio al traslado.
- Los capítulos de las Partes VII, VIII y IX, y los tutoriales de «Mi lista de tareas», ahora conectan explícitamente su contenido con los conceptos de MVVM introducidos en el capítulo 29, en vez de presentar la arquitectura recién en la Parte VIII.

## 2026-09-29

### Añadido

- Workflow de CI para desplegar el sitio de documentación en GitHub Pages.

## 2026-09-23

### Cambiado

- Se reestructuró el curso en **10 partes** con numeración global de capítulos (antes 9), y se profundizó el contenido de `Result` y la navegación entre capítulos.
- Se consolidó la reestructuración: proyecto final «Mis Contactos» (Parte X), Room como segunda fuente de datos, Hilt para inyección de dependencias, y anexos A-D (principios de diseño, Java→Kotlin, SDK de Android, `String.format`).

## 2026-09-21

### Añadido

- Capítulo sobre diseño adaptable con *Window Size Classes* y mejoras en la navegación del curso.

## 2026-09-18

### Añadido

- Segunda parte del tutorial «Mi lista de tareas» (formularios, validación y navegación).

## 2026-09-17

### Cambiado

- Se reorganizó el contenido de Jetpack Compose (entonces Parte VI) y se añadió el tutorial introductorio de «Mi lista de tareas».

## 2026-08-28

### Añadido

- Ejercicios de repaso para las partes I a IV.

### Cambiado

- Se reorganizaron los capítulos en partes temáticas y se migró el contenido a `docs/content/`.
- Se aplicó la paleta de marca y el diseño personalizado al tema de MkDocs Material.

## 2026-07-31

### Cambiado

- Se reemplazó el proyecto de ejemplo (PokéDex) por una app de gestión de contactos y se agregó la API REST (`code/contact-list-api`) que la respalda.

## 2026-07-29 – 2026-07-30

### Añadido

- Contenido inicial de los capítulos de Jetpack Compose y MVVM (26 a 39).

### Cambiado

- Se migró la estructura de la documentación a MkDocs.
