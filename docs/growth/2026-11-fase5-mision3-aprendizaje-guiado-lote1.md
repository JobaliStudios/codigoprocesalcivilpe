# Fase 5 / Misión 3 — Lote 1: aprendizaje guiado en cuestionarios

**Fecha:** 12 de noviembre de 2026.  
**Estado:** PR propuesto, pendiente de CI y aceptación manual; sin fusión ni Google Play.

## Problema detectado en main

Tras responder una pregunta, la pantalla mostraba si el usuario acertó, la opción correcta y sus puntos, pero no ofrecía abrir directamente el artículo legal desde el mismo paso. Al terminar una sesión con errores, la tarjeta «Revisar errores» abría otra sesión de práctica sin enseñar primero el detalle de las respuestas equivocadas. Existía una pantalla `QuizzCPCReviewErrors` con ambas respuestas y enlace jurídico, pero la pantalla final no exponía dicha revisión de forma independiente. Además, `QuizzCPCViewModel` solo retiraba un error pendiente cuando el usuario lo acertaba específicamente en el modo de errores anteriores.

## Cambios de alcance limitado

1. **Fundamento en el lector:** después de responder aparece «Consultar el artículo del CPC». Reutiliza `RelatedArticleNumberExtractor` + `ArticleNavigationResolver` para abrir el lector en el artículo exacto; sin resolver referencias no muestra el botón. Se oculta al iniciar la siguiente pregunta y reaparece si se restaura una pregunta ya contestada tras recrear la Activity.
2. **Dos acciones distintas al finalizar:** «Ver respuestas y artículos» abre la revisión detallada de las respuestas incorrectas de **esta sesión**, comparando la opción elegida y la correcta, y permite abrir cada artículo en el lector. La tarjeta «Volver a practicar errores» conserva el modo de práctica existente; el mensaje y la descripción accesible indican que inicia otra ronda. Sin errores, la acción detallada se oculta y se mantiene el estado de sesión perfecta.
3. **Consolidación de errores previos:** cualquier acierto de una pregunta registrada en `QuizErrorStore`, incluso en modo Todos, permite retirarla de los errores pendientes. Un fallo sigue registrándose igual y los errores de otras preguntas permanecen.
4. **Accesibilidad:** nuevo botón con tamaño táctil mínimo y descripción del número de artículo; feedback marcado para anuncios accesibles no intrusivos. No se introducen explicaciones jurídicas generadas ni inferencias normativas automáticas.

## Integridad y privacidad

- El banco sigue conteniendo **120 preguntas**, y sus enunciados, respuestas y IDs SHA-256 permanecen sin alterar.
- No se modifica el corpus `articles.json` ni se declaran vigentes automáticamente artículos que requieren revisión jurídica posterior al TUO de 07/06/2025. El enlace muestra el texto **que la app ya contiene**, no una certificación legal actual.
- Sin SDKs, analytics, permisos, red, almacenamiento persistente nuevo ni cambios en publicidad.
- Se respetan las preferencias locales y el borrado de datos del usuario; las estadísticas siguen limitadas a la sesión.
- No se modifican `versionName 1.08`, `versionCode 13`, `applicationId`, AdMob, UMP ni la ficha de Google Play.

## Pruebas y aceptación

- [ ] Java/Robolectric: `QuizzCPCQuestionsRecreationTest` comprueba el CTA oculto antes de responder, visible al contestar y después de recrear la pantalla, y navegación al lector.
- [ ] `QuizzCPCResultTest` comprueba revisión detallada con datos exactos, conservación del acceso a práctica y acción oculta en sesión sin errores.
- [ ] `QuizzCPCGamificationViewModelTest`: responder correctamente en modo Todos retira solo el error de la misma pregunta y conserva errores ajenos.
- [ ] CI: pruebas unitarias completas, Lint, debug y release/R8 en verde.
- [ ] En teléfono: responder bien y mal; abrir artículo y volver; cambiar de pregunta; rotar/recrear si procede; terminar con fallos y probar ambas acciones; terminar con 100 % y confirmar que no aparece revisión vacía.
- [ ] En teléfono: revisar errores existentes, acertar uno desde modo Todos, cerrar y reabrir para confirmar que solo ese desapareció del repaso; sin conexión.
- [ ] Revisar contraste y vigencia jurídica antes de cualquier lanzamiento.

**Regla de integración:** no fusionar con CI rojo; no fusionar ni publicar en Google Play sin decisión del propietario. **Misión 3 permanece abierta** tras este primer lote.
