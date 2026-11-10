# Decisiones pendientes de Seguridad de los datos y AdMob

**Estado:** propuesta para el propietario. **No se modificó Play Console ni AdMob**, y la app sigue usando anuncios personalizados cuando el consentimiento y la configuración lo permiten.

## Lo confirmado por archivos del proyecto

- `app/build.gradle` declara **Google Mobile Ads SDK 25.4.0** y UMP **4.0.0**, `minSdk 30`, `targetSdk 36`.
- Existe una consulta de permiso `canRequestAds()` al precargar y al intentar mostrar intersticiales; UMP solicita actualización en el inicio y gestiona el formulario de privacidad desde el Centro de privacidad cuando corresponde.
- El envío 15 con tres ajustes de «opcional» a «necesaria» aparece **publicado según el informe recuperado de Codex**; esta continuación no lo comprobó desde la cuenta de Play.
- No se encontró en el código configuración expresa para tratamiento infantil, tratamiento de menores de edad de consentimiento ni calificación máxima del contenido publicitario.

## Contraste de fuentes públicas

- [Google Mobile Ads, declaración de datos para Google Play](https://developers.google.com/admob/android/privacy/play-data-disclosure): la guía consultada el 9/11/2026 identifica **25.5.0** como versión descrita. Enumera IP, interacciones, diagnósticos e identificadores, con finalidades de anuncios, estadísticas y prevención del fraude; Google advierte que el editor es responsable de su formulario.
- [Google UMP para Android](https://developers.google.com/admob/android/privacy): requiere actualizar el consentimiento en cada inicio, ofrecer opciones cuando lo indique el SDK y consultar `canRequestAds()` antes de anuncios.
- [Google Play, público objetivo](https://support.google.com/googleplay/android-developer/answer/9867159?hl=es): cuando la audiencia **incluye menores**, deben revisarse las políticas de Familias y la compatibilidad de anuncios. La ficha histórica señala **13–15, 16–17 y 18+**.

**No es correcto equiparar una guía para 25.5.0 con una auditoría demostrada para 25.4.0.** No se agregaron finalidades ni se retiraron campos en Play por inferencia.

## Propuesta pendiente de autorización y evidencia

| Asunto | Acción recomendada | Estado |
| --- | --- | --- |
| Diagnósticos | Comprobar en la versión realmente distribuida si «Diagnósticos» necesita declararse además de «Registros de fallas» | Por contrastar con documentación específica/soporte y formulario |
| Finalidad «prevención de fraude» | Revisar Ubicación aproximada, Interacciones y Diagnósticos respecto de la declaración vigente | Propuesta; no cambiar la consola todavía |
| Recopilación necesaria | Conservar los tres cambios ya publicados salvo nueva evidencia contradictoria | Documentado por Codex |
| Público de 13–17 años | Decidir si **realmente está dirigida a menores**. Si lo está, diseñar cumplimiento publicitario por audiencia, evaluar pantalla de edad neutral y SDK/creatividades permitidas. Si no lo está, declarar únicamente la audiencia real tras confirmarlo | **Bloqueante de lanzamiento**, requiere decisión de producto/cumplimiento |
| Anuncios personalizados | Conservar para usuarios elegibles únicamente después de verificar restricciones aplicables | Preferencia del propietario; requiere implementación conforme al público |
| Borrado de datos | No representar el borrado local como eliminación del historial que Google trate por sí mismo | Mantener coherencia de política pública |

Cualquier modificación de audiencias, finalidades o producción publicitaria debe revisarse y aprobarse explícitamente. Este documento **no es una certificación legal ni una aprobación de publicación**.
