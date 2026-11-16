# Fase 6 — Misión 2: progreso, índice contextual y navegación

## Alcance de este PR

- Encabezado compacto con Atrás, bloque jurídico, artículo visible y ordinal \`n/N\`,
  índice contextual y controles «Aa», más indicador de progreso de 3dp.
- Reorganización de acciones inferiores: Anterior, Favorito, Nota, Escuchar, Más
  (Copiar/Compartir) y Siguiente con número del siguiente artículo.
  Se mantienen \`btnCopyArticle\`, \`btnShareArticle\`, \`btnListenArticle\`
  y los otros IDs históricos para compatibilidad de automatización.
- \`ChapterIndexBottomSheet\` con búsqueda local normalizada, filtros Todos,
  Favoritos, Con notas y Resaltados, sumilla de hasta dos líneas, indicadores
  persistidos y entrada al artículo actual. «Otro capítulo» reutiliza la ruta
  al listado y «Ir al artículo…» la hoja preexistente.
- Gesto horizontal sujeto a preferencia \`swipe_navigation\` (apagado de fábrica),
  umbral simultáneo de desplazamiento y velocidad y exclusión de bordes de 24dp.
  No captura eventos DOWN/MOVE; prioriza selección y desplazamiento vertical.
- No se introducen Gson DTO nuevos, dependencias, permisos, telemetry o red.

## Compatibilidad y estado

\`SectionContentActivity\` sigue guardando \`STATE_CURRENT_ARTICLE_NUMBER\`
cuando se recrea. El índice obtiene siempre la secuencia de
\`ArticleRepository.getBlock\`, sin crear un orden paralelo ni duplicar texto.
Las notas/resaltados y favoritos se consultan de sus almacenes originales.
La navegación dentro del bloque registra navegación sin interrupción
como hacen los botones Anterior/Siguiente; fuera del bloque se conserva
la resolución jurídica de \`ArticleNavigationResolver\`.

La UI del índice usa el texto del corpus como sumilla y los ejemplos de
las capturas solo como guía visual. La etiqueta de barra inferior sigue
siendo «Configuración». No se modifica la versión, anuncios, privacidad,
corpus, quiz ni configuración de firma.

## Controles que se deben ejecutar

1. \`./gradlew test\`.
2. \`./gradlew lint\`.
3. \`./gradlew assembleDebug\`.
4. \`./gradlew testReleaseUnitTest minifyReleaseWithR8\`.
5. Validadores Python y sincronización de \`articles.json\` integrados en
   GitHub Actions. No se edita \`strings.xml\` legal.
6. Pruebas manuales en Android: comenzar/terminar capítulo; índice con
   filtros combinados, favoritos, notas y resaltados; apertura de otro
   capítulo, Ir al artículo; desplazamiento vertical, selección,
   enlaces, TTS, barras de sistema, modo enfoque; gestos
   desactivados/activados, bordes de 24dp, TalkBack y fontScale 200 %.

**CI y validaciones manuales:** consignar resultados reales del workflow del PR;
esta documentación no da por realizadas pruebas aún no ejecutadas.

## Riesgos a revisar

- El lector conserva vistas con IDs históricos fuera de la barra compacta
  para evitar regresiones funcionales; deben verificarse en tamaños pequeños.
- El índice consulta resaltados cifrados mediante \`HighlightsManager\`; no
  descifra ni reescribe contenido por su cuenta.
- El resto de la Fase 6 (glosario, Inicio/Configuración definitivo) queda
  expresamente fuera de este PR.

**No fusionar ni publicar desde esta rama sin revisión del propietario.**
