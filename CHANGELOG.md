# Changelog

Registro de los hitos más relevantes del curso **"Android con Kotlin desde cero"**. Sigue, de forma simplificada, el formato de [Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/), en orden cronológico descendente.

Este archivo resume los cambios de alto nivel del contenido del curso. Para el detalle sesión a sesión de las decisiones de rediseño, consulta `CONTEXT.md`.

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
