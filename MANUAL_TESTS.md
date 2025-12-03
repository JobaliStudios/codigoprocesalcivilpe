# Pruebas manuales rápidas

## SplashScreen
1. Abrir la app desde un estado limpio.
2. Antes de que transcurran los 3 segundos, presionar el botón Atrás para cerrar el splash.
3. Confirmar que no se inicia `MainActivity` después de regresar a la pantalla anterior.

## Banco de preguntas
1. Abrir cualquier cuestionario y verificar que las preguntas y opciones se muestran correctamente (contenido cargado desde `res/raw/questions.json`).
2. Probar el mismo cuestionario con diferentes cantidades solicitadas (por ejemplo, 5 y 10) y confirmar que nunca se muestran más preguntas de las pedidas.
