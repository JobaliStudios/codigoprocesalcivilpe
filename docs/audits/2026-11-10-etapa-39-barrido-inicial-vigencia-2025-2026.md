# Código Procesal Civil PE — Etapa 39: barrido inicial de vigencia post-TUO (corte 10/11/2026)

## Objeto y alcance

Cotejar los textos ya auditados con **disposiciones posteriores al 07/06/2025**, distinguiendo modificación directa de los artículos del CPC, normativa administrativa de aplicación, proyectos de ley y referencias secundarias.

**Ventana buscada:** 08/06/2025–10/11/2026. Se efectuaron búsquedas públicas por Código Procesal Civil / DL 768 / ley modificatoria / incorporación / derogación y fechas en: [buscador de normas de El Peruano](https://busquedas.elperuano.pe/), [normas actualizadas de Editora Perú](https://diariooficial.elperuano.pe/normas/normasactualizadas), repositorio normativo del Congreso y como contraste secundario [LP Derecho](https://lpderecho.pe/codigo-procesal-civil-actualizado/), última entrada consultada de mayo de 2026.

**Este barrido es documental inicial, NO es una certificación de ausencia de todas las normas posteriores.** Los buscadores por título y metadatos pueden omitir reformas indirectas en disposiciones complementarias, fe de erratas, normas especiales o textos no indexados. La fecha legal es distinta de la fecha de recrawling de un buscador. Se mantiene `NO_CERTIFICADA` en la matriz.

## Resultados de normas y estados

| Referencia | Publicación | Tipo y consecuencia | Estado |
|---|---|---|---|
| [Ley 32377](https://busquedas.elperuano.pe/dispositivo/NL/2407453-7) | **07/06/2025**, anterior a ventana | Modifica CPC **759** y **834**, deroga **835** y modifica Ley 26662 art.6; actualización de sucesión intestada. La app `main` incorpora el contenido vigente publicado. | **Comprobado en app, ya recogido por TUO 2025**. No tratar como reforma posterior ni nuevo hallazgo |
| [RA 000481-2025-CE-PJ](https://busquedas.elperuano.pe/dispositivo/NL/2472911-1) | **31/12/2025**, dentro de ventana | Establece **URP 2026 = S/550** y aprueba cuadro aranceles 2026, relacionado con aplicación práctica de multas, tasas, cuantías del CPC. | **Norma administrativa publicada confirmada**, **NO** cambia el literal de los artículos del CPC |
| [Proyecto de Ley 9337/2024-CR: dictamen](https://comunicaciones.congreso.gob.pe/noticias/comision-de-justicia-aprueba-dictamenes-sobre-pension-de-alimentos-y-derechos-ciudadanos/) | **06/05/2026**, dentro de ventana | Comisión de Justicia del Congreso aprobó dictamen para fortalecer la ejecución de actas de conciliación alimentaria. | **Proyecto/dictamen**; **no constan en esta búsqueda elementos suficientes para afirmar promulgación/publicación como ley**. NO aplicar texto propuesto a la app |
| [LP Derecho, edición CPC 2026](https://lpderecho.pe/codigo-procesal-civil-actualizado/) | **12/05/2026** según resultado indexado | Identifica Ley 32377 como última modificación directa conocida de CPC. La misma página contiene un detalle cronológico contradictorio sobre el día de publicación; prevalece fuente primaria oficial 07/06/2025. | **Secundaria, apoyo no concluyente** para el período mayo-noviembre 2026 |
| Otras coincidencias por “Código Procesal Penal” | Varias, incluso noviembre 2026 | No son CPC del DL768. | **Excluidas por falso positivo** |

**Resultado provisional:** dentro de fuentes consultadas **no se identificó reforma directa promulgada al CPC con fecha posterior al 07/06/2025** hasta el 10/11/2026. Esto NO permite declarar que no existió ninguna, ni certificar en bloque la vigencia de sus 862 artículos. La comprobación debe continuar con fuentes legislativas completas, disposiciones finales/derogatorias y publicación diaria oficial.

## Pruebas de regresión en artículos 759, 834 y 835

Se inspeccionó el texto vivo del repositorio `main:app/src/main/assets/articles.json` contra el texto auténtico HTML de Ley 32377 en El Peruano:

- **759**: mantiene párrafo añadido que dispensa notificación al Ministerio Público en sucesión intestada, salvo menores de edad o Consejo de Familia previamente constituido.
- **834**: mantiene plazo de **quince días** para solicitar inclusión de heredero y la posibilidad de resolver sin audiencia si transcurre plazo sin apersonamiento/contradicción.
- **835**: figura expresamente **[Derogado]** por Ley 32377.

Estos controles refuerzan la integridad de una reforma preexistente, no cuentan como nueva legislación ni como nuevas filas auditadas.

## Efecto práctico de la URP 2026

La RA N.º 000481-2025-CE-PJ fija **S/ 550 por URP en 2026**. Para ejemplificar la interpretación del CPC, sin sustituir el cuadro de aranceles ni las excepciones legales:

- 10 URP = S/ 5,500
- 20 URP = S/ 11,000
- 50 URP = S/ 27,500
- 100 URP = S/ 55,000
- 500 URP = S/ 275,000
- 1,000 URP = S/ 550,000

La app contiene múltiples umbrales expresados en URP, pero **no se ha demostrado que muestre importes calculados o desactualizados en soles**. No alterar sus artículos. Si se decide añadir una tabla informativa anual, requerirá fuente oficial fechada, actualización anual y autorización de producto.

## Priorización de las alertas existentes para decisiones de corrección

| Prioridad | Hallazgo | Acción siguiente |
|---|---|---|
| Alta | **JUR-016/017**, art.648: `embargado` vs `embargo` y cita del expediente TC | Cotejar fuente primaria y redactar diff mínimo sin tocar producción; requiere aprobación |
| Alta | **JUR-031**, art.408: ambigüedad de numerales tras DL1384 | No preferir interpretación doctrinal a publicación original; contrastar legislador y compilado |
| Alta | **JUR-037**, art.391: `hubiera tenido tuvo` | Examinar facsímil y fe de erratas Ley31591; anomalía compartida no se corrige unilateralmente |
| Media | **JUR-040**, art.688: `cambiaría` por `cambiaria` | Propuesta ortográfica sobre término técnico, pendiente validación y autorización |
| Media/alta | **JUR-041/042**, arts.719/523-A | Concordancia de régimen de arbitraje DL1071 y expropiación DL1192; evitar declarar aplicación general sin fundamento |
| Media | **JUR-043**, art.815: `los costas` | Revisar texto legislativo original/fe de erratas; evitar edición libre de texto legal |

## Métricas, restricciones y siguiente etapa

**862/862 artículos** tienen revisión documental inicial con distintos niveles de profundidad; **0/862** poseen certificación total de vigencia al 10/11/2026. La matriz mantiene `NO_CERTIFICADA` en los 862. La estimación administrativa previa de Fase 1 continúa provisionalmente en 60% y no se equipara ni al 100% de primera pasada ni a certificación de vigencia.

El PR #54 continúa **abierto, en borrador y sin fusionar**. Solo documentación `docs/audits`. **Sin modificación** de `app/src/main/assets/articles.json`, `strings.xml`, metadatos Android, AAB/APK ni Google Play.

**Etapa 40:** iniciar revisión específica de **fuentes originales y fe de erratas** de JUR-016/017, JUR-031, JUR-037 y JUR-040; preparar un paquete de propuestas jurídicas claramente delimitadas y sin aplicación automática. Continuar verificación de publicaciones 2025–2026 para evitar falsos negativos y revisar efectos de leyes especiales.
