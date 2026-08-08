# Registro de cambios

Este archivo documenta los cambios relevantes de cada versión publicada de la aplicación.

## [1.05] - 2026-08-07

### Añadido

- Navegación jerárquica unificada para recorrer las seis secciones, títulos, capítulos y subcapítulos del Código.
- Índice estructurado `articles.json`, generado desde los recursos jurídicos, para la búsqueda global y la consulta directa por número de artículo.
- Resaltados y notas persistentes; las notas se protegen localmente mediante Android Keystore.
- Centro de privacidad para gestionar el consentimiento publicitario y eliminar búsquedas, favoritos, resaltados y notas guardados.
- Preferencias de lectura y compatibilidad con modo claro y oscuro.
- Checklist automático de prepublicación y workflow de GitHub Actions para pruebas, Lint, compilación `debug`, integridad de `articles.json` y detección de archivos o credenciales sensibles.

### Mejorado

- Flujo de navegación y presentación del contenido, con menos pantallas duplicadas y referencias centralizadas.
- Búsqueda por texto y artículo, incluyendo variantes alfanuméricas.
- Gestión de favoritos, ordenamiento, eliminación segura y compatibilidad con destinos guardados por versiones anteriores.
- Integración de Google UMP y AdMob: solicitudes condicionadas al consentimiento, recuperación ante fallos y anuncios de prueba en compilaciones `debug`.
- Compatibilidad con Android API 36, rendimiento de compilaciones `release` y configuración de R8.
- Documentación de desarrollo, pruebas, firma y publicación manual.

### Corregido

- Errores de recursos, estilos y referencias derivados de la migración de pantallas.
- Manejo de fallos de carga de anuncios y comportamiento sin conexión.
- Integridad y sincronización entre `strings.xml`, el catálogo de navegación y `articles.json`.
- Limpieza de binarios generados y metadatos de compilación que no deben versionarse.

### Seguridad

- Firma `release` configurable mediante archivo local ignorado, variables de entorno o propiedades Gradle locales, sin credenciales en el repositorio.
- Reglas de exclusión y validaciones para impedir que APK, AAB, almacenes de claves o contraseñas se incorporen al control de versiones.

El contenido jurídico continúa incluido dentro de la aplicación y no se actualiza remotamente.
