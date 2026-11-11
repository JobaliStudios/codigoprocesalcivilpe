# Etapa 44 — Control de cierre y autorizaciones (10/11/2026)

## Por qué se retira la cifra fija de 60%
En las etapas anteriores se mantuvo **60%** como apreciación administrativa, sin denominador de tareas ni estimación de esfuerzo pendiente. **No es una medición exacta** y ya no debe presentarse como tal. El 100% de cotejo documental inicial (862/862) no es 100% de vigencia jurídica, y el 0% de certificaciones integrales refleja que no se ha aprobado un dictamen por artículo.

## Tablero de control de entregables

| ID | Entregable/criterio observable de aceptación | Estado al corte | Evidencia/acción que falta |
|---|---|---|---|
| C01 | Todos los 862 artículos con cotejo/corroboración inicial registrada | **COMPLETO** | Matriz: 0 registros SIN_COTEJO_INTEGRAL_NUEVO |
| C02 | Matriz legible por parser CSV de 862 filas, 15 columnas y sin pérdida de IDs | **COMPLETO** | Commit 174a3fd; validación y seguimiento de matriz tras etapa 40 |
| C03 | Barrido legislativo **exhaustivo** de 08/06/2025 a 10/11/2026, con disposiciones finales y derogatorias y vigencias diferidas | **ABIERTO** | Fuente oficial por cada norma, trazabilidad positiva/negativa; no sustituir por búsqueda general |
| C04 | Conflictos de fuentes resueltos para 648(6), 408 y 391, o dictamen documentado de mantener texto con advertencia | **ABIERTO** | Fe de erratas/cambios posteriores, técnica legislativa y juicio jurídico |
| C05 | Decisiones del propietario sobre cambios mínimos JUR-017, JUR-040 y, en su caso, notas | **REQUIERE APROBACIÓN** | Aprobación expresa de sustituciones específicas |
| C06 | Correcciones autorizadas aplicadas en PR **separado de producción**, con diff reducido | **NO INICIADO** | Solo después de C05; no mezclar con PR documental #54 |
| C07 | Pruebas Android de integridad textual, enlaces, búsqueda, accesibilidad, compilación e instalación | **NO INICIADO** | Evidencia de prueba de versión candidata |
| C08 | Revisión final y publicación de versión en Google Play | **NO INICIADO** | Validación final y autorización explícita de publicación |

**Indicadores verificables:**
- Cobertura documental inicial: **862/862 (100%)**.
- Criterios de cierre plenamente completados: **2 de 8 = 25% de hitos de cierre**, sin ponderación temporal. **No equivale al porcentaje de trabajo o tiempo restante**.
- Estado de certificación jurídica integral: **0/862**.
- Correcciones listas para decisión: **2**.
- Cifra administrativa histórica **60%**: **RETIRADA** por falta de metodología verificable.

## Decisiones de corrección que necesitan autorización del titular

**A1: Art. 648, solo la cita del TC (JUR-017):**
```diff
- Expediente 006-97-AI-TC LIMA
+ Expediente 006-96-AI/TC LIMA
```
Fuente: [STC Exp. 006-96-AI/TC, 30/01/1997](https://www.tc.gob.pe/jurisprudencia/1996/00006-1996-AI.pdf).

**A2: Art. 688, inciso 4 (JUR-040):**
```diff
- acción cambiaría
+ acción cambiaria
```
Fuente: [DL 1069 original, 28/06/2008](https://www.sunat.gob.pe/legislacion/nor_graf/2008/junio/nl20080628.pdf).

La aprobación de A1 y A2 **no autoriza** reformatear otros artículos, alterar reglas de embargo, fusionar PR #54 ni publicar en Play. **No aplicar** JUR-016 (embargado/embargo) hasta resolver discrepancia de fuente original Ley26599 vs TUO, ni JUR-031 (408) o JUR-037 (391).

## Ruta crítica concreta

1. Investigación dirigida C03: catálogo de actos legislativos de la ventana de tiempo y búsquedas exhaustivas por artículos/disposiciones finales, con evidencia primaria.
2. Investigación C04: resolver los puntos críticos, documentar irresolubles sin introducir redacción no publicada.
3. Obtener decisión A1/A2 en C05 (independiente de continuar C03/C04).
4. Si autorizada, implementar diffs mínimos en PR de código aparte; verificar que otros 860 artículos no sufran alteración accidental.
5. Realizar QA final y preparar Play solamente con validación y permiso explícito.

## Seguridad y restricciones
El PR #54 sigue como **borrador sin fusionar** y contiene únicamente documentación bajo `docs/audits`. No modificar `app/src/main/assets/articles.json`, versión Android, APK/AAB ni la ficha de Google Play desde esta etapa. El usuario puede contestar «apruebo A1 y A2», «solo A1», «solo A2» o «aún no»; cualquier decisión es independiente de los controles normativos.
