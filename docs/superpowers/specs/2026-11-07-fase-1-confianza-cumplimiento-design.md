# Fase 1: confianza, cumplimiento y vigencia — Diseño

## Intención

Cerrar la brecha entre lo que la aplicación realmente hace y lo que comunican Google Play, la política pública y sus activos de marca. La fase debe reducir riesgo jurídico y publicitario sin publicar ninguna corrección normativa que el propietario no haya revisado personalmente.

## Objetivos

- Revalidar el contenido jurídico contra fuentes oficiales y entregar un informe de diferencias para revisión humana.
- Actualizar y publicar la política de privacidad pública en español.
- Auditar la declaración de Seguridad de los datos de Google Play contra el código y los SDK instalados.
- Reemplazar el ícono fechado “ACTUALIZADA 2025” por una marca atemporal y legible.
- Mantener anuncios personalizados sujetos a Google UMP, pero limitar los intersticiales a pausas naturales.
- Eliminar cualquier afirmación de actualización normativa automática.
- Preparar una única versión Android posterior a la aprobación del informe jurídico.

## No objetivos

- No modificar el texto jurídico como parte de la auditoría inicial.
- No publicar correcciones normativas sin aprobación expresa del propietario.
- No rediseñar todavía las capturas, descripción comercial o demás activos de la fase 2.
- No eliminar AdMob, los anuncios personalizados ni los banners.
- No implementar actualizaciones jurídicas remotas.
- No reescribir la aplicación en Kotlin o Compose.

## Restricciones

- Público objetivo: personas en Perú; política y comunicación únicamente en español.
- Fuentes jurídicas admitidas: Congreso de la República, Diario Oficial El Peruano, SPIJ/Ministerio de Justicia y Poder Judicial.
- La aplicación debe seguir declarándose independiente y no gubernamental.
- Las notas, búsquedas, favoritos, historial y preferencias continúan locales; las notas continúan cifradas mediante Android Keystore.
- Google AdMob puede procesar datos para publicidad personalizada, medición, analítica y prevención de fraude cuando UMP lo permita.
- Los cambios públicos en GitHub Pages o Play Console se preparan primero y se publican solo después de la confirmación requerida en el momento de la acción.
- La implementación móvil continúa en Java 17, XML, View Binding y la arquitectura existente.

## Enfoques considerados

### 1. Actualización coordinada en dos pasos — seleccionado

Publicar primero la política corregida y auditar Seguridad de los datos. En paralelo preparar el informe jurídico, el ícono y la colocación publicitaria. Tras la revisión humana del informe, generar una única versión Android con las correcciones normativas aprobadas y el resto de cambios.

Ventajas: corrige pronto la documentación pública, evita publicar texto jurídico no revisado y reduce el trabajo de pruebas y lanzamiento a un solo AAB.

### 2. Versión no jurídica inmediata

Publicar primero ícono, anuncios y privacidad, y dejar las correcciones jurídicas para otra versión. Mejora antes la imagen pública, pero duplica releases, pruebas y notas de versión.

### 3. Esperar y publicar todo junto

Retener incluso la política pública hasta terminar la auditoría jurídica. Mantiene sincronización absoluta, pero prolonga una política desactualizada y un ícono incorrecto.

## Diseño de la auditoría jurídica

La auditoría comparará el corpus local con las fuentes oficiales disponibles al 7 de noviembre de 2026. No se aceptarán blogs, agregadores jurídicos ni resultados de buscadores como autoridad normativa.

La jerarquía de evidencia será: (1) norma modificatoria publicada en el Diario Oficial El Peruano; (2) texto consolidado oficial de SPIJ; (3) Congreso y Poder Judicial como fuentes de contraste y trazabilidad. Ante una discrepancia entre una consolidación y la norma publicada, el informe conservará ambas evidencias y no propondrá una corrección automática.

El entregable será `docs/audits/2026-11-07-auditoria-contenido-juridico.md` y contendrá:

- alcance, fecha y fuentes consultadas;
- versión y hash SHA-256 del corpus auditado;
- inventario de normas modificatorias posteriores a la validación del 8 de agosto de 2026;
- una entrada por diferencia con artículo, texto actual, texto oficial, norma, enlace oficial, impacto y corrección propuesta;
- una sección explícita “sin diferencias encontradas” si corresponde;
- limitaciones de la revisión y checklist para aprobación humana.

La fecha visible `content_verified_date` y `LEGAL_SOURCES.md` solo se actualizarán después de que el propietario apruebe el informe y las correcciones propuestas.

Cada hallazgo tendrá una casilla independiente de aprobación, rechazo u observación. La aprobación de una diferencia no implicará aprobar las demás; el release solo incorporará los hallazgos aprobados expresamente.

## Diseño de la política de privacidad

La política pública sustituirá la versión del 7 de marzo de 2025 y tendrá fecha de actualización de noviembre de 2026. Debe cubrir, en lenguaje claro:

- identidad de la app, **Jobali Studios** como responsable y `studiosjobali@gmail.com` como canal de contacto;
- ausencia de cuenta y de servidor propio del desarrollador;
- datos locales: búsquedas, favoritos, historial, preferencias, quiz, resaltados y notas;
- cifrado local de notas mediante Android Keystore;
- finalidades y retención local;
- eliminación por categoría y eliminación total desde el Centro de privacidad;
- Google Mobile Ads y UMP;
- categorías que Google puede procesar: dirección IP/ubicación aproximada, actividad e interacción con anuncios, diagnósticos e identificadores de dispositivo o publicidad;
- publicidad personalizada cuando el consentimiento y la región lo permitan;
- transmisión cifrada y tratamiento sujeto a políticas de Google;
- opciones de consentimiento publicitario;
- copias de seguridad: exclusión de notas/resaltados y búsquedas recientes;
- público general y aclaración de que no está dirigida específicamente a niños;
- fuentes jurídicas oficiales, independencia gubernamental y ausencia de asesoría legal;
- derechos, contacto y mecanismo de actualización de la política.

La página principal de soporte mantendrá un enlace visible a esta política y el correo `studiosjobali@gmail.com`.

La línea base para la política y Seguridad de los datos será la versión 1.06 publicada en Google Play. Si `main` contiene funciones posteriores aún no publicadas, solo se incluirán anticipadamente cuando no cambien categorías de datos ni finalidades; cualquier categoría nueva se documentará junto con el release que la introduce.

## Auditoría de Seguridad de los datos

Se construirá una matriz entre permisos, SDK, comportamiento real, política pública y respuestas actuales del formulario. Como mínimo se revisarán:

- `INTERNET`, `ACCESS_NETWORK_STATE` y `AD_ID`;
- Google Mobile Ads SDK y Google UMP;
- ubicación aproximada inferida por IP;
- actividad de la app, interacciones publicitarias, diagnósticos e identificadores;
- tratamiento por el desarrollador frente a tratamiento por Google;
- cifrado en tránsito;
- posibilidades de eliminación local y solicitudes relacionadas con datos procesados por terceros.

Si la consola requiere cambios, se prepararán las respuestas exactas y se solicitará confirmación inmediatamente antes de enviarlas. No se declarará “no se recopilan datos” si el SDK publicitario exige declarar recopilación o uso compartido.

También se leerán, sin modificarlas inicialmente, las selecciones actuales de público objetivo, edades y tratamiento infantil en Play Console y AdMob. Cualquier inconsistencia se presentará al propietario antes de cambiar la configuración publicitaria.

## Diseño del ícono

Dirección seleccionada: **A — Autoridad sobria**.

- Fondo borgoña profundo con variación tonal discreta.
- Balanza jurídica blanca/marfil con acento dorado.
- Sin palabras, siglas, año, bandera, escudo ni símbolo gubernamental.
- Silueta reconocible a 48 px.
- Área segura compatible con máscara adaptativa.
- Activo de Google Play de 512×512 y recursos Android adaptativos consistentes.

Debido a que es una marca geométrica simple que debe mantenerse nítida y escalable, la versión final se producirá de forma determinista como vector y PNG, no mediante una ilustración raster generativa.

## Diseño de la publicidad intersticial

Se mantienen anuncios personalizados, UMP, banners y la política actual de frecuencia:

- ocho navegaciones antes del primer intersticial;
- máximo dos intersticiales por sesión;
- mínimo diez minutos entre intersticiales.

Los eventos de navegación pueden seguir acumulando frecuencia, pero el anuncio solo se muestra en una pausa natural segura:

- después de que el usuario haya visto el resultado de un quiz y pulse **Volver al inicio**;
- al abandonar explícitamente una sesión de lectura mediante la acción interna **Subir/Volver al índice**, siempre que el destino sea un índice dentro de la aplicación.

No se muestra un intersticial:

- antes de abrir un artículo solicitado;
- entre artículo anterior y siguiente;
- antes de abrir un resultado de búsqueda o una referencia relacionada;
- durante la lectura o reproducción por voz;
- al iniciar la aplicación;
- al pulsar Atrás para salir de la aplicación.
- al pulsar Reintentar quiz o abrir un artículo relacionado desde resultados;
- al usar Atrás del sistema, aunque el destino inmediato sea un índice interno.

La navegación debe continuar inmediatamente si no hay consentimiento, anuncio cargado o conexión.

## Eliminación de promesas automáticas

La ficha, política, sitio, strings y documentación se revisarán para evitar “actualización automática”, “siempre actualizado” o equivalentes. La redacción autorizada es:

> Contenido jurídico disponible sin conexión y actualizado periódicamente mediante nuevas versiones de la aplicación.

También debe indicarse la fecha concreta de la última verificación jurídica.

## Secuencia de entrega

1. Localizar la fuente de GitHub Pages y preparar la política en español.
2. Auditar Seguridad de los datos y preparar cualquier corrección de consola.
3. Solicitar confirmación y publicar política/cambios de consola.
4. Ejecutar la auditoría jurídica y entregar el informe sin tocar el corpus.
5. Crear y validar los nuevos activos de ícono.
6. Implementar y probar los puntos seguros para intersticiales.
7. Recibir la revisión humana del informe jurídico.
8. Aplicar únicamente correcciones normativas aprobadas y actualizar fechas/fuentes.
9. Ejecutar pruebas unitarias, Lint, pruebas instrumentadas disponibles y checklist de prepublicación.
10. Preparar el AAB, notas de versión y cambios de Play Store; solicitar confirmación antes de subirlo al canal de pruebas internas.
11. Instalar y validar la entrega procesada por Google Play y, tras la aprobación del propietario, promover el mismo artefacto a producción mediante despliegue gradual.

## Criterios de aceptación

- La política pública visible corresponde al comportamiento de la versión actual.
- Seguridad de los datos coincide con los permisos, SDK y comportamiento real.
- Existe un informe jurídico trazable y no se modificó contenido sin aprobación.
- Ningún activo o texto público promete actualizaciones automáticas.
- El ícono no contiene texto ni fecha y es legible a 48 px.
- Ningún intersticial precede la apertura de contenido solicitado.
- El quiz ofrece un punto de intersticial solo después de mostrar resultados.
- Las pruebas automáticas y la compilación pasan antes del lanzamiento.
- La publicación Android espera la aprobación humana del informe jurídico.

## Decisiones dejadas al constructor

- Organización interna del HTML/CSS del sitio estático, conservando la URL pública.
- Herramienta determinista usada para rasterizar el vector del ícono.
- Clases auxiliares exactas para separar el registro de navegación del momento seguro de mostrar anuncios.
- Redacción menor que no cambie categorías de datos, finalidades ni derechos.

