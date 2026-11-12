# Fase 5 / Misión 4 — Valoraciones y métricas responsables

**Fecha:** 12 de noviembre de 2026.  
**Estado:** propuesta de implementación en PR; sin actualizar Google Play.  
**Base:** Fase 5 / Misión 3, lotes 1–4 fusionados (PR #70–#73).

## Decisiones de producto

La aplicación puede fomentar opiniones genuinas sin condicionarlas al rendimiento del quiz ni instrumentar vigilancia. Las mejoras se limitan a **un registro educativo voluntario y privado** para quien estudia, y una **invitación infrecuente** a abrir la ficha de Play Store tras un cuestionario completado.

**No se añaden SDKs, Google Analytics, Firebase, identificadores de publicidad, cuentas, telemetría ni endpoints.** No se almacena ni se transmite ningún texto de respuestas, artículos, puntajes individuales, historial por fecha, correo ni información demográfica.

## 1. Invitación a valorar sin manipulación

- Solo aparece **tras terminar una sesión** y alcanzar al menos **tres sesiones completadas**; se contabilizan sesiones terminadas con independencia del porcentaje de aciertos (incluido 0 %).
- **Máximo dos invitaciones por instalación**, separadas por al menos **90 días**. El conteo y la última fecha mostrada quedan en preferencias locales `quiz_review_prompt`.
- No se interrumpe una pregunta, no aparece un diálogo bloqueante y no existen recompensas ni preguntas previas de satisfacción, sesgos sobre la puntuación ni exigencia de calificación positiva.
- Acciones disponibles: **Abrir Google Play**, **Ahora no** y **No volver a mostrar**. La última opción se guarda en el dispositivo. La decisión de valorar y la reseña en sí pertenecen a Google Play, no a la app.
- El botón usa `market://details?id=<applicationId>` con Google Play; si no hay Play Store, abre la ficha HTTPS. No se utiliza `launchReviewFlow` desde un CTA explícito: las cuotas del diálogo podrían hacer que el botón aparentemente no funcionara.
- El botón no confirma ni intenta determinar si el usuario dejó una reseña.

**Referencias oficiales:**
- [Google Play — política sobre valoraciones y reseñas](https://support.google.com/googleplay/android-developer/answer/9898684?hl=es).
- [Android Developers — In-App Review (límites de frecuencia y CTA explícito)](https://developer.android.com/guide/playcore/in-app-review).

## 2. Resumen educativo local con consentimiento previo

En la pantalla inicial del cuestionario, el control **«Guardar mi progreso en este dispositivo»** está **apagado por defecto**. Si se activa, la app registra solo totales agregados de sesiones completadas, preguntas respondidas y aciertos. Se muestra el porcentaje de aciertos **sobre todas las preguntas respondidas en sesiones completadas**; no es una certificación jurídica ni representa el dominio total del CPC.

- Archivo privado `quiz_local_metrics`, sin conexión ni exportación automática.
- Las preguntas no respondidas de un borrador no se incluyen; los contadores solo cambian cuando termina la sesión.
- Al desactivar el control, se **borran de inmediato los totales anteriores**, sin conservar un identificador de usuario.
- La privacidad no depende de activar esas métricas: la opción de valoración funciona independientemente, sin puntajes ni consultas sobre respuestas.
- `LocalDataManager.clearAllLocalData()` elimina tanto estadísticas y preferencia de habilitación como los contadores/dismissal de invitaciones. El almacenamiento local de Android puede incluir esos datos en copias de seguridad del sistema; esto se explica en la política de privacidad.

## 3. Pruebas

- [ ] `QuizLocalMetricsStoreTest`: apagado por defecto, solo guarda datos con aceptación, agregación, invalidación de entradas y borrado al desactivar o borrar todos los datos.
- [ ] `QuizReviewPromptPolicyTest`: tres rondas mínimas, 90 días, dos invitaciones como máximo, «No volver a mostrar» y borrado total.
- [ ] `QuizMission4UiTest`: interruptor real y resumen, tarjeta neutral incluso con **0 aciertos**, apertura de ficha Google Play, opt-out, no mostrar sin sesión real, registrar exactamente una finalización.
- [ ] CI en el commit final: integridad del banco CPC, pruebas Android, Lint, debug y release/R8 sin firmar.
- [ ] Pruebas físicas: completar tres rondas (varios resultados), verificar tarjeta solo tras la tercera, «Ahora no», Play Store o web, navegación hacia atrás, tema claro/oscuro, TalkBack, letra 200 %, acceso sin conexión.
- [ ] Verificar control opt-in apagado inicialmente; registrar dos rondas tras activarlo; confirmar agregados; desactivar y confirmar borrado; revisar en Centro de privacidad «Borrar todos los datos locales».
- [ ] Revisar la política de privacidad antes de actualizar la versión pública. No modificar directamente la ficha, política externa ni Play Console sin aprobación.

## 4. Exclusiones y pendientes

No se realizan encuestas, clasificación de sentimientos, incentivos, medición de usuarios activos, retención por usuario, identificación de dispositivos, captación remota de métricas o segmentación por puntuación. Por ello **no es posible obtener métricas de instalaciones/retención reales de todos los usuarios** desde estos contadores privados. Para métricas globales, contrastar en la futura Misión 5 las estadísticas agregadas ya disponibles en Play Console sin introducir rastreadores.

No cambia la versión Android **1.08 (13)**, los 120 ítems del quiz, su contenido jurídico ni los permisos. La verificación jurídica posterior al corte normativo de junio de 2025 y el ciclo de pruebas y distribución firmado siguen fuera del alcance de este PR.

**Criterio de cierre:** código fusionado y CI verde, validación de usuario/dispositivo y aceptación documentada. **Misión 5 y publicación siguen pendientes.**
