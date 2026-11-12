# Fase 5 / Misión 3 — Lote 4: guardar y retomar un cuestionario

**Fecha:** 12 de noviembre de 2026.  
**Estado:** propuesta en PR; CI y pruebas físicas pendientes; sin publicación Google Play.

## Motivación

Tras el Lote 3 (PR #72, fusionado), la persona puede seleccionar 5, 10 o 20 preguntas en cualquier modo. Sin embargo, en `QuizzCPCQuestions` tanto el botón cerrar como Atrás llamaban directamente a `finish()`: se perdía una ronda empezada al intentar salir. El `ViewModel` solo conservaba las respuestas durante una recreación con el proceso activo (como la rotación).

## Implementación

1. **Salida segura:** Cerrar y Atrás comparten un diálogo con «Guardar y salir», «Descartar sesión» y «Seguir estudiando».
2. **Borrador local único:** `QuizDraftStore` guarda un único resumen privado (`quiz_session_draft/draft_v1`) con la configuración del modo/sección, tamaño, orden de IDs de preguntas SHA-256, índices de opciones respondidas, posición actual y tiempo transcurrido. No guarda nombres, textos de respuestas, artículos completos ni historial de sesiones. El guardado es atómico y se actualiza tras iniciar, responder y avanzar.
3. **Retomar:** en la pantalla inicial aparece «Tienes un cuestionario pendiente» con N respuestas de M y un botón «Retomar cuestionario». El `ViewModel` reconstruye respuestas, puntuación, racha y pregunta actual **sin volver a modificar el almacén de errores pendientes**. Durante la pausa el reloj no suma tiempo.
4. **Protección contra pérdida:** iniciar otra sesión cuando hay un borrador exige confirmación y sustituye el anterior solo si la persona acepta.
5. **Finalización:** al completar la sesión y abrir resultados se borra el borrador. Al descartarlo expresamente también se borra. Las preguntas incorrectas permanecen en el repaso local normal, que es distinto del borrador.
6. **Control de datos:** `LocalDataManager.clearAllLocalData()` elimina el borrador; la política de privacidad española en `docs/privacy/politica-privacidad-es.html` informa del almacenamiento local, retención, borrado y posibilidad de copia de seguridad de Android.

## Integridad y fallos esperados

- El borrador tiene versión de esquema, valida cantidades, índices de opciones, unicidad y existencia de IDs en el banco actual y coherencia de avance. Ante datos corruptos o un banco de preguntas incompatible se elimina el borrador de forma segura, sin mostrar preguntas incorrectas ni caer en una excepción.
- La puntuación reconstituida reproduce `QuizSessionStats.recordAnswer` sin duplicar cambios en `QuizErrorStore`.
- Retomar no altera las 120 preguntas ni los identificadores existentes de favoritos/errores. Sin backend, red, SDKs o permisos nuevos.
- Los datos nuevos permanecen solo en el almacenamiento privado de Android, sujeto a la política de copias de seguridad del sistema. No se publica política nueva ni versión Play en este lote.
- No se cambia la versión candidata `1.08 (13)`, la publicidad ni el corpus jurídico. La vigencia normativa posterior al corte documental de junio de 2025 sigue pendiente de verificación separada.

## Comprobaciones

- [ ] `QuizDraftStoreTest`: guardado/restauración exacta, prefijo de respuestas, reconstrucción de puntuación, ausencia de efectos secundarios, rechazo de borradores corruptos y referencias obsoletas.
- [ ] `QuizzCPCResumeDraftTest`: tarjeta visible, intención de reanudación, pregunta/puntos restaurados, limpieza al terminar y cierre seguro ante borrador corrupto.
- [ ] `LocalDataManagerTest`: eliminar todos los datos elimina también el borrador.
- [ ] Regresiones de rotación/tema/modos de 5/10/20 con estado aislado entre pruebas.
- [ ] CI verde: validación jurídica, pruebas unitarias, Lint, debug y release/R8 sin firmar. Las pruebas instrumentadas cuentan solo si realmente se ejecutaron.
- [ ] Android físico: responder tres preguntas, guardar y salir, reabrir, retomar, constatar mismo orden, selección, puntos y racha; abrir lector y volver, rotar pantalla, iniciar una sesión distinta con aviso, descartar y terminar una ronda.
- [ ] Centro de privacidad: «Borrar todos los datos locales» elimina el borrador; comprobar sin conexión y en temas claro/oscuro.

**Puerta de fusión:** CI verde, revisión del propietario y prueba física; no fusionar ni publicar automáticamente. **Misión 3 aún abierta.**
