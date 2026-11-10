# Auditoría jurídica — Tercer lote de siete artículos (10/11/2026)

**Estado: cotejo histórico-documental contra versión oficial 2025; no certifica vigencia 2026. Sin cambios en `strings.xml`, `articles.json`, fecha visible ni Android.**

## Alcance

Se han inspeccionado los textos completos de los artículos **10, 21, 22, 34, 53, 128 y 204** en el `app/src/main/assets/articles.json` de `main`. Proceden de los **135 casos** que la matriz histórica del 09/11 clasificó `SIN_RESOLUCION_DOCUMENTADA`.

Se compararon con el [TUO del Código Procesal Civil de Editora Perú](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), edición **actualizada 07/06/2025**, y, en el artículo 21, con el [DL 1384 original, publicado 04/09/2018](https://busquedas.elperuano.pe/dispositivo/NL/1687393-2). Las páginas son del PDF oficial, contado desde 1, y se documentan como guía para repetición de la lectura.

| Artículo | Evidencia localizada | Resultado |
| --- | --- | --- |
| 10 — Competencia por cuantía | TUO PDF pág. 5 (índice 4) | Local «o **de** sus anexos», TUO «o sus anexos». Restantes reglas sin diferencias de contenido detectadas. **Variante editorial**, no cambio de cuantía. |
| 21 — Regulación de capacidad jurídica | TUO PDF pág. 6 (índice 5); DL 1384 art. 4 | Publicación de DL 1384: «donde se encuentra las personas»; app: «donde se encuentran las personas». **Concordancia gramatical corregida** en app, sin variación aparente del supuesto de competencia. La versión indexada del TUO reproduce plural y la extracción del PDF puede contener el singular; usar la fuente modificatoria directa como criterio literal. |
| 22 — Derogado | TUO PDF pág. 6 (índice 5) | Número conservado y marcado **[Derogado]**, consistente con DL 845 de 1996. |
| 34 — Procesos de ejecución | TUO PDF pág. 7 (índice 6) | Texto completo de una oración coincidente. |
| 53 — Facultades coercitivas | TUO PDF pág. 9 (índice 8) | Numerales 1 y 2, multas y detención de 24 h coinciden, más dos párrafos finales. |
| 128 — Admisibilidad y procedencia | TUO PDF pág. 18 (índice 17) | Regla completa (requisito de forma frente a fondo) coincide. |
| 204 — El acta de la audiencia | TUO PDF pág. 26 (índice 25); Ley 30293 de 2014 | Texto completo coincidente en contenido. **Formato de letras**: app «a.- / b.- / c.-» frente a TUO «a. / b. / c.». |

**Resultado del lote:** 4 coincidencias de contenido sin variantes relevantes (22, 34, 53, 128), 2 diferencias editoriales (10, 204) y 1 variante gramatical cotejada con norma original (21). No se han identificado errores normativos sustantivos confirmados en estos siete registros; no es una prueba de ausencia de errores.

### Verificación adicional estructural del DL 1384

El [Decreto Legislativo 1384](https://busquedas.elperuano.pe/dispositivo/NL/1687393-2) incorpora el artículo **119-A** y los artículos **841–847** del Código Procesal Civil. Se comprobó la **presencia de los ocho identificadores** en `articles.json` con los epígrafes correspondientes. Esta es una comprobación **de inventario, no comparación de su contenido completo ni vigencia actual**. Un ejemplo de discrepancia menor en esos registros: «659 A» en el texto original frente a «659A» en la app (art. 842), pendiente de normalización editorial si se decide.

### Seguimiento JUR-001 y JUR-002

**JUR-001:** siguen pendientes tanto cotejo contra legislación vigente hasta **10/11/2026** como rastreo completo de publicaciones y disposiciones transitorias. La matriz tiene **862 filas**. De los **135 casos** inicialmente sin resolución contextual, **7** cuentan ahora con este cotejo de 2025 y quedan **128 todavía sin un nuevo cotejo contextual**. Sin embargo, el histórico de 160 casos sigue sin **certificación jurídica integral a 2026**. No sumar esta cifra a artículos certificados.

**JUR-002:** la [nota de comisión del Congreso de 06/05/2026](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/) registra dictamen aprobado del proyecto **9337/2024-CR** sobre actas alimentarias. Las búsquedas dirigidas por título/número en normas publicadas no confirmaron una ley promulgada ni resolución definitiva del expediente; **el estado final queda sin demostrar**. Tampoco puede inferirse ausencia de reformas generales por búsquedas sin resultados.

El [registro parlamentario de legislación 2026](https://www3.congreso.gob.pe/didp/seguimiento-reglamentacion-leyes/leyes-reglamentos-disposiciones-2026) consultado cubre un conjunto parcial y no sustituye la búsqueda en el Diario Oficial. Las referencias a proyectos y resoluciones judiciales publicadas en 2026 no deben clasificarse como modificaciones legislativas del CPC sin disposición expresa.

## Límites, decisiones y siguientes pasos

No hay justificación suficiente para editar ninguno de estos siete artículos por motivos jurídicos: las diferencias detectadas son de estilo o gramática y demandarían autorización individual. Evitar replicar una errata de la fuente primaria del DL 1384 por fidelidad mecánica. Priorizar siguiente lote **arts. 47, 49, 56, 91, 96, 118, 140**, y seguir rastreando reformas posteriores al 07/06/2025. Consultar SPIJ o edición oficial posterior a 2025 para cerrar legalmente.

**Estimación global de Fase 1: se mantiene 60%** por estar abierto el cotejo normativo integral. La cifra es avance de gestión, no porcentaje legal validado.
