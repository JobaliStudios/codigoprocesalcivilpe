# Pruebas en dispositivo — 9 de noviembre de 2026

Dispositivo físico: modelo 2303ERA42L, Android 15. Compilación debug de la rama de fase 1, versión 1.06 / código 11. No es un artefacto de producción.

## Estado vigente

Los bloqueos de instalación, apertura y entrada de MIUI se resolvieron con intervención del propietario. **Siete pruebas instrumentadas aprobadas**, en tres ejecuciones: 3 sin interfaz, 2 de navegación principal/quiz y 2 específicas de los puntos de salida de fase 1. La cronología siguiente conserva los fallos anteriores para trazabilidad; ya no son bloqueos activos.

La compilación posterior del ajuste de idioma «back» → «Atrás» pasó con Lint (cero errores, 463 advertencias). Se instaló el APK actualizado y se verificó con aapt2 que el recurso empaquetado `string/back` contiene «Atrás». Hash actual del APK: `F20E99A55D89E4388A9C517935B74186F6F47DE514E34B0B85BD0F3185F6CF41`. El hash del apartado siguiente corresponde al APK usado en las siete pruebas, anterior únicamente a este cambio de texto.

## Corpus y artefacto

- Corpus después de JUR-007: 104 bloques, 862 artículos; SHA-256 `E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538`.
- APK debug probado: SHA-256 `0A1165DED357E9C6D76117903BC7DA9434894A8012D28AB7D913059DF543FCCB`.
- Pruebas unitarias posteriores al cambio: 13 pruebas dirigidas, cero fallos y cero errores.

## Resultados observados

1. La instalación fue inicialmente bloqueada por `INSTALL_FAILED_USER_RESTRICTED`. El propietario intervino y después se instalaron correctamente la app y el paquete de pruebas. No se eludió la restricción ni se modificaron ajustes del teléfono por automatización.
2. La prueba del quiz contenía referencias a IDs retirados (`option5`, `option10`, `btnStartQuiz`). Se adaptó al botón actual de repaso rápido; ambos APK compilan.
3. La ejecución conjunta inició cinco pruebas, pero quedó detenida al comenzar `MainNavigationInstrumentedTest`. El registro no mostró lanzamiento de una actividad de la app. Se detuvo el proceso de pruebas tras no observar avance; esa ejecución no cuenta como aprobada. El entorno Gradle retiró posteriormente los paquetes de prueba y se reinstalaron para aislar el problema.
4. Ejecución directa de `QuestionBankTest` y `jobalistudiosInstrumentedTest`: **OK (3 tests)**, 0.145 segundos. Se confirmó carga del banco, límite de preguntas y contexto del paquete.
5. Apertura normal mediante la actividad de launcher: `Status: ok`, arranque en frío de 1473 ms. La jerarquía UI mostró el onboarding «Consulta sin conexión», paso 1 de 3, y botón «Siguiente». Este control acredita la primera pantalla; no valida el recorrido completo ni rendimiento estable.
6. El reintento separado con límite de 60 segundos produjo `TestTimedOutException` dentro de `Instrumentation.startActivitySync`, antes de ejecutar los pasos de navegación. El registro del sistema mostró `ActivityStarterImpl: MIUILOG- Permission Denied Activity` tanto para `MainActivity` como para `QuizzCPCStartScreen`. MIUI rechaza la apertura desde la instrumentación; no es un resultado aprobado de interfaz ni prueba de un bloqueo al abrir normalmente la app. Se solicitó al propietario revisar el permiso correspondiente; la revisión manual puede continuar sin cambiar ajustes por automatización.
7. Tras el aviso del propietario de haber habilitado el permiso, las dos pruebas abrieron las pantallas y avanzaron a la interacción. Ambas fallaron al inyectar toques: `SecurityException: Injecting input events requires ... INJECT_EVENTS`. El mismo rechazo apareció al intentar un toque manual por ADB. No se continuó enviando entrada. Se solicitó al propietario revisar «Depuración USB (Configuración de seguridad)», opción adicional de MIUI; pendiente de respuesta y reintento. El permiso de apertura ya no es el bloqueo actual.
8. Después de habilitar el control de entrada, `MainNavigationInstrumentedTest` y `QuizzNavigationTest` pasaron: **OK (2 tests)**, 8.968 segundos. Se comprobaron pestañas principales, inicio de preguntas y regreso al inicio del quiz.
9. Se añadieron y ejecutaron dos comprobaciones de integración de fase 1: **OK (2 tests)**, 7.506 segundos. `quizResults_areVisibleBeforeReturningToPracticeStart` comprueba resultados visibles y botón de salida al inicio; `readerBackToIndex_opensTheArticlesParentIndex` abre 506-A y comprueba el título y lista del índice padre. El escenario no fuerza el umbral publicitario ni acredita que se haya mostrado un intersticial real.

## Límites

Siguen pendientes TTS, borrado real, máscaras del launcher, cambio real de consentimiento y visualización del anuncio al cumplirse el umbral de frecuencia. El retorno desde resultados y al índice padre sí están verificados en los escenarios indicados. La compilación debug usa IDs de anuncio de prueba; no acredita personalización de la configuración de producción. No se publicó una versión Android en Google Play.
