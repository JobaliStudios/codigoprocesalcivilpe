# Fase 6 — Misión 4: Inicio y Configuración rediseñados

## Inicio (HomeFragment, HomeDashboardStateBuilder)

- Se conserva \`adContainer2\` **en la parte superior**, con la ubicación del banner
  y el código de anuncios intactos.
- \`card_codigos\` conserva el título, el aviso de disponibilidad sin conexión,
  **la fecha jurídica verificada ya publicada** (\`content_verified_date\`) y
  el botón de consultar el Código. La fecha del prototipo **no** sustituye la
  fecha documental real del recurso.
- \`card_continue_reading\` muestra el último artículo válido consultado, su
  sumilla, posición \`n/N\` dentro del bloque obtenido de \`ArticleRepository\`,
  barra de progreso y botón Continuar. Conserva el toque en la tarjeta.
- \`cardArticleOfTheDay\` muestra «ARTÍCULO DEL DÍA», fecha local, número,
  sumilla, extracto no superior a 180 caracteres de cuerpo y acciones
  Leer artículo y estrella. La estrella usa \`FavoritesManager\` y se actualiza
  tras reanudaciones. No presenta la selección como consejo jurídico.
- \`ArticleOfTheDaySelector\`: se ordenan los elegibles como
  \`ArticleRepository.getBlocks().values()\` (orden de assets/articles.json),
  excluyendo títulos vacíos y títulos \`[Derogado]\`, con
  \`floorMod(localDateISO.hashCode(), elegibles.size())\`.
  El reloj \`Clock\` se inyecta en tests; no hay red, notificaciones,
  preferencias nuevas, fecha de servidor ni caché de selecciones.
- Cuadrícula 2×2 con Consultar el Código, Buscar artículo, Cuestionarios
  y Mis notas: reutiliza las actividades y destinos reales.
- «Vistos recientemente»: chips horizontales que abren artículos;
  se conservan los favoritos recientes y su acceso preexistente.
- \`layout/fragment_home.xml\` apila contenido; \`layout-w840dp/fragment_home.xml\`
  mantiene \`home_left_pane\` y \`home_right_pane\` como columnas hermanas.

## Configuración (ConfiguracionFragment)

- Tres agrupaciones: tarjeta Apariencia de la app (Sistema/Claro/Oscuro),
  tarjeta Lectura (filas de tema, fuente, tamaño que abren
  \`AppearanceBottomSheet\`; justificar, glosario, pantalla encendida,
  modo enfoque y deslizamiento; restablecer solo valores de lectura),
  y los accesos Soporte, Acerca de y Centro de privacidad.
- Se conservan \`appThemeToggle\`, \`switch_modo_lectura\`,
  \`switchSwipeNavigation\` y los IDs de tarjetas legacy.
- Los interruptores usan SharedPreferences a través de
  \`ReadingPreferenceManager\`, con \`saveEnabled=false\` para no sobrescribir
  preferencias tras restaurar vistas. El reinicio de lectura **no** borra
  favoritos, notas, historial ni tema global.
- La barra inferior sigue etiquetada «Configuración».

## Restricciones

No se alteran \`articles.json\`, \`questions.json\`, \`LEGAL_SOURCES.md\`,
el contenido de strings legales, versión 1.08 (code 13), aplicación,
anuncios, privacidad, permisos ni dependencias.

## Pruebas

- \`ArticleOfTheDaySelectorTest\`: fecha local, medianoche, zonas horarias,
  selección estable, artículo derogado/sin título, corpus vacío,
  extracto en límite de palabra.
- \`ConfigurationPremiumReadingTest\`: opciones visibles, persistencia entre
  recreaciones, restablecimiento sin tocar el tema global.
- \`AdaptiveLayoutsTest\`: tarjetas en móvil y dos paneles en 960dp.
- \`HomeDashboardNavigationTest\`: rutas antiguas y nueva cuadrícula.
- Mantener en verde \`HomeFragmentReadingHistoryTest\` y
  \`HomeDashboardStateBuilderTest\`.

### Verificaciones manuales pendientes

1. Verificar banner sin cambios y fecha jurídica exacta en Inicio.
2. Comprueba abrir Continuar, Artículo del día, favoritos y los 4 accesos.
3. Prueba cambio de fecha local con fecha/zona simuladas y reinicio de app.
4. Compara teléfono, tablet ancha, orientación y fontScale 200 %.
5. Abre Apariencia de lectura desde las 3 filas, cambia opciones,
   vuelve a Configuración, comprueba estados y «Restablecer».
6. TalkBack, orden de foco e iconos con etiquetas accesibles.

**PR separado, sin fusionar ni publicar.** La revisión de accesibilidad
instrumentada y las notas finales de lanzamiento pertenecen a Misión 5.
