# Código Procesal Civil PE — Etapa 42: registro reproducible de legislación y falsos positivos (10/11/2026)

## Objetivo y método

Formalizar un **registro de evidencias legislativas** para el intervalo **08/06/2025–10/11/2026**, con distinción verificable entre: (i) reforma directa del CPC, (ii) norma administrativa de aplicación, (iii) proyecto/dictamen, (iv) norma ajena al CPC, (v) resolución que cita el CPC y (vi) referencia secundaria. El registro reproducible está en el archivo hermano `2026-11-10-etapa-42-registro-evidencias-legislativas.csv` con **7 entradas**.

Búsquedas efectuadas (sintaxis conceptual): `"Código Procesal Civil" "2026" modifica`, `"Decreto Legislativo 768" modifica ley 2025 2026`, `"Código Procesal Civil" incorpora 2026`, publicaciones de El Peruano, archivo digital de leyes del Congreso y notas de prensa legislativa. Los resultados de búsquedas **no garantizan un censo de todas las disposiciones** en boletines oficiales; las leyes pueden modificar indirectamente el CPC desde artículos finales sin que el título o indexado las detecte.

**No se atribuye a la primera búsqueda negativa carácter de certificación legal.**

## Tabla de decisiones basada en fuente oficial

| Registro | Fecha publicada | Evidencia | Clasificación | Resultado |
|---|---|---|---|---|
| E42-001 | **07/06/2025** | [Ley 32377, texto íntegro El Peruano](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7) | **Reforma directa ANTERIOR a la ventana**, adoptada en TUO 2025 | Modifica arts. **759, 834** y deroga **835**; la app contiene esas reglas. No genera nuevo cambio post-07/06 |
| E42-002 | **31/12/2025** | [R. A. 000481-2025-CE-PJ](https://busquedas.elperuano.pe/dispositivo/NL/2472911-1) | Norma administrativa vigente con efecto práctico | URP del ejercicio 2026 **S/ 550**. No modifica el literal CPC; aplicar en cuantías/aranceles calculados, con exoneraciones y cuadro vigente |
| E42-003 | **06/05/2026** | [Comisión de Justicia del Congreso: dictamen PL 9337/2024-CR](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/) | Propuesta legislativa, **no ley acreditada por esa fuente** | No trasladar a art. de alimentos/ejecución como derecho positivo. Verificar historial parlamentario, si llega a haber ley |
| E42-004 | **08/11/2026** | [Decreto Legislativo 1744](https://busquedas.elperuano.pe/dispositivo/EX/2572858-2/) | **Falso positivo**: Código *Procesal Penal* (D. Leg. 957) y Código Penal, no CPC (D. Leg. 768) | Excluir de matriz de reformas directas CPC |
| E42-005 | **2026** | [Resoluciones del JNE que citan CPC](https://busquedas.elperuano.pe/dispositivo/NL/2555576-1) | Actos que *aplican/citan* CPC, no lo reforman | No cambiarlos por referencias en resoluciones; evaluar jurisprudencia vinculante por separado |
| E42-006 | **12/05/2026** | [Edición de LP Derecho CPC 2026](https://lpderecho.pe/codigo-procesal-civil-actualizado/) | Apoyo secundario | Declara Ley 32377 como última modificación conocida; **no prueba ausencia de reformas entre mayo y noviembre**. Presenta un día de publicación erróneo dentro del cuerpo de la nota; prevalece 07/06/2025 del Diario Oficial |
| E42-007 | **10/11/2026** | [Catálogo de Normas Legales Actualizadas Editora Perú](https://diariooficial.elperuano.pe/normas/normasactualizadas) | Control negativo de alcance **limitado** | No identificada ley que cambie **directamente** el CPC después de 07/06/2025 *en muestras recuperadas*. **NO afirmar que no existe ninguna** |

## Verificaciones cruzadas de la ley límite

La [Ley 32377](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7) publicada en 07/06/2025 no solo modifica los arts.759 y 834 y deroga 835; contiene una **disposición complementaria final única** que ordena al Ejecutivo adecuar el TUO CPC en **90 días calendario** contados desde entrada en vigor. Conviene identificar **si se produjo una publicación de adecuación posterior** y comprobar si alteró el texto compilado o fue medida formal de consolidación, pues ese acto podría ser posterior a la ventana sin constituir nueva modificación legislativa. **Pendiente, no comprobado**.

## Protocolo reproducible antes de certificar

1. Enumerar las publicaciones oficiales por fecha de la ventana y consultar títulos y texto íntegro; recuperar disposiciones complementarias, transitorias, modificatorias, derogatorias y fe de erratas.
2. Distinguir `Código Procesal Civil / DL 768` de `Código Procesal Penal / DL 957`, `Código Civil / DL 295` y `Código Procesal Constitucional / Ley 31307`.
3. Por cada norma candidata, guardar **número**, **fecha de publicación**, **fecha de entrada en vigor** (puede diferir), **artículo CPC afectado**, **texto anterior/posterior**, **especialidad/temporalidad**, **fuente primaria** y **resultado de cotejo con `articles.json`**.
4. Documentar que un proyecto, dictamen, resolución administrativa o sentencia **no reescribe** directamente el articulado, aunque pueda afectarlo por otras vías.
5. Contrastar ediciones del TUO y normas especiales, supletoriedad y jurisprudencia; no certificar por silencio de buscadores.
6. Una vez revisada la ventana íntegra, volver a evaluar **JUR-016/017**, **JUR-031**, **JUR-037**, **JUR-040** y otras alertas jurídicas, y crear un acta de decisión por artículo.

## Medición de avance

| Indicador | Valor |
|---|---|
| Cotejos iniciales documentados | **862/862 (100%)** |
| Registros sin cotejo nuevo | **0** |
| Vigencia jurídica integral certificada a 10/11/2026 | **0/862** |
| Avance administrativo Fase 1 | **60% estimado, 40% restante** |

El 60% es la **estimación previa**, no una medición nueva de tareas ponderadas. Esta etapa crea trazabilidad y aumenta confianza en la clasificación de referencias, **pero no justifica cambiar el porcentaje de certificación ni afirmar que la auditoría está jurídicamente terminada**.

## Cambios y siguientes pasos

En esta etapa se creó el **CSV de evidencias** y el **presente informe MD** en `docs/audits`; sin cambio en `app/src/main/assets/articles.json` ni la matriz de vigencia individual. PR #54 permanece **abierto como borrador, sin fusionar**. Ningún AAB/APK, versionado Android ni actualización de Google Play.

**Etapa 43:** rastrear formalmente la adecuación del TUO ordenada por Ley 32377 y la posible promulgación del PL 9337/2024-CR; completar el inventario de leyes publicadas durante la ventana y verificar fe de erratas originales JUR-016/JUR-037. Solo tras eso, preparar decisiones de corrección aprobables.
