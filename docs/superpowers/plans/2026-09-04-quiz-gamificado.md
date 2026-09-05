# Quiz gamificado Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Añadir modos de práctica locales, selección centralizada, racha, puntaje y repaso de errores al Quiz existente.

**Architecture:** Una capa pura selecciona preguntas a partir de `QuizSessionConfig`; el `ViewModel` existente conserva la sesión y actualiza `QuizSessionStats`. `QuizErrorStore` será la única persistencia nueva y local para errores; Activities solo muestran el estado y navegan mediante el contrato de sesión.

**Tech Stack:** Java, AndroidX ViewModel, SharedPreferences, Material Components, Gson ya incluido, JUnit/Robolectric.

**Spec:** `docs/superpowers/specs/2026-09-04-quiz-gamificado-design.md`

## Global Constraints

- No modificar preguntas, alternativas, respuestas, explicaciones, `articles.json` ni fuentes jurídicas.
- No modificar favoritos, Mis apuntes, TTS, búsqueda, Ads/UMP, onboarding, release, SDK, versión o firma.
- No añadir dependencias, red, cuentas, leaderboard, daily streaks ni anuncios entre preguntas.
- Usar `FavoritesManager`, `RelatedArticleNumberExtractor`, `ArticleRepository` y `LegalContentCatalog` existentes.
- No crear commit ni tag; dejar los cambios para revisión del usuario.
- Ejecutar `test`, `lint`, `assembleDebug`; ejecutar `connectedAndroidTest` solo con dispositivo disponible.

---

## Estructura de archivos

- Crear `home/Quizzes/QuizPracticeMode.java`: enum de modos.
- Crear `home/Quizzes/QuizSessionConfig.java`: configuración Parcelable, incluido filtro opcional de errores de la sesión.
- Crear `home/Quizzes/QuizSessionStats.java`: aciertos, errores, rachas, puntos y total de una sesión.
- Crear `home/Quizzes/QuizScoreCalculator.java`: fórmula pura de puntos.
- Crear `home/Quizzes/QuizQuestionIdentity.java`: identificador estable de una pregunta existente.
- Crear `home/Quizzes/QuizErrorStore.java`: conjunto local de identificadores de errores pendientes.
- Crear `home/Quizzes/QuizSection.java` y `QuizSectionCatalog.java`: secciones derivadas de la jerarquía jurídica real.
- Crear `home/Quizzes/QuizQuestionSelector.java` y `QuizSelection.java`: pools, prioridades, deduplicación y estados vacíos.
- Modificar `QuestionBank`, `QuizzCPCViewModel`, `QuizSessionContract`, `QuizzCPCStartScreen`, `QuizzCPCQuestions`, `QuizzCPCResult` y sus XML/strings.
- Añadir pruebas unitarias y Robolectric junto a las pruebas existentes de `home/Quizzes`.

## Task 1: Modelos de práctica, identidad y puntaje

**Files:**
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizPracticeMode.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSessionConfig.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSessionStats.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizScoreCalculator.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizQuestionIdentity.java`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSessionStatsTest.java`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizScoreCalculatorTest.java`

**Interfaces:**
- Produces `QuizSessionConfig(QuizPracticeMode mode, @Nullable String sectionId, int requestedQuestionCount, boolean shuffle, List<String> sessionErrorIds)`.
- Produces `QuizSessionStats.recordAnswer(boolean correct): int` and getters for correct, incorrect, currentStreak, bestStreak, score and total.
- Produces `QuizScoreCalculator.pointsForCorrectAnswer(int resultingStreak): int`.

- [ ] **Step 1: Write failing pure-model tests**

```java
@Test public void threeCorrectAnswersAccumulateStreakAndCappedBonus() {
    QuizSessionStats stats = new QuizSessionStats(6);
    assertEquals(110, stats.recordAnswer(true));
    assertEquals(120, stats.recordAnswer(true));
    assertEquals(130, stats.recordAnswer(true));
    assertEquals(3, stats.getBestStreak());
    assertEquals(360, stats.getScore());
}

@Test public void incorrectAnswerAwardsNothingAndResetsStreak() {
    QuizSessionStats stats = new QuizSessionStats(3);
    stats.recordAnswer(true);
    assertEquals(0, stats.recordAnswer(false));
    assertEquals(0, stats.getCurrentStreak());
}
```

- [ ] **Step 2: Run the model tests and verify compilation fails because the types do not exist**

Run: `./gradlew testDebugUnitTest --tests "*.QuizSessionStatsTest" --tests "*.QuizScoreCalculatorTest"`

- [ ] **Step 3: Implement the focused models**

```java
public final class QuizScoreCalculator {
    public static int pointsForCorrectAnswer(int resultingStreak) {
        return 100 + Math.min(Math.max(resultingStreak, 1) * 10, 50);
    }
}
```

Implement `QuizSessionStats.recordAnswer` so a correct response increments the
streak before calling the calculator, an incorrect response increments only
the incorrect count and resets the current streak, and each getter exposes the
current immutable scalar. Implement `QuizQuestionIdentity.forQuestion` from
question text, related article, correct index and option texts; use SHA-256
and lowercase hexadecimal so preferences do not retain the whole question.

- [ ] **Step 4: Make `QuizSessionConfig` Parcelable and immutable**

Store the enum by name, normalize requested count to at least one, expose a
`withSessionErrorIds(List<String>)` method and a `forAnotherRound()` method
that clears only the session-error filter while keeping the practice mode.

- [ ] **Step 5: Run focused tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizSessionStatsTest" --tests "*.QuizScoreCalculatorTest"`
Expected: PASS.

## Task 2: Banco visible y persistencia única de errores

**Files:**
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/model/QuestionBank.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizErrorStore.java`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizErrorStoreTest.java`

**Interfaces:**
- Consumes `QuestionModel` and `QuizQuestionIdentity.forQuestion`.
- Produces `QuestionBank.getQuestions(Context): List<QuestionModel>` as an immutable read-only bank.
- Produces `QuizErrorStore.recordIncorrect(QuestionModel)`, `resolve(QuestionModel)`, `contains(QuestionModel)`, `getQuestionIds()` and `clear()`.

- [ ] **Step 1: Write failing persistence tests**

```java
@Test public void incorrectQuestionSurvivesANewStoreInstance() {
    QuizErrorStore first = new QuizErrorStore(context);
    first.recordIncorrect(question);
    assertTrue(new QuizErrorStore(context).contains(question));
}

@Test public void resolvedQuestionIsRemovedWithoutChangingOtherErrors() {
    store.recordIncorrect(firstQuestion);
    store.recordIncorrect(secondQuestion);
    store.resolve(firstQuestion);
    assertFalse(store.contains(firstQuestion));
    assertTrue(store.contains(secondQuestion));
}
```

- [ ] **Step 2: Run the store tests and verify they fail**

Run: `./gradlew testDebugUnitTest --tests "*.QuizErrorStoreTest"`

- [ ] **Step 3: Expose safe question-bank access and implement the store**

Make `QuestionBank.getQuestions(Context)` public, keep its cache private, and
return the cached unmodifiable list. Implement `QuizErrorStore` over one
private SharedPreferences key containing a `Set<String>` of SHA-256 identities;
copy sets before writing. Do not store answers or add another error source.

- [ ] **Step 4: Run focused tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizErrorStoreTest" --tests "*.GsonReleaseContractTest"`
Expected: PASS.

## Task 3: Secciones legales y selector central

**Files:**
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSection.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSectionCatalog.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSelection.java`
- Create: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizQuestionSelector.java`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizQuestionSelectorTest.java`

**Interfaces:**
- `QuizQuestionSelector(Context, List<QuestionModel>, Set<String> favoriteArticles, Set<String> errorIds, Random)`.
- `QuizSelection select(QuizSessionConfig)` exposes `getQuestions()` and `getEmptyReason()`.
- `QuizSectionCatalog.getAvailableSections(Context, List<QuestionModel>): List<QuizSection>`.

- [ ] **Step 1: Write failing selector tests with a fixed `Random`**

```java
@Test public void quickReviewUsesErrorsThenFavoriteThenGeneralWithoutDuplicates() {
    QuizSelection selection = selector.select(QuizSessionConfig.quickReview());
    assertEquals(Arrays.asList(error, favorite, general), selection.getQuestions());
}

@Test public void favoritesUsesOnlyQuestionsForCurrentFavoriteArticleNumbers() {
    assertEquals(Collections.singletonList(article564Question),
            selector.select(QuizSessionConfig.favorites()).getQuestions());
}

@Test public void sectionPoolContainsOnlyResolvedQuestionsForRequestedSection() {
    QuizSelection selection = selector.select(QuizSessionConfig.section(sectionId));
    assertTrue(selection.getQuestions().stream().allMatch(question ->
            sectionId.equals(catalog.resolveSectionId(question))));
}
```

Also test: no favorites, favorites without matching questions, no previous
errors, fewer than ten questions, duplicate error+favorite identity, `ALL`
without duplicates and omission of unresolvable questions from section/favorite
pools.

- [ ] **Step 2: Run selector tests and verify they fail**

Run: `./gradlew testDebugUnitTest --tests "*.QuizQuestionSelectorTest"`

- [ ] **Step 3: Implement legal resolution and selection**

`QuizSectionCatalog` must extract the number with
`RelatedArticleNumberExtractor`, resolve it through `ArticleRepository`, match
the resulting block key to `LegalContentCatalog.Entry.textRes`, and use that
entry's `sectionNameRes` resource entry name as the stable section id. Never
infer section from question prose.

Within the selector, copy and shuffle each priority bucket with the injected
`Random`, insert identities into a `LinkedHashSet`, and stop after
`requestedQuestionCount`. Return a typed empty reason instead of an empty
session.

- [ ] **Step 4: Run focused tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizQuestionSelectorTest"`
Expected: PASS.

## Task 4: Integrar selección, stats y errores en el ViewModel

**Files:**
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCViewModel.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/model/QuizAnswerResult.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCViewModelTest.java`

**Interfaces:**
- Consumes `QuizSessionConfig`, selected list, `QuizSessionStats` and `QuizErrorStore`.
- Produces `initSession(QuizSessionConfig, List<QuestionModel>)`, `getStats()`, `getConfig()` and existing navigation getters.

- [ ] **Step 1: Add failing ViewModel tests**

```java
@Test public void doubleSubmissionKeepsOneAnswerAndOneScoreAward() {
    viewModel.initSession(config, Collections.singletonList(question));
    viewModel.submitAnswer(question.getCorrectAnswerIndex());
    viewModel.submitAnswer(question.getCorrectAnswerIndex());
    assertEquals(1, viewModel.getStats().getCorrect());
    assertEquals(110, viewModel.getStats().getScore());
}

@Test public void correctAnswerInPreviousErrorsResolvesStoredError() {
    store.recordIncorrect(question);
    viewModel.initSession(previousErrorsConfig, Collections.singletonList(question));
    viewModel.submitAnswer(question.getCorrectAnswerIndex());
    assertFalse(store.contains(question));
}
```

- [ ] **Step 2: Run the ViewModel tests and verify they fail**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCViewModelTest"`

- [ ] **Step 3: Implement the session transition**

Replace count-based initialization with `initSession(config, questions)` only
when no session is loaded. Retain the selected list and config in the existing
ViewModel so rotation does not reselect questions. On first answer only,
record stats and either record an incorrect error or resolve a correct prior
error. Extend `QuizAnswerResult` with `pointsAwarded`, update its parceling
and all fixtures; do not introduce a duplicate answer model.

- [ ] **Step 4: Run focused and recreation tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCViewModelTest" --tests "*.QuizzCPCQuestionsRecreationTest"`
Expected: PASS.

## Task 5: Contrato y pantalla inicial de modos

**Files:**
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSessionContract.java`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCStartScreen.java`
- Modify: `app/src/main/res/layout/activity_quizz_cpcstart_screen.xml`
- Create: `app/src/main/res/layout/bottom_sheet_quiz_sections.xml`
- Modify: `app/src/main/res/values/strings.xml`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCStartScreenTest.java`
- Test: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizSessionContractTest.java`

**Interfaces:**
- `QuizSessionContract.createQuestionIntent(Context, QuizSessionConfig)` and getters for config/stats.
- Start screen reads availability once in `onResume` and starts only non-empty selections.

- [ ] **Step 1: Write failing UI and contract tests**

```java
@Test public void quickReviewStartsQuestionsWithQuickReviewConfigAndLimitTen() {
    activity.findViewById(R.id.btnQuickReview).performClick();
    Intent intent = shadowOf(activity).getNextStartedActivity();
    assertEquals(QuizPracticeMode.QUICK_REVIEW,
            QuizSessionContract.getConfig(intent).getMode());
    assertEquals(10, QuizSessionContract.getConfig(intent).getRequestedQuestionCount());
}

@Test public void noPreviousErrorsShowsTodoAlDiaAndDoesNotStartEmptyQuiz() {
    // Configure empty store, resume activity, click errors card.
    assertTrue(emptyStateText.contains("Todo al día"));
    assertNull(shadowOf(activity).getNextStartedActivity());
}
```

- [ ] **Step 2: Run the start and contract tests and verify they fail**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCStartScreenTest" --tests "*.QuizSessionContractTest"`

- [ ] **Step 3: Implement entry UI and Bottom Sheet**

Replace the 5/10/20 chooser with a prominent 10-question CTA and four
secondary Material cards. Display dynamic favorite/error counts using the
selector on entry and `onResume`. On section click, show a Material Bottom
Sheet populated only with `QuizSectionCatalog.getAvailableSections`; each row
has its real label and count. Use existing semantic colors/dimens, accessible
content descriptions and wrapping text. Add clear empty state copy for no
favorites, no favorite questions, and no errors.

- [ ] **Step 4: Pass config and stats through the contract**

Add Parcelable extras for `QuizSessionConfig` and `QuizSessionStats`; retain
the existing incorrect-answer list for review compatibility. Update all
contract fixtures and API-33 safe reads.

- [ ] **Step 5: Run focused tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCStartScreenTest" --tests "*.QuizSessionContractTest"`
Expected: PASS.

## Task 6: Sesión visible y resultado gamificado

**Files:**
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCQuestions.java`
- Modify: `app/src/main/res/layout/activity_quizz_cpcquestions.xml`
- Modify: `app/src/main/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCResult.java`
- Modify: `app/src/main/res/layout/activity_quizz_cpcresult.xml`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCResultTest.java`
- Modify: `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/QuizzCPCQuestionsRecreationTest.java`

**Interfaces:**
- Questions Activity consumes `QuizSessionConfig`, selector and ViewModel stats.
- Result consumes `QuizSessionStats`, configuration and session incorrect answers.

- [ ] **Step 1: Add failing HUD and result tests**

```java
@Test public void answeredQuestionShowsPointsAndStreakAndLocksOptions() {
    clickCorrectOption(activity);
    assertEquals("110 puntos", text(activity, R.id.tvQuizPoints));
    assertEquals("Racha 1", text(activity, R.id.tvQuizStreak));
    assertAllOptionsLocked(options(activity));
}

@Test public void anotherRoundStartsQuestionsWithSameModeAndNoSessionErrorFilter() {
    click(activity, R.id.btnAnotherRound);
    QuizSessionConfig config = QuizSessionContract.getConfig(nextIntent());
    assertEquals(QuizPracticeMode.FAVORITES, config.getMode());
    assertTrue(config.getSessionErrorIds().isEmpty());
}
```

- [ ] **Step 2: Run the activity/result tests and verify they fail**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCQuestionsRecreationTest" --tests "*.QuizzCPCResultTest"`

- [ ] **Step 3: Implement HUD and feedback**

Load config from the contract, invoke the selector once for a fresh ViewModel,
and show an empty state instead of a zero-question session. Add text progress,
linear indicator, points and streak to a wrapping header. On a first answer,
show correct/incorrect text plus awarded points and streak; retain explicit
icons and correct-answer feedback, and advance only when the user taps
**Siguiente**. Keep the existing option lock and rotation behavior.

- [ ] **Step 4: Implement result actions**

Render correct/total, percentage, session points, best streak and session
error count with professional performance copy: 90–100 excellent, 75–89 very
good, 60–74 good progress, otherwise continue practicing. Hide **Repasar
errores** when no session errors exist; otherwise start a config with exactly
those question identities. **Otra ronda** uses `forAnotherRound()` and
**Volver** opens the start screen. Do not use elapsed time in score.

- [ ] **Step 5: Run focused activity tests**

Run: `./gradlew testDebugUnitTest --tests "*.QuizzCPCQuestionsRecreationTest" --tests "*.QuizzCPCResultTest" --tests "*.QuizzCPCReviewErrorsTest"`
Expected: PASS.

## Task 7: Cobertura final y verificación de restricciones

**Files:**
- Modify: only files required by failed tests from Tasks 1–6.
- Test: all Quiz tests under `app/src/test/java/com/jobalistudios/codigoprocesalcivilpe/home/Quizzes/`.

- [ ] **Step 1: Add gap tests before the full run**

Add explicit tests for `ALL`, section-only questions, favorites refreshed after
removal, no favorite question crash prevention, zero errors copy, quick-review
less-than-ten behavior, no duplicated identities, score sequence `C C C I C
C`, session-error-only review, dark-theme start cards and TalkBack progress
description.

- [ ] **Step 2: Run the full unit suite**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL with all existing and new tests passing.

- [ ] **Step 3: Run static analysis**

Run: `./gradlew lint`
Expected: BUILD SUCCESSFUL; fix errors introduced by this feature without
disabling Lint or suppressing errors globally.

- [ ] **Step 4: Assemble the debug application**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Check whether an Android target is connected**

Run: `adb devices -l`

If a device/emulator is listed, run `./gradlew connectedAndroidTest` and fix
failures. If none is listed, report it as not executed rather than failed.

- [ ] **Step 6: Inspect the final diff without committing**

Run: `git diff --check` and `git status --short`

Confirm no legal content, release configuration, Ads/UMP files, favorite
storage implementation, Mis apuntes, TTS, onboarding, APK/AAB or credentials
changed.
