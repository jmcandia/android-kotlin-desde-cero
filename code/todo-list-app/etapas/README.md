# Etapas de «Mi lista de tareas»

El proyecto de esta carpeta (`code/todo-list-app/`) es la app de tareas en su **versión final** (etapa 5: Room). Aquí se guardan los archivos de las etapas intermedias, que son la base de los tutoriales de las Partes VIII y IX.

| Etapa | Tutorial | Qué agrega | Contenido de la carpeta |
|---|---|---|---|
| `etapa3/` | Tutorial 3 (tras el cap. 40) | `TareasViewModel` con `StateFlow`, `UiState` como `data class` y Snackbar con «Deshacer». Sin Hilt. | Solo `src/main` (`java/`, `AndroidManifest.xml`, `keepRules/`). Los recursos son iguales a los de la etapa 4. |
| `etapa4/` | Tutorial 4 (tras el cap. 42) | Repositorio en memoria (`TareasRepository`) + Hilt, con pruebas unitarias del `ViewModel`. | `src/` completo, `app.build.gradle.kts`, `build.gradle.kts` (raíz) y `libs.versions.toml`. |
| Proyecto raíz | Tutorial 5 (tras el cap. 47) | Room detrás del mismo repositorio. | — |

Para compilar una etapa intermedia, copia sus archivos sobre una copia de este proyecto: `src/` → `app/src/`, `app.build.gradle.kts` → `app/build.gradle.kts`, `build.gradle.kts` → raíz y `libs.versions.toml` → `gradle/`. La etapa 3 usa los archivos de compilación de la etapa 4, sin las dependencias de Hilt.
