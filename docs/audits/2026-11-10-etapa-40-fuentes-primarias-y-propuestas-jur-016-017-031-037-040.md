# Código Procesal Civil PE — Etapa 40: cotejo de fuentes primarias y propuestas de corrección (10/11/2026)

**Objeto:** resolver el origen textual de JUR-016, JUR-017, JUR-031, JUR-037 y JUR-040. Solo documentación y propuesta; **no se modifica** `app/src/main/assets/articles.json`, recursos Android ni se publica versión.

## Resumen ejecutivo de hallazgos

| Id | Art. | Evidencia nueva | Resultado / política de cambios |
|---|---|---|---|
| **JUR-016** | **648(6)** | Ley 26599 **original** publicada el 24/04/1996 dice **«el embargado procederá»**, igual que app; el TUO de Editora Perú de 2025 escribe **«el embargo procederá»**. | **Rectificación de auditoría previa:** NO clasificar como error de app probado frente a norma original; investigar discrepancia de fuentes, fe de erratas y eventuales normas posteriores. **CAMBIO SUSPENDIDO**. |
| **JUR-017** | **648, nota TC** | Sentencia del TC del 30/01/1997 identifica expediente **006-96-AI/TC** (Lima); app pone **006-97-AI-TC**. | **Error documental confirmado**. Preparar corrección exacta del identificador tras autorización. |
| **JUR-031** | **408(2)** | Artículo 4 del DL 1384 original **modifica expresamente el numeral 2** del artículo 408 y redacta referencia a interdicción/tutor/curador/apoyo. | Las listas 1 y 2 parecen duplicadas en app y TUO, pero no cabe atribuir la modificación al numeral 1 por inferencia. **SIN CAMBIO NORMATIVO** hasta dictamen competente. |
| **JUR-037** | **391(4)** | Texto Consolidado de Ley 31591 en Archivo Digital del Congreso y TUO Editora Perú incluyen **«hubiera tenido tuvo»**, igual que app. La captura visual del PDF del Congreso lo confirma. | Anomalía de fuente compartida, **no errata exclusivamente local**. Falta cotejo del facsímil de publicación y fe de erratas, **SIN CAMBIO**. |
| **JUR-040** | **688(4)** | Decreto Legislativo 1069 original, en la separata oficial del 28/06/2008, usa **«acción cambiaria»**; la app escribe **«acción cambiaría»**. | **Error ortográfico de término jurídico confirmado** contra fuente primaria. Propuesta mínima: eliminar tilde; **PENDIENTE DE AUTORIZACIÓN**. |

## Fuentes originales y pasajes

### 1) Artículo 648: corrección de nuestro criterio sobre el error

La [publicación original de **Ley 26599** en el Diario Oficial *El Peruano*, 24/04/1996](https://www.leyes.congreso.gob.pe/documentos/Leyes/26598.pdf), aparece en el **mismo cuadernillo, página impresa 139104**, aunque el PDF agrupado por Congreso tenga nombre `26598.pdf`. En su art.1, numeral 6, el texto literal sobre obligaciones alimentarias dice:

> Cuando se trata de garantizar obligaciones alimentarias, **el embargado procederá** hasta el sesenta por ciento del total de los ingresos...

Esto coincide con la app. En cambio, el [TUO consolidado de Editora Perú de 07/06/2025](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007) presenta **el embargo procederá**. La auditoría anterior decía incorrectamente que la Ley 26599 original decía *embargo*. **Se rectifica expresamente** esa aseveración; no debe perpetuarse en propuestas ni corregirse automáticamente el literal, pues debe verificarse si medió corrección legal posterior.

La [**sentencia original del Tribunal Constitucional**](https://www.tc.gob.pe/jurisprudencia/1996/00006-1996-AI.pdf), página 1, identifica **Exp. N.º 006-96-AI/TC**, Lima; fecha 30/01/1997. La app tiene número erróneo **006-97-AI-TC**. No se cambia el estado **[Inconstitucional]** del inciso 1 ni ningún límite del numeral 6.

**Propuesta para aprobación, exclusivamente JUR-017:**
```diff
- Expediente 006-97-AI-TC LIMA
+ Expediente 006-96-AI/TC LIMA
```

**JUR-016: NO proponer reemplazo literal** por ahora.

### 2) Artículo 408: el DL 1384 dice numeral 2

El [DL 1384 original, 04/09/2018](https://busquedas.elperuano.pe/dispositivo/NL/1687393-2) incluye en su artículo 4 los cambios al CPC y, respecto al 408, prescribe textualmente **«2.- La que declara la interdicción y el nombramiento de tutor, curador o designación de apoyo»**. Se verifica por fuente primaria, sin deducir que era numeral 1.

La presencia de un numeral 1 similar (interdicción y nombramiento de tutor o curador) en compilación y app puede parecer redundante, pero **no autoriza a cambiar el alcance de la consulta** unilateralmente. Revisar fe de erratas, acuerdos de corte y comentarios doctrinales antes de resolver si debe existir nota editorial. Estado jurídico sigue NO_CERTIFICADA.

### 3) Artículo 391: duplicación en archivo legal del Congreso

En el [Archivo Digital del Congreso, Texto Consolidado de **Ley 31591** (26/10/2022)](https://leyes.congreso.gob.pe/Documentos/2021_2026/ADLP/Texto_Consolidado/31591-TXM.pdf), numeral 4 de art.391, la frase **«hubiera tenido tuvo»** aparece tanto en la capa de texto como en la imagen de la página correspondiente (captura visual página PDF índice 4). Esta reproducción legal coincide con la app y TUO 2025.

No consta en estas comprobaciones fe de erratas definitiva ni cotejo de la separata facsimilar de publicación original. Se mantiene el texto exacto hasta resolver fidelidad normativa; si se pidiera una versión más legible, se deberá valorar nota aclaratoria en interfaz separada de la norma, con aprobación.

### 4) Artículo 688: errata de la aplicación plenamente corroborada

La [separata original de 28/06/2008 de *El Peruano* alojada por SUNAT](https://www.sunat.gob.pe/legislacion/nor_graf/2008/junio/nl20080628.pdf) reproduce el texto modificatorio del DL 1069, **artículo 688**, inc. 4: **«acción cambiaria»**. El [Sistema Peruano de Información Jurídica (SPIJ) de MINJUSDH](https://spij.minjus.gob.pe/Normas/textos/280608T.pdf) coincide, al igual que TUO 2025.

En la app: **«acción cambiaría»**, que es distinta forma verbal. Es una errata textual contrastada con publicación primaria.

**Propuesta para aprobación, exclusivamente JUR-040:**
```diff
- Los Títulos Valores que confieran la acción cambiaría,
+ Los Títulos Valores que confieran la acción cambiaria,
```
No modificar el inciso 5 ni otros títulos.

## Paquete de decisiones, sin aplicación

**Listas para aprobación del propietario, con diferencia exacta:** **JUR-017 (648, cita TC) y JUR-040 (688, tilde)**. Están contrastadas con fuentes originales y pueden formar una corrección mínima en la siguiente fase **si el propietario la autoriza**.

**No aprobadas y requieren investigación adicional:** **JUR-016 (648 palabra embargado/embargo), JUR-031 (408 numerales), JUR-037 (391 anomalía legislativa compartida)**.

Las decisiones sobre corrección se restringen a dos sustituciones literales y no alteran plazos, competencias, porcentajes, tarifas, anexos ni la presentación completa del articulado.

## Integridad y estado

Se actualizaron únicamente **4 filas** de la matriz (391, 408, 648 y 688) para rectificar/sustentar hallazgos; JUR-017 se documentó junto con JUR-016 en fila 648. El CSV mantiene **862 registros de 15 campos**, **862/862** `vigencia_2026_11_10=NO_CERTIFICADA`, y **0** registros con `SIN_COTEJO_INTEGRAL_NUEVO`. Ningún artículo tiene certificación legal integral a 10/11/2026; el barrido de reformas 2025–2026 sigue siendo inicial/no exhaustivo.

El PR #54 sigue abierto en borrador, sin fusionar; exclusivamente documentación `docs/audits`. **No se modifica producción**, ni metadatos Android, APK/AAB o Google Play.

## Etapa 41 recomendada

Completar barrido legislativo 08/06/2025–10/11/2026 e investigación de fe de erratas (Ley 26599, Ley 31591), discriminar modificaciones legales de jurisprudencia, verificar concordancias DL 1384, DL 1192 y DL 1071. Tras aprobación expresa, preparar PR separado de código para sustituciones JUR-017 y JUR-040 y realizar pruebas de regresión y publicación solo cuando corresponda.
