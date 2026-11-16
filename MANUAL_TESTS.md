# Pruebas manuales rápidas

## Checklist mínimo de UI/UX (obligatorio)
- [ ] **Consistencia de estilos/tokens:** verificar que colores, tipografías, espacios y tamaños usen estilos/tokens definidos y no valores hardcodeados.
- [ ] **Contraste y legibilidad:** validar contraste suficiente entre texto/fondo, tamaños legibles y jerarquía tipográfica clara.
- [ ] **Estados vacíos/carga/error:** revisar que cada flujo relevante tenga estados vacíos, de carga y de error correctamente representados.
- [ ] **Feedback táctil y jerarquía visual:** confirmar estados de interacción (pressed, focused, disabled), áreas táctiles adecuadas y prioridad visual clara.

## Requisito de revisión antes de merge
- [ ] **Revisión por pantalla completa:** ningún PR de UI/UX se mergea sin revisión por pantalla de las vistas impactadas (capturas o video corto por pantalla modificada).

## SplashScreen
1. Abrir la app desde un estado limpio.
2. Antes de que transcurran los 3 segundos, presionar el botón Atrás para cerrar el splash.
3. Confirmar que no se inicia `MainActivity` después de regresar a la pantalla anterior.

## Banco de preguntas
1. Abrir cualquier cuestionario y verificar que las preguntas y opciones se muestran correctamente (contenido cargado desde `res/raw/questions.json`).
2. Probar el mismo cuestionario con diferentes cantidades solicitadas (por ejemplo, 5 y 10) y confirmar que nunca se muestran más preguntas de las pedidas.

## Navegación del código (post-migración)
1. Desde Códigos → Código Procesal Civil, entrar a **cada una de las 6 secciones** y verificar
   que se abre la lista genérica con títulos/capítulos y sus rangos de artículos.
2. Bajar hasta una hoja (ej. Sección Quinta → Título II → Capítulo II → Subcapítulo 3) y
   verificar que el contenido se muestra con el encabezado correcto.
3. En la pantalla de contenido: usar el FAB de búsqueda interna (contador de coincidencias,
   siguiente/anterior, limpiar, cerrar).
4. Seleccionar texto → Resaltar / Agregar nota; verificar que persiste al salir y volver.
5. Desde Búsqueda global, abrir un resultado: debe abrir el contenido con el término ya
   resaltado y la barra de búsqueda visible.
6. Favoritos guardados con versiones anteriores deben seguir navegando correctamente.

## Contenido por artículo (articles.json)
Mantenimiento: tras editar cualquier string de contenido en `strings.xml`, ejecutar
`powershell -ExecutionPolicy Bypass -File tools\generate_articles.ps1` para regenerar
`app/src/main/assets/articles.json` (el test `ArticleRepositoryTest` falla si quedó desfasado).

1. Abrir varios bloques de contenido y verificar que el texto se ve idéntico a antes
   (encabezados coloreados, resaltados del usuario intactos).
2. En Búsqueda global escribir `647`: debe aparecer arriba la fila **Ir al Artículo 647 —
   Secuestro de vehículo**; al tocarla se abre el subcapítulo desplazado directamente al
   artículo (sin barra de búsqueda interna).
3. Probar variantes: `art. 647`, `artículo 647-a`, `647 A` → aparece la fila de salto;
   `9999` → no aparece.
4. Con un chip de sección activo que no contiene ese artículo, la fila de salto no aparece.
5. Una búsqueda de texto normal (ej. "embargo") sigue funcionando y abre el resultado con
   el término resaltado, como antes.

## Consentimiento de anuncios (UMP)
Requisito previo (una sola vez): en la consola de AdMob crear el mensaje de GDPR en
*Privacidad y mensajería → Mensaje de GDPR* para esta app; sin eso el formulario no carga.

1. **Build debug en emulador** (simula EEA automáticamente): en un arranque limpio debe
   aparecer el formulario de consentimiento de Google sobre el splash; la app no navega a
   `MainActivity` hasta cerrarlo.
2. Aceptar el consentimiento → los banners cargan en las pantallas de secciones.
3. Ir a *Configuración*: debe aparecer la tarjeta **Opciones de privacidad**; al tocarla se
   reabre el formulario y se puede cambiar la decisión.
4. Rechazar/limitar el consentimiento → verificar que la app sigue funcionando (con anuncios
   limitados o sin anuncios) y no crashea en pantallas con banner.
5. **Sin red** en el primer arranque: el splash no debe quedarse colgado; la app continúa sin
   anuncios y los carga en sesiones futuras.
6. En un dispositivo con región real fuera de EEA (release), el formulario no aparece y los
   banners cargan directo; la tarjeta de privacidad en Configuración queda oculta.


---

## Fase 6 — Lector premium 1.08 (pendiente de ejecución en Android real)

Estas pruebas complementan Gradle, los validadores Python y la regresión en
emulador. **Las casillas vacías significan que la prueba manual NO se ha
realizado**; conservar capturas/video de las pantallas modificadas antes de
fusionar. Mantener instalada la 1.07 para la prueba de actualización;
NO borrar datos de usuario para simular migración.

- [ ] **MAN-13 — Temas de lectura.** Abrir un artículo con referencias,
  historial normativo y resaltados. Desde «Aa» recorrer *Como la app / Claro /
  Sepia / Oscuro*. Verificar contraste legible (texto y resaltados),
  barra de estado/navegación correcta, fondo uniforme en hojas y retorno al
  mismo artículo y fracción del desplazamiento.
- [ ] **MAN-14 — Fuente, tamaño y formato.** Probar Sans / Literata /
  Atkinson Hyperlegible, tamaños 85–160 %, interlineado 1.5/1.7/1.9,
  márgenes 16/24/40 y justificar. Verificar que títulos y cuerpo cambian,
  mientras botones y menús conservan fuente del sistema, sin desalinear
  selección, referencias ni notas.
- [ ] **MAN-15 — Enfoque y accesibilidad.** Activar modo enfoque al abrir,
  ocultar barras, mostrar controles con toque central, usar acción de
  accesibilidad «Mostrar controles» y salir con Atrás o la píldora. Validar
  que TalkBack anuncia una vía para recuperar controles.
- [ ] **MAN-16 — Índice y progreso.** Comprobar «Artículo n de N» con
  TalkBack; filtrar índice por Todos / Favoritos / Con notas / Resaltados
  y búsqueda local; entrar en artículo, ir al primero y último, probar
  «Otro capítulo» e «Ir al artículo…». No debe perderse el desplazamiento.
- [ ] **MAN-17 — Gestos.** Con deslizamiento desactivado verificar scroll
  normal; activarlo desde Configuración, probar desplazamiento horizontal
  rápido y lento, desde bordes a menos de 24dp, selección y scroll vertical.
  Usar botones Anterior/Siguiente con TalkBack como alternativa accesible.
- [ ] **MAN-18 — Glosario vacío o corrupto.** Verificar que con
  glossary.json de producción (terms: []) no aparecen spans ni nuevas hojas
  y que el texto jurídico se lee igual. Con glosario malformado en *una
  compilación de prueba*, comprobar que no se cierra la app.
- [ ] **MAN-19 — Glosario editorial con fixtures exclusivamente de test.**
  En una compilación local de pruebas, agregar un término sintético y
  verificar primer match por artículo, ausencia de solapamientos con
  referencias/resaltados, selección, hoja editorial, fuente, revisor,
  fecha, búsqueda, chips relacionados y copiar. El recurso de producción
  debe seguir vacío. Probar «Términos del glosario» apagado/encendido.
- [ ] **MAN-20 — Inicio y Artículo del día.** Verificar banner existente,
  fecha jurídica real (sin cambiarla), Continuar leyendo con progreso
  n/N, artículo y extracto correctos, estrella de favoritos y chips de
  vistos recientemente. Cambiar fecha/zona horaria del dispositivo y
  comprobar que la selección solo cambia cuando cambia la fecha local.
  Verificar los accesos Código, Buscar, Cuestionarios y Mis notas.
- [ ] **MAN-21 — Configuración.** Probar Sistema/Claro/Oscuro con
  recreación de Activity; tocar Tema, Fuente y Tamaño y comprobar que abren
  «Apariencia de lectura». Alternar justificar, glosario, pantalla encendida,
  modo enfoque, swipe. Restablecer lectura y comprobar que no se alteran
  el tema global, favoritos, notas ni historial. Verificar Soporte,
  Acerca de y Centro de privacidad.
- [ ] **MAN-22 — Migración 1.07 → 1.08.** Anotar favoritos, notas,
  resaltados, historial, ajustes de lectura y tema en 1.07, instalar la
  1.08 candidata sobre esa instalación sin borrar almacenamiento y
  verificar integridad, navegación y migración de tema día/noche.
  Revisar que el contenido jurídico no se ha cambiado.
- [ ] **MAN-23 — TalkBack y teclado.** En lector, hojas de apariencia,
  índice y glosario, Inicio y Configuración: comprobar orden de foco,
  botones de ≥48dp, acciones con nombre y estado (favorito, nota,
  artículo derogado, capítulo), encabezados, anuncio del progreso y
  que ninguna información esencial depende solo del color.
- [ ] **MAN-24 — Tamaño de fuente de Android al 200 %.** En dispositivo
  estrecho y tablet, en vertical y horizontal, comprobar acciones críticas
  completas. Desplazar horizontalmente los controles del lector y grupos
  de opciones si no caben; en Inicio deben apilarse los cuatro accesos
  cuando corresponda. Ninguna acción debe desaparecer o quedar cortada.
- [ ] **MAN-25 — Rendimiento del bloque largo.** Abrir
  seccionquintatit4cap2subcap1 (18 430 caracteres en el corpus usado
  para esta misión). Cambiar interlineado, márgenes, fuente, tamaño, tema
  y glosario; con Android Studio Profiler/Perfetto registrar latencia de
  respuesta, frames lentos y ANR. Repetir con resaltados/notas y TTS.
  No afirmar fluidez si hay tirones; adjuntar mediciones reales del equipo.
- [ ] **MAN-26 — Comprobación release final.** Ejecutar app instalada desde
  AAB candidata firmado en pista interna (solo con autorización del dueño);
  comprobar R8/Gson, consultas offline, anuncios tal como están,
  selección/resaltados, notas, privacidad, navegación y accesibilidad.
  No realizar publicación a producción durante la validación.

### Evidencias obligatorias

Anotar dispositivo/API/orientación/fontScale, fecha y resultado por caso,
adjuntar capturas o video de Inicio, Configuración, lector y hojas, más
registros de rendimiento cuando se ejecute MAN-25. Marcar únicamente las
pruebas realmente ejecutadas; la CI no equivale a validación humana.
