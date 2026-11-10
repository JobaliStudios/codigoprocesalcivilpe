# Auditoría de Seguridad de los datos — borrador en curso

Consulta: 8 de noviembre de 2026. Aplicación `com.jobalistudios.codigoprocesalcivilpe`, versión 1.06, código 11.

## Evidencia técnica

- Permisos declarados: INTERNET, ACCESS_NETWORK_STATE y AD_ID.
- Dependencias declaradas: `play-services-ads:25.4.0` y `user-messaging-platform:4.0.0`.
- Jobali Studios conserva datos funcionales en el dispositivo; Google procesa datos del SDK publicitario. No se identificó un backend propio ni Firebase Analytics.
- Fuente oficial consultada: https://developers.google.com/admob/android/privacy/play-data-disclosure . La documentación disponible corresponde a 25.5.0; la app declara 25.4.0. Esta diferencia limita la atribución exacta por versión y debe contrastarse antes de cerrar respuestas.

## Categorías de Google Play y propuesta pendiente

La documentación de Google informa recopilación y uso compartido de dirección IP/ubicación aproximada, interacciones con la app y los anuncios, diagnósticos e identificadores. Finalidad: publicidad, medición/analítica publicitaria y prevención del fraude y seguridad. Cifrado en tránsito: TLS según Google.

Opcionalidad: el identificador publicitario tiene controles de Android; la autorización de solicitudes se gobierna por UMP. Esto no prueba que todo el procesamiento del SDK sea opcional. Tratamiento efímero: no acreditado; no marcarlo como tal sin evidencia adicional.

Respuesta recomendada preliminar: declarar que la app recopila o comparte datos mediante el SDK; contrastar las categorías detalladas y finalidades con el formulario real antes de enviar. No declarar ausencia total de recopilación.

## Respuesta actual de consola

Acceso restablecido por el propietario el 8 de noviembre. Se verificó la cuenta Jobali Studios y la app correcta. Contenido de la app muestra 11 declaraciones completadas; Seguridad de los datos fue modificada el 10 de agosto de 2026 y Política de privacidad el 7 de marzo de 2025. En el paso «Seguridad y recopilación de datos» están seleccionadas las respuestas «Sí» a recopilación/uso compartido y «Sí» a cifrado en tránsito. No se modificaron respuestas ni se guardaron formularios. Falta inspeccionar las categorías y finalidades detalladas.

Público objetivo y edades: pendientes de lectura. Tratamiento infantil en Play Console y AdMob: pendiente de lectura. Mensajes UMP y declaración de anuncios: pendientes de lectura. La redacción de público general de la política debe contrastarse con esas selecciones.

## Eliminación local: hallazgo PRI-001

`QuizErrorStore` guarda identificadores de preguntas pendientes en `quiz_error_review`. `LocalDataManager.clearAllLocalData()` borra búsquedas, favoritos, resaltados/notas, historial y `app_settings`, pero no borra ese almacenamiento del quiz. El control de eliminación total requiere una corrección en la siguiente versión con una prueba de regresión. La política preparada describe los controles efectivos sin afirmar que ese botón elimina el repaso del quiz.

## Estado

Verificación final de publicación: https://jobalistudios.github.io/privacy-policy/ muestra el documento aprobado con fecha 7 de noviembre de 2026, responsable Jobali Studios, contacto correcto y diez secciones en español. Se verificaron visualmente el contenido y enlaces presentes. El archivo cargado y el canónico local comparten SHA-256 `A5D1089D5B14E9036AC04CF2862C74EC4477A401B8AB1787F4FBF66DFA44967A`. Publicación de política completada; cambios del formulario de Seguridad de los datos siguen pendientes y no se enviaron.

### Publicación autorizada de política

El propietario autorizó expresamente publicar el documento canónico. El 8 de noviembre de 2026, 11:27 de Lima, se reemplazó `index.html` en el repositorio real `JobaliStudios/privacy-policy` (no en el repositorio raíz del sitio). Commit: https://github.com/JobaliStudios/privacy-policy/commit/47e9140f3add18308754a4ea2cf1a19d15cbcd93 . Despliegue: https://github.com/JobaliStudios/privacy-policy/actions/runs/42499990914 . Al registrar este punto el despliegue seguía en progreso; la URL aún mostraba agosto de 2026. La política anterior realmente encontrada en el repositorio tenía fecha 10 de agosto de 2026: la referencia previa a marzo de 2025 era la fecha del formulario de Play Console, no la última revisión del HTML. Pendiente verificación final pública.

### AdMob observado

Público objetivo en Play Console: seleccionados 13–15, 16–17 y mayores de 18 años; menores de 5, 6–8 y 9–12 desmarcados. Última modificación visible: 6 de marzo de 2025. La app incluye adolescentes en su público declarado; «público general» no equivale a «solo adultos». Mantener esta precisión al revisar publicidad y política.

Idiomas confirmados al desplegar el selector del mensaje europeo: español (es) e inglés (en). El editor se abrió solo para inspección; no se pulsó «Publicar cambios». La preferencia del propietario es español únicamente; pendiente preparar el ajuste exacto y comprobar el idioma de respaldo exigido por la plataforma.

La aplicación figura con estado de aprobación «Lista». En Privacidad y mensajería aparece un mensaje de reglamentos europeos activo. Su listado lo vincula a Código Procesal Civil PE, con idioma español (es) y uno más, última modificación 10 de agosto de 2026, estado «Publicado» y publicación activada. El panel muestra cero mensajes mostrados y 0 % de consentimiento; esto no demuestra por sí solo un fallo, pues depende del tráfico y región. Reglamentaciones estatales de EE. UU. ofrece «Crear», sin mensaje activo visible. No se modificó configuración.

### Evidencia detallada de consola — 8 de noviembre

- Dispositivo u otros IDs: recopilados y compartidos, no efímeros, recopilación declarada necesaria. Finalidades tanto de recopilación como de uso compartido: Estadísticas, Publicidad o marketing y Seguridad/cumplimiento/prevención de fraudes. Personalización y funciones de app desmarcadas. La declaración agrupa varios identificadores; el control Android del ID publicitario no basta para concluir que todos sean opcionales.

- Interacciones en la app: recopiladas y compartidas, no efímeras y declaradas opcionales. Estadísticas y Publicidad o marketing seleccionadas tanto para recopilación como para uso compartido; prevención de fraude desmarcada en ambas.

- Registros de fallas: recopilados, no compartidos, no efímeros y declarados opcionales. Única finalidad seleccionada: Estadísticas. Publicidad y prevención de fraude desmarcadas.
- Ubicación aproximada, finalidades de uso compartido: Estadísticas y Publicidad o marketing seleccionadas; prevención de fraude desmarcada.

- Actividad en apps: seleccionado «Interacciones en la app»; historial de búsqueda, apps instaladas, contenido generado y otras acciones desmarcados.
- Rendimiento: seleccionado «Registros de fallas»; «Diagnóstico» y «Otros datos de rendimiento de la app» desmarcados. Hallazgo PRI-002: contrastar esta selección con los datos de rendimiento documentados por Google; todavía no se recomienda quitar registros de fallas sin verificar su alcance.
- Ubicación aproximada: recopilada y compartida; no efímera; declarada opcional. Finalidades de recopilación seleccionadas: Estadísticas y Publicidad o marketing. Seguridad/cumplimiento/prevención de fraudes desmarcada. Hallazgo PRI-003: validar opcionalidad real y la finalidad de prevención de fraude antes de proponer respuestas definitivas.

Lectura adicional de consola: no permite crear cuentas; no acepta cuentas externas; método de solicitud de borrado declarado «No». Tipos seleccionados: ubicación aproximada (ubicación precisa desmarcada), Actividad en apps 1/5, Información de la app y rendimiento 1/3 y Dispositivo u otros IDs 1/1. Información personal, financiera, salud, mensajes, fotos, audio, archivos/documentos, calendario, contactos y navegación web muestran cero tipos seleccionados. Queda pendiente desplegar los subtipos y leer finalidades.

Auditoría abierta. Las respuestas anteriores son propuestas para revisión, no respuestas verificadas de consola ni cambios publicados. Pendientes: acceso al proyecto, lectura de AdMob, matriz CSV y cierre de evidencia por versión.
