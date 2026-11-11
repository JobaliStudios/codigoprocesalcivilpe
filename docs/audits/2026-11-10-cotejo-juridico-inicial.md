# Auditoría jurídica inicial — CPC PE, 10/11/2026

**Estado: abierto. Solo documentación; no se ha modificado `strings.xml`, `articles.json` ni la fecha visible.** El objetivo es registrar evidencias, no proclamar que el corpus cumple la vigencia al 10 de noviembre de 2026.

## Integridad de material de partida

El corpus auditado de Fase 1 contiene **104 bloques y 862 artículos** (SHA-256 **`E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538`**, verificado en copia local). La matriz documental anterior tenía 685 comparaciones parciales normalizadas, 17 registros de rangos derogados y 160 por cotejar contextualmente. De esos 160, 135 carecían de revisión contextual documentada, 13 tenían examen parcial, 11 eran variantes candidatas y 1 correspondía al artículo 122 ya corregido mediante JUR-007. **Los cotejos parciales no certifican ni el artículo completo ni su vigencia.**

## Fuentes empleadas

1. [Editora Perú: TUO del Código Procesal Civil](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007). La edición consultada indica **ACTUALIZADA 07/06/2025** en portada. Sus diferencias son válidas como comparación con esa edición, pero no prueban vigencia al 10/11/2026.
2. [Decreto Legislativo 1070, publicación original 28/06/2008](https://www.leyes.congreso.gob.pe/Documentos/DecretosLegislativos/01070.pdf): contraste de artículos 554 y 760.
3. [Decreto Legislativo 1069, publicación original 28/06/2008](https://www.leyes.congreso.gob.pe/Documentos/DecretosLegislativos/01069.pdf): contraste del artículo 690.
4. [Ley 32377, publicación oficial 07/06/2025](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7): modifica 759 y 834, y deroga 835. El corpus local refleja estas modificaciones; sigue pendiente rastreo 2026.
5. [Congreso, 06/05/2026: dictamen del PL 9337/2024-CR](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/). **Es un dictamen, no prueba de una ley promulgada.**

Se buscó evidencia pública reciente de reformas 2026, pero **no se confirmó una nueva norma modificatoria promulgada después de agosto**. Tampoco se confirmó que no existan modificaciones posteriores. Debe verificarse de forma exhaustiva el diario oficial, SPIJ y el registro legislativo, incluida la entrada en vigor.

## Cotejo dirigido de las 11 variantes de la cola anterior

| Artículo | Tipo | Comparación resumida | Acción |
|---|---|---|---|
| 353 | SUMILLA Y PUNTUACION | Recursos de apelación → Recursos | El título no corresponde al consolidado y difiere una coma |
| 420 | SUMILLA | Literalidad de la multa → Literalidad y destino de la multa | El texto operativo del pasaje examinado coincide; difiere la sumilla |
| 670 | SUMILLA | Conversión de la recaudación a administración de unidad de producción o comercio → Conversión a administración de unidad de producción o comercio | Cambio de título sin diferencia operativa evidente en pasaje examinado |
| 840 | SUMILLA | Entrega de copia certificada del expediente → Entrega del expediente | Cambio de título sin diferencia operativa evidente en pasaje examinado |
| 492 | VARIANTE GRAFICA | cuarentacinco días → cuarenticinco días | Ambas expresiones indican 45 días |
| 527 | SUMILLA Y TEXTO | Audiencia de Pruebas / discrepen / en un plazo → Audiencias de pruebas / discrepan / en plazo | Requiere comprobar publicación original Ley 27117 antes de redactar corrección |
| 554 | VARIANTE GRAFICA | trascurrido → transcurrido | El DL 1070 de 2008 confirma transcurrido; sin cambio en número de días |
| 676 | VARIANTE VERBAL | si fuera necesario → si fuere necesario | Diferencia gramatical; mantener prueba documental |
| 690 | VARIANTE REDACCIONAL | favor; contra / litisconsorte / desconociera / prescrito en el artículo → favor, contra / litis consorte / desconociere / prescrito el artículo | Requiere contraste jurídico; publicación original usa frase gramaticalmente inusual |
| 760 | SINGULAR PLURAL | audiencia de prueba → audiencia de pruebas | DL 1070 confirma plural |
| 837 | OMISION PREPOSICION | El proceso que se refiere → El proceso a que se refiere | Cotejar norma modificatoria Ley 26572 para una corrección autorizada |

### Interpretación

Las 11 variantes quedan documentadas contra la edición oficial **2025**; no se clasifican como 11 nuevas reformas ni como fallos de contenido jurídico sustantivo. Hay cinco diferencias de sumilla (353, 420, 670, 840 y 527), además de variantes de redacción en 527, 554, 676, 690 y 837 y la grafía del plazo en 492. El artículo 760 difiere en singular/plural. Las prioritarias para consulta de texto modificatorio original son **527, 690 y 837**. No reemplazar automáticamente erratas aparentes de la propia edición consolidada.

## Pendientes bloqueantes

**JUR-001:** recotejar el contenido **completo** de 862 artículos contra fuente oficial, preservando párrafos posteriores a notas intermedias, y localizar toda reforma aplicable publicada hasta el 10/11/2026. El cotejo de 2025 no es sustituto.

**JUR-002:** investigar el resultado legislativo y publicación de la iniciativa sobre ejecución alimentaria (PL 9337/2024-CR); un dictamen de comisión no permite modificar el código.

Después de localizar discrepancias con norma primaria y vigencia comprobada, registrar propuestas **JUR-008 y siguientes**, cada una con literal local, literal oficial, número de ley/decreto, fecha y vigencia, alcance y decisión individual del propietario. Solo tras aprobación modificar `strings.xml`, regenerar `articles.json` y ejecutar regresiones. No actualizar fecha declarada del corpus ni preparar publicación de producción mientras siga abierto el cierre integral.

## Archivo de trabajo

`docs/audits/2026-11-10-matriz-862-articulos.csv` conserva las **862 filas** con estado previo, once variantes documentadas y calificación explícita **NO_CERTIFICADA** a noviembre de 2026. Todos los registros constan como `modificacion_codigo=NO` y `aprobacion_propietario=PENDIENTE`.

**Limitación:** Este documento es una etapa verificable de la auditoría; no debe presentarse como dictamen jurídico definitivo ni revisión exhaustiva de vigencia.
