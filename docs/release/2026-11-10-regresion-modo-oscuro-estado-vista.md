# Regresión: modo oscuro vuelve tras borrar datos

## Evidencia de prueba — Xiaomi 2303ERA42L (Android 15)

Versión debug del PR #53 hasta commit `c5fc3db`: después de **Borrar todos los datos locales**, la interfaz pasó brevemente a modo claro, pero regresó a oscuro; el interruptor quedó activado y tras reiniciar siguió en modo oscuro. Es una **falla real** de la Misión 4, no un problema de instalar la APK antigua.

## Causa probable identificada

El interruptor `switch_modo_oscuro` registraba `OnCheckedChangeListener` antes de que Android restaurara el estado guardado de la jerarquía de vistas tras recrear la Activity por un cambio DayNight. Ese estado podía reactivar `true` y volver a escribir `dark_mode_enabled=true`, deshaciendo el borrado. El destello claro seguido de modo oscuro es compatible con ese flujo. El bug exacto aún requiere verificación en dispositivo.

## Corrección en revisión

- `fragment_configuracion.xml`: `android:saveEnabled="false"` para ambos interruptores de preferencias persistentes (modo oscuro y modo lectura). Android deja de restaurar automáticamente un estado de vista obsoleto. Los valores deben proceder de `ThemePreferenceManager` y `ConfiguracionViewModel` al construir/reanudar la pantalla.
- `ConfigurationSwitchSavedStateTest`: comprueba con Robolectric que, tras guardar y restaurar la jerarquía, los interruptores nuevos **no recuperan** los valores antiguos.
- Se conserva la actualización diferida del tema tras cerrar el diálogo y el resto del flujo de borrado sin cambios.

## Validación pendiente

En la rama PR #53: `git fetch origin`, `git pull --ff-only origin fix/privacidad-preferencias-en-sesion` y `.\\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:installDebug`. Comprobar que la app no retorna a oscuro, que el interruptor permanece desactivado y que el modo claro persiste al reiniciar. Verificar también activar y desactivar modo oscuro manualmente, para confirmar que se sigue guardando la elección del usuario. **No fusionar hasta validar.**
