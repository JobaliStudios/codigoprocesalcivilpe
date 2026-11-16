# Registro de cambios

Este archivo documenta los cambios relevantes de cada versión publicada de la aplicación.

## [1.08] — Candidata, pendiente de publicación

**Estado:** desarrollo y validación interna; no implica que esta actualización esté publicada.

### Añadido
- Lector premium offline: temas Claro, Sepia y Oscuro; tipografías Literata y Atkinson Hyperlegible incluidas localmente; ajustes de tamaño, interlineado, márgenes, justificación y modo enfoque.
- Progreso por artículo, índice del capítulo con buscador y filtros, navegación Anterior/Siguiente y gesto horizontal opcional, desactivado por defecto.
- Infraestructura opcional para glosario editorial, con validación de origen y revisión. El glosario distribuido está vacío hasta la aprobación expresa de definiciones.
- Inicio con Continuar leyendo, Artículo del día determinista por fecha local y cuatro accesos rápidos.
- Configuración reorganizada: apariencia de la app, preferencias de lectura y accesos a Soporte, Acerca de y Centro de privacidad.

### Mejorado
- Accesibilidad de controles del lector, hojas, Inicio y Configuración; controles desplazables o apilados con escala de texto del sistema al 200 % y etiquetas TalkBack para notas y ajustes.
- Evita reconstruir todos los spans jurídicos al cambiar únicamente fuente, márgenes, interlineado o justificación.
- Pruebas automáticas de navegación, datos locales, estilos, selector diario, glosario y formatos Gson ofuscados; pruebas instrumentadas de accesibilidad y rendimiento con el corpus real.

**Sin cambios en los artículos ni en su contenido jurídico.** La compilación candidata se mantiene en versionName 1.08, versionCode 13, sin cambios de permisos, anuncios o conectividad. La validación manual, la revisión jurídica y la aprobación de lanzamiento siguen pendientes.

## [1.07] — Pendiente de redacción del propietario

La descripción y fecha definitiva de 1.07 serán proporcionadas por el propietario. No se atribuyen aquí cambios que aún no han sido documentados.

## [1.06] - 2026-08-11

### Corregido

- Restaurada en compilaciones `release` la carga del índice local de artículos que alimenta las acciones de anterior/siguiente, favoritos por artículo, notas, búsqueda global e historial de lectura.
- Restaurada la carga de preguntas del cuestionario en compilaciones minimizadas, evitando el cierre al iniciar una sesión.
- Protegidos explícitamente los contratos JSON frente a la optimización de R8 y añadidas pruebas de regresión para esos modelos.

El contenido jurídico no fue modificado en esta corrección.

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
