# Auditoría jurídica — Vigesimotercer lote y cierre de los 135 cotejos contextuales (10/11/2026)

**Ámbito:** artículos **829, 836, 844 y 847** de `main:app/src/main/assets/articles.json` cotejados contra el [TUO de Editora Perú, actualización al 07/06/2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007) y, para 844, [Decreto Legislativo 1384 original, publicación 04/09/2018](https://busquedas.elperuano.pe/dispositivo/NL/1687393-2). Revisión de páginas impresas **88, 89 y 90** (índices PDF 87, 88 y 89). La captura del índice PDF 89 apareció en blanco; la verificación textual de esa página se realizó mediante su capa indexada y publicación original del DL 1384. **No se acredita vigencia al 10/11/2026 únicamente por cotejar la edición de 2025.**

| Artículo | Cotejo completo | Resultado |
|---|---|---|
| 829 — Trámite especial | Errores manifiestos en partidas y rectificación de plano sin trámite 754; TUO pp.88-89 | Coincide |
| 836 — Ejecución | Aplicación del artículo 762 una vez firme declaración de herederos; TUO p.89 | Coincide |
| 844 — Solicitante con discapacidad | Requisitos a) y b) coinciden; diferencia en el verbo introductorio: app «sean» versus TUO y DL1384 original «sea»; TUO p.90 | Variante editorial, pendiente |
| 847 — Contenido de la resolución final | Apoyos, actos, duración, salvaguardias, inscripción y formato de lectura fácil; TUO p.90 | Coincide |

## JUR-030 — Artículo 844, divergencia de concordancia (MEDIA, SIN APROBACIÓN)

- **App:** `En el caso de que las personas solicitantes sean una persona con discapacidad:`
- **TUO 2025 y DL 1384 original:** `En el caso de que las personas solicitantes sea una persona con discapacidad:`
- **Análisis:** la publicación original contiene una discordancia gramatical y la app ajusta el verbo al sujeto plural, sin resolver la incongruencia nominal `personas... una persona`. No corresponde tratar la redacción original como cambio jurídico sustantivo, ni sustituirla automáticamente. Solicitar aprobación explícita para reproducir la fuente primaria, explicar la divergencia en una nota, o dejar la versión local; valorar accesibilidad y fidelidad legal.

## Cierre del grupo contextual, pero NO cierre de Fase 1

Se actualizaron las últimas **4** filas de los **135** casos con `SIN_RESOLUCION_DOCUMENTADA`: el grupo alcanza **135/135 (100%) con cotejo contextual documentado** respecto al TUO 2025. Quedan **0** en estado `SIN_COTEJO_INTEGRAL_NUEVO` dentro del grupo. **Esto NO constituye una auditoría completa de los 862 registros**: la matriz conserva comparaciones parciales heredadas, notas editoriales pendientes, artículos derogados y decisiones legales por validar.

**862/862 registros mantienen `vigencia_2026_11_10=NO_CERTIFICADA`.** El porcentaje global de Fase 1 sigue siendo una estimación de gestión del **60%** hasta reevaluar la cobertura restante y verificar posibles normas posteriores a junio de 2025. JUR-001 (recotejo completo y vigencia), JUR-002 (trámite de reforma alimentaria) y propuestas editoriales JUR-003 a JUR-030 continúan abiertas según sus expedientes; el cierre de una subcola no implica aprobar correcciones.

## Próxima línea de trabajo

1. Inventariar los registros **con igualdad normalizada parcial**, los rangos derogados y las variantes documentadas; definir un plan de cotejo completo por bloques y criterios objetivos de aceptación.
2. Identificar y comprobar **normas modificatorias promulgadas y en vigor hasta el 10/11/2026** usando El Peruano, SPIJ y textos primarios. No deducir vigencia de noticias, dictámenes ni páginas secundarias.
3. Cerrar discrepancias prioritarias con evidencias literales y **aprobación individual** antes de cambiar producción.
4. Solo después realizar regresión funcional, compilar y preparar publicación Android/Google Play.

**Garantía:** solo documentación; no se modifica `app/src/main/assets/articles.json`, `strings.xml`, la fecha visible, los metadatos de versión ni se genera APK/AAB. PR #54 continúa abierto como borrador sin fusionarse.
