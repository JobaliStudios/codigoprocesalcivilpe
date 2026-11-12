# Fase 5 / Misión 3 — Lote 2: refuerzo por sección según errores de la sesión

**Fecha:** 12 de noviembre de 2026.  
**Estado:** PR en revisión; no fusionado, sin publicación en Google Play.

## Necesidad detectada

Tras el Lote 1 de Misión 3 (PR #70, fusionado), la app puede consultar los artículos después de responder y revisar o practicar errores de la sesión. La pantalla de resultados todavía no ofrece un **siguiente paso orientado a una sección concreta**: todas las equivocaciones aparecen juntas y los usuarios deben elegir manualmente una sección en el menú general.

## Comportamiento propuesto

- Cuando hay errores en la sesión, `QuizSectionFocusAdvisor` cuenta únicamente las respuestas **incorrectas de esa sesión** y las asigna a las secciones existentes mediante el artículo CPC. No usa resultados anteriores, puntuaciones globales ni un perfil.
- Se muestra una tarjeta **«Sección para reforzar»** con el nombre de la sección y su número de errores de la sesión. La acción **«Practicar esta sección»** inicia el modo `SECTION` ya existente, con su selección local de preguntas.
- Solo se ofrece la sección con **más errores absolutos de la sesión**. En empate se utiliza el orden del catálogo jurídico (I–VI). Esto **no pretende medir el dominio general del estudiante ni comparar tasas de acierto entre secciones**.
- Sesión sin errores, referencias nulas o no resueltas, o sin preguntas en la sección: **no se muestra la tarjeta**. Los enlaces a la revisión detallada y a la práctica de errores continúan disponibles con su comportamiento previo.
- La tarjeta usa los estilos, dimensiones táctiles y textos localizados existentes; la decisión se calcula al abrir resultados y **no se almacena**.

## Restricciones respetadas

- Banco de 120 preguntas e identificadores SHA-256 sin cambios; no se modifica el contenido de `articles.json`, preguntas, respuestas correctas ni referencias jurídicas.
- Sin permisos, backend, nuevas librerías, publicidad, telemetría, migraciones o claves de preferencias.
- No se modifica la política de privacidad, ni se afirma vigencia jurídica de las normas posterior al corte oficial del 07/06/2025.
- Sin cambio en `versionName 1.08` / `versionCode 13` ni publicación en Google Play.

## Verificación automática y manual

- [ ] `QuizSectionFocusAdvisorTest`: priorizar la sección con más errores, resolver empates jurídicamente, no recomendar si todos son correctos o las referencias no resuelven.
- [ ] `QuizzCPCResultTest`: mostrar tarjeta con errores conocidos, pasar exactamente el ID de sección al selector, ocultar ante sesión perfecta o referencia desconocida, conservar revisión detallada.
- [ ] GitHub Actions: pruebas unitarias, Lint, compilación debug, release/R8 en verde.
- [ ] Android real: realizar una sesión fallando más en una sección, comprobar título y cantidad, abrir práctica específica, volver, y verificar que revisión de errores y siguiente ronda siguen funcionando.
- [ ] Android real: probar empate, cero errores, rotación, navegación hacia atrás y funcionamiento offline.
- [ ] Propietario revisa y decide si fusionar; sin lanzamiento Google Play.

**Cierre:** esta entrega es el segundo lote de la Misión 3; la misión continúa abierta para mejoras posteriores. No debe fusionarse con CI rojo.
