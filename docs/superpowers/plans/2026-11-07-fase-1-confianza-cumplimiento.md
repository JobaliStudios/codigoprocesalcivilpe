# Fase 1: confianza, cumplimiento y vigencia — Implementation Plan

> **Required execution skill:** Use `superpowers:executing-plans` for inline/native execution, or `superpowers:subagent-driven-development` if the owner explicitly chooses delegated execution.

**Goal:** Alinear la aplicación publicada, su política, Seguridad de los datos, contenido jurídico, publicidad intersticial e identidad visual; entregar un informe jurídico trazable antes de tocar el corpus y preparar un único AAB para pruebas internas después de la aprobación humana.

**Architecture:** La documentación pública tendrá una fuente canónica local y una copia publicada en GitHub Pages. La auditoría jurídica será un artefacto separado e inmutable respecto del corpus hasta recibir decisiones por hallazgo. La política de anuncios separará el registro de navegación del permiso para mostrar: todo evento puede acumular frecuencia, pero solo dos ubicaciones tipadas serán elegibles. El ícono tendrá una fuente geométrica determinista y recursos Android/Play derivados reproducibles.

**Tech Stack:** Java 17, Android XML/View Binding, JUnit 4, Robolectric, Gradle, PowerShell, HTML/CSS estático, GitHub Pages, Google Play Console, Google UMP y Google Mobile Ads SDK.

**Spec:** `docs/superpowers/specs/2026-11-07-fase-1-confianza-cumplimiento-design.md`

## Global Constraints

- Toda comunicación pública se redacta únicamente en español y para público peruano.
- El responsable se identifica como **Jobali Studios** y el contacto como `studiosjobali@gmail.com`.
- La línea base de privacidad y Seguridad de los datos es la versión 1.06 actualmente publicada, no funciones de `main` todavía no distribuidas que cambien categorías o finalidades.
- Se mantienen anuncios personalizados, Google UMP, banners y los límites actuales: ocho navegaciones, máximo dos anuncios por sesión y diez minutos entre anuncios.
- Ningún intersticial puede preceder contenido solicitado, búsqueda, referencia, anterior/siguiente, TTS, inicio de app, reintento de quiz ni Atrás del sistema.
- No se modifica `app/src/main/assets/articles.json`, los strings jurídicos, `content_verified_date` ni `LEGAL_SOURCES.md` antes de que el propietario decida cada hallazgo del informe.
- La evidencia jurídica se jerarquiza así: El Peruano (norma modificatoria), SPIJ (consolidado), Congreso/Poder Judicial (contraste). Un conflicto entre fuentes se informa y no se resuelve automáticamente.
- Publicar en GitHub Pages, enviar cambios en Play Console o subir/promover un AAB requiere confirmación inmediatamente antes de la acción externa.
- El primer destino del AAB es pruebas internas; producción reutiliza el mismo artefacto mediante despliegue gradual.
- Conservar cambios ajenos del worktree. Antes del primer cambio ejecutar `git status --short` y revisar cualquier solapamiento.

## Review Focus

1. **Diferencia entre v1.06 y `main`:** verificar que la política describa lo publicado y que cualquier función futura quede fuera si altera datos o finalidades. Prueba: matriz de inventario con columna `v1.06 / main / política / consola` y validador documental.
2. **Consentimiento o anuncio no disponibles:** la navegación debe continuar una sola vez y sin bloqueo cuando UMP no autoriza, no hay red, no hay anuncio cargado o falla `show()`. Pruebas: `InterstitialNavigationCoordinatorTest` e `InterstitialAdCoordinatorTest`.
3. **Ubicaciones naturales:** solo `QUIZ_RESULT_EXIT` y `READER_TO_INDEX` pueden intentar mostrar; el resto solo registra navegación. Pruebas: nueva `InterstitialPlacementPolicyTest` y pruebas Robolectric de quiz/lector.
4. **Ícono bajo máscaras y tamaño pequeño:** balanza reconocible a 48 px, sin texto/año y con capa monocroma. Pruebas: generador determinista, verificador de dimensiones/archivos, Android Lint y revisión visual con máscaras redonda, squircle y lágrima.
5. **Fuentes jurídicas contradictorias:** registrar ambas evidencias, impacto e incertidumbre; nunca editar el corpus como efecto de la auditoría. Prueba: checklist documental y comparación de hash del corpus antes/después.

---

### Task 1: Congelar la línea base y aislar artefactos de trabajo

**Files:**
- Modify: `.gitignore`
- Verify only: `app/build.gradle`
- Verify only: `app/src/main/AndroidManifest.xml`
- Verify only: `app/src/main/assets/articles.json`
- Verify only: `docs/superpowers/specs/2026-11-07-fase-1-confianza-cumplimiento-design.md`

**Step 1: Capture the baseline**

Run:

```powershell
git status --short
git rev-parse HEAD
Get-FileHash app/src/main/assets/articles.json -Algorithm SHA256
rg -n "versionCode|versionName|targetSdk|minSdk" app/build.gradle
```

Record the commit, SHA-256, version 1.06/versionCode 11 and date in the audit documents created later.

**Step 2: Add the failing hygiene check**

Run:

```powershell
git check-ignore .superpowers/brainstorm/placeholder
```

Expected: non-zero because `.superpowers/` is not yet ignored.

**Step 3: Implement the minimum hygiene rule**

Append `/.superpowers/` to `.gitignore`. Do not delete user material; the generated companion remains local and untracked.

**Step 4: Verify**

Run:

```powershell
git check-ignore .superpowers/brainstorm/placeholder
git status --short
```

Expected: the companion directory no longer appears; the approved spec and plan remain visible.

**Step 5: Commit**

```powershell
git add .gitignore docs/superpowers/specs/2026-11-07-fase-1-confianza-cumplimiento-design.md docs/superpowers/plans/2026-11-07-fase-1-confianza-cumplimiento.md
git commit -m "docs: define fase 1 trust and compliance plan"
```

### Task 2: Crear la política canónica en español y su validador

**Files:**
- Create: `docs/privacy/politica-privacidad-es.html`
- Create: `tools/verify_phase1_privacy.ps1`
- Reference: `app/src/main/AndroidManifest.xml`
- Reference: `app/src/main/res/xml/backup_rules.xml`
- Reference: `app/src/main/res/xml/data_extraction_rules.xml`
- Reference: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/privacidad/PrivacyCenterActivity.java`
- Reference: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/GoogleMobileAdsConsentManager.java`

**Step 1: Write the failing contract check**

Create `tools/verify_phase1_privacy.ps1` so it exits non-zero unless the HTML contains, at minimum: app/package, Jobali Studios, email, no account/server, every local category, Keystore, deletion routes, backups, AdMob/UMP, personalized ads subject to consent/region, IP/approximate location, ad interaction, diagnostics, device/ad identifiers, encrypted transit, Google third-party links, general audience/not directed specifically to children, independence from government, no legal advice, official sources, update mechanism and November 2026 date. It must reject “actualización automática” and “siempre actualizado”.

Run:

```powershell
powershell -ExecutionPolicy Bypass -File tools/verify_phase1_privacy.ps1
```

Expected: FAIL because the canonical HTML does not exist.

**Step 2: Implement the policy**

Create semantic, responsive HTML with plain-language sections, a visible contact link, last-updated date, Google Privacy & Terms links, and no analytics claim unsupported by the installed SDKs. Describe v1.06 and explicitly distinguish local app data from data processed by Google.

**Step 3: Verify the policy against code**

Run:

```powershell
powershell -ExecutionPolicy Bypass -File tools/verify_phase1_privacy.ps1
rg -n "INTERNET|ACCESS_NETWORK_STATE|AD_ID" app/src/main/AndroidManifest.xml
rg -n "include|exclude|notes|search|highlight" app/src/main/res/xml/backup_rules.xml app/src/main/res/xml/data_extraction_rules.xml
```

Expected: validator PASS; every privacy statement has a code/configuration basis.

**Step 4: Commit**

```powershell
git add docs/privacy/politica-privacidad-es.html tools/verify_phase1_privacy.ps1
git commit -m "docs: add canonical Spanish privacy policy"
```

### Task 3: Auditar Seguridad de los datos y configuración publicitaria

**Files:**
- Create: `docs/audits/2026-11-07-seguridad-de-los-datos.md`
- Create: `docs/audits/2026-11-07-inventario-datos.csv`
- Modify: `tools/verify_phase1_privacy.ps1`
- Reference: `app/build.gradle`
- Reference: `app/src/main/AndroidManifest.xml`
- Reference: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/`
- Reference: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/privacidad/`

**Step 1: Extend the failing documentation contract**

Require the audit Markdown to contain package/version, permissions, SDKs, developer-vs-Google processor distinction, each Play category, collection/sharing purpose, optionality, ephemerality, encryption, deletion, target-age/child-treatment observations, current console answer, recommended answer and evidence.

Run the validator and expect FAIL while files are absent.

**Step 2: Read current consoles without mutation**

In Play Console and AdMob, capture the current Data safety selections, target audience/ages, ads declaration, UMP messages and child-directed treatment. Do not click Save/Submit. Record “no observable” where access or a field is unavailable; do not infer a console state.

**Step 3: Build the matrix**

Create one CSV row per category/processing purpose and a Markdown conclusion with exact proposed console answers. Explicitly assess IP-derived approximate location, app/ad interaction, diagnostics, identifiers and fraud/security.

**Step 4: Verify**

Run:

```powershell
powershell -ExecutionPolicy Bypass -File tools/verify_phase1_privacy.ps1
./gradlew.bat :app:dependencies --configuration releaseRuntimeClasspath
```

Expected: documents PASS and installed ad/consent dependencies match the matrix.

**Step 5: Commit**

```powershell
git add docs/audits/2026-11-07-seguridad-de-los-datos.md docs/audits/2026-11-07-inventario-datos.csv tools/verify_phase1_privacy.ps1
git commit -m "docs: audit Play data safety declarations"
```

### Task 4: Publicar política y, si procede, corregir Seguridad de los datos

**External file:**
- Create/update: `JobaliStudios/JobaliStudios.github.io/privacy-policy/index.html`
- Verify: `JobaliStudios/JobaliStudios.github.io/index.html`
- Potential external mutation: Google Play Console → Seguridad de los datos

**Step 1: Prepare an exact diff**

Compare the local canonical HTML with the target GitHub Pages path. Confirm the site root still links to `/privacy-policy/` and displays `studiosjobali@gmail.com`.

**Step 2: Gate — request action-time confirmation**

Present the policy diff, URL and any proposed Play Console answer changes. Ask for confirmation immediately before the first public commit/save. If confirmation is denied, preserve the prepared artifacts and continue with non-public tasks.

**Step 3: Publish only after confirmation**

Create/update `privacy-policy/index.html` in the Pages repository using the authenticated Jobali Studios account. If approved, apply the exact audited Data safety answers and no unrelated console changes.

**Step 4: Verify the public result**

Open the live URL in a fresh page and verify HTTP success, November 2026 date, email, responsive rendering, Google links and absence of stale March 2025 content. Re-open Play Console and record the saved state in the audit.

**Step 5: Record evidence locally**

Add publication time, remote commit URL/hash and console status to `docs/audits/2026-11-07-seguridad-de-los-datos.md`, then commit that evidence.

### Task 5: Ejecutar la auditoría jurídica sin modificar el corpus

**Files:**
- Create: `docs/audits/2026-11-07-auditoria-contenido-juridico.md`
- Create: `docs/audits/2026-11-07-hallazgos-juridicos.csv`
- Create: `tools/verify_legal_audit.ps1`
- Reference only: `app/src/main/assets/articles.json`
- Reference only: `app/src/main/res/values/strings.xml`
- Reference only: `LEGAL_SOURCES.md`

**Step 1: Write the failing audit verifier**

The verifier must require: scope/date, baseline SHA-256, all permitted source domains, search cutoff, post-2026-08-08 modifying norms, a finding table with independent `PENDIENTE/APROBADO/RECHAZADO/OBSERVADO` status, current text, official text, norm, publication date, direct official URL, impact, proposed correction, source conflict and limitations. It must also compare the current corpus hash with the baseline recorded in the report.

Run and expect FAIL before the report exists.

**Step 2: Identify candidate modifying norms**

Search official sources only. Start with El Peruano for norms published after 2026-08-08 that mention or modify the Código Procesal Civil; cross-check the consolidated SPIJ text and use Congreso/Poder Judicial for traceability. Save direct document URLs and access dates.

**Step 3: Compare every candidate to the bundled corpus**

Map norms to article numbers, compare exact operative content and distinguish substantive differences from punctuation/formatting. For a source conflict, retain both versions and mark the finding `OBSERVADO`, never auto-correct.

**Step 4: Produce owner-reviewable findings**

Use one stable ID per finding (`JUR-001`, etc.) and independent decision fields. If there are no differences, include an explicit signed-off “Sin diferencias encontradas” section and the search evidence.

**Step 5: Verify non-mutation and report completeness**

Run:

```powershell
powershell -ExecutionPolicy Bypass -File tools/verify_legal_audit.ps1
git diff -- app/src/main/assets/articles.json app/src/main/res/values/strings.xml LEGAL_SOURCES.md
```

Expected: validator PASS and empty corpus/source diff.

**Step 6: Commit**

```powershell
git add docs/audits/2026-11-07-auditoria-contenido-juridico.md docs/audits/2026-11-07-hallazgos-juridicos.csv tools/verify_legal_audit.ps1
git commit -m "docs: add traceable legal content audit"
```

### Task 6: Blindar las afirmaciones de vigencia

**Files:**
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/contenido/LegalUpdateDisclosureTest.java`
- Modify only if needed: `app/src/main/res/values/strings.xml`
- Modify only if needed: `README.md`
- Verify: `docs/privacy/politica-privacidad-es.html`
- Verify: public Play listing copy

**Step 1: Add a failing repository-wide claim test**

Extend the test to scan user-visible resources for prohibited claims such as `actualización automática`, `se actualiza automáticamente`, `siempre actualizado` and year-stamped branding. Keep legitimate phrases such as `ACTUALIZACIÓN NORMATIVA` out of the prohibited pattern. Require the authorized offline/periodic wording and concrete verification date.

Run:

```powershell
./gradlew.bat testDebugUnitTest --tests "*.LegalUpdateDisclosureTest"
```

Expected: FAIL if a prohibited or missing claim is found.

**Step 2: Make minimal copy corrections**

Change only unsupported promises. Do not advance the legal verification date and do not edit normative text.

**Step 3: Verify**

Re-run the focused test plus:

```powershell
rg -ni "actualización automática|se actualiza automáticamente|siempre actualizado|actualizada 20[0-9]{2}" app/src/main README.md docs/privacy
```

Expected: no unsupported public promise; allowed legal headings may be documented as exclusions.

**Step 4: Commit**

```powershell
git add app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/contenido/LegalUpdateDisclosureTest.java app/src/main/res/values/strings.xml README.md
git commit -m "test: prevent automatic legal update claims"
```

### Task 7: Crear el ícono atemporal y reproducible

**Files:**
- Create: `branding/launcher-icon-spec.svg`
- Create: `tools/generate_launcher_icons.ps1`
- Create: `tools/verify_launcher_icons.ps1`
- Modify: `app/src/main/res/drawable/ic_launcher_background.xml`
- Modify: `app/src/main/res/drawable-v24/ic_launcher_foreground.xml`
- Create: `app/src/main/res/drawable/ic_launcher_monochrome.xml`
- Modify: `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml`
- Modify: `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml`
- Modify: `app/src/main/res/mipmap-*/ic_launcher.png`
- Modify: `app/src/main/res/mipmap-*/ic_launcher_round.png`
- Modify: `app/src/main/res/mipmap-*/ic_launcher_foreground.png`
- Modify: `app/src/main/ic_launcher-playstore.png`

**Step 1: Write the failing asset verifier**

Require the 512×512 Play asset, all density sizes, adaptive foreground/background, monochrome layer, allowed palette, no metadata/text element in SVG, and no `2025`/year string in icon sources. Run and expect FAIL because the monochrome/source assets are absent.

**Step 2: Implement the vector source and Android layers**

Draw direction A: burgundy background, centered ivory scales and restrained gold accent. Keep essential geometry inside the 66×66 adaptive safe zone and make the silhouette survive a single-color monochrome rendering.

**Step 3: Generate PNGs deterministically**

Use PowerShell/System.Drawing geometry matching the SVG constants; do not hand-edit density PNGs. Generate legacy square/round/foreground resources and the 512×512 Play asset.

**Step 4: Verify visually and mechanically**

Run:

```powershell
powershell -ExecutionPolicy Bypass -File tools/generate_launcher_icons.ps1
powershell -ExecutionPolicy Bypass -File tools/verify_launcher_icons.ps1
./gradlew.bat lintDebug assembleDebug
```

Inspect at 48 px and under circle, squircle and teardrop masks; confirm no clipping, false government resemblance, tiny unreadable details or year/text.

**Step 5: Commit**

```powershell
git add branding tools/generate_launcher_icons.ps1 tools/verify_launcher_icons.ps1 app/src/main/res/drawable app/src/main/res/drawable-v24 app/src/main/res/mipmap-* app/src/main/ic_launcher-playstore.png
git commit -m "feat: replace dated launcher icon"
```

### Task 8: Tipar las ubicaciones publicitarias y proteger la navegación

**Files:**
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/InterstitialPlacement.java`
- Create: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/InterstitialPlacementPolicyTest.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/InterstitialAdCoordinator.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/InterstitialAdCoordinatorTest.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/anuncios/InterstitialNavigationCoordinatorTest.java`

**Step 1: Write failing placement tests**

Define cases that require only `QUIZ_RESULT_EXIT` and `READER_TO_INDEX` to be eligible. Explicitly reject `ARTICLE_OPEN`, `ARTICLE_PREVIOUS_NEXT`, `SEARCH_RESULT`, `RELATED_ARTICLE`, `TTS`, `APP_START`, `SYSTEM_BACK` and `QUIZ_RETRY`.

**Step 2: Add the typed policy**

Create an enum/value object with `isNaturalBreak()` and expose coordinator methods with distinct intent:

```java
void recordNavigationWithoutInterruption();
void navigateAtNaturalBreak(Activity activity, String adUnitId,
                            InterstitialPlacement placement, Runnable destination);
```

`navigateAtNaturalBreak` must always run the destination, reject non-natural placements defensively, preserve last-destination serialization and continue immediately when UMP/ad/lifecycle/network conditions fail.

**Step 3: Verify focused policy and failure paths**

Run:

```powershell
./gradlew.bat testDebugUnitTest --tests "*.InterstitialPlacementPolicyTest" --tests "*.InterstitialAdCoordinatorTest" --tests "*.InterstitialNavigationCoordinatorTest" --tests "*.InterstitialFrequencyPolicyTest"
```

Expected: PASS, including no-ad, fail-to-show, duplicate callback and rapid-navigation scenarios.

**Step 4: Commit**

```powershell
git add app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/anuncios app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/anuncios
git commit -m "refactor: restrict interstitials to natural breaks"
```

### Task 9: Integrar quiz e índice de lectura sin interrumpir contenido

**Files:**
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCResult.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCResultTest.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/navigation/LegalHierarchyRepository.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/navigation/LegalHierarchyRepositoryTest.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/SectionContentActivity.java`
- Modify: `app/src/main/res/layout/activity_section_content.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/SectionContentActivityArticleActionsTest.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/navigation/SectionListActivity.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Codigos/CodigoProcesalCivilMain.java`

**Step 1: Write failing behavior tests**

- Quiz: `Volver al inicio` delegates to `QUIZ_RESULT_EXIT`; `Reintentar` and error review never do.
- Hierarchy: every leaf resolves to its immediate non-leaf parent and a valid `SectionListActivity` intent.
- Reader: a visible `Volver al índice` action stops TTS and delegates to `READER_TO_INDEX`; system Back keeps logical history/finishes without ad.
- Article/index entry points: opening hierarchy, leaf, previous/next, related/search calls only `recordNavigationWithoutInterruption()` and proceeds immediately.

Run the focused tests and expect FAIL before implementation.

**Step 2: Implement parent-index navigation**

Add a repository helper such as:

```java
@Nullable public static Node findParentNode(String childId);
@Nullable public static Intent buildParentIndexIntent(Context context, String childId);
```

Add an outlined `Volver al índice` button to the reader. Hide/disable it only if a valid internal parent index cannot be resolved.

**Step 3: Wire the two natural breaks**

Use `navigateAtNaturalBreak(... QUIZ_RESULT_EXIT ...)` on quiz exit and `READER_TO_INDEX` on the new reader action. Leave retry, review and system Back untouched by the ad gate.

**Step 4: Remove ad gates before requested content**

Replace current `navigate(...)` uses in `CodigoProcesalCivilMain`, `SectionListActivity` and article navigation with frequency recording plus immediate destination execution. Do not alter banner behavior.

**Step 5: Verify focused tests**

```powershell
./gradlew.bat testDebugUnitTest --tests "*.QuizzCPCResultTest" --tests "*.LegalHierarchyRepositoryTest" --tests "*.SectionContentActivityArticleActionsTest" --tests "*.Interstitial*Test"
```

Also manually test offline/no-consent, loaded ad, unavailable ad, quiz retry, article open, search result, related article, previous/next, TTS and system Back.

**Step 6: Commit**

```powershell
git add app/src/main/java app/src/main/res/layout/activity_section_content.xml app/src/main/res/values/strings.xml app/src/test/java
git commit -m "feat: show interstitials only after quiz or reader exit"
```

### Task 10: Entregar el informe para decisiones humanas

**Files:**
- Modify: `docs/audits/2026-11-07-hallazgos-juridicos.csv`
- Modify: `docs/audits/2026-11-07-auditoria-contenido-juridico.md`

**Step 1: Present the report, not code changes**

Summarize each finding ID, article, evidence, risk and proposed correction. Ask the owner to mark each row `APROBADO`, `RECHAZADO` or `OBSERVADO`; no blanket approval is inferred.

**Step 2: Gate — stop legal mutations**

Do not continue to Task 11 until every release-relevant finding has an explicit decision. Privacy, icon and ad work may already be complete.

**Step 3: Record decisions verbatim**

Update only decision/status/owner-note fields, preserve original evidence, and commit the reviewed report.

### Task 11: Aplicar únicamente correcciones jurídicas aprobadas

**Files:**
- Modify as approved: `app/src/main/res/values/strings.xml`
- Regenerate as approved: `app/src/main/assets/articles.json`
- Modify: `LEGAL_SOURCES.md`
- Modify: `app/src/main/res/values/strings.xml` (`content_verified_date` only after complete verification)
- Modify/add focused tests under: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/contenido/`

**Step 1: Add one failing regression test per approved finding**

Assert article identity, required operative phrases, amendment metadata and absence of superseded text. Rejected/observed findings get no corpus change.

**Step 2: Apply the minimum approved source edits**

Edit the source strings, regenerate `articles.json` with the repository's existing generator, update official URLs in `LEGAL_SOURCES.md`, and advance the visible verification date only if the entire stated corpus/date scope was actually checked.

**Step 3: Verify approved scope and provenance**

Run all content/catalog tests, the legal-audit verifier and inspect the diff finding by finding. Update the report with resulting commit IDs without rewriting the original evidence.

**Step 4: Commit**

```powershell
git add app/src/main/res/values/strings.xml app/src/main/assets/articles.json LEGAL_SOURCES.md app/src/test docs/audits
git commit -m "fix: apply approved CPC legal updates"
```

### Task 12: Verificación integral y revisión de código

**Files:**
- Modify only for proven defects from review/tests
- Create: `docs/release/2026-11-fase-1-checklist.md`

**Step 1: Run the full automated gate**

```powershell
./gradlew.bat clean testDebugUnitTest lintDebug assembleDebug
powershell -ExecutionPolicy Bypass -File tools/verify_phase1_privacy.ps1
powershell -ExecutionPolicy Bypass -File tools/verify_legal_audit.ps1
powershell -ExecutionPolicy Bypass -File tools/verify_launcher_icons.ps1
```

Record exact test counts, Lint errors/warnings and artifact paths. Do not describe warnings as clean; triage any new warning caused by this phase.

**Step 2: Perform focused review**

Review the five items in “Review Focus”, privacy-v1.06 consistency, ad callback races, accessibility/content descriptions, RTL/masks, and that legal diffs exactly equal approved findings. Fix only verified defects and re-run relevant tests.

**Step 3: Device/emulator checklist**

Validate fresh install, upgrade, consent choices, personalized ads when permitted, privacy deletion, notes encryption behavior, backup exclusions, quiz result exit, reader-to-index, all forbidden ad positions, offline navigation, dark mode and 48 px icon rendering.

**Step 4: Commit verification evidence**

```powershell
git add docs/release/2026-11-fase-1-checklist.md
git commit -m "docs: record fase 1 release verification"
```

### Task 13: Preparar y subir un único AAB a pruebas internas

**Files:**
- Modify: `app/build.gradle` (next versionCode/versionName selected at release time)
- Create: `docs/release/2026-11-notas-es-PE.txt`
- Generated, never committed: `app/build/outputs/bundle/release/app-release.aab`

**Step 1: Prepare release metadata**

Increment versioning without reusing versionCode 11. Write concise Spanish release notes covering verified legal updates only if approved/applied, privacy alignment, atemporal icon and less disruptive ads.

**Step 2: Build and inspect release artifact**

```powershell
./gradlew.bat testDebugUnitTest lintRelease bundleRelease
Get-FileHash app/build/outputs/bundle/release/app-release.aab -Algorithm SHA256
```

Inspect signing, package, version, permissions and manifest with Android build tools. Record SHA-256 and size in the checklist.

**Step 3: Gate — request upload confirmation**

Present test results, unresolved warnings, legal decision summary, AAB version/hash and release notes. Ask for confirmation immediately before uploading to Play Console internal testing.

**Step 4: Upload only after confirmation**

Upload the exact AAB to the internal track, attach the reviewed notes and do not promote to production. Install the Google Play-processed build from the internal test link and repeat smoke tests.

**Step 5: Gate production promotion separately**

After internal validation, present evidence and request a separate confirmation. Promote the same artifact with a gradual rollout; do not upload a different binary under the same review evidence.

## Final Self-Review Checklist

- [ ] Every user decision from the design is represented; no new product decision was silently introduced.
- [ ] The public policy describes v1.06 and names Jobali Studios/contact correctly.
- [ ] Console reads were separated from public mutations and every mutation had action-time confirmation.
- [ ] The legal audit used only approved official-source classes and preserved conflicts/limitations.
- [ ] Corpus hash remained unchanged before owner decisions.
- [ ] Every applied legal change maps to one explicitly approved finding and regression test.
- [ ] Interstitial frequency limits remain 8 / 2 per session / 10 minutes.
- [ ] Only quiz exit and explicit reader-to-index can attempt an interstitial.
- [ ] System Back and every requested-content path continue immediately without an ad gate.
- [ ] The icon is timeless, mask-safe, monochrome-capable and legible at 48 px.
- [ ] Full tests, Lint, debug build, release build and device smoke tests have recorded evidence.
- [ ] The AAB entered internal testing first and production promotion, if any, reused the same artifact.

