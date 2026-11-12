# Código Procesal Civil PE — Fase 5: crecimiento y aprendizaje

**Inicio:** 12 de noviembre de 2026.  
**Estado:** en ejecución, Misión 1 en PR independiente.  
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
