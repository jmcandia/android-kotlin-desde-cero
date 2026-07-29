# Android con Kotlin desde cero

Curso que lleva a programadores con base en Java desde los fundamentos de **Kotlin** hasta una aplicación **Android** completa con **Jetpack Compose**, construyendo una PokéDex que consume la [PokeAPI](https://pokeapi.co).

El contenido está escrito en Markdown y se publica como sitio web con [MkDocs](https://www.mkdocs.org) y el tema [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/).

## Contenido

Todo el material vive en la carpeta [`docs/`](docs/), organizado por capítulos y partes. La página de inicio del sitio es [`docs/index.md`](docs/index.md).

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
├── docs/                 # todo el contenido del curso
│   ├── assets/           # recursos del contenido
|   |   ├── css/          # estilos personalizados
|   |   ├── images/       # imágenes, organizadas por capítulo
|   │   └── js/           # scripts personalizados
│   ├── index.md          # página de inicio del sitio
│   ├── chapterNN.md      # capítulos
│   └── appendix-*.md     # anexos
├── LICENSE               # licencia del proyecto
├── mkdocs.yml            # configuración de MkDocs
└── README.md             # este archivo
```

## Autor

- José Miguel Candia — [GitHub](https://github.com/jmcandia)

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

Copyright (c) 2026 José Miguel Candia

Por la presente se concede permiso, de forma gratuita, a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados (el "Software"), para utilizar el Software sin restricción, incluyendo, sin limitación, los derechos de uso, copia, modificación, fusión, publicación, distribución, sublicenciar y/o vender copias del Software, sujeto a las siguientes condiciones:

El aviso de copyright anterior y este aviso de permiso se incluirán en todas las copias o partes sustanciales del Software.

EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O IMPLÍCITA. EN NINGÚN CASO LOS AUTORES O TITULARES DEL COPYRIGHT SERÁN RESPONSABLES DE NINGUNA RECLAMACIÓN, DAÑOS U OTRAS RESPONSABILIDADES.
