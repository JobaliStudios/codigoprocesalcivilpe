# Código Procesal Civil PE

## Descripción del proyecto

Aplicación Android nativa para consultar el Código Procesal Civil del Perú. Incluye navegación por las seis secciones del código, búsqueda global y por número de artículo, favoritos, resaltados, notas, cuestionarios y preferencias de lectura con modo claro y oscuro.

El contenido jurídico se distribuye dentro de la aplicación para que la consulta principal funcione sin conexión. La conectividad se utiliza para los servicios externos de anuncios y consentimiento.

> [!IMPORTANT]
> El contenido jurídico no se actualiza remotamente. Los textos incluidos en una instalación solo cambian cuando se modifica el repositorio, se regenera `articles.json` y se publica una nueva versión de la aplicación. Antes de publicar, debe comprobarse la vigencia del texto con una fuente oficial.

## Requisitos técnicos

- Windows con PowerShell 5.1 o posterior para los scripts de `tools/`.
- Android Studio Narwhal 3 Feature Drop (`2025.1.3`) o una versión posterior compatible con Android Gradle Plugin `8.13.2`. Consulte la [tabla oficial de compatibilidad](https://developer.android.com/studio/releases#android_gradle_plugin_and_android_studio_compatibility).
- JDK 17. El proyecto compila el código fuente con Java 17 y AGP 8.x requiere como mínimo ese JDK; consulte la [documentación de Java para builds Android](https://developer.android.com/build/jdks).
- Android SDK Platform 36 instalado.
- Gradle `8.13`, descargado automáticamente por el wrapper del repositorio.
- Para ejecutar la app: emulador o dispositivo con Android 11/API 30 o posterior.
- Para pruebas instrumentadas: dispositivo o emulador iniciado y visible mediante `adb devices`.

Configuración Android principal:

- `applicationId`: `com.jobalistudios.codigoprocesalcivilpe`
- `compileSdk`: `36`
- `targetSdk`: `36`
- `minSdk`: `30`

## Ejecutar la aplicación

1. Abra la raíz del repositorio en Android Studio.
2. Espere a que finalice **Sync Project with Gradle Files**. Android Studio creará o solicitará `local.properties` con la ubicación del SDK.
3. Seleccione un emulador o dispositivo con API 30 o posterior.
4. Ejecute la configuración `app` con **Run**.

Desde PowerShell también puede compilar e instalar la variante `debug`:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Las compilaciones `debug` utilizan unidades publicitarias de prueba. No deben cambiarse por unidades reales durante el desarrollo.

## Pruebas y análisis estático

Ejecute las pruebas unitarias:

```powershell
.\gradlew.bat test
```

Ejecute Android Lint:

```powershell
.\gradlew.bat lint
```

Con un dispositivo o emulador conectado, ejecute las pruebas instrumentadas:

```powershell
.\gradlew.bat connectedAndroidTest
```

El checklist de pruebas manuales está en [`MANUAL_TESTS.md`](MANUAL_TESTS.md). Debe completarse antes de promover una versión fuera del canal de pruebas internas.

## Contenido jurídico y `articles.json`

La fuente textual se encuentra en:

```text
app/src/main/res/values/strings.xml
```

El índice estructurado utilizado para búsquedas se genera en:

```text
app/src/main/assets/articles.json
```

Después de cambiar textos jurídicos o la estructura de `LegalContentCatalog.java`, regenere el JSON:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\generate_articles.ps1
```

Después valide la sincronización con los recursos Android:

```powershell
.\gradlew.bat testDebugUnitTest --tests "com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepositoryTest"
```

No edite `articles.json` manualmente. Tampoco cambie el contenido jurídico para corregir un problema meramente técnico salvo que exista una inconsistencia claramente identificada y revisada.

## Firma release sin exponer secretos

La configuración admite, en orden de prioridad:

1. Variables de entorno.
2. Propiedades Gradle locales.
3. El archivo local `keystore.properties`.

Las cuatro propiedades necesarias son:

```properties
RELEASE_STORE_FILE=<C:/ruta/segura/clave-existente.jks>
RELEASE_STORE_PASSWORD=<contraseña-del-almacén>
RELEASE_KEY_ALIAS=<alias-existente>
RELEASE_KEY_PASSWORD=<contraseña-de-la-clave>
```

Para la configuración local recomendada en Windows:

```powershell
Copy-Item .\keystore.properties.example .\keystore.properties
notepad .\keystore.properties
```

Use `/` en las rutas escritas dentro de archivos `.properties`. No genere ni sustituya la clave de publicación si ya existe una clave válida.

Como alternativa, coloque las mismas propiedades en `%USERPROFILE%\.gradle\gradle.properties` o configúrelas como secretos/variables de entorno del proceso de CI. No coloque credenciales en el `gradle.properties` versionado del proyecto ni las pase por argumentos que puedan quedar en el historial de la terminal.

Nunca deben versionarse:

- `keystore.properties`
- Archivos `*.jks`, `*.keystore`, `*.p12` o `*.pfx`
- Contraseñas, alias reales o material de clave privada

Consulte también [`SIGNING.md`](SIGNING.md). Si faltan credenciales, las tareas de empaquetado `release` se detienen con un mensaje que enumera las propiedades necesarias.

## Generar un AAB release

Con la firma configurada y usando la clave existente:

```powershell
.\gradlew.bat bundleRelease
```

El bundle firmado se genera en:

```text
app/build/outputs/bundle/release/app-release.aab
```

Los AAB, APK y demás salidas de compilación están excluidos de Git.

## Checklist automático de prepublicación

El script de prepublicación ejecuta, en orden, limpieza, pruebas unitarias, Lint, regeneración y validación de `articles.json`, compilación `release`, generación del AAB, comprobación de identificador y versión, y escaneo de credenciales:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\tools\pre_release_check.ps1
```

El proceso se detiene ante el primer error. El script genera artefactos locales, pero no inicia sesión ni sube archivos a Google Play.

## Proceso recomendado de publicación

1. Confirme la vigencia del contenido jurídico y regenere `articles.json` si hubo cambios.
2. Actualice `versionCode` y `versionName` en `app/build.gradle`. El `versionCode` debe ser mayor que cualquier versión subida previamente a Google Play Console.
3. Complete [`MANUAL_TESTS.md`](MANUAL_TESTS.md) en dispositivos representativos, incluyendo navegación, búsquedas, datos guardados, temas y consentimiento de anuncios.
4. Ejecute `tools\pre_release_check.ps1` y conserve la ruta y el SHA-256 que muestra para el AAB.
5. Revise manualmente el AAB, las notas de versión, la ficha de Play Store, la declaración de seguridad de datos, el uso de `AD_ID` y los mensajes de privacidad de AdMob/UMP.
6. Suba manualmente el AAB a un canal de pruebas internas de Google Play Console. Este repositorio no automatiza la publicación.
7. Instale y pruebe la entrega procesada por Google Play antes de promoverla a producción.

## Guía de estilos y design tokens

Este proyecto usa tokens en `app/src/main/res/values/` para evitar valores hardcodeados y mantener consistencia visual.

### Dónde están los tokens

- **Colores semánticos:** `colors.xml` (`colorSurface`, `colorOnSurface`, `colorPrimaryContainer`, `colorOutline`, etc.).
- **Tipografía / spacing / radios:** `dimens.xml` (`text_title_l`, `text_body_m`, `space_m`, `radius_l`, etc.).
- **Estilos base reutilizables:** `styles.xml`.

### Guía de uso por componente

- **Título principal (pantalla/card):** `@style/App.Text.Title.L`
- **Subtítulo / heading secundario:** `@style/App.Text.Subtitle.M`
- **Texto body:** `@style/App.Text.Body.M` o `@style/App.Text.Body.S`
- **CTA principal (botón):** `@style/App.Button.Primary`
- **Card recurrente (Home / Configuración / Favoritos / Búsqueda):** `@style/App.Card.Recurrent`
- **Chips de filtro:** `@style/App.Chip.Filter`

### Reglas prácticas

1. No usar colores hex directos en layouts; usar `@color/...` semántico.
2. No usar `dp/sp` hardcodeados en layouts; usar `@dimen/...`.
3. Para cards repetidas, aplicar siempre `@style/App.Card.Recurrent`.
4. Si necesitas una variante, crear estilo derivado en `styles.xml` en lugar de repetir atributos en XML.
