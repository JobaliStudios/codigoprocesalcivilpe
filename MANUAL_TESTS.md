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
