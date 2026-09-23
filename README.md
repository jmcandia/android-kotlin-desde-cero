# Android con Kotlin desde cero

Curso que lleva a programadores con base en Java desde los fundamentos de **Kotlin** hasta una aplicación **Android** completa con **Jetpack Compose**, construyendo «Mis Contactos», una app de gestión de contactos que consume una API REST con operaciones CRUD completas y persistencia local *offline-first*.

El contenido está escrito en Markdown y se publica como sitio web con [MkDocs](https://www.mkdocs.org) y el tema [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).

## Contenido

Todo el material vive en la carpeta [`docs/`](docs/), organizado por capítulos y partes. La página de inicio del sitio es [`docs/index.md`](docs/index.md).

## El código

La carpeta [`code/`](code/) contiene todo el código del curso:

- [`code/contact-list-api/`](code/contact-list-api/) — la **Contact List API**, el servicio REST que consume la app. Está construida con **Maven**, **Java**, **Spring Boot** y una base de datos en memoria **H2**, así que no necesitas configurar ninguna base de datos externa. Se ejecuta con `./mvnw spring-boot:run` y queda disponible en `http://localhost:8080`. Ofrece operaciones CRUD completas sobre contactos (listar con búsqueda y paginación, ver el detalle, crear, editar y eliminar).
- [`code/contact-list-app/`](code/contact-list-app/) — **«Mis Contactos»**, la app Android que se construye paso a paso en la **Parte X**: MVVM, Jetpack Compose, Retrofit, Room y Hilt.

## Ver el sitio en local

Necesitas Python 3. Instala las dependencias:

```bash
pip install -r requirements.txt
```

Levanta el servidor de desarrollo (con recarga automática):

```bash
mkdocs serve
```

Luego abre `http://127.0.0.1:8000` en tu navegador.

Para generar el sitio estático (en la carpeta `site/`):

```bash
mkdocs build
```

## Estructura

```text
android-kotlin-desde-cero/
├── code/                   # todo el código del curso
│   ├── contact-list-api/   # la API REST (Maven + Java + Spring Boot + H2)
│   └── contact-list-app/   # la app Android «Mis Contactos» (Parte X)
├── docs/                   # todo el contenido del curso
│   ├── assets/             # recursos del contenido
|   |   ├── css/            # estilos personalizados
|   |   ├── images/         # imágenes, organizadas por capítulo
|   │   └── js/             # scripts personalizados
│   ├── content/            # contenido de los capítulos
│   │   ├── partNN/         # partes del curso (chapterNN.md, exercises.md, tutorial_*.md)
│   │   └── appendix/       # anexos
│   └── index.md            # página de inicio del sitio
├── LICENSE                 # licencia del proyecto
├── mkdocs.yml              # configuración de MkDocs (fuente de verdad de la tabla de contenidos)
└── README.md               # este archivo
```

## Autor

- José Miguel Candia — [GitHub](https://github.com/jmcandia)

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
