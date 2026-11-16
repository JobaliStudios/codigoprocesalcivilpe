# Fase 6 — Misión 3: glosario editorial opcional

## Resultado

Se implementa la infraestructura **offline** del glosario sin añadir contenido
editorial jurídico no aprobado. El archivo versionado de producción es
\`app/src/main/assets/glossary.json\`, con el contenido inicial
\`{"version": 1, "terms": []}\`. No se descarga ninguna definición.

### Esquema editorial

Cada término aprobado debe contener los campos \`id\`, \`term\`, \`aliases\`,
\`definition\`, \`articles\`, \`related\`, \`source\`, \`reviewedBy\` y
\`reviewedAt\` (fecha ISO \`YYYY-MM-DD\`). El \`id\` es estable en kebab-case.
\`articles\` referencia números reales del corpus; \`related\` enlaza IDs de
otros términos. El validador Python no permite un término sin fuente, revisor o
fecha y rechaza duplicados y artículos desconocidos. Este validador comprueba
**estructura, no exactitud jurídica**. Las pruebas utilizan fixtures falsos
que **no** son parte de la compilación de producción.

**Protocolo editorial:** el propietario debe aportar y aprobar expresamente
las definiciones, la fuente verificable y la fecha de revisión antes de que se
reemplazara \`terms: []\` en un PR futuro. No se incluyen ejemplos editoriales
en el asset de producción.

### Integración

- \`GlossaryRepository\` usa Gson \`@SerializedName\`; caché de proceso,
  lectura del asset una sola vez y degradación segura ante archivo corrupto.
  Un glosario vacío no dispara reconocimiento de texto ni crea spans.
- \`GlossaryMatcher\` reutiliza \`SearchTextNormalizer\`, resuelve mayúsculas,
  acentos y límites de palabra, prioriza términos más largos, evita
  solapamientos y sólo marca una aparición por término dentro de cada artículo.
  Se restringen coincidencias al texto íntegro del artículo, sin cruzar bloques.
- \`GlossaryTextDecorator\` añade \`GlossaryTermSpan\` con el color primario de
  la paleta y subrayado sin tocar el texto legal. Excluye los rangos que tienen
  enlaces jurídicos, historial normativo o resaltados del usuario.
- \`HighlightController\` conserva prioridad de referencias e historial
  normativo; a continuación abre el glosario y por último los resaltados.
- \`GlossaryBottomSheet\`: aviso de naturaleza **editorial**, fuente, revisor,
  artículos de origen, términos relacionados, copia y apertura de un índice.
  \`GlossaryListBottomSheet\`: buscador de términos y aliases.
  Los chips de artículos reutilizan la navegación jurídica existente.
- La preferencia local \`glossary_enabled\` del PR de Misión 1 sigue siendo
  optativa y se resetea con el resto de las preferencias de lectura.

### R8 y pruebas

Reglas de conservación para los dos DTO de Gson en
\`app/proguard-rules.pro\`, contrato por reflexión ampliado en
\`GsonReleaseContractTest\`; se comprobará con release minimizado.

\`tools/validate_glossary.py\` y \`tools/test_validate_glossary.py\` corren
en Android CI y exigen correspondencia exacta con \`articles.json\`.
Tests Android adicionales cubren parseo, modo vacío y corrupto, matcher,
spans, exclusiones, búsqueda y fichas editoriales.

## Verificación manual antes de fusionar

1. Con \`terms: []\`, recorrer artículos y buscar: el lector no cambia.
2. En una compilación **de pruebas únicamente** con un término editorial
   simulado, comprobar apertura con toque y TalkBack, copia, enlaces, relacionados,
   búsqueda y temas Claro/Sepia/Oscuro.
3. Selección, resaltado, notas, referencias cruzadas y anotaciones normativas
   tienen prioridad y siguen operando sin pérdida de datos.
4. Cambiar \`glossary_enabled\` y usar «Restablecer valores de lectura»; las
   preferencias se limpian y el comportamiento vacío permanece seguro.
5. Revisar truncamientos con escala 200 %, rotación y restauración de hojas.

## Invariantes

No cambiar textos legales ni \`articles.json\`, versiones de Android
(\`versionCode=13\`, \`versionName=1.08\`), permisos, anuncios, datos de usuario
ni conectividad. No hay aplicación de red ni definiciones creadas por IA.

No fusionar ni publicar sin revisión del propietario. El resto del plan
(inicio/configuración y cierre final de Fase 6) queda fuera de esta misión.
