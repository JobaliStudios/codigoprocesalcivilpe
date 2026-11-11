# Procedencia LP Derecho y cotejos primarios — 10/11/2026

**Alcance:** anexo del PR #54, exclusivamente documental. No se modifica el texto jurídico de la aplicación ni se declara vigente el corpus al 10 de noviembre de 2026.

## Fuente comunicada por el propietario

La mayor parte del contenido jurídico de Código Procesal Civil PE fue tomada de **[LP Derecho: Código Procesal Civil peruano (actualizado 2026)](https://lpderecho.pe/codigo-procesal-civil-actualizado/)**. Esta procedencia ayuda a identificar decisiones editoriales, notas, títulos, puntuación y posibles errores de transcripción, pero LP Derecho es **una fuente secundaria**. Para decidir qué texto mostrar, prevalece la norma publicada en **El Peruano**, el texto original de la ley modificatoria y, como herramienta auxiliar, SPIJ.

La página de LP muestra publicación **12/05/2026**; su introducción indica última modificación **Ley 32377, publicada el 07/06/2025**. Sin embargo, en otro párrafo atribuye erróneamente esa publicación al **11/04/2025**: discordancia interna verificable frente al Diario Oficial. El título «actualizado 2026» no implica cotejo de todas las normas hasta noviembre de 2026.

### Ejemplo de una diferencia heredada de LP

LP conserva el encabezado antiguo **«Artículo 558.- Trámite de la apelación con efecto suspensivo»**, aunque el propio cuerpo ya reproduce la nueva regla de recursos de apelación con y sin efecto suspensivo. La **[Ley 31464, artículo 4, El Peruano](https://busquedas.elperuano.pe/dispositivo/NL/2063845-3)** dispone la denominación **«Trámite de los recursos de apelación»**. La app ya exhibe este último encabezado, corregido mediante **JUR-006** con aprobación expresa del propietario. **No revertirlo para imitar LP**.

## Nuevo cotejo dirigido del contenido local con fuentes primarias

Se extrajeron los textos completos de estos cuatro registros del archivo actual `app/src/main/assets/articles.json` de `main`. Las comparaciones se refieren únicamente al **pasaje de cada norma modificatoria**, no a una revisión de vigencia posterior.

| Artículo | Norma primaria y fecha | Conclusión limitada |
| --- | --- | --- |
| 558 | [Ley 31464, art. 4](https://busquedas.elperuano.pe/dispositivo/NL/2063845-3), 04/05/2022 | Sumilla y cuerpo de la app coinciden con la regla promulgada sobre apelaciones con y sin efecto suspensivo. El encabezado de LP no coincide. |
| 759 | [Ley 32377, art. 1](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7), 07/06/2025 | Intervención del Ministerio Público y excepción en sucesión intestada coinciden con la publicación original. |
| 834 | [Ley 32377, art. 1](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7), 07/06/2025 | Plazo de quince días, apersonamiento y resolución sin audiencia coinciden con la publicación original. |
| 835 | [Ley 32377, disposición complementaria derogatoria única](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7), 07/06/2025 | La app conserva el identificador, marcado **[Derogado]**, sin presentar una norma derogada como vigente. |

**Resultado de este lote:** cuatro registros cotejados con normas primarias en los aspectos citados, **cero cambios de código propuestos**, cuatro registros documentados en la matriz CSV. Ni estos resultados ni los anteriores certifican ausencia de reformas en la fecha actual.

## Búsqueda de reformas posteriores y JUR-002

La Ley 32377 publicada 07/06/2025 sigue siendo la última reforma expresamente identificada en la página fuente de LP. La búsqueda web puntual de 2026 no arrojó una nueva reforma promulgada confirmada; **un resultado negativo de búsqueda no permite afirmar que no existan reformas**.

La [nota del Congreso del 06/05/2026](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/) documenta aprobación del **dictamen** del PL 9337/2024-CR sobre ejecución de actas de conciliación alimentaria. No se confirmó ley promulgada ni número de publicación posterior. **JUR-002 permanece abierto**.

## Estimación de avance global para orientar al propietario

La siguiente puntuación es una **estimación de gestión, no un certificado jurídico ni el porcentaje de artículos legalmente verificados**:

| Bloque de la Fase 1 hasta la preparación de la publicación | Peso | Avance acreditado |
| --- | ---: | ---: |
| Producto, funcionalidad y pruebas de dispositivo (misiones 1–5, incluido icono, TTS, eliminación y navegación) | 40 puntos | 40 |
| Privacidad/UMP y controles de AdMob (prueba UMP simulada superada; decisiones de producción pendientes) | 15 puntos | 12 |
| Inventario, trazabilidad y auditoría de vigencia jurídica (862 inventariados, 160 contextuales, cinco correcciones aprobadas, 11 variantes y cuatro cotejos primarios; **no hay certificación integral**) | 35 puntos | 8 |
| Preparación final de Google Play (auditoría final, AAB firmado, release e inscripción en pruebas internas) | 10 puntos | 0 |
| **Total ponderado estimado** | **100** | **60** |

La puntuación jurídica 8/35 mide avances de **organización, evidencia y correcciones puntuales**, no 8/35 de los artículos ni un grado de certeza de vigencia. Es razonable recalibrar estos pesos conforme se cierren los bloqueos.

## Siguientes actividades (sin cambios normativos automáticos)

1. Rastrear sistemáticamente normas modificatorias posteriores al 07/06/2025 hasta 10/11/2026, corroborar publicación, vigencia y disposición concreta; revisar 2026 en El Peruano, Congreso y SPIJ.
2. Contrastar integralmente los **862 artículos**, incluso párrafos tras notas y sus sumillas, de una versión oficial de corte conocido. La matriz histórica que da 685 coincidencias parciales no habilita declararlos totalmente revisados.
3. Priorizar las variantes de potencial impacto (527, 690, 837) y los 135 pendientes sin cotejo contextual previo. Emitir JUR-008 y siguientes solo si se fundamentan con literal local y literal oficial de la norma modificatoria.
4. Aprobación individual de toda modificación por el propietario; solo entonces editar `strings.xml`, generar `articles.json`, ejecutar pruebas y fechar la revisión.
5. Cierre independiente de AdMob/Google Play y solo entonces preparar entrega de producción.

**No cambiar fecha visible de verificación legal ni afirmar contenido actualizado a noviembre 2026 con la evidencia actual.**
