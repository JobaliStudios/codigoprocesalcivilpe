# Propuesta de corrección de Seguridad de los datos

Fecha: 8 de noviembre de 2026. Estado actualizado al 9 de noviembre: tres correcciones de opcionalidad publicadas por Google; las demás recomendaciones siguen pendientes.

## Ejecución autorizada — opcionalidad

El propietario aprobó expresamente las tres correcciones. Se cambió únicamente a «La recopilación de datos es necesaria» en Ubicación aproximada, Registros de fallas e Interacciones en la app. Se guardó cada categoría y la declaración completa. El resumen contenía un solo cambio, Seguridad de los datos; se envió a revisión y Play confirmó «Se envió 1 cambio a revisión», con sección «Cambios en la etapa de revisión». Las verificaciones rápidas estaban pendientes al terminar. Categorías, finalidades, edades e identificadores no se modificaron. El resto de recomendaciones de este documento NO se envió.

## Recomendaciones respaldadas por documentación

Fuente del SDK: https://developers.google.com/admob/android/privacy/play-data-disclosure . Fuente del formulario: https://support.google.com/googleplay/android-developer/answer/10787469?hl=es . La documentación actual describe SDK 25.5.0 y la app usa 25.4.0: la matriz siguiente es propuesta, no certificación específica de esa versión.

| Campo | Respuesta propuesta | Base / límite |
| --- | --- | --- |
| Recopila o comparte datos | Sí | Incluye SDKs de terceros; el procesamiento local no elimina esta obligación. |
| Cifrado en tránsito | Sí | Google documenta TLS para los datos del SDK. |
| Creación de cuentas | No permite cuentas | No se encontró autenticación de usuarios en la app. |
| Ubicación aproximada | Recopilada y compartida; no efímera; publicidad, estadísticas y prevención de fraude | IP usada para estimación general; añadir finalidad de fraude respecto de declaración actual. |
| Interacciones en la app | Recopiladas y compartidas; no efímeras; publicidad, estadísticas y prevención de fraude | Añadir finalidad de fraude respecto de declaración actual. |
| Diagnósticos | Incluir; recopilados y compartidos; no efímeros; publicidad, estadísticas y prevención de fraude | La guía identifica datos de rendimiento del SDK; el formulario actual solo selecciona registros de fallas. |
| Dispositivo u otros IDs | Mantener recopilados/compartidos y las tres finalidades actuales | Incluye más que AD_ID; no afirmar que borrar AD_ID elimina todos los identificadores. |
| Datos locales del desarrollador | No declararlos como enviados por código propio si permanecen en dispositivo | Notas, búsquedas y favoritos no se envían a un backend propio. Compartir voluntariamente usa aplicaciones elegidas por el usuario. |

## Campos que no deben cerrarse por inferencia

### Opcionalidad resuelta por regla del formulario

La ayuda oficial de Google Play exige que la opción de excluir la recopilación esté disponible para todos los usuarios, independientemente de región/dispositivo, para declararla opcional. Esta app no tiene un control universal para desactivar anuncios y su formulario UMP solo se ofrece cuando corresponde. Por tanto, la mera disponibilidad regional de UMP no justifica «opcional». Cambio concreto propuesto: Ubicación aproximada, Interacciones en la app y Registros de fallas pasan de opcional a necesaria. Dispositivo u otros IDs ya figura necesaria y se conserva. Fuente: https://support.google.com/googleplay/android-developer/answer/10787469?hl=es . No se han enviado cambios.

Notas de versión consultadas: https://developers.google.com/admob/android/rel-notes . 25.4.0 registra correcciones y mejoras, sin desglose histórico de divulgación; no permite deducir por sí sola cada categoría procesada. La diferencia de versión no impide aplicar la regla general de opcionalidad, pero permanece relevante para retirar categorías como fallas.

- **Opcionalidad:** aplicar la resolución anterior; conservar evidencia de controles de ID publicitario por separado, sin equipararlos a exclusión de toda la categoría.
- **Registros de fallas:** no eliminar automáticamente la selección existente. La guía describe diagnósticos agregados, pero la ausencia de Firebase Crashlytics no prueba que el SDK no trate datos de fallas. Resolver alcance por versión o consulta de soporte antes de retirar categorías.
- **Solicitudes de eliminación de datos de terceros:** el borrado local no elimina lo procesado por Google. No marcar disponibilidad de eliminación remota basándose en el Centro de privacidad.

## Público y tratamiento infantil

Play declara edades 13–15, 16–17 y 18+, sin grupos menores de 13. El código no fija explícitamente `setTagForChildDirectedTreatment`, `setTagForUnderAgeOfConsent` ni `setMaxAdContentRating`. Esto no demuestra por sí solo tratamiento infantil habilitado o deshabilitado. La pantalla Configuración de la aplicación de AdMob revisada no expone un valor de tratamiento infantil; registrar como no observable en esa superficie. No cambiar edades ni desactivar publicidad personalizada sin decisión del propietario.

## Acciones restantes

1. Resuelto: envío 15, un único cambio de Seguridad de los datos, figura «Publicado». La consola muestra 8 de noviembre de 2026, 11:32 p.m. Verificado el 9 de noviembre en Actividad de envío → Detalles.
2. Resolver la evidencia específica 25.4.0 para diagnósticos/fallas y las finalidades adicionales propuestas.
3. Presentar al propietario las selecciones adicionales justificadas antes de enviarlas.

Los apartados de diagnóstico conservan el estado anterior al envío. La sección «Ejecución autorizada» registra los únicos cambios guardados y enviados en Play Console. No se modificó AdMob.
