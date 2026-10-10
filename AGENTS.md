# AGENTS.md

Spanish-language course **"Android con Kotlin desde cero"**: Markdown content published as a MkDocs site, plus the code the course builds. See `CLAUDE.md` for a deeper architectural guide, `CONTEXT.md` for the session-by-session work log, and `CHANGELOG.md` for high-level milestones. Update `CONTEXT.md`/`CHANGELOG.md` when you make structural course changes.

## Layout — three independent subprojects, no root build

- `docs/` — course content (Markdown). Nothing else depends on it.
- `code/contact-list-api/` — Spring Boot 4 / Java 21 REST API (Maven). Backend for the app.
- `code/contact-list-app/` — "Mis Contactos" Android app (Gradle), the Part X final project.
- `code/todo-list-app/` — "Mi lista de tareas" final app (Gradle, etapa 5 = Room); `code/todo-list-app/etapas/{etapa3,etapa4}/` hold earlier stages used by tutorials (see its `README.md`).
- `site/` — generated MkDocs output; committed-looking but gitignored. Never edit it by hand.

Each project builds/tests on its own; there is no aggregate build.

## Docs (content) — the part agents get wrong

- `mkdocs.yml` `nav:` is the source of truth for the table of contents. **A new/renamed chapter file does not appear in the site until it is added there in order.**
- Chapter files are `docs/content/partNN/chapterNN.md` where the number is the **global, strictly sequential chapter number (1–61)**, not per-part. The H1 must be `# Capítulo N` matching the filename number — this drifts after renumbering, so check both.
- Renumbering is a cascade: `git mv` **descending** order (so you don't overwrite targets), then fix every cross-reference (`capítulo N`, `cap. N`, `chapterNN.md`, ranges) and the `nav:`.
- Content, headings and UI strings are in Spanish.
- Prefer the enabled extensions over ad-hoc formatting: admonitions/callouts use the `> [!NOTE]Nota` / `> [!TIP]Sugerencia` / `> [!WARNING]Advertencia` / `> [!IMPORTANT]` form (`mkdocs-callouts`), plus `pymdownx` superfences (incl. `mermaid`), details, and highlight.
- CI (`.github/workflows/deploy-docs.yml`) fails on any MkDocs warning, so verify with:

```powershell
venv\Scripts\mkdocs.exe build --strict   # local venv, if present
# or, after: pip install -r requirements.txt
```

## contact-list-api (Maven)

- `mvnw` has no exec bit in this repo; on Unix use `sh ./mvnw`. On Windows: `mvnw.cmd`.
- Run: `mvnw.cmd spring-boot:run` (http://localhost:8080). Swagger: `/swagger-ui.html`, OpenAPI: `/v3/api-docs`.
- Tests: `mvnw.cmd test`, single class: `mvnw.cmd test -Dtest=ApiApplicationTests`.
- H2 is in-memory: data resets on every restart.
- Lombok and MapStruct are annotation-processor based (wired in `pom.xml`). Build via Maven so generated sources stay in sync; do not hand-edit generated mappers.
- From the Android emulator the API is `http://10.0.2.2:8080`, not `localhost`.

## Android apps (Gradle)

- Versions live in `gradle/libs.versions.toml`. Note the two apps differ: `contact-list-app` uses Kotlin `2.4.20`; `todo-list-app` uses `2.2.10`.
- Use `androidx.hilt:hilt-lifecycle-viewmodel-compose` for `hiltViewModel()` — the `hilt-navigation-compose` variant is obsolete and was migrated away.
- Low-memory build (emulator off): `./gradlew assembleDebug --max-workers=2 -Pkotlin.compiler.execution.strategy=in-process "-Dorg.gradle.jvmargs=-Xmx2g"`.
- Networking to the local API requires all of: `INTERNET` permission, a cleartext `network_security_config.xml`, and the runtime `ACCESS_LOCAL_NETWORK` permission (required on Android 17/API 37+, else OkHttp throws `SocketTimeoutException`).

## Verifying Kotlin snippets from the docs

Console examples are checked with the Android Studio-bundled compiler, e.g.:
`kotlinc archivo.kt -include-runtime -d a.jar && java -jar a.jar`
