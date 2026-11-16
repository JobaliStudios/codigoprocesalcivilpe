# Fase 6 — Misión 5: cierre técnico del lector premium 1.08

## Alcance

Último PR funcional de Fase 6, separado de M1–M4. Este PR **no es una
autorización de publicación**. Mantiene versionName 1.08, versionCode 13,
applicationId, texto jurídico, anuncios y SDKs de producción sin cambios.

### Accesibilidad

- Se anuncian por nombre **y estado** las notas del artículo (agregar /
  editar) y los campos Tema, Fuente y Tamaño de Configuración (incluyendo
  el valor persistido). Se marcan encabezados de Inicio y Configuración.
- Cuando el tamaño de letra del sistema es al menos 175 %, los cuatro
  accesos de Inicio se apilan; los controles inferiores de lectura y los
  segmentos de apariencia/configuración se desplazan horizontalmente
  con ancho natural, sin recortar etiquetas. En tamaños habituales
  se mantiene la cuadrícula 2×2 y la barra existente.
- Se conservan contenidos accesibles del índice, favoritos, botón Aa,
  historial normativo, notas, capítulo y acción de enfoque «Mostrar
  controles». No se reemplazan estados semánticos por colores.
- ReaderAccessibilitySizingTest valida los layouts con configuración
  Robolectric de letra 200 % y 100 %; las comprobaciones reales en
  dispositivo corresponden a MAN-23/24.
- ReaderPremiumAccessibilityInstrumentedTest habilita los
  AccessibilityChecks de Espresso sobre acciones principales de Inicio,
  Configuración, lector, hoja de apariencia e índice de capítulo.
  **No reemplaza la evaluación manual con TalkBack**.

### Rendimiento

- ReaderRenderPolicy define cuándo hace falta recomponer spans:
  **sí** al cambiar el tema (colores de referencias/callouts), tamaño
  real del texto (callouts) o glosario activado; **no** cuando solo cambian
  la familia de fuente, márgenes, interlineado o justificación.
- SectionContentActivity conserva el artículo y la fracción del
  desplazamiento aunque el formato cambie sin reconstruir spans.
  La sección pesada de formato usa una traza Android
  «CPC:formatearSpansJurídicos» para facilitar un perfilado.
- ReaderAppearancePerformanceInstrumentedTest emplea el corpus real:
  bloque seccionquintatit4cap2subcap1 con 18 430 caracteres según
  el asset inspeccionado el 16/11/2026. Usa reloj monotónico
  (elapsedRealtimeNanos) para registrar las latencias síncronas
  reales de interlineado, márgenes y tema. Comprueba presupuestos
  máximos de 700 ms para cambios tipográficos y 1 500 ms para
  cambio de tema; son **límites de regresión, no una certificación
  de ausencia de frames perdidos**.
- Se ejecutará en Android CI la job de emulador para la rama
  «feat/fase6-lector-premium-m5-*». Reportar resultados reales solo
  cuando GitHub Actions termine; los resultados de dispositivos
  físicos quedan pendientes de MAN-25. No inventar promedios ni
  extrapolar de Robolectric.

### Documentación de lanzamiento

- MANUAL_TESTS.md incluye MAN-13 a MAN-26, todos sin marcar hasta
  que el propietario aporte evidencia.
- CHANGELOG.md añade 1.08 como **candidata, no publicada**;
  1.07 queda marcada como pendiente de redacción del propietario.
- docs/releases/2026-11-fase2-1.08-notas.md recoge las
  funciones del lector y restricciones de lanzamiento.
- docs/licenses/README.md recopila atribuciones existentes de
  Literata y Atkinson Hyperlegible y remite a los dos textos
  SIL Open Font License 1.1 ya versionados. No añade fuentes,
  licencias de terceros ni archivos de distribución.

## Política de aceptación

La CI debe verificar sincronización de articles.json tras ejecutar el
generador, validadores Python de cuestionarios, descubrimiento y glosario,
y comandos Android:

- ./gradlew test
- ./gradlew lint
- ./gradlew assembleDebug
- ./gradlew testReleaseUnitTest minifyReleaseWithR8
- connectedDebugAndroidTest, en el emulador Android 15 para este PR

La validación automatizada y el checklist manual son complementarios.
No hay aprobación para merge, firma o publicación en Google Play.
