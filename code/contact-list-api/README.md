# Contact List API

API REST para la gestión de una lista de contactos, construida con **Spring Boot**. Es el backend que consume la app Android **MyContactListApp** en la Parte IX del curso *Android con Kotlin desde cero*.

Ofrece operaciones **CRUD** completas sobre contactos, con **búsqueda** y **paginación**, y respuestas en formato **HAL** (HATEOAS).

## Tecnologías

- **Java** (JDK 21 o superior)
- **Spring Boot**
- **Maven** (con *wrapper* incluido)
- **H2**, base de datos en memoria (los datos se reinician cada vez que se arranca la aplicación)

## Requisitos

- Un JDK 21 o superior instalado.
- No necesitas instalar Maven (el proyecto incluye el *wrapper* `mvnw`) ni configurar ninguna base de datos (H2 es en memoria).

## Ejecutar la API

Desde la carpeta del proyecto:

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

> [!WARNING]
> Al usar H2 en memoria, la base de datos se crea vacía en cada arranque y los datos se pierden al detener la aplicación.

## Documentación interactiva (Swagger)

El proyecto expone su documentación OpenAPI con springdoc:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Especificación OpenAPI (JSON)**: `http://localhost:8080/v3/api-docs`

## Endpoints

Todos los recursos cuelgan de `/api/contact`.

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/contact` | Lista los contactos (con búsqueda y paginación). |
| `GET` | `/api/contact/{id}` | Obtiene un contacto por su id. |
| `POST` | `/api/contact` | Crea un contacto. |
| `PUT` | `/api/contact/{id}` | Actualiza un contacto existente. |
| `DELETE` | `/api/contact/{id}` | Elimina un contacto. |

## El modelo Contact

Un contacto tiene los siguientes campos:

| Campo | Tipo | Obligatorio | Ejemplo |
| :--- | :--- | :--- | :--- |
| `firstName` | string | Sí | `Ana` |
| `lastName` | string | Sí | `López` |
| `email` | string (email) | Sí | `ana.lopez@example.com` |
| `phone` | string | No | `555-0101` |
| `address` | string | No | `Calle Principal 123` |
| `city` | string | No | `Santiago` |

Al **crear** o **actualizar** se envía un `ContactRequest` (los campos anteriores). Las respuestas devuelven un `ContactResponse`, que además incluye el `id` y un objeto `_links` (HAL).

## Crear un contacto (ejemplo)

Petición:

```http
POST /api/contact
Content-Type: application/json
```

```json
{
  "firstName": "Ana",
  "lastName": "López",
  "email": "ana.lopez@example.com",
  "phone": "555-0101",
  "address": "Calle Principal 123",
  "city": "Santiago"
}
```

Respuesta (`201 Created`, `application/hal+json`):

```json
{
  "id": 1,
  "firstName": "Ana",
  "lastName": "López",
  "email": "ana.lopez@example.com",
  "phone": "555-0101",
  "address": "Calle Principal 123",
  "city": "Santiago",
  "_links": {
    "self": { "href": "http://localhost:8080/api/contact/1" }
  }
}
```

## Listado, búsqueda y paginación

`GET /api/contact` admite estos parámetros de consulta:

| Parámetro | Descripción |
| :--- | :--- |
| `search` | Texto para filtrar contactos (opcional). |
| `page` | Número de página, empezando en `0`. |
| `size` | Cantidad de elementos por página. |
| `sort` | Campo y sentido de ordenamiento (por ejemplo, `lastName,asc`). |

La respuesta es un `PagedModel` en formato HAL: los contactos vienen dentro de `_embedded.objectList`, y los metadatos de paginación, en `page`.

Ejemplo (`GET /api/contact?page=0&size=2`):

```json
{
  "_embedded": {
    "objectList": [
      { "id": 1, "firstName": "Ana", "lastName": "López", "email": "ana.lopez@example.com" },
      { "id": 2, "firstName": "Diego", "lastName": "Torres", "email": "diego.torres@example.com" }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8080/api/contact?page=0&size=2" }
  },
  "page": {
    "size": 2,
    "totalElements": 42,
    "totalPages": 21,
    "number": 0
  }
}
```

## Formato de errores

Cuando algo falla, la API responde con un `ApiErrorResponse`:

```json
{
  "timestamp": "2024-01-01T00:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found",
  "path": "/api/contact/1",
  "errors": []
}
```

El campo `errors` lista los mensajes de validación cuando la petición trae datos inválidos (por ejemplo, un `email` con formato incorrecto o un campo obligatorio vacío).

## Códigos de estado

| Código | Significado |
| :--- | :--- |
| `200 OK` | Petición correcta (obtener / actualizar). |
| `201 Created` | Contacto creado. |
| `204 No Content` | Contacto eliminado. |
| `400 Bad Request` | Datos inválidos. |
| `404 Not Found` | El contacto no existe. |
| `500 Internal Server Error` | Error del servidor. |

## Uso desde la app Android

La app **MyContactListApp** consume esta API. Ten en cuenta que, **desde el emulador de Android**, `localhost` apunta al propio emulador y no a tu máquina: para llegar a esta API, usa `http://10.0.2.2:8080` en lugar de `http://localhost:8080`.

---

*Parte del curso educativo Android con Kotlin desde cero.*
