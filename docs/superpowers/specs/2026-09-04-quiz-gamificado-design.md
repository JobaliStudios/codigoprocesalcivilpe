# Diseño: Quiz gamificado para 1.07

## Objetivo

Evolucionar el Quiz del Código Procesal Civil PE hacia sesiones breves de estudio
activo: modos de práctica, progreso, racha, puntaje de sesión y repaso de
errores. La experiencia debe permanecer profesional, local y sin anuncios entre
preguntas.

## Hallazgos de la auditoría

- `QuestionBank` carga 31 preguntas locales desde `res/raw/questions.json`.
- Cada pregunta tiene `relatedArticle`, pero no una sección explícita.
- `RelatedArticleNumberExtractor`, `ArticleRepository` y el catálogo jurídico
  permiten resolver el artículo y su sección real sin modificar el JSON.
- `FavoritesManager` conserva favoritos locales cuyo destino de artículo usa el
  prefijo `article:`.
- Los errores actuales solo se transportan a la revisión de la sesión mediante
  `Intent`; no hay historial persistente. Esta mejora crea una única fuente
  local para el repaso de errores.
- `QuizzCPCViewModel` ya retiene la sesión durante recreación por rotación y
  bloquea una segunda respuesta a la misma pregunta.

## Arquitectura

### Configuración y selección

- `QuizPracticeMode`: `ALL`, `FAVORITES`, `PREVIOUS_ERRORS`, `SECTION` y
  `QUICK_REVIEW`.
- `QuizSessionConfig`: modo, id de sección opcional, máximo solicitado y
  bandera de aleatorización. Se transporta entre pantallas y define una sesión.
- `QuizQuestionSelector`: punto único de selección. Recibe la configuración,
  preguntas locales, favoritos y errores. No habrá filtros duplicados en las
  Activities.
- `QuizSection`: representación de una sección real del catálogo jurídico,
  con etiqueta, id estable y cantidad disponible. El selector deriva la
  pertenencia de cada pregunta resolviendo el artículo; las preguntas que no
  se puedan resolver solo se incluyen en `ALL` y en el relleno general de
  `QUICK_REVIEW`.

### Pools

- `ALL`: todas las preguntas válidas, aleatorizadas y sin repetición.
- `FAVORITES`: solo preguntas cuyo artículo canónico esté entre los destinos
  `article:` de `FavoritesManager`. Sin favoritos y sin coincidencias son
  estados distintos y explícitos.
- `PREVIOUS_ERRORS`: solo preguntas cuyo identificador esté en
  `QuizErrorStore` y continúen disponibles en el banco.
- `SECTION`: solo preguntas que resuelvan a la sección elegida; el Bottom Sheet
  muestra exclusivamente secciones con al menos una pregunta.
- `QUICK_REVIEW`: agrega primero errores, luego favoritos no incluidos y por
  último preguntas generales no incluidas, hasta 10. Cada pool se mezcla de
  forma inyectable/testeable y el resultado no contiene duplicados.

## Errores y estado de sesión

`QuizErrorStore` usa preferencias locales para guardar identificadores
deterministas de las preguntas. El identificador se calcula de los campos
actuales de la pregunta y no modifica el contenido jurídico. Al fallar una
pregunta se incorpora al repaso. Al acertarla dentro del modo
`PREVIOUS_ERRORS`, se elimina: esa es la política de error corregido.

`QuizSessionStats` mantiene solo en memoria el total, aciertos, errores,
racha actual, mejor racha y puntos. `QuizScoreCalculator` aplica 100 puntos
por respuesta correcta más `min(racha * 10, 50)`; una incorrecta da cero y
reinicia la racha. El tiempo no interviene en la puntuación.

La lista seleccionada, las respuestas, el modo, el índice, la racha y el
puntaje se mantienen en el `ViewModel` existente durante rotación. Los datos
gamificados no se persisten entre sesiones; los errores sí.

## Interfaz

La pantalla de inicio muestra el CTA principal **Repasar 10 preguntas** y las
tarjetas **Todo el Código**, **Mis favoritos**, **Errores anteriores** y
**Una sección**. Los conteos se calculan al entrar o volver a la pantalla, no
en cada frame. El modo de sección abre un Bottom Sheet.

Durante una sesión, el encabezado presenta texto accesible de progreso,
barra lineal, racha y puntaje en una disposición que puede ajustarse con
fuente grande. Al responder se deshabilitan las alternativas, se mantiene el
feedback actual y el usuario avanza al pulsar **Siguiente**.

El resultado muestra sesión completada, aciertos, porcentaje, puntos, mejor
racha y preguntas para repasar. **Repasar errores** inicia una sesión con los
errores de esa sesión. **Otra ronda** conserva la configuración de práctica y
crea una nueva selección aleatoria.

Las pantallas usan recursos semánticos existentes y nuevos recursos de tema;
no usan color como única señal de acierto/error. No se incorporan animaciones
pesadas, sonidos nuevos, dependencias, red, cuentas, anuncios, daily streaks
ni leaderboard.

## Pruebas

Se añadirán pruebas unitarias para selección de cada modo, deduplicación,
prioridad del repaso rápido, estados vacíos, identificadores y persistencia de
errores, cálculo de puntaje/racha y prevención de doble contabilización.
Las pruebas Robolectric cubrirán inicio, resultados, reinicio con el mismo
modo y preservación durante recreación. La validación final ejecutará
`test`, `lint` y `assembleDebug`; `connectedAndroidTest` solo se ejecutará si
hay un dispositivo o emulador disponible.

## Límites

No se modifican preguntas, alternativas, respuestas correctas, explicaciones
jurídicas, `articles.json`, favoritos, Mis apuntes, TTS, historial normativo,
búsqueda, referencias cruzadas, anuncios, consentimiento, configuración de
release ni versiones. No se crea commit ni tag.
