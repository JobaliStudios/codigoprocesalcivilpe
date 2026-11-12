# Código Procesal Civil PE — Fase 5: crecimiento y aprendizaje

**Inicio:** 12 de noviembre de 2026.  
**Estado:** Misión 2 con objetivo mínimo de 120 preguntas integrado; Misión 3 en desarrollo sin publicación.  
**Versión de desarrollo:** `1.08 (versionCode 13)`; **no publicar ni distribuir como actualización**.

## Objetivo

Aumentar el valor educativo, la participación y el descubrimiento de la aplicación sin sacrificar la exactitud jurídica, privacidad ni estabilidad logradas en las fases 1–4.

## Auditoría inicial de preguntas (datos reales de `questions.json`)

- **31 preguntas**, con **30 artículos distintos** como referencias.
- Artículos referenciados concentrados entre el **4 y el 42**, la mayoría sobre competencia. El artículo 6 aparece en dos preguntas.
- La sesión por defecto ofrece hasta **10 preguntas**, seleccionadas sin repetir; ya existen modos Todos, por Sección, Favoritos, Errores y Repaso rápido.
- Ya se registran errores para repaso local mediante IDs SHA-256 derivados de cada pregunta: editar el enunciado, opciones, índice de respuesta o referencia cambia ese ID y podría desvincular errores almacenados.
- El formato actual de pregunta usa cuatro campos: `questionText`, `options`, `correctAnswerIndex` (base 0) y `relatedArticle`.
- **No equivale a 31 respuestas jurídicamente verificadas**: una referencia que existe en el corpus no garantiza que su interpretación sea correcta ni que la norma siga vigente.

### Riesgo principal
Agregar decenas de preguntas sin revisión jurídica documentada puede enseñar respuestas incorrectas o basadas en textos obsoletos. La ampliación del banco será progresiva y auditada; no debe confundirse con la auditoría documental con salvedades de la Fase 1.

## Misiones

| Misión | Alcance y entrega mínima | Criterio de aceptación |
|---|---|---|
| **1. Calidad del banco** | Auditoría base y validador automático del JSON, respuestas, duplicados y referencias reales del CPC; pruebas locales y CI | Ningún cambio al banco original; CI y regresión de sesiones verde |
| **2. Ampliación a 120–200 preguntas** | Lotes manejables de preguntas sobre diversas secciones, cada una con artículo preciso, respuesta única y revisión de fuente primaria | Revisión legal del lote y pruebas; no incorporar preguntas únicamente por cantidad |
| **3. Aprendizaje y participación** | Mejoras en explicaciones, repaso de errores y avance educativo apoyándose en los modos existentes; evitar duplicar funciones | Pruebas de sesiones, errores, recreación y accesibilidad |
| **4. Valoración y métricas responsables** | Diseñar solicitud de reseña ocasional y optativa, y métricas mínimas, sin seguimiento silencioso ni incumplir declaraciones de Play | Consentimiento y política de privacidad comprobados; no introducir SDK por defecto |
| **5. Descubrimiento y continuidad** | Experimentos de ficha de Play, web informativa, proceso periódico de verificación jurídica y cierre de resultados | Ninguna publicación ni cambio de ficha sin autorización explícita |

## Misión 1 — Puerta de calidad que se añade al CI

Se ejecutan dos controles complementarios:

1. `python3 tools/validate_quiz_bank.py`: valida JSON, campos, de 2 a 6 opciones no vacías ni duplicadas, índice de respuesta correcto, enunciados únicos por artículo y que cada `relatedArticle` apunte a un número presente en `assets/articles.json`. No usa bibliotecas adicionales.
2. `QuestionBankIntegrityTest` (Robolectric): carga el banco como lo hace la app y confirma que cada pregunta resuelve su artículo, sección y un ID de repaso único. Además valida que la selección aleatoria conserve ese ID estable.
3. `tools/test_validate_quiz_bank.py`: comprueba que el validador rechaza duplicados, índices inválidos, artículos inexistentes y formatos incorrectos.

Esto **no cambia** `questions.json`, las respuestas existentes, las claves de errores/favoritos, la selección ni los textos jurídicos. Debe ejecutarse sin problemas con el contenido actual.

## Procedimiento editorial obligatorio para nuevos lotes

Antes de agregar preguntas en la Misión 2:

1. Elegir artículos y áreas temáticas con escasa cobertura; no concentrar todo el banco en los primeros 42 artículos.
2. Registrar la **fuente normativa primaria**, enlace exacto, fecha de consulta y situación del artículo (vigente, modificado, derogado o pendiente de cotejo).
3. Redactar enunciado y opciones inequívocas, con una única respuesta correcta, sin extrapolar más allá de la norma.
4. Revisar juridicamente cada pregunta y documentar responsable, evidencia y fecha de validación antes de integrarla. Si hay duda, **no incluir** la pregunta en la app.
5. Comprobar que su número existe en el corpus y que la nueva explicación no contradiga el texto disponible. Si el contenido jurídico está desactualizado, gestionarlo por auditoría jurídica separada.
6. Ejecutar el CI, verificar modos de cuestionario, conservación de IDs de preguntas previas y prueba manual antes de aprobar cada lote.

### Criterios y límites

- Objetivo de producto: **120–200 preguntas**, sin fecha de lanzamiento comprometida.
- No convertir el objetivo numérico en un requisito que obligue a inventar preguntas.
- No introducir dependencia de red para responder cuestionarios; mantener el banco disponible sin Internet.
- No añadir rastreadores, publicidad ni solicitudes de valoración en esta primera misión.
- No modificar `applicationId`, `versionCode`, `versionName`, UMP ni Google Play.
- El lanzamiento de 1.08 seguirá siendo un proceso separado, sujeto a pruebas firmadas y aprobación del propietario.

## Pruebas manuales de Misión 1

- [ ] Entrar a Cuestionarios y comenzar una sesión de 10 preguntas.
- [ ] Responder algunas incorrectamente, salir y volver a «Mis errores».
- [ ] Probar selección de Sección y de Favoritos; comprobar que las preguntas abren el artículo asociado.
- [ ] Confirmar que no cambió el número de preguntas, sus textos ni la puntuación.
- [ ] Verificar que la app funciona sin conexión durante la práctica.

## Misión 2 — Primer lote de ampliación (en PR)

Se han preparado **32 preguntas adicionales**, sin modificar las **31 originales**, para un total de **63 preguntas y 62 artículos distintos**. Están distribuidas por las cinco secciones que antes no tenían cobertura. Las preguntas nuevas se han cotejado con el TUO del CPC del Diario Oficial El Peruano actualizado al **7 de junio de 2025**, y los artículos 759 y 834 con la Ley N.° 32377. **No se certifica por ello la vigencia normativa hasta noviembre de 2026**. Para el inventario de artículos, fuentes, respuestas y comprobaciones pendientes, consultar [lote 1 — 32 preguntas](2026-11-fase5-lote1-32-preguntas.md).

La meta de 120–200 preguntas requiere lotes adicionales, sin alterar las IDs de errores de preguntas originales ni publicar prematuramente la versión 1.08.

## Misión 2 — Segundo lote de 32 preguntas (fusionado en main, PR #68)

El segundo lote amplía el banco de **63 a 95 preguntas** y de **62 a 94 artículos diferentes**. La ampliación cubre cinco secciones adicionales a la Primera y mantiene el acceso por sección, favoritos y repaso de errores; las **63 preguntas preexistentes quedan congeladas** mediante un nuevo baseline automático. El detalle de las 32 respuestas, referencias y límites jurídicos se documenta en [Lote 2 — 32 preguntas](2026-11-fase5-lote2-32-preguntas.md).

**Avance histórico al cerrar Lote 2:** 95/120 = **79,2 %**, entonces quedaban 25 preguntas. La meta ampliada de 200 requiere 105 preguntas adicionales. La revisión de vigencia normativa posterior al TUO oficial de junio de 2025 y una discrepancia textual identificada en el artículo 551 permanecen como asuntos jurídicos de revisión independiente, **no resueltos ni publicados**.

Esta adición **no cambia** el corpus del CPC ni la versión candidata 1.08.

## Misión 2 — Tercer lote de 25 preguntas (fusionado, PR #69)

Se incorporaron **25 preguntas nuevas**, de forma que el banco integrado en `main` pasa de **95 a 120 preguntas** y de **94 a 119 artículos distintos**. Se amplían las seis secciones del CPC (I: 3, II: 4, III: 6, IV: 4, V: 5 y VI: 3), con conceptos cotejados frente al TUO oficial compilado al 07/06/2025 y `assets/articles.json`; ver [registro editorial de Lote 3](2026-11-fase5-lote3-25-preguntas.md).

**Preservación:** las 95 entradas anteriores quedan inalteradas y protegidas mediante `tools/quiz_bank_legacy_95.json`, manteniéndose los baselines anteriores de 31 y 63 y las IDs de repaso. Se añaden pruebas del banco de **120 preguntas / 119 artículos**, cobertura de secciones y persistencia del repaso de errores.

**Estado:** meta mínima de **120 preguntas integrada en `main` mediante PR #69**. Continúan pendientes la verificación jurídica temporal posterior a junio de 2025 y cualquier prueba física restante antes de publicar. La versión candidata 1.08 aún no se publica en Google Play.

La Misión 2 alcanzó su meta cuantitativa y queda condicionada a las verificaciones jurídicas y de publicación descritas. Quedan **80 preguntas adicionales** si se decidiera perseguir la meta ampliada de 200; no son un requisito de cierre de la meta mínima.

## Misión 3 — Lote 1: aprendizaje guiado y revisión de errores (fusionado, PR #70)

Se propone separar **revisión detallada** y **nueva práctica** de errores, y permitir la consulta directa del artículo CPC tras responder sin abandonar la sesión. Un acierto sobre una pregunta ya registrada en errores la resuelve en cualquier modo de práctica. El detalle, los límites jurídicos y las comprobaciones están en [Misión 3 / Lote 1](2026-11-fase5-mision3-aprendizaje-guiado-lote1.md).

El cambio reutiliza el lector, los resultados y el almacén local; no incorpora un nuevo sistema de métricas, red, contenido normativo, anuncios ni dependencias. **Fusionado mediante PR #70**; las pruebas físicas y la aceptación final siguen siendo obligaciones previas a cualquier publicación. La Misión 3 continúa abierta.

## Misión 3 — Lote 2: refuerzo por sección (fusionado, PR #71)

La pantalla final puede sugerir la **sección con mayor cantidad de errores de la sesión** y ofrecer una práctica de esa sección con el selector existente. Los empates siguen el orden del CPC; en caso de sesión perfecta o artículos no resolubles no se muestra recomendación. Este cálculo es efímero y no crea historial nuevo ni una medición de rendimiento global. Ver [registro de Lote 2](2026-11-fase5-mision3-lote2-refuerzo-por-seccion.md).

Sin alteraciones de las 120 preguntas, textos legales, anuncios, preferencias, versión candidata 1.08 ni Google Play. **Fusionado en `main` mediante PR #71;** conserva pendientes las comprobaciones físicas previas al lanzamiento.

## Misión 3 — Lote 3: personalizar sesiones (PR pendiente)

El usuario podrá elegir **5, 10 o 20 preguntas** antes de empezar cualquiera de los modos existentes. La configuración inicial sigue siendo 10; la selección se conserva al recrear la pantalla y al repetir la ronda. Con pocas preguntas disponibles no se generan duplicados. Se corrige además la vista previa de preguntas disponibles en Favoritos y Errores, que anteriormente se limitaba artificialmente a diez.

La funcionalidad, pruebas y criterios de aceptación se describen en [Misión 3 / Lote 3](2026-11-fase5-mision3-lote3-sesiones-personalizadas.md). No altera el banco de 120 preguntas, la normativa, los identificadores ni los datos permanentes de los estudiantes. **Pendiente de CI, revisión y fusión; sin publicación Google Play.**
