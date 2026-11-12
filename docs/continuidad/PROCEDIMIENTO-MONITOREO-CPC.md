# Procedimiento de continuidad jurídica — Código Procesal Civil PE

**Creado:** 12/11/2026. **Estado:** procedimiento propuesto; no equivale a certificación de vigencia ni a publicación de actualización.

## Alcance y situación heredada

El expediente de auditoría documental de Fase 1 se cerró **con salvedades**: se efectuó una primera pasada de **862/862 artículos**, pero **0/862** están declarados individualmente como vigencia integral certificada a noviembre de 2026. La edición oficial de referencia del TUO del CPC de Editora Perú tiene fecha de corte **07/06/2025**. Las correcciones puntuales previamente aprobadas están fusionadas; esto **no** demuestra ausencia de normas posteriores.

**No reiniciar 862 cotejos de rutina.** Continuar por fuentes primarias, riesgos materiales y cambios efectivos. La fuente LP Derecho puede ayudar a descubrir diferencias, pero no sustituye la publicación oficial.

Documentos de control:
- [Acta de cierre documental de Fase 1](../audits/2026-11-10-ACTA-CIERRE-AUDITORIA-DOCUMENTAL-FASE-1.md).
- [Control legislativo reproducible / etapa 42](../audits/2026-11-10-etapa-42-control-legislativo-reproducible.md).
- [Registro vivo de alertas pendientes](cpc-registro-alertas.csv).

## Responsables y periodicidad

- **Responsable operativo (desarrollador):** revisar el registro y recuperar publicaciones oficiales **cada mes**, idealmente durante la primera semana, así como ante noticias de reformas que mencionen el CPC.
- **Responsable de evaluación jurídica:** persona competente que contrasta redacción, competencia, derogaciones, efectos temporales y fuentes primarias. No se debe presentar como certificación profesional una comprobación automatizada.
- **Responsable de publicación:** propietario de la app, con **aprobación por PR** de cambios textuales y autorización separada para distribuir Android o cambiar la ficha de Play.
- Si no se realiza la comprobación del periodo, dejar la revisión **PENDIENTE**; no inventar el resultado ni actualizar la fecha del último cotejo.
- No hay consulta remota ni sincronización en segundo plano dentro de la app: el usuario recibe cambios de texto jurídico solo cuando instala una actualización aprobada.

## Secuencia mensual de 8 pasos

1. **Abrir fuentes primarias:** [Diario Oficial El Peruano: normas actualizadas](https://diariooficial.elperuano.pe/normas/normasactualizadas), [TUO CPC / texto consolidado](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), [SPIJ](https://spij.minjus.gob.pe/), y textos de leyes publicados con número y fecha. Registrar la fecha real de consulta.
2. **Cubrir la ventana desde el último corte demostrado:** buscar todas las normas aprobadas/publicadas desde **07/06/2025** hasta el nuevo corte acreditado, incluidas disposiciones finales, complementarias, transitorias, modificatorias, derogatorias, fe de erratas y posibles normas de eficacia diferida. Un motor de búsqueda o una muestra negativa no prueba exhaustividad; no declarar «sin reformas» sin una cobertura demostrada.
3. **Distinguir norma publicada vs. proyecto o dictamen:** los proyectos sobre alimentos (JUR-002), entre otros, no se convierten en normas sin promulgación y publicación oficial verificadas.
4. **Extraer por cada candidato:** número de norma, publicación, entrada en vigor, artículo(s) del CPC, texto anterior y texto posterior, fuente primaria vinculable, efectos temporales y la concordancia con `app/src/main/res/values/strings.xml` y `app/src/main/assets/articles.json`.
5. **Priorizar el registro:** localizar la eventual adecuación del TUO ordenada por Ley 32377, resolver el conflicto material sobre el artículo 551 («impugnable»/«inimpugnable») y triar JUR-016, JUR-031, JUR-037; conservar salvedades no resueltas sin edición automática.
6. **Preparar una decisión explícita:** `PENDIENTE` → `EVIDENCIA_OBTENIDA` → `REVISADO` → `DECISION_APROBADA`. Registrar la evidencia y el aprobador; el hecho de encontrar una noticia no permite saltar a «aprobado».
7. **Aplicar cambios por PR jurídico aislado:** edición mínima de textos afectos y, si corresponde, actualización de preguntas relacionadas, notas históricas, índices y tests. Preservar IDs de preguntas anteriores o documentar migración de errores locales. Ejecutar integridad del corpus y CI; nunca publicar automáticamente por una mera alerta.
8. **Cerrar evidencia de publicación:** comprobar en dispositivo, versión y paquete; registrar autorización del dueño para prueba interna y de forma separada para producción; solo entonces cambiar descripción, avisos y fecha de actualización visible cuando sea verdadera.

## Estado del registro y criterio de cierre

El fichero [`cpc-registro-alertas.csv`](cpc-registro-alertas.csv) tiene una fila por asunto y campos explícitos de fecha de publicación, consulta, vigencia efectiva, responsable, decisión y PR. **Los campos vacíos no significan «sin cambios»: significan no verificado.** Las siete alertas iniciales proceden de expedientes anteriores, y su inclusión **no afirma** que se hayan vuelto a comprobar al 12/11/2026.

Para una norma nueva, abrir una nueva fila; no sobrescribir la evidencia pasada. Una fila `IMPLEMENTADO` requiere fuente primaria verificable, decisión aprobada, PR fusionado y pruebas. Si hay conflicto entre normas, mantener `PENDIENTE` y consultar a un especialista antes de editar el texto jurídico.

## Control de metadatos y versión de Play

Antes de **cada** candidata:
- Verificar que la ficha y el «Qué hay de nuevo» describan solo funciones **efectivamente incluidas en la versión distribuida**.
- No prometer «actualización automática», «vigente a 2026» ni «certificación» sin pruebas correspondientes.
- Revisar que la web informativa, la política de privacidad pública y la descripción de Play sean consistentes con el almacenamiento local, anuncios y condiciones de acceso.
- Verificar los límites vigentes del título, descripción breve y larga y comprobar capturas reales con los estados de la candidata.
- Mantener las métricas agregadas de Play Console separadas de las métricas voluntarias privadas de cada dispositivo; **nunca inferir usuarios o retención real a partir del progreso local de una persona**.
- Una publicación en producción requiere PR revisado/fusionado, pruebas manuales en teléfono y variantes de pantalla, AAB firmado en entorno controlado, instalación desde pista interna y **consentimiento expreso de publicación**.

## Fuentes de la metodología

- [El Peruano — normas legales actualizadas](https://diariooficial.elperuano.pe/normas/normasactualizadas).
- [TUO del Código Procesal Civil publicado por El Peruano (07/06/2025)](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007).
- [Ley 32377 (07/06/2025)](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7).
- [Política de metadatos de Google Play](https://support.google.com/googleplay/android-developer/answer/9898842?hl=es).

El presente procedimiento **no genera avisos automáticos, no consulta fuentes en ejecución y no cambia normas en producción**.
