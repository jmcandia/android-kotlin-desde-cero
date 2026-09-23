# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository overview

This repo is an educational course, **"Android con Kotlin desde cero"** (Spanish), that takes Java programmers from Kotlin fundamentals to a complete Android app built with Jetpack Compose. It has two parts:

- `docs/` — the course content itself (Markdown), published as a MkDocs Material site.
- `code/` — the code built throughout the course: `code/contact-list-api/` (a Spring Boot REST API) and `code/contact-list-app/` (the "Mis Contactos" Android app, built step by step in Part X and documented in `docs/content/part10/`).

Course content and the API code are two independent things that should build/lint/test separately.

## Working with the course site (docs/)

Setup and local preview:

```bash
pip install -r requirements.txt
mkdocs serve   # http://127.0.0.1:8000, with live reload
mkdocs build   # static site output to site/
```

Structure:
- `docs/content/partNN/chapterNN.md` — chapters, grouped into 9 parts (Kotlin fundamentals → control flow/functions → collections → OOP → coroutines → Compose intro → MVVM → Retrofit/REST → final project).
- `docs/content/partNN/exercises.md` — end-of-part review exercises (parts I–IV).
- `docs/content/partNN/tutorial_*.md` — standalone worked tutorials interleaved within a part (e.g. the two-part "Mi lista de tareas" tutorial in part06).
- `docs/content/appendix/*.md` — appendices (design principles, Java `Scanner`, Android SDK selection).
- `docs/assets/{css,js,images}/` — site theming. `custom.css` defines a custom brand palette via CSS variables for MkDocs Material's light (`default`) and dark (`slate`) color schemes; `custom.js` adds accordion-style single-open-branch behavior to the nav sidebar.
- **`mkdocs.yml`** is the source of truth for the table of contents (`nav:`) — every new chapter/tutorial/appendix file must be added here explicitly, in the desired order, or it won't appear in the site.

Content conventions to follow when adding/editing chapters:
- Content and UI strings are in Spanish (`es`).
- Chapter nav labels use inline `<code>` for Kotlin/Android identifiers (e.g. `<code>when</code>`), consistent with the rest of `mkdocs.yml`.
- Admonitions (`> [!WARNING]`-style / `!!! note` etc. via `admonition` and `pymdownx.details`), `pymdownx.superfences` (including `mermaid` diagrams), and `pymdownx.highlight` for code blocks are all enabled — prefer these over ad hoc formatting.

## Working with the Contact List API (code/contact-list-api/)

A Spring Boot 4 / Java 21 REST API for CRUD contact management (list with search/pagination, get, create, update, delete), backed by in-memory H2, with HAL/HATEOAS responses and springdoc/Swagger docs. It's the backend the Android app (built in Part X) consumes.

Run from `code/contact-list-api/`:

```bash
./mvnw spring-boot:run          # starts API at http://localhost:8080
./mvnw test                     # run all tests
./mvnw test -Dtest=ApiApplicationTests   # run a single test class
./mvnw clean package            # build the jar
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`; OpenAPI JSON: `http://localhost:8080/v3/api-docs`.
- H2 is in-memory and reset on every restart — no external DB setup needed, but data does not persist.
- From an Android emulator, reach this API via `http://10.0.2.2:8080`, not `localhost`.

Architecture (`src/main/java/com/example/contact_list/api/`):
- `controller/` — REST endpoints, all under `/api/contact`.
- `dto/` — `ContactRequest` (input), `ContactResponse` (output, includes HAL `_links`), `ApiErrorResponse` (error shape: `timestamp`, `status`, `error`, `message`, `path`, `errors[]`).
- `mapper/` — MapStruct mapper between `Contact` entity and DTOs (generated at compile time via the annotation processor wired in `pom.xml`; regenerate by rebuilding rather than hand-editing).
- `model/` — JPA entity (`Contact`).
- `repository/` — Spring Data JPA repository.
- `service/` / `service/ContactServiceImpl.java` — business logic behind the service interface.
- `exception/` + `GlobalHandlerException` — centralized exception handling mapped to `ApiErrorResponse`.
- `config/DataInitializer.java` — seeds sample data on startup using Datafaker.
- `config/SwaggerConfig.java` — OpenAPI/Swagger setup.

Lombok and MapStruct both rely on annotation processing configured explicitly in `pom.xml`'s `maven-compiler-plugin` — if adding new annotation-processor-dependent code, build via Maven (not just an IDE) to make sure generated sources stay in sync.
