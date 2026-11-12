# Fase 5 / Misión 3 — Lote 3: sesiones personalizadas de 5, 10 o 20 preguntas

**Preparado:** 12 de noviembre de 2026.  
**Estado:** PR de revisión, sin fusionar ni publicar en Google Play.

## Hallazgo en la experiencia anterior

La pantalla principal del cuestionario ofrecía modos Todos, Favoritos, Errores anteriores, Por sección y Repaso rápido. Sin embargo, todos se iniciaban por defecto con **10 preguntas** y el estudiante no podía cambiar la duración de la sesión desde esa pantalla. Los textos «5 preguntas», «10 preguntas» y «20 preguntas» ya existían en recursos, pero no estaban conectados al flujo gamificado. Además, los contadores mostrados en las tarjetas Favoritos y Errores anteriores se limitaban a 10, incluso si había más preguntas disponibles.

## Comportamiento implementado

- Se puede escoger **5, 10 o 20 preguntas** al entrar al cuestionario; por defecto permanecen **10** para respetar la experiencia previa.
- La elección se aplica sin nuevas pantallas a Todos, Favoritos, Errores anteriores, Por sección y Repaso rápido mediante `QuizSessionConfig.withQuestionCount()`; no altera el modo ni la sección elegida.
- El botón de repaso rápido deja de afirmar que siempre repasará diez preguntas y pasa a llamarse **«Repaso rápido»**.
- La sesión se limita a la cantidad de preguntas realmente disponibles. El selector ya deduplica las entradas; pedir 20 a un conjunto de menos de 20 no crea duplicados.
- La opción elegida sobrevive a la recreación de la pantalla (por ejemplo, rotación), usando `onSaveInstanceState`. **No se crea una preferencia permanente nueva**. Las rondas repetidas conservan el tamaño a través del `Parcelable` ya existente.
- La pantalla de inicio informa el **total real de preguntas disponibles** en Favoritos y Errores anteriores, no solamente las primeras 10 que tomaría una sesión.
- Al empezar una sesión válida se limpia el mensaje anterior de estado vacío.

## Compatibilidad, privacidad y límites

- Se mantiene el banco de **120 preguntas**, las respuestas, los identificadores locales de errores y el corpus normativo sin cambios.
- Sin nuevos SDKs, red, telemetría, permisos, migraciones, publicidad o cambios en políticas.
- Conserva `versionName 1.08` / `versionCode 13`. No se firma AAB ni se publica en Google Play.
- Esta mejora educativa no declara vigencia jurídica posterior al corte de la compilación oficial de junio de 2025.

## Pruebas y aceptación

- [ ] `QuizzCPCPracticeLengthTest`: 10 por defecto, selección 20 para Todos, 5 para Repaso rápido y persistencia tras recrear Activity.
- [ ] `QuizSessionConfigTest`: el cambio de cantidad no altera modo, sección, orden ni IDs; se mantiene al parcelizar y al repetir ronda.
- [ ] `QuizQuestionSelectorTest`: no duplica preguntas cuando se solicitan 20 y solo hay 3.
- [ ] CI: integridad del banco/recursos, pruebas Android, Lint, debug y release/R8 sin firmar.
- [ ] Prueba física: comprobar 5, 10 y 20 preguntas en Todos, Favoritos, Errores y Por sección; repaso rápido de 5; segunda ronda y rotación; tema claro/oscuro, offline.
- [ ] Revisar la accesibilidad y disposición de tres botones en pantalla estrecha y texto ampliado.

**Condición:** el propietario decidirá la fusión tras CI y verificación en teléfono. **La Misión 3 continúa abierta.**
