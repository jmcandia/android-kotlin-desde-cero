# Ejercicio de cierre: Parte X

Llegaste al final del curso. Los capítulos 48 a 57 construyeron «Mis Contactos» paso a paso, con guía detallada; este ejercicio es distinto: **no hay guía paso a paso**. Su objetivo es comprobar que puedes transferir, por tu cuenta, el patrón completo que aprendiste (MVVM + Retrofit + Room, offline-first, rutas tipadas) a una funcionalidad nueva.

Toma como referencia los capítulos 50 a 57: cada pieza que necesites (DTO, interfaz de API, repositorio, `ViewModel`, pantalla) ya tiene un equivalente en el proyecto; tu trabajo es replicar el patrón en un caso distinto. Intenta avanzar sin mirar las soluciones parciales de los capítulos y usa este ejercicio como evaluación honesta de lo que dominas.

---

## Ejercicio: Buscar contactos por ciudad

### Descripción

La API del curso (`code/contact-list-api/`) expone contactos con nombre, teléfono, correo y demás campos que ya conoces. Tu tarea es agregar a «Mis Contactos» una **búsqueda por ciudad**: el usuario escribe (o selecciona) una ciudad y la app muestra los contactos que viven en ella.

Para eso deberás:

1. **En la API**: agregar un endpoint que filtre contactos por ciudad (por ejemplo, `GET /api/contact?ciudad=Valparaíso`). Sigue el patrón del `ContactController` existente: recibe el parámetro, delega en el servicio y devuelve la misma estructura de respuesta.
2. **En la app — capa remota**: agregar el método al DTO y a la interfaz de Retrofit que ya usas (capítulo 50), pasando la ciudad como parámetro de consulta.
3. **En la app — repositorio**: exponer una función `buscarPorCiudad(ciudad: String): Result<List<Contacto>>` que combine la respuesta remota con la caché local, siguiendo la estrategia offline-first del capítulo 52.
4. **En la app — interfaz**: crear una nueva pantalla (o sección de la lista) con un campo de búsqueda, estados de *loading* / *success* / *error*, y navegación tipada hacia el detalle de cada contacto (capítulo 56).

### Requisitos

- Respeta la arquitectura existente: la pantalla no conoce Retrofit ni Room; solo habla con el `ViewModel`, y este con el repositorio.
- La búsqueda debe tener *debounce*: no disparar una petición por cada letra (como hiciste en el capítulo 53 con el buscador por nombre).
- Maneja los errores con `Result` (capítulo 14b) y muéstralos en la interfaz sin que la app se detenga.
- Si la ciudad no produce resultados, muestra un estado vacío con un mensaje claro.
- Escribe primero el contrato (la firma de la función del repositorio y el estado `UiState` de la pantalla) y solo después la implementación.

### Criterios de autoevaluación

Antes de dar por terminado el ejercicio, verifica:

- [ ] El endpoint de la API responde correctamente con `curl` para una ciudad con resultados y para una sin ellos.
- [ ] El DTO y la interfaz de Retrofit compilan sin advertencias y la ciudad viaja como parámetro de consulta, no incrustada en la ruta.
- [ ] El repositorio devuelve `Result` y combina red + caché; al detener el servidor, la pantalla sigue mostrando los contactos guardados localmente (con el aviso correspondiente).
- [ ] La nueva pantalla usa `Scaffold`, estados de carga/error/vacío reutilizables (los componentes compartidos del capítulo 57) y rutas tipadas hacia el detalle.
- [ ] Escribir en el campo de búsqueda no lanza una petición por letra (revisa el *log* de la API).

### Pistas (léelas solo si te atascas)

??? tip "Pista 1 — Por dónde empezar"
    Empieza por la API: abre `ContactController.java`, mira cómo está implementado el listado paginado existente y agrega el filtro por ciudad como parámetro opcional del mismo endpoint. No necesitas un endpoint nuevo si el listado ya lo puedes filtrar; decide tú cuál opción queda más limpia y justifícala.

??? tip "Pista 2 — El orden de las capas"
    Sigue el orden del proyecto: primero el DTO y la interfaz de Retrofit (capítulo 50), después el repositorio (52), después el `ViewModel` con su `UiState` (39), y al final la pantalla (53). Si intentas empezar por la pantalla, no tendrás nada que mostrar.

??? tip "Pista 3 — El debounce"
    En el capítulo 53 el buscador por nombre ya implementa *debounce* con un `Flow` y `debounce()`. La búsqueda por ciudad puede reutilizar exactamente el mismo mecanismo, cambiando la función que consulta el repositorio.

> [!NOTE]Nota
> Si terminaste los cuatro puntos sin necesidad de leer las pistas, has logrado el objetivo del curso: transferir de forma autónoma un patrón arquitectónico completo a un caso nuevo. Ese salto —de seguir una guía a trabajar sin ella— es exactamente lo que distinguirá tu próximo proyecto personal.
