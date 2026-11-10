# Regresión de modo oscuro tras borrar datos locales

## Defecto reportado en Xiaomi 2303ERA42L — Android 15

Al pulsar **Borrar todos los datos locales** se restablecieron modo lectura y escala de letra, pero el modo oscuro seguía activo, incluso tras salir y volver a entrar. El APK de la rama PR #53 estaba instalado mediante `gradlew :app:installDebug` y Gradle informó **BUILD SUCCESSFUL**. La compilación no valida por sí sola la corrección visual.

## Segundo ajuste

`PrivacyCenterActivity` ya no aplica `AppCompatDelegate.setDefaultNightMode` mientras el diálogo de confirmación está ejecutando el callback de su botón. Encola el cambio para el siguiente ciclo de la UI, después de que Android haya procesado el cierre del diálogo. El restablecimiento de preferencias ocurre en el mismo instante del botón.

Se agregó `PrivacyCenterThemeResetTest` (Robolectric) para comprobar el borrado de preferencias y el valor de `AppCompatDelegate` tras confirmar. Este test **no sustituye** la inspección visual en el dispositivo y no borra datos de un teléfono real.

## Validación pendiente en Windows

Desde la rama `fix/privacidad-preferencias-en-sesion`:

```powershell
git status
git fetch origin
git pull --ff-only origin fix/privacidad-preferencias-en-sesion
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:installDebug
```

Repetir manualmente, solo con datos descartables:
1. Activar modo oscuro y comprobar pantalla oscura.
2. Abrir Configuración → Privacidad y datos → Borrar todos los datos locales.
3. Aceptar confirmación, comprobar que el tema vuelve a claro sin reiniciar.
4. Regresar a Configuración: interruptor desactivado, modo lectura predeterminado.
5. Cerrar y reabrir: conserva modo claro.

Si falla, distinguir **interruptor activado** (persistencia) de **interruptor desactivado con pantalla oscura** (configuración/recursos) antes de otra modificación. Conservar sin fusionar el PR hasta validar en el teléfono.
