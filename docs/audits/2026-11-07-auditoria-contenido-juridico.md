# Auditoría jurídica — informe de trabajo para revisión

Consulta realizada el 8 de noviembre de 2026. Estado: EN CURSO; no acredita todavía revisión integral ni vigencia a esta fecha.

## Línea base y alcance

Seguimiento JUR-007: aprobada expresamente el 9 de noviembre y aplicada solo al párrafo posterior al numeral 7 del artículo 122. Regeneración: 104 bloques, 862 artículos. SHA-256 vigente: `E557162714E1D48672D9A0BD3B06C722119771DC6170B3AEF4C978B35C9DF538`. El diagnóstico previo se conserva abajo como evidencia; su estado pendiente quedó sustituido por la aprobación en el CSV. No cambia la fecha visible ni se altera ningún otro candidato jurídico.

Estado vigente al 9 de noviembre: JUR-005 y JUR-006 aprobadas expresamente y aplicadas únicamente a la oración omitida del artículo 90 y la sumilla del artículo 558. Los apartados de hallazgos conservan el diagnóstico previo a la decisión; el CSV registra la aprobación. Corpus regenerado: 104 bloques, 862 artículos, SHA-256 `D13D114256A49B47A1B86A4A44AA3A845D96CBCB77A6CF7DB0999E465E0B9042`. Fecha visible de verificación conservada. Ninguna otra diferencia candidata fue corregida.

Actualización JUR-003: aprobada expresamente por el propietario el 8 de noviembre de 2026 y aplicada solo a los conectores finales de los numerales 7 y 8 del artículo 561. Se regeneraron 104 bloques y 862 artículos, conservando el inventario. Hash posterior: `98605ECDE0DE8B564776479FC7CD24F2D021529861A614691152D9F1E9D35109`. La fecha visible de verificación no se modifica. El hash inicial se conserva abajo como evidencia de línea base.

App 1.06, código 11. Corpus: `app/src/main/assets/articles.json`, generado desde `app/src/main/res/values/strings.xml`. SHA-256: `BC9EE587FA6ECCA923DD0A163B8739E8CE9976C09A79C7551E88661B50B946D9`. La fecha declarada en la app sigue siendo 8 de agosto de 2026.

Se investiga el período posterior al 8 de agosto de 2026 y se contrasta el corpus con el consolidado oficial. Jerarquía: norma modificatoria en El Peruano, SPIJ consolidado, Congreso/Poder Judicial como contraste. Las propuestas legislativas y dictámenes no equivalen a normas promulgadas.

## Evidencia obtenida

1. [Consolidado oficial de Editora Perú](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007): PDF de 98 páginas, portada «ACTUALIZADA 07/06/2025». Por su fecha no basta para certificar ausencia de reformas posteriores.
2. [Ley 32377 en El Peruano](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7): ficha confirma publicación 07/06/2025 y número 32377. El extractor devuelve una cabecera temática ajena antes de la norma; cualquier cotejo literal debe usar el PDF o el cuerpo normativo, no esa cabecera.
3. [Nota oficial del Congreso, 6 de mayo de 2026](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/): informa aprobación de un dictamen sobre ejecución de actas de conciliación alimentaria. Es evidencia de trámite parlamentario; falta comprobar si culminó en promulgación.
4. [Agenda oficial del Congreso, 19 de marzo de 2026](https://www2.congreso.gob.pe/Sicr/RelatAgenda/PlenoComiPerm20112016.nsf/Apleno/D8B9B6E630CAC55505258DBD00568C31/%24FILE/AGENDA-PLENO-19-03-2026.pdf): contiene propuestas relativas al CPC y arbitraje. Su inclusión en agenda no acredita vigencia.
5. [SPIJ](https://spij.minjus.gob.pe/): la consulta automatizada agotó el tiempo de espera. Pendiente acceso alternativo; no se presume el contenido del consolidado.

## Hallazgos para decisiones independientes

### JUR-007 — excepción incompleta para decretos en artículo 122 (APROBADO Y APLICADO)

Consulta: 9 de noviembre de 2026. El párrafo local posterior al numeral 7 dice: «La resolución que no cumpliera con los requisitos antes señalados será nula, salvo los decretos que no requerirán de los signados en los incisos 3, 5 y 6, y los autos del expresado en el inciso 6.»

La publicación de la Ley 27524, página 210871 de El Peruano del 6 de octubre de 2001, reproduce: «La resolución que no cumpla con los requisitos antes señalados será nula, salvo los decretos que no requieren cumplir con lo establecido en los incisos 3, 4, 5 y 6, y los autos del expresado en el inciso 6.» Fuente primaria: https://www.leyes.congreso.gob.pe/Documentos/Leyes/27524.pdf . Descarga directa correcta; extracción de caracteres defectuosa, por lo que se renderizó y leyó visualmente la única página. El consolidado de Editora Perú, página física 18, coincide en ese párrafo. La descarga del DL 25940 en la ruta candidata devolvió 404 y no se usa como prueba.

Impacto: la lista local omite el inciso 4 y cambia la redacción de la excepción. Propuesta: sustituir únicamente este párrafo por el transcrito en la publicación oficial; conservar numerales y párrafos restantes. Aprobada expresamente por el propietario el 9 de noviembre de 2026 y aplicada únicamente a ese párrafo. No se certifica actualización integral por este cotejo puntual.

Otros resultados del cotejo de notas intermedias: 24, 61, 208, 408, 424, 446, 451, 731 y 749 recuperan igualdad normalizada operativa al retirar las notas en su posición interna, en lugar de cortar en la primera nota. Esto es una comprobación auxiliar, no se cambia la clasificación mecánica de la matriz anterior. En 478/491 persisten cortes por referencias a «Artículo 440» y se requiere revisar el pasaje completo; no se declara una omisión basándose en ese corte.

### JUR-005 — oración omitida en artículo 90 (APROBADO Y APLICADO)

Consulta: 9 de noviembre de 2026. El tercer párrafo local termina después de «medios probatorios acompañados al pedido.». El consolidado oficial añade inmediatamente «La decisión es apelable sin efecto suspensivo.», antes del párrafo sobre acumulación de oficio. La misma oración aparece en el Decreto Legislativo 768 publicado el 4 de marzo de 1992, archivo oficial del Congreso: https://www.leyes.congreso.gob.pe/Documentos/DecretosLegislativos/00768.pdf . Contraste: consolidado de Editora Perú ya citado, artículo 90.

Impacto: omisión de una regla procesal sobre impugnación. Corrección aprobada expresamente por el propietario el 9 de noviembre: insertar únicamente esa oración al final del tercer párrafo, conservando los demás párrafos. Fuentes coincidentes en este pasaje; no se identificó conflicto. Aplicada e índice regenerado. Este hallazgo puntual no acredita ausencia de reformas posteriores.

### JUR-006 — sumilla anterior en artículo 558 (APROBADO Y APLICADO)

Consulta: 9 de noviembre de 2026. Sumilla local: «Trámite de la apelación con efecto suspensivo». El artículo 4 de la Ley 31464, publicado el 4 de mayo de 2022, establece «Trámite de los recursos de apelación». Fuente primaria: https://busquedas.elperuano.pe/dispositivo/NL/2063845-3 . El cuerpo operativo local ya incluye ambos efectos y coincide en la regla remitida a los artículos 376 y 377; la propuesta afecta únicamente la sumilla. Coincide con el consolidado oficial, página física 61. Aprobada expresamente por el propietario el 9 de noviembre de 2026 y aplicada únicamente a la sumilla; índice regenerado. La ficha HTML presenta una cabecera temática ajena; el número, fecha y cuerpo de la ley fueron comprobados, sin usar aquella cabecera como evidencia.

### Cotejo dirigido adicional del 9 de noviembre

Seguimiento de 478/491: se reconstruyó cada pasaje hasta el encabezado del artículo siguiente, conservando la referencia intermedia al artículo 440. En 491 se recupera igualdad normalizada de todo el texto operativo. En 478 coincide el contenido operativo, incluido el numeral 9 derogado: la diferencia remanente era que el corpus usa la etiqueta `[Derogado]`, mientras el PDF explica la derogación en una nota. Los numerales posteriores están presentes en ambas versiones. No se propone corrección por esos cortes del extractor.

Se incorporó `2026-11-09-cobertura-cotejo.csv`, con una fila por cada uno de los 862 artículos locales después de JUR-005/006: 685 con igualdad normalizada parcial frente a algún candidato extraído, 17 comprendidos en rangos derogados verificados y 160 que requieren cotejo contextual. Estos grupos son excluyentes. Los 160 no son errores confirmados: incluyen sumillas, cabeceras, notas, artículos derogados y diferencias ya revisadas narrativamente. El CSV registra el resultado mecánico y no sustituye las decisiones jurídicas del informe.

Método: extracción pypdf de las 98 páginas; encabezado de artículo al inicio de línea; números alfanuméricos conservados; comparación sin espacios, puntuación ni diacríticos, sin cabeceras reconocidas y cortada en el primer marcador de nota. Límites: se pierden notas y párrafos posteriores a notas intermedias; puede haber varios candidatos o referencias; igualdad normalizada no demuestra identidad literal, vigencia ni ausencia de omisiones posteriores a una nota. PDF usado: SHA-256 `D7552E506C51E2CA3F48BC2221AE61D21E972C79E4FAE387350955DF29541E4E`, corte de portada 07/06/2025. Corpus de esta matriz: `D13D114256A49B47A1B86A4A44AA3A845D96CBCB77A6CF7DB0999E465E0B9042`.

La igualdad normalizada subió de 682 a 683 artículos tras aplicar JUR-004. No equivale a identidad literal ni cierre de la auditoría. En 566-A se confirmó que la oración final sobre sustitución de denuncia penal sí está presente en la página física 63; el primer marcador de nota intermedio causaba el falso negativo. En 38 la diferencia normalizada era el ordinal «46°», convertido en «46o» por la normalización, sin cambio operativo observado.

Las diferencias de sumilla de 353, 420, 670 y 840 y variantes menores en 492, 527, 554, 676, 690, 760 y 837 se conservan como candidatos de contraste con norma original; no se aplican cambios automáticos ni se consideran todos errores sustantivos. El artículo 554 del consolidado incluso conserva «demando [sic]» frente a «demandado» local: no se debe trasladar esa errata sin análisis de fuente.

### JUR-004 — omisión de «superior» en artículo 364

Seguimiento: APROBADO expresamente por el propietario y aplicado insertando únicamente «superior». Índice regenerado: 104 bloques y 862 artículos. Hash posterior `99540206C298025AF96BE2D45FC82F9A0CD703C42B6A2155DDB0B4AB45DA5AE7`. El texto original descrito a continuación se conserva como evidencia histórica; la propuesta ya no está pendiente.

Estado: PENDIENTE DE APROBACIÓN. Texto local: «que el órgano jurisdiccional examine». Consolidado oficial: «que el órgano jurisdiccional superior examine». Fuente: https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007, artículo 364. Impacto: precisión sobre el órgano que revisa la resolución apelada. Propuesta mínima: insertar «superior» entre «jurisdiccional» y «examine» en la fuente local y regenerar el índice. No se aplicó. Decisión: [ ] APROBADO [ ] RECHAZADO [ ] OBSERVADO.

Otros candidatos revisados: 437 presenta título editorial más extenso en la app, con cuerpo operativo coincidente; no se propone cambio automático. El numeral 14 del artículo 749 sí figura en la fuente, después de una nota de modificación: la normalización truncaba ese pasaje, por lo que no hay omisión local. En 561 el consolidado mantiene conectores antiguos; la norma modificatoria primaria Ley 32266 justifica la corrección JUR-003 ya aprobada, conservando así la jerarquía de fuentes acordada.

| ID | Estado | Artículo/alcance | Evidencia e impacto | Propuesta | Decisión del propietario |
| --- | --- | --- | --- | --- | --- |
| JUR-001 | OBSERVADO | Corpus completo / fecha de verificación | La fuente consolidada accesible tiene corte 07/06/2025; no prueba vigencia al 08/11/2026. | Mantener fecha actual hasta terminar búsqueda de normas y comparación integral. | PENDIENTE |
| JUR-002 | OBSERVADO | Posibles cambios de ejecución alimentaria | La nota oficial solo acredita dictamen, no promulgación. | Rastrear expediente y eventual ley antes de proponer cualquier texto. | PENDIENTE |

## Limitaciones y trabajo pendiente

## Cotejo dirigido de reformas incorporadas

### Cobertura automatizada del consolidado oficial

### Revisión de candidatos de baja similitud

Artículos 559 y 722: cotejados contra páginas 61 y 78 respectivamente. Coinciden las reglas operativas; las notas locales conservan antecedentes modificatorios adicionales. No se propone corrección.

Se añadió una pasada de igualdad normalizada: elimina espacios/puntuación/diacríticos, cabeceras reconocidas y el texto posterior al primer marcador de nota. Resultado: **682 de 862 artículos** tienen igualdad bajo ese criterio frente a algún candidato oficial. Este control no compara notas históricas ni certifica identidad literal o vigencia posterior al corte del PDF. Los 180 restantes incluyen artículos derogados ya verificados, errores de extracción y posibles diferencias editoriales o sustantivas todavía por clasificar; no son 180 errores confirmados.

Actualización del artículo 528: se cotejó también la continuación entre páginas 58 y 59, incluidos numerales 4–6 y plazos para predios rústicos, urbanos y muebles. No se identificó diferencia sustantiva en ese cotejo; la fuente introduce cabeceras al cambiar de página y presenta pequeñas variantes editoriales, por lo que no se declara identidad literal.

Se verificó además el estado derogado de 385, 389, 390 y 399 (Ley 29364) y 392-A (Ley 31591) frente a las páginas 42–44. Coincide con los títulos locales. Para 471 se aplica el rango derogado 469–472 ya revisado; para 542 el rango 540–545. La mención aislada «Artículo 542» de la página 95 corresponde a una modificación de otro código dentro de las disposiciones, no al artículo 542 del CPC: se descarta ese emparejamiento del extractor.

El patrón de encabezados se restringió a inicio de línea, reduciendo menciones de 979 a 916 sin alterar la cobertura de identificadores. Persisten referencias y disposiciones ajenas que exigen revisión contextual. Los siguientes candidatos operativos prioritarios son 559 y 722; no se considera cerrado el cotejo integral.

Se cotejaron los pasajes completos disponibles de las páginas físicas 6, 20, 36, 76, 77 y 78 para los artículos 22, 156, 326, 329, 693, 696 y 718: coinciden en estado derogado. La baja similitud proviene principalmente de notas históricas adicionales del corpus local y de texto vecino capturado por el extractor. No se propone reactivar ni suprimir esos registros.

Los textos operativos de los artículos 754 (página 82) y 847 (página 90) coinciden en el cotejo dirigido, salvo disposición de párrafos y notas. En 847 el extractor anexaba disposiciones complementarias, lo que reducía artificialmente la similitud. No se propone corrección.

El artículo 528 requiere comparar la continuación en páginas sucesivas: el primer pasaje coincide en estructura y reglas iniciales, pero no se cierra el cotejo del artículo completo con una extracción truncada. Ninguno de estos candidatos se contabiliza como discrepancia sustantiva confirmada.

Se descargó y extrajo el PDF oficial de 98 páginas con pypdf. Se localizaron 979 menciones con patrón de artículo y 866 identificadores distintos; las menciones incluyen referencias cruzadas, por lo que no equivalen a 866 artículos vigentes. De los 862 identificadores locales, 845 tienen candidato individual en la extracción.

Los 17 restantes fueron comprobados en las páginas 52, 60, 77 y 78: la fuente oficial los presenta mediante rangos colectivos de artículos derogados (469–472, 540–545, 697–703 y 713–714). Los 17 registros locales correspondientes están titulados `[Derogado]`. La falta de encabezado individual fue un efecto de extracción, no una omisión del corpus.

Esta comprobación cubre la localización de los 862 identificadores, pero NO acredita igualdad textual integral. La comparación de similitud produjo candidatos de revisión (por ejemplo 22, 156, 326, 329, 528, 693, 696, 718, 754 y 847), afectados potencialmente por notas, maquetación y referencias. Ninguno se convierte en hallazgo jurídico sin cotejo del pasaje y su norma modificatoria. El corte del PDF sigue siendo 07/06/2025.

| Artículo | Fuente oficial | Resultado del cotejo local | Acción propuesta |
| --- | --- | --- | --- |
| 561, numeral 9 | Ley 32266, PDF del Congreso citado arriba | El supuesto de representación por familiares cuidadores está incorporado. La enumeración anterior conserva conectores y puntuación de la versión previa: el numeral 7 termina con «y,» y el 8 con punto, mientras la ley coloca el conector al final del numeral 8. | JUR-003: armonización editorial de numerales 7 y 8 para reproducción fiel; sin cambio sustantivo. Requiere aprobación. |
| 759 | Ley 32377, cuerpo normativo de El Peruano | Coinciden los dos párrafos operativos; se conserva la excepción relativa a menores y consejo de familia. | Sin corrección propuesta en este cotejo. |
| 731, párrafos modificados | Artículo 4 de Ley 32297, https://busquedas.elperuano.pe/dispositivo/NL/2389754-2 | Coinciden la convocatoria tras tasación y la regla de REM@JU con remisión a Ley 30229. La publicación emplea elipsis para contenido no transcrito; los párrafos restantes requieren contraste con el consolidado. | Sin corrección de los párrafos modificados; pendiente cotejo del resto. |
| 834 | Ley 32377, cuerpo normativo de El Peruano | Coinciden plazo de quince días, verificación documental y resolución sin audiencia en el supuesto legal. | Sin corrección propuesta en este cotejo. |
| 835 | Disposición derogatoria de Ley 32377 | Está identificado como derogado y conserva número para trazabilidad. | Sin corrección propuesta. |

JUR-003 — Estado PENDIENTE. Texto actual: numeral 7 finaliza «menores; y,» y numeral 8 «ley.». Texto oficial: numeral 7 finaliza «menores;» y numeral 8 «ley; y». Impacto editorial bajo; no altera sujetos habilitados. Decisión independiente: [ ] APROBADO [ ] RECHAZADO [ ] OBSERVADO. Fuente: https://api.congreso.gob.pe/spley-portal-service/archivo/MjY5MjM3/pdf (página 1, Ley 32266).

- Las búsquedas iniciales no identificaron una norma promulgada posterior al corte con evidencia suficiente. Esto NO significa «sin diferencias encontradas».
- Pendiente comparación artículo por artículo con fuente oficial y control de cobertura, incluidos artículos alfanuméricos y derogados.
- Pendiente acceso a SPIJ y búsqueda dirigida de publicaciones entre agosto y noviembre de 2026.
- Existe una propuesta editorial limitada (JUR-003), pendiente de decisión. No hay corrección sustantiva acreditada para aplicar. Los estados APROBADO/RECHAZADO/OBSERVADO se asignarán por hallazgo; aprobar uno no aprueba los demás.
- El corpus no se modificó durante esta investigación. No se avanzó `content_verified_date` ni `LEGAL_SOURCES.md`.
