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
