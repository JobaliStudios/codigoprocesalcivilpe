# Código Procesal Civil PE — Fase 4: mantenibilidad y calidad técnica

**Inicio:** noviembre de 2026 · **Estado:** en ejecución · **Base:** `main` tras la integración de Fase 2 / candidata 1.08 (`versionCode=13`).

## Objetivo
Facilitar cambios futuros, reducir el riesgo de regresiones y mejorar la separación de responsabilidades sin cambiar la experiencia de los usuarios. La Fase 4 **no es una actualización Android** ni autoriza publicar o sustituir la versión de Google Play.

### Reglas de ejecución
1. Un PR pequeño por misión, a partir de `main`, con pruebas que demuestren equivalencia funcional.
2. No cambiar textos ni metadatos del corpus jurídico, clasificación de vigencia, publicidad/consentimiento, almacenamiento de favoritos/notas/resaltados ni referencias de legislación salvo autorización explícita.
3. No cambiar `applicationId`, `versionCode=13`, `versionName=1.08`, dependencias o credenciales durante los refactors.
4. Exigir `test`, `lint`, `assembleDebug`, pruebas de variante release con R8 y regresión instrumental cuando la afectación lo amerite.
5. Documentar antes/después y distinguir pruebas automáticas de comprobación manual física.

## Diagnóstico inicial (código de la rama principal, al comenzar la fase)

| Componente | Hallazgo observado | Riesgo a gestionar |
|---|---|---|
| `SectionContentActivity.java` | **~1.210 líneas / ~53 KB**; reúne vista del artículo, historia de lectura, navegación, TTS, resaltados, favoritos, notas, compartir y diálogo Aa | Cambios locales pueden afectar múltiples funciones del lector. Extraer responsabilidades paulatinamente y con contratos de regresión |
| `navigation/LegalHierarchyRepository.java` | **~548 líneas / ~50 KB**; árbol de secciones con métodos de navegación y búsquedas | Distinguir estructura declarativa de duplicación real antes de refactorizar; preservar correspondencia de identificadores jurídicos |
| `lectura/ArticleTextToSpeechController.java` | **~622 líneas / ~19 KB** | Revisar separación entre máquina de estados y adaptador de plataforma, evitando cambios en voz, pausas o eventos |
| `busqueda/BusquedaViewModel.java` | **~390 líneas / ~14 KB**; lógica de consultas asíncronas y preferencias recientes | Mantener compatibilidad del debounce de 275 ms y protección ante respuestas obsoletas |
| CI del repositorio | Pruebas unitarias, Lint, debug y R8; job de 7 pruebas instrumentadas para la candidata 1.08 | Preservar cobertura y no confundir un build correcto con rendimiento medido o pruebas físicas |

**Precaución:** los tamaños de archivo son un indicador de complejidad potencial, **no una demostración de código duplicado o errores**. No se propone modificar automáticamente clases solo por ser largas.

## Misiones propuestas

### Misión 1 — Inventario y extracción segura del control Aa
- Documentar prioridades técnicas con datos observados.
- Separar la construcción y persistencia del diálogo `Aa` de `SectionContentActivity` a una clase dedicada.
- Mantener el render de texto, el tamaño elegido, el guardado y el botón Restablecer; añadir pruebas de equivalencia.
- **Criterio de cierre:** CI verde + revisión del diálogo en teléfono, sin cambiar las preferencias existentes.

### Misión 2 — Reducir responsabilidades del lector
- Examinar y separar un área funcional por PR (copiar/compartir, comandos de artículo, o estado del encabezado) solo tras identificar sus dependencias.
- Conservar explícitamente selección de artículo visible, notas, favoritos, historial y anuncios en transiciones.
- Añadir pruebas de regresión para los límites entre componentes.

### Misión 3 — Navegación y catálogo
- Analizar `LegalHierarchyRepository` y `LegalContentCatalog` para localizar lógica realmente repetida o costosa.
- Mantener intactas las claves de artículos, estructura de libros/secciones y destinos internos.
- Agregar pruebas de identidad de rutas antes de optimizar índices o modularizar.

### Misión 4 — Servicios y contratos de datos
- Revisar persistencia de favoritos, historial, notas, resaltados y la caché jurídica, buscando dependencias innecesarias y contratos implícitos.
- Extraer lógica sin migraciones de almacenamiento salvo que el usuario lo autorice.
- Fortalecer pruebas de rotación, recreación, lectura y resultados asincrónicos.

### Misión 5 — Cierre técnico de mantenibilidad
- Revisión de reglas de CI, pruebas de Android/R8, legibilidad, documentación de arquitectura y checklist de mantenimiento.
- Auditoría final de diffs contra `main` para demostrar ausencia de cambios jurídicos, SDK/ads, versión y formatos persistidos.
- Cierre de Fase 4 solo tras integrar cada PR y verificar pruebas pertinentes.

## Fuera del alcance
- Auditar o certificar vigencia legal de 862 artículos: la Fase 1 documental permanece cerrada con salvedades.
- Publicar en Google Play, generar un AAB firmado o cambiar el canal de despliegue.
- Añadir funcionalidades o contenido de crecimiento: reservar para Fase 5.

## Puerta de aceptación de Misión 1
- [ ] Tests nuevos de Aa (selección, guardado y restauración) y regresiones del lector aprobados.
- [ ] Android Lint, compilación debug y R8 sin fallos.
- [ ] Revisión de PR y fusión aprobadas por el propietario.
- [ ] Prueba manual: abrir un artículo, Aa 125 %, salir/reabrir, Restablecer, verificar preferencia y texto.

## Ejecución de Misión 2 — Acciones Copiar y Compartir

**Cambio acotado:** la lógica de acceso al portapapeles Android y construcción del `Intent.ACTION_CHOOSER` se trasladó desde `SectionContentActivity` a `ReaderArticleTransferController` (un único componente para las acciones de transferencia de texto).

**Responsabilidad que permanece en el lector:** detectar el artículo visible inmediatamente antes de pulsar el botón; dar feedback con Snackbar; controlar la disponibilidad del artículo y abrir el chooser de Android.

**Responsabilidad extraída:** construir el texto mediante el `ArticleShareFormatter` ya existente; copiar el artículo con la etiqueta de portapapeles actual; generar `ACTION_SEND` con `text/plain`, asunto y texto idénticos a la versión anterior.

**Contratos protegidos:** 
- Artículo `506-A`: numeración y título exactos al copiar.
- Artículo `835`: compartir conserva íntegro el texto y la nota de derogación.
- Copiar y Compartir producen el mismo contenido para un mismo artículo.
- Desplazamiento `560 → 562`: Compartir utiliza el artículo visible en el momento de pulsar (regresión de la actividad).
- Continúan vigentes todas las pruebas existentes de navegación, favoritos, historial, resaltados y acciones del lector.

**Alcance explícitamente excluido:** no se toca `ArticleShareFormatter`, `articles.json`, los archivos de preferencias, XML de interfaz, anuncios, UMP, navegación, dependencias ni la versión 1.08/13.

**Puerta de aceptación:**
- [ ] CI `test`, `lint`, `assembleDebug`, `testReleaseUnitTest`, `minifyReleaseWithR8` verde.
- [ ] Revisar PR y fusionar a `main`.
- [ ] En dispositivo, copiar artículo 506-A y compartir el 835, comprobar texto completo y nota jurídica; cambiar de artículo por desplazamiento y volver a probar ambos botones.
