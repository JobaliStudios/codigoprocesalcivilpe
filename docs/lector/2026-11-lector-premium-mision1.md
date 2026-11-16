# Fase 6 — Misión 1: temas y tipografía de lectura

## Objetivo y alcance

Implementación incremental de la apariencia premium del lector nativo en la rama
\`feat/fase6-lector-premium-m1-temas-tipografia\`, sin fusionar ni publicar.
La misión 2 modifica navegación/índice; la 3, glosario; la 4, pantalla de Inicio y
Configuración definitiva; la 5, accesibilidad y cierre.

## Contratos de compatibilidad

- Se preservan \`applicationId\`, \`versionCode=13\`, \`versionName=1.08\`, integraciones AdMob/UMP y permisos.
- No se editan \`strings.xml\` jurídico, \`articles.json\`, \`questions.json\` ni fuentes legales.
  Los textos de UI se agregan en \`reader_premium_strings.xml\`.
- \`app_settings.dark_mode_enabled=true/false\` de 1.07 migra una vez a
  \`app_theme_mode=DARK/LIGHT\`; sin la clave antigua, el modo predeterminado
  es \`SYSTEM\`. La selección del usuario no se cambia silenciosamente.
- Se conservan los almacenes locales de favoritos, notas, resaltados e historial.
  \`resetReadingPreferences\` borra solo las preferencias de lectura;
  el Centro de privacidad borra \`app_settings\` como antes.
- Los gestos de deslizar se guardan como opt-in, \`false\` de fábrica; su
  reconocimiento real se implementará en la Misión 2.
- El glosario está habilitado como preferencia, pero no se carga ni dibuja
  contenido hasta la Misión 3 y la aprobación editorial del propietario.
- \`Configuración\` sigue siendo la etiqueta de la barra inferior.
- Las familias Literata y Atkinson Hyperlegible se distribuyen como archivos
  TTF locales, bajo las licencias OFL incluidas en \`docs/licenses\`.

## Componentes y riesgos

- \`ThemePreferenceManager\` y \`ConfiguracionFragment\`: cambio de boolean a
  selector Sistema/Claro/Oscuro. El grupo impide guardar el estado de vista
  para evitar la regresión del interruptor oscuro detectada en 1.07.
- \`ReadingPreferenceManager\`: nuevas preferencias persistentes y restablecimiento
  selectivo. Los valores antiguos de escala y mantener pantalla encendida siguen leyendo la misma clave.
- \`SectionContentActivity\`, \`ReaderTypography\`, \`ReaderAppearancePalette\`:
  apariencia del texto jurídico, overlays y ventana; reconstrucción desde el mismo
  corpus y preservación de ancla/fracción de desplazamiento.
- \`AppearanceBottomSheet\`: vista previa, variantes de tema y fuente,
  85–160 %, interlineado, márgenes, justificación, enfoque, pantalla encendida
  y restablecimiento. Los cambios se notifican mediante FragmentResult.
- Modo enfoque: barra del lector oculta, indicador y acción explícita de salida;
  el detector de toque observa eventos sin consumir los manejados por el controlador
  existente de selección/resaltados.

## Verificaciones necesarias antes de aprobación del PR

En un entorno con Android SDK 36, JDK 17 y PowerShell:

\`\`\`powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\generate_articles.ps1
git diff --exit-code -- app/src/main/assets/articles.json
python tools/validate_quiz_bank.py
python tools/validate_discovery_assets.py
.\gradlew.bat test lint assembleDebug testReleaseUnitTest minifyReleaseWithR8
\`\`\`

Efectuar también pruebas de dispositivo: actualización 1.07 → 1.08, preservación
de datos de usuario, lectura de un artículo largo, letras al 200 % del tamaño
del dispositivo, temas de sistema, cambio Aa en vivo, TalkBack, selección,
referencias cruzadas, resaltados en cuatro colores, notas, TTS, giro y modo enfoque.

**Estado de validación:** el conector GitHub permite cambios de código pero no
ejecuta la compilación Gradle ni PowerShell; no se afirma que estos comandos
hayan pasado. La fuente Literata suministrada por Google Fonts es un TTF
variable completo: el subconjunto latino y la revisión visual final permanecen
como trabajo de optimización/verificación para este PR.

## Fuentes y licencias

- Literata: Google Fonts (\`ofl/literata\`), OFL 1.1.
- Atkinson Hyperlegible: Google Fonts (\`ofl/atkinsonhyperlegible\`), OFL 1.1.
- Avisos originales: \`docs/licenses/LITERATA-OFL.txt\` y
  \`docs/licenses/ATKINSON-HYPERLEGIBLE-OFL.txt\`.
