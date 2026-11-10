# Continuación del trabajo pendiente — Fase 1

**Estado:** trabajo local sobre rama `continuacion-fase-1`, derivada de `codex/fase-1-confianza-cumplimiento` (`577d5d9`). **Sin publicación ni AAB firmado.**

## Implementación de este lote

1. Corrección del cómputo de intersticiales: `InterstitialAdCoordinator` ya no llama a `recordShown` al **intentar** abrir el anuncio; registra la impresión al recibir `onAdShowedFullScreenContent`. Se usa `InterstitialImpressionTracker` para evitar doble registro de un callback repetido.
2. Tres pruebas de regresión nuevas: intento fallido sin consumir cuota, impresión confirmada que inicia enfriamiento y callback repetido que no cuenta dos veces.
3. Script `tools/verify_phase1_local.py`: verifica hash del corpus previamente aprobado, 104 bloques / 862 IDs únicos, presencia de las cinco correcciones jurídicas y consistencia de la matriz mecánica histórica. **No certifica vigencia.**
4. Cola de revisión de 160 artículos contextualizados y dos documentos de decisiones pendientes: jurídico y Play/AdMob. No hay cambios jurídicos en código.

## Pruebas ejecutadas en esta continuación

| Verificación | Resultado | Límite |
| --- | --- | --- |
| Validación offline del corpus | PASS: 104 bloques, 862 artículos; SHA idéntico al aprobado | No confirma ausencia de reformas |
| Compilación JDK 21 de las clases puras `InterstitialFrequencyPolicy` e `InterstitialImpressionTracker` | PASS | No compila toda la app |
| 5 tests de políticas/imprensiones intersticiales (`InterstitialFrequencyPolicyTest` y nuevo `InterstitialImpressionTrackerTest`) | **5 PASS / 0 FAIL** en entorno Java con adaptador mínimo de `org.junit` | Ejecución independiente, no sustituye el `testDebugUnitTest` oficial |
| Gradle Android, Lint, APK, instrumentadas y dispositivo | **NO EJECUTADOS** | Este entorno no dispone de Android SDK/Gradle ni ADB conectado |

## Pruebas obligatorias a ejecutar en Windows

### 1. Compilación y regresión

Desde la carpeta del proyecto en la **nueva rama/copia**, con Android Studio/SDK instalados:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug :app:assembleDebugAndroidTest
```

Comprobar **BUILD SUCCESSFUL**, XML de resultados unitarios en `app/build/test-results/testDebugUnitTest/`, `app/build/reports/lint-results-debug.html`, APK `app/build/outputs/apk/debug/app-debug.apk`. No interpretar «cero errores» como «cero advertencias».

### 2. Anuncios: entorno de prueba, no producción

1. Instalar la APK **debug**, que selecciona el identificador oficial de anuncios de prueba para intersticiales. Abrir la app y completar onboarding/consentimiento si corresponde.
2. Abrir un artículo desde búsqueda o índice; verificar que el artículo aparezca sin anuncio interpuesto.
3. Hacer **al menos ocho navegaciones** y salir explícitamente del quiz desde resultados o regresar del lector al índice. Si existe anuncio precargado y consentimiento válido, debe mostrarse solo en esas pausas naturales. Si no hay red o inventario, la navegación **no se bloquea**.
4. Confirmar que una impresión **real** contabiliza una cuota, nunca más de **dos impresiones por sesión**, y un intervalo de **diez minutos** entre impresiones. Un fallo de presentación no debe consumir una de las dos cuotas.
5. Verificar que la app no retorne a un artículo anterior ni abra dos destinos por un doble toque durante el intersticial.

> Las pruebas de frecuencia no garantizan que Google sirva anuncios. Usar `Logcat`/inspector de anuncios para registrar eventos del SDK y no «forzar» publicidad real.

### 3. Privacidad UMP

1. Ejecutar con configuración de prueba y registrar el ID hash de prueba de UMP cuando se necesite simular región EEE en un móvil físico. No registrar identificadores de producción o de personas en un documento público.
2. Comprobar formulario requerido, rechazo/aceptación, aparición del acceso para gestionar preferencias **solo cuando UMP lo exige**, y que no se soliciten anuncios cuando `canRequestAds()` es falso.
3. Abrir y cerrar opciones de privacidad: comprobar que no se reutilicen anuncios precargados antes del cambio; validar un nuevo anuncio bajo los permisos vigentes.
4. Comprobar que los banners e intersticiales no aparezcan si el estado de consentimiento no lo permite.

### 4. TTS, borrado local y marca

1. TTS: elegir artículo extenso; iniciar lectura, pausar, reanudar, cambiar de artículo y volver; comprobar que no siga hablando en otra pantalla. Probar voz/idioma español disponible y caso de voz no instalada.
2. Privacidad: crear de forma **deliberada y en una instalación de pruebas** un favorito, búsqueda reciente, resaltado, nota, historial y repaso de quiz. Usar **Borrar todos** tras confirmar que son datos descartables; salir, reabrir y comprobar que desaparecieron. No ejecutar este test sobre anotaciones reales sin copia.
3. Ícono: revisar `mipmap-mdpi` de **48x48**, círculo, máscara adaptativa y opción monocroma en un teléfono real; comprobar legibilidad y ausencia de recortes. La verificación del generador realizada por Codex no sustituye esta prueba visual.

## Bloqueantes para cerrar Fase 1

- **JUR-001 y JUR-002:** comparación integral contra fuente normativa vigente y decisiones individuales de cualquier corrección adicional. No adelantar la fecha jurídica.
- **Play/AdMob:** resolver diagnósticos/finalidades respecto a SDK real; decisión de audiencia de 13–17 años y publicidad compatible con políticas aplicables.
- **Android:** suite completa, Lint, pantalla real UMP, anuncios, TTS, borrado e ícono; registrar versiones y hashes nuevos.
- **Release:** producir AAB y preparar publicación solo después de cerrar esos puntos y aprobar separadamente cualquier envío externo.

## Precaución de Git

El ZIP original estaba sobre `main` con cambios locales ajenos a este lote. **No** aplicar `reset --hard`, `clean` ni fusión automática sobre ese árbol. La entrega segura adjunta una copia del **código de la fase 1 más el lote local** sin `.git`, compilaciones ni secretos y un parche relativo al commit de Codex.
