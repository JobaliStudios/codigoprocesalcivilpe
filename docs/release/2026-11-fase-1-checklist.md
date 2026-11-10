# Fase 1 — registro de verificación

Rama: `codex/fase-1-confianza-cumplimiento`. Este registro no autoriza publicación.

## Estado vigente después de JUR-007 — 9 de noviembre

- JUR-007 aprobada y aplicada solo al párrafo indicado del artículo 122. Corpus: `E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538`, 104 bloques y 862 artículos.
- Pruebas dirigidas posteriores: 13, cero fallos y errores; APK actualizado compilado. SHA-256 APK: `0A1165DED357E9C6D76117903BC7DA9434894A8012D28AB7D913059DF543FCCB`.
- Instalación en Android 15 completada. Tres pruebas instrumentadas sin interfaz pasan; apertura normal llega al onboarding.
- Restricciones de MIUI resueltas por el propietario. Pasaron dos pruebas de navegación principal/quiz y dos nuevas de resultados e índice padre; junto con las tres sin interfaz, son siete pruebas instrumentadas aprobadas en ejecuciones separadas. TTS, borrado, UMP real, anuncio con umbral cumplido y máscaras siguen pendientes.
- Ajuste de idioma «back» → «Atrás» compilado, instalado y comprobado en el recurso del APK. Lint final: cero errores y 463 advertencias. APK actual: `F20E99A55D89E4388A9C517935B74186F6F47DE514E34B0B85BD0F3185F6CF41`; corpus jurídico sin cambio.
- Play Console: envío 15, un único cambio de Seguridad de los datos, estado «Publicado». Publicación indicada por la consola: 8 de noviembre, 11:32 p.m.; comprobación realizada el 9 de noviembre. El envío autorizado ya no está pendiente de resolución.
- Fuente del estado consolidado: `INFORME-FASE-1.md`; detalle en `2026-11-09-pruebas-dispositivo.md`. Las secciones siguientes conservan evidencia histórica y sus hashes no son el corpus vigente.

## Evidencia anterior a JUR-007 — 9 de noviembre

Esta sección es anterior al estado vigente indicado arriba.

- JUR-003/004/005/006 aprobadas individualmente y aplicadas. Corpus actual: 104 bloques, 862 artículos; SHA-256 `D13D114256A49B47A1B86A4A44AA3A845D96CBCB77A6CF7DB0999E465E0B9042`.
- Después de JUR-004 la suite completa pasó: 332 pruebas, cero fallos y errores. La tarea integrada terminó en 3m 48s. Como 90/558 se editaron después de compilar recursos de esa ejecución, no se usa para certificar esos cambios.
- Después de JUR-005/006 pasaron 13 pruebas dirigidas: repositorio de artículos, regresión normativa y divulgación de actualización. Incluyen dos controles nuevos sobre todos los textos y marca sin año.
- Validadores de privacidad e ícono: PASS. Validador jurídico documental: PASS; modo `-RequireClosed`: falla deliberadamente porque el cotejo integral y rastreo de reformas siguen abiertos. No confundir trazabilidad con vigencia certificada.
- Hay teléfono Android 15 conectado. La primera compilación instrumentada detectó referencias obsoletas en `QuizzNavigationTest`; se sustituyeron por el flujo actual. Reintento compilado; no se ejecutaron pruebas por bloqueo de instalación del teléfono.
- Instalación manual del APK debug bloqueada por el dispositivo: `INSTALL_FAILED_USER_RESTRICTED: Install canceled by user`. Requiere intervención del propietario en el teléfono; no se borraron datos ni se desinstalaron aplicaciones.
- Seguridad de los datos: tres correcciones autorizadas de opcionalidad enviadas a revisión. Resolución de Google no confirmada. Política pública publicada.
- Compilación final `lintDebug assembleDebug assembleDebugAndroidTest`: correcta. Lint: cero errores, 463 advertencias. APK debug SHA-256 `D8D46E8F925DC847D5B25E9E4D0637CA61143DCF5DFE54D994341D0DB0956668`.
- No se generó ni publicó AAB; la fase permanece abierta.

## Implementado

Actualización de publicación: política aprobada publicada y verificada en https://jobalistudios.github.io/privacy-policy/ mediante commit remoto `47e9140f3add18308754a4ea2cf1a19d15cbcd93`.

Revisión independiente: hallazgo importante de caché tras cambio UMP corregido en `c37d346`, descartando anuncio y callbacks antiguos; ajuste de zona segura del ícono verificado por píxeles y recursos Android en `1365976`.

- Política canónica en español, responsable Jobali Studios y correo de soporte. Publicada y verificada.
- Inventario y observaciones de Play Console/AdMob documentados; recomendaciones finales pendientes.
- Borrado total incluye preguntas de repaso del quiz. Regresión observada fallando antes de corregir y pasando después.
- Apertura de contenido sin intersticial; salida del quiz e índice del lector como puntos naturales. Límites de frecuencia conservados.
- Acción del lector al índice padre y corrección de reutilización de índice obsoleto; prueba RED→GREEN.
- Ícono borgoña/marfil/dorado, sin texto ni año, PNG Play y densidades, vectores adaptativos y monocromo.

## Evidencia automática

Verificación posterior a JUR-003: `testDebugUnitTest lintDebug assembleDebug` terminó correctamente en 2m 40s; 332 pruebas, cero fallos y cero errores. El hash vigente del corpus tras la única corrección editorial aprobada es `98605ECDE0DE8B564776479FC7CD24F2D021529861A614691152D9F1E9D35109`; el hash inicial citado al final se conserva como línea base histórica.

- Suite completa anterior a la última corrección de navegación: 329 pruebas, cero fallos.
- Última corrección de navegación: pruebas de acciones del lector y jerarquía pasan.
- Recursos del ícono: `processDebugResources` pasa.
- `verify_launcher_icons.ps1`: detectó borde parcialmente transparente; generador corregido y control final PASS.
- `verify_phase1_privacy.ps1`: PASS documental. No equivale a certificación jurídica ni de políticas de Google.
- Lint y APK debug: `lintDebug assembleDebug` finalizaron correctamente en 2m 7s. Lint: cero errores y 463 advertencias. No se presenta como compilación libre de advertencias.
- Advertencias del ícono identificadas: cinco `IconLauncherShape` en recursos heredados cuadrados y un `UnusedResources` para PNG de primer plano, pues el adaptativo usa vector. Pendiente limpieza de recursos y revisión de la forma heredada.

## Pendientes para cerrar

Comprobación de dispositivos: `adb devices -l` no devuelve dispositivos ni emuladores conectados. La prueba instalada no se realizó; el APK debug disponible está en `app/build/outputs/apk/debug/app-debug.apk`.

- Cotejo jurídico integral y decisiones individuales del propietario; el informe actual es parcial.
- Contrastar opcionalidad y finalidades de datos del SDK, y tratamiento infantil en AdMob.
- Política publicada. Queda pendiente cerrar y presentar las respuestas concretas de Seguridad de los datos antes de enviar cambios de consola.
- Revisión visual del ícono en máscaras y 48 px; validación de lector/quiz en dispositivo.
- Se añadió consulta de `canRequestAds()` al intentar mostrar el intersticial, además de la comprobación al precargar. La prueba de política comprueba que no se invoque el gate cuando UMP no permite solicitar anuncios. Esto no sustituye la prueba instalada del formulario ni demuestra por sí solo actualización de personalización de un anuncio ya cargado.
- Revisión final del código y pruebas de release; generar AAB solo tras cierre jurídico.
- Pruebas internas y aprobación de promoción a producción.

El hash del corpus jurídico permanece `BC9EE587FA6ECCA923DD0A163B8739E8CE9976C09A79C7551E88661B50B946D9`; no se adelantó la fecha visible de verificación.
