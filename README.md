# Desarrollo de Aplicaciones Móviles Android con Kotlin

Un curso práctico que te lleva **desde cero hasta una aplicación completa**, con **Jetpack Compose**, arquitectura **MVVM**, **Retrofit** y **Material 3**. El proyecto que construimos en este curso es una **PokéDex** que consume la [PokeAPI](https://pokeapi.co/docs/v2), pero los conceptos aplican a cualquier app que consuma una API REST.

## Sobre este material

Está pensado para personas con base en programación (por ejemplo, quienes ya conocen **Java** y **POO**) que quieren dar el salto al desarrollo móvil moderno. Cada concepto se explica desde lo más básico y, cuando ayuda, se compara con Java para acelerar el aprendizaje.

El curso avanza en nueve partes: primero el lenguaje Kotlin de forma sólida, luego los fundamentos de Android con Compose, la arquitectura MVVM, el consumo de APIs REST y, finalmente, la construcción paso a paso del proyecto final.

> Los capítulos marcados con *opcional* son material de referencia avanzada. Puedes saltártelos en una primera lectura y volver cuando los necesites.

## Contenido

### Parte I · Fundamentos de Kotlin

1. [El entorno de desarrollo (IDE e IntelliJ IDEA)](/chapter01.md)
2. [Un primer vistazo a Kotlin](/chapter02.md)
3. [Conceptos básicos de Kotlin](/chapter03.md)
4. [Tipos de datos básicos](/chapter04.md)
5. [Operadores: aritméticos, de comparación y lógicos](/chapter05.md)
6. [Entrada y salida estándar y plantillas de cadena](/chapter06.md)
7. [Trabajar con cadenas: longitud, concatenación y `repeat`](/chapter07.md)

### Parte II · Estructuras de control y funciones

1. [Control de flujo: `if` y `when` como expresión](/chapter08.md)
2. [Bucles: `for`, `while` y `do-while`](/chapter09.md)
3. [Funciones: definición, parámetros y valores de retorno](/chapter10.md)

### Parte III · Colecciones y código robusto

 1. [Colecciones I: `List`, `Set` y `Map`](/chapter11.md)
 2. [Colecciones II: operaciones funcionales (`map`, `filter`…)](/chapter12.md)
 3. [Null safety: manejo seguro de valores nulos](/chapter13.md)
 4. [Manejo de excepciones: `try`, `catch` y `Result`](/chapter14.md)
 5. [Formato de cadenas con `String.format`](/chapter15.md) *(opcional)*

### Parte IV · Programación orientada a objetos en Kotlin

 1. [Clases, propiedades y constructores](/chapter16.md)
 2. [Herencia, interfaces y clases abstractas](/chapter17.md)
 3. [`data class`, `copy` y desestructuración](/chapter18.md)
 4. [`object`, `companion object` y singletons](/chapter19.md)
 5. [`enum` y `sealed class`](/chapter20.md)
 6. [Genéricos, funciones de extensión y lambdas](/chapter21.md)

### Parte V · Asincronía con Coroutines

 1. [¿Por qué asincronía? El hilo principal](/chapter22.md)
 2. [`suspend`, `launch`, `async`, scopes y dispatchers](/chapter23.md)
 3. [`Flow`, `StateFlow` y `SharedFlow`](/chapter24.md)

### Parte VI · Introducción a Android con Jetpack Compose

 1. [Android Studio y anatomía de un proyecto Android](/chapter25.md)
 2. [Ciclo de vida de una `Activity` y tu primer `@Composable`](/chapter26.md)
 3. [Fundamentos de Compose: composables, recomposición y `Modifier`](/chapter27.md)
 4. [Estado en Compose: `remember`, `mutableStateOf` y *state hoisting*](/chapter28.md)
 5. [Layouts y listas: `Column`, `Row`, `LazyColumn`](/chapter29.md)
 6. [Material 3: tema, color, tipografía y componentes](/chapter30.md)
 7. [Navegación con Navigation Compose](/chapter31.md)

### Parte VII · Arquitectura MVVM

 1. [Qué es MVVM y por qué usarlo](/chapter32.md)
 2. [`ViewModel` y el estado de la interfaz (`UiState`)](/chapter33.md)
 3. [Repositorio y separación de capas](/chapter34.md)
 4. [Inyección de dependencias (manual y con Hilt)](/chapter35.md)

### Parte VIII · Consumo de API REST con Retrofit

 1. [HTTP, REST y JSON: explorando la PokeAPI](/chapter36.md)
 2. [Retrofit: configuración, interfaces y endpoints](/chapter37.md)
 3. [Serialización JSON y modelos de datos (DTO)](/chapter38.md)
 4. [Estados de red: *loading*, *success* y *error*](/chapter39.md)

### Parte IX · Proyecto final: PokéDex con PokeAPI

 1. [Diseño de la app: pantallas, navegación y arquitectura](/chapter40.md)
 2. [Setup del proyecto y dependencias](/chapter41.md)
 3. [Capa de datos: DTOs, `ApiService` y `Repository`](/chapter42.md)
 4. [Pantalla de lista con paginación](/chapter43.md)
 5. [Carga de sprites con Coil](/chapter44.md)
 6. [Pantalla de detalle: stats, tipos y habilidades](/chapter45.md)
 7. [Búsqueda y filtrado](/chapter46.md)
 8. [Persistencia local con Room: favoritos y caché offline](/chapter47.md)
 9. [Pulido: estados de UI, Material 3 y accesibilidad](/chapter48.md)
10. [Cierre: testing básico, buenas prácticas y próximos pasos](/chapter49.md)

### Anexos

- A. [Principios de diseño: DRY, SRP y KISS](/appendix-design-principle.md)
- B. [Entrada estándar con `Scanner` de Java](/appendix-java-scanner.md)

## Autor

- José Miguel Candia — [GitHub](https://github.com/jmcandia)

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

Copyright (c) 2026 José Miguel Candia

Por la presente se concede permiso, de forma gratuita, a cualquier persona que obtenga una copia de este software y de los archivos de documentación asociados (el "Software"), para utilizar el Software sin restricción, incluyendo, sin limitación, los derechos de uso, copia, modificación, fusión, publicación, distribución, sublicenciar y/o vender copias del Software, sujeto a las siguientes condiciones:

El aviso de copyright anterior y este aviso de permiso se incluirán en todas las copias o partes sustanciales del Software.

EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O IMPLÍCITA. EN NINGÚN CASO LOS AUTORES O TITULARES DEL COPYRIGHT SERÁN RESPONSABLES DE NINGUNA RECLAMACIÓN, DAÑOS U OTRAS RESPONSABILIDADES.
