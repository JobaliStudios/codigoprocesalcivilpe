# Código Procesal Civil PE — Cierre técnico de Fase 4 (Mantenibilidad)

**Fecha de corte:** 12 de noviembre de 2026.  
**Estado:** cierre técnico propuesto en el PR de Misión 5; **sujeto a CI del commit definitivo, revisión, fusión y prueba manual**.  
**Publicación:** prohibida por decisión del propietario; `1.08 (13)` continúa como candidata NO publicada.

## 1. Alcance real y evidencia de integración

Punto de partida: `main` en commit `d3d1385be66321a4eefe14f007e4ba48a17edad6`, inmediatamente antes de #61.  
Punto de comparación de las primeras cuatro misiones: `0e31c34e3de061fe3a0a5d74a2ac8e25a7099e3b` (fusión de #64).

| Misión | PR y resultado | Evidencia automática | Cambio concreto |
|---|---|---|---|
| 1. Control de fuente | [#61](https://github.com/JobaliStudios/codigoprocesalcivilpe/pull/61), fusionado | [CI](https://github.com/JobaliStudios/codigoprocesalcivilpe/actions/runs/43147322438) verde | `ReaderFontSizeDialog`: extrae Aa sin cambiar preferencias |
| 2. Acciones del lector | [#62](https://github.com/JobaliStudios/codigoprocesalcivilpe/pull/62), fusionado | [CI](https://github.com/JobaliStudios/codigoprocesalcivilpe/actions/runs/43150298508) verde | `ReaderArticleTransferController`: portapapeles y selector nativo de compartir |
| 3. Navegación | [#63](https://github.com/JobaliStudios/codigoprocesalcivilpe/pull/63), fusionado | [CI](https://github.com/JobaliStudios/codigoprocesalcivilpe/actions/runs/43161620716) verde | `LegalHierarchyIndex`: índice interno por ID, padre y `textRes` |
| 4. Persistencia | [#64](https://github.com/JobaliStudios/codigoprocesalcivilpe/pull/64), fusionado | [CI](https://github.com/JobaliStudios/codigoprocesalcivilpe/actions/runs/43162843178) verde | Evita sobrescribir bloques de notas cifradas parcialmente ilegibles |
| 5. Cierre técnico | Este PR, sin fusionar | **Pendiente** CI de este PR | Auditoría de alcance, regresión instrumentada y mapa de componentes |

La comparación GitHub de base a fusión de #64 registró **13 rutas afectadas** (código Java, pruebas y documentación). No se cambiaron textos del corpus, el asset jurídico, build config, manifest, publicidad ni privacidad.

## 2. Comprobación reproducible de alcance

`tools/check_phase4_scope.py` compara, con Git, el baseline previo a #61 y el `HEAD` evaluado. Valida que todas las diferencias pertenezcan a una lista exhaustiva de archivos de las cinco misiones y comprueba:
- `applicationId = com.jobalistudios.codigoprocesalcivilpe`
- `versionCode = 13` y `versionName = 1.08`
- Ausencia de modificaciones de `app/src/main/assets/articles.json` y los textos normativos, `app/build.gradle`, `AndroidManifest.xml`, anuncios, consentimiento, política de privacidad, dependencias y preferencias.

Se ejecuta en CI del PR de cierre de la Fase 4. **No es una garantía de vigencia jurídica ni un escáner de seguridad**. Solo verifica la superficie de cambios del refactor respecto de una versión concreta. No se debe aplicar indiscriminadamente a futuros cambios normativos autorizados.

## 3. Mapa de arquitectura y contratos que permanecen

| Capa | Componente principal | Responsabilidad / contrato |
|---|---|---|
| Entrada Android | `SplashScreen`, `GoogleMobileAdsConsentManager` | Obtener consentimiento antes de avanzar; conservar política publicitaria existente |
| Navegación | `LegalHierarchyRepository`, `LegalHierarchyIndex` | Nodos, padres, destinos por ID y recurso; orden de catálogo intacto |
| Corpus | `LegalContentCatalog`, `ArticleRepository`, `articles.json` | Textos por artículo, índices y respaldo a recursos compilados; sin modificaciones en Fase 4 |
| Lector | `SectionContentActivity` | Estado del artículo visible, navegación interna, TTS, notas, resaltados e integración de UI |
| Presentación auxiliar | `ReaderFontSizeDialog`, `ReaderArticleTransferController` | Aa persistente, Copiar y Compartir sin reproducir lógica del lector |
| Búsqueda | `BusquedaViewModel`, `LegalSearchEngine` | Índice de fondo, debounce de 275 ms, búsqueda consistente con filtros; sin cambios en Fase 4 |
| Datos del usuario | `FavoritesManager`, `ReadingHistoryManager`, `HighlightsManager`, `ArticleQuickNotes` | Datos locales; nombres de preferencias, JSON y cifrado existentes intactos |
| Protecciones | `AndroidKeystoreNoteCipher`, `NoteCipher` | Notas cifradas con Android Keystore; sin rotar alias ni clave |

**Limitación de arquitectura:** `SectionContentActivity` sigue siendo una clase grande (más de 1.000 líneas). La Fase 4 hizo **extracciones incrementales**, no una separación total del lector ni una nueva arquitectura de módulos. La máquina de estados de TTS y las pantallas del quiz permanecen en sus componentes actuales por el riesgo de regresión.

## 4. Evidencias automáticas y comprobación final del PR

El CI de este PR requiere:
- [ ] `python3 tools/check_phase4_scope.py` conforme en el commit definitivo.
- [ ] Revisión de archivos y credenciales del repositorio.
- [ ] `articles.json` sincronizado con `strings.xml`.
- [ ] `./gradlew test` aprobado (tests de las cinco misiones y regresiones previas).
- [ ] `./gradlew lint` aprobado.
- [ ] `./gradlew assembleDebug` aprobado.
- [ ] `./gradlew testReleaseUnitTest minifyReleaseWithR8` aprobado.
- [ ] `./gradlew connectedDebugAndroidTest` en emulador AOSP Android 15 (API 35); guardar artefactos de ejecución.
- [ ] PR revisado y fusionado a `main` sin conflictos.

**Nota:** compilación debug/release con R8 no equivale a generar, firmar ni publicar AAB. Pruebas Robolectric e instrumentadas no sustituyen comprobación de datos reales de un teléfono.

## 5. Pruebas manuales de aceptación (no marcar aprobadas sin confirmación)

- [ ] **Lector:** abrir artículo 564, cambiar Aa a 125 %, cerrar y reabrir, restablecer.
- [ ] **Copiar/Compartir:** artículo 506-A y artículo derogado 835; desplazamiento entre 560 y 562 para verificar el artículo actual.
- [ ] **Navegación:** seis secciones, títulos, capítulos, subcapítulos, artículo desde Búsqueda y Favoritos y retorno al índice.
- [ ] **Persistencia:** favorito, historial, nota rápida y resaltado; cerrar/reabrir y editar sin pérdida.
- [ ] **Integración:** búsqueda rápida, filtros, TTS, tema oscuro, consentimiento UMP y Centro de privacidad.
- [ ] **Pantallas grandes:** tablet vertical/horizontal, plegable y fuente ampliada al 150/200 % (pendiente de evidencia cuando no se haya ejecutado).
- [ ] **Actualización:** 1.07 instalada por Play → 1.08 firmada de forma compatible, sin perder datos (continúa pendiente; NO se lanza).

Los pasos no probados físicamente deben conservar el estado **PENDIENTE**, aunque el código y CI estén verdes.

## 6. Riesgos y deuda técnica que NO se consideran resueltos

1. **Bloques de notas ilegibles:** se conserva el ciphertext sin sobrescribirlo, pero la UI aún puede aparentar que la edición fue guardada cuando se omitió; se debe diseñar un estado de error explícito en una futura mejora. Una clave Android Keystore perdida definitivamente no se recupera mediante esta protección.
2. **Tamaño de lector/TTS:** la actividad y controlador de voz siguen siendo complejos. Nuevos refactors requieren PRs y pruebas específicas; no se declara eliminado todo el acoplamiento.
3. **Rendimiento real:** las mejoras de indexación se justifican por estructura, pero no se midieron latencias reales en teléfonos comparables.
4. **Vigencia jurídica:** los trabajos documentales de Fase 1 conservan salvedades. Este cierre **no certifica la vigencia de los 862 artículos a noviembre de 2026**.
5. **Google Play:** la Fase 3 comercial y la publicación 1.08 requieren validación independiente. No se ha aprobado publicación.

## 7. Reglas después de la fase

- Mantener un PR por cambio funcional, pruebas unitarias y checks release/R8; usar la regresión del emulador en entregas amplias.
- Antes de tocar preferencias, crear pruebas con formato histórico y copia de datos; evitar migraciones silenciosas.
- No mezclar actualización de legislación con refactor de navegación o contenido.
- Repetir verificación de fuentes oficiales cuando se prepare una actualización jurídica.
- **Avanzar a Fase 5 (crecimiento) solo después de fusionar este PR de cierre**, sin necesidad de publicar 1.08.
