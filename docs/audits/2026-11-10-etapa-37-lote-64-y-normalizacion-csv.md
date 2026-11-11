# Auditoría jurídica CPC PE — Etapa 37 y reparación de matriz (10/11/2026)

## Lote de ejecución, remates y no contenciosos

Se registraron **64 artículos pendientes del intervalo 705–785**, en `main:app/src/main/assets/articles.json`, mediante cotejo documental dirigido con el [TUO oficial del Código Procesal Civil de Editora Perú (actualización 07/06/2025)](https://diariooficial.elperuano.pe/Normas/obtenerDocumento?idNorma=120007), pp.78–85 aproximadamente (paginación impresa). Las observaciones reflejan lectura de texto indexado y cotejo de pasajes prioritarios, **no un cotejo exhaustivo palabra por palabra ni verificación de modificaciones posteriores a junio 2025**.

**Relación de registros cotejados:** 706, 707, 708, 709, 710, 711, 712, 713, 714, 715, 717, 719, 720, 721, 722, 723, 724, 725, 726, 728, 729, 730, 732, 733, 734, 735, 736, 737, 738, 739, 740, 741, 742, 744, 747, 750, 751, 752, 753, 754, 756, 757, 758, 761, 763, 764, 765, 766, 767, 769, 770, 771, 772, 773, 774, 775, 776, 777, 778, 779, 781, 782, 783 y 784. Ningún registro previo fue contado dos veces.

**Clasificación:** 53 cotejos dirigidos ordinarios sin diferencias normativas relevantes advertidas en pasajes revisados; 2 corroboraciones de derogación (arts. **713 y 714**, DL 1069); 9 controles especiales (arts. **719, 733, 735, 736, 742, 750, 753, 754, 781**). No se acredita vigencia integral al 10/11/2026.

## Control de riesgos y concordancias

- **709:** cumplimiento de formalización de documentos, plazo de 3 días.
- **711:** deshacer obligación de no hacer, plazo de 10 días.
- **719 — JUR-041:** aplicación y TUO conservan referencia a **Ley General de Arbitraje**, originalmente Ley 26572, frente al régimen posterior del DL 1071. Es una **concordancia normativa**, no una discrepancia de transcripción confirmada. No editar sin norma primaria.
- **733:** publicidad del remate: tres días para muebles y seis días para inmuebles; estudiar conjuntamente régimen de remate electrónico REM@JU y modificación del art.731 por Ley 32297.
- **735:** depósito de postor, al menos 10% del valor de tasación en supuesto regulado.
- **736:** precio base de dos terceras partes del valor de tasación.
- **742:** reducciones progresivas de 15% y 15% adicional según convocatoria.
- **750:** hasta 50 URP para determinados asuntos no contenciosos ante Paz Letrado.
- **753:** contradicción dentro de 5 días.
- **754:** audiencia dentro de 15 días, con excepción legal.
- **781:** concordancia con DL 1384 sobre representante/apoyo de adoptado mayor de edad.

El art.731 ya había sido auditado en etapas anteriores y no se duplica.

## JUR-DATA-001 — Incidencia de integridad del CSV, subsanada técnicamente

La matriz original contenía **862 filas**; no obstante, el análisis con un lector CSV estándar identificó **446 registros** con anchura distinta de las 15 columnas esperadas, originados principalmente por comillas y comas mal escapadas en observaciones históricas o sumillas. El archivo requería reconstrucción estructural, ya que una lectura simple por líneas podía dar una falsa impresión de integridad.

La reparación utiliza los **títulos canónicos de artículos en el archivo de producción** para reconstruir `sumilla_local`, conserva los cuatro campos de triaje desde el estado identificado, recupera los seis campos terminales (prioridad, fuente, fecha, vigencia, modificación y aprobación), preserva los valores de `literal_local_breve` y `literal_fuente_breve` y agrupa fragmentos sobrantes de coma/comillas en `detalle`. Reemite **todo el CSV con escapado estándar** y comprueba mediante parseo de estado que:
- hay **862 filas de datos + 1 cabecera**;
- las **862 filas tienen 15 campos exactos**;
- `vigencia_2026_11_10=NO_CERTIFICADA` en **862/862**;
- `cotejo_2026_11_10=SIN_COTEJO_INTEGRAL_NUEVO` en **51/862**.

**Limitación:** la recuperación sintáctica recompone observaciones legibles, pero **no es una certificación semántica línea por línea de los 862 comentarios**. Algunas comillas internas o signos históricos pueden requerir limpieza editorial adicional. Se mantienen intactos los estados jurídicos y el contenido de producción.

## Estado acumulado

- **811/862 (94,08%)** registros cuentan con cotejo nuevo documental dirigido o confirmación de derogación.
- **51/862** están pendientes de cotejo nuevo; incluyen artículos ordinarios restantes y artículos con sufijo A.
- **862/862** continúan sin vigencia 2026 certificada, pese a los cotejos.
- La estimación administrativa antigua de Fase 1 permanece en 60% sin revisión sustantiva de la legislación posterior al corte.
- PR #54 abierto, borrador, no fusionado. No cambiar producción, `articles.json`, `strings.xml`, versionado Android, APK/AAB o Play Store sin aprobación.

## Próxima revisión

Auditar los **51 restantes**, especialmente los artículos **786–846** y los artículos con sufijo **119-A, 523-A y 565-A**. Luego hacer una revisión del corpus 08/06/2025–10/11/2026, confirmar anomalías prioritarias JUR-016/017 (648), JUR-031 (408), JUR-037 (391) y JUR-040 (688), y pasar a fase de propuestas de corrección con autorización explícita.
