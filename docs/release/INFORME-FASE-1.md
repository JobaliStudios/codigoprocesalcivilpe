# Informe de fase 1

Actualizado: 9 de noviembre de 2026. Rama: `codex/fase-1-confianza-cumplimiento`. **Fase abierta; sin publicación Android.**

## Implementación realizada

| Área | Resultado | Estado |
| --- | --- | --- |
| Política pública | Español, Jobali Studios y correo de soporte; datos locales y tratamiento de Google documentados | Publicada y verificada en https://jobalistudios.github.io/privacy-policy/ |
| Seguridad de los datos | Ubicación aproximada, interacciones y registros de fallas cambiados a recopilación necesaria con autorización | Publicados por Google; envío 15 verificado en consola |
| Eliminación local | Borrado total incluye repaso de errores del quiz | Implementado y probado |
| Intersticiales | Contenido abre inmediatamente; anuncio al salir de resultados del quiz o volver explícitamente al índice | Implementado; límites de 8 navegaciones, 2 por sesión y 10 minutos conservados |
| Consentimiento | UMP se comprueba al mostrar; se invalidan caché y cargas anteriores al cambiar privacidad | Implementado y probado por unidad; SDK real pendiente. Publicidad personalizada conservada |
| Ícono | Balanza borgoña, marfil y dorado; sin texto ni fecha; adaptativo, monocromo y Play | Verificaciones de píxeles y recursos pasan; launcher real pendiente |
| Contenido jurídico | JUR-003/004/005/006/007 aprobadas y aplicadas | Cotejo integral abierto; fecha visible conservada al 8 de agosto de 2026 |

## Correcciones jurídicas autorizadas

- JUR-003, artículo 561: puntuación y conectores de numerales 7 y 8.
- JUR-004, artículo 364: restitución de «superior».
- JUR-005, artículo 90: restitución de la oración sobre apelabilidad sin efecto suspensivo.
- JUR-006, artículo 558: actualización únicamente de la sumilla.
- JUR-007, artículo 122: sustitución únicamente del párrafo posterior al numeral 7 por la redacción de la publicación original de Ley 27524, incluida la excepción relativa al inciso 4.

Índice regenerado: 104 bloques y 862 artículos. Hash vigente: `E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538`. La comparación estructural posterior a JUR-007 confirmó que solo cambió el registro 122 respecto del estado anterior. No se aplicaron otras diferencias candidatas.

La matriz `docs/audits/2026-11-09-cobertura-cotejo.csv` contiene 862 filas: 685 coincidencias normalizadas parciales, 17 artículos en rangos derogados verificados y 160 que requieren cotejo contextual. Corresponde al estado posterior a JUR-005/006; su método corta en la primera nota y no certifica todos los párrafos. El informe jurídico añade cotejos contextuales posteriores, incluido JUR-007. No equivale a 160 errores confirmados ni a vigencia integral certificada.

## Verificación técnica

La última suite completa anterior a JUR-005/006/007 pasó con 332 pruebas, cero fallos y cero errores. Después de JUR-007 pasaron 13 pruebas dirigidas de repositorio, regresión normativa y textos públicos. El control de promesas de actualización automática cubre todos los recursos de texto y la marca se comprueba sin año.

Ambos APK debug compilan. La compilación más reciente, después de traducir «back» a «Atrás», pasó con Lint: cero errores y 463 advertencias. El corpus jurídico no cambió con ese ajuste de interfaz. Las pruebas dirigidas posteriores a JUR-007 pasan; no se atribuye al corpus nuevo una suite completa anterior.

APK debug posterior a JUR-007: `app/build/outputs/apk/debug/app-debug.apk`; SHA-256 `F20E99A55D89E4388A9C517935B74186F6F47DE514E34B0B85BD0F3185F6CF41`. No es un artefacto de producción.

En teléfono Android 15 pasaron siete pruebas instrumentadas, en ejecuciones separadas: tres de banco/contexto, dos de navegación principal y regreso del quiz, y dos nuevas de resultados antes de salir y retorno del lector al índice padre. Las restricciones de MIUI se resolvieron con intervención del propietario. Los puntos de navegación funcionan en el escenario probado; no se ha demostrado todavía visualización efectiva del intersticial con umbral cumplido ni cambio real de personalización UMP. Se conservan pendientes TTS, borrado real y máscaras del launcher.

Play Console confirmó el envío 15 con un único cambio, Seguridad de los datos, como «Publicado»; la consola muestra publicación el 8 de noviembre de 2026 a las 11:32 p.m. Corresponde al envío limitado ya autorizado, sin nueva modificación externa en esta comprobación.

Validadores documentales de privacidad, ícono y trazabilidad jurídica: PASS. El modo jurídico `-RequireClosed` rechaza deliberadamente la auditoría abierta. Un PASS documental no certifica cumplimiento ni vigencia.

## Qué falta para terminar

1. Completar cotejo jurídico contextual y rastreo de reformas posteriores al corte; SPIJ sigue sin responder. Cerrar JUR-001/002 con evidencia, distinguiendo dictámenes y leyes promulgadas.
2. Cerrar las recomendaciones restantes de Seguridad de los datos: diagnósticos/finalidades adicionales y tratamiento infantil en AdMob. Las tres correcciones ya enviadas están publicadas.
3. Completar las pruebas restantes en dispositivo: TTS, borrado real, formulario y cambio de consentimiento, intersticial con umbral de frecuencia cumplido y máscaras del ícono. La navegación principal, el regreso del quiz, los resultados y el índice padre ya tienen pruebas instrumentadas aprobadas.
4. Tras esos cierres, ejecutar verificación final y preparar nueva versión, AAB firmado, notas y material de Play para revisión. La subida a pruebas internas requiere la confirmación prevista en el diseño.
5. Validar la entrega procesada por Play y obtener aprobación separada para promoción gradual a producción.

## Evidencia detallada

- `docs/audits/2026-11-07-auditoria-contenido-juridico.md` y CSV: fuentes, textos y aprobaciones independientes.
- `docs/audits/2026-11-08-propuesta-seguridad-datos.md`: envío limitado y recomendaciones restantes.
- `docs/release/2026-11-09-pruebas-dispositivo.md`: instalación, resultados y diagnóstico de MIUI.
- `docs/release/2026-11-revision-independiente.md`: revisión y correcciones de caché UMP y zona segura del ícono.

Las modificaciones externas realizadas fueron la publicación de la política y el envío de las tres correcciones de opcionalidad. No se publicó una versión Android ni se cambiaron las demás categorías, finalidades o edades.
