# Código Procesal Civil PE — Fase 5 / Misión 5: descubrimiento y continuidad

**Fecha:** 12 de noviembre de 2026.  
**Estado:** entregables preparados en PR. Sin publicación, sin edición de la ficha pública y con validación física/jurídica pendiente.  
**Versión objetivo:** 1.08 (13), aún candidata. En Play figura una versión anterior; no confundir la ficha de producción con el contenido que estará disponible tras publicar la candidata.

## 1. Diagnóstico de la ficha pública

Se consultó la [ficha pública de Google Play](https://play.google.com/store/apps/details?id=com.jobalistudios.codigoprocesalcivilpe) y las [directrices de metadatos](https://support.google.com/googleplay/android-developer/answer/9898842?hl=es), con fecha de trabajo 12/11/2026.

Hallazgos de la vista pública accesible (volver a confirmar directamente en Play Console, pues la versión web puede mostrar traducciones o caché):
- Descripción aún habla de **actualizaciones automáticas de los cambios legislativos**. La app usa corpus offline y **no sincroniza automáticamente reformas**; esa promesa debe eliminarse de la próxima edición publicada.
- La referencia al texto «actualizado a 2025» necesita un límite verificable: la versión oficial cotejada tiene **edición al 07/06/2025**, y el cierre documental de Fase 1 tiene **salvedades**, sin certificación íntegra a noviembre de 2026.
- La ficha pública no presenta todas las funciones que se han incorporado a la candidata **1.08** (120 preguntas, repaso de errores, guardar/retomar). **No anunciarlas como publicadas mientras siga visible 1.07.**
- La ficha consultada incluye declaraciones de seguridad de datos cuya revisión debe continuar en la puerta de privacidad/release. No cambiar formularios con base en una simple comparación de la web.
- El perfil está identificado como proyecto independiente de Jobali Studios. Conservar ese aviso sin insinuar filiación oficial.

**Límites de campos verificados en ayuda oficial de Play:** nombre **30**, descripción breve **80** y completa **4000** caracteres. No usar rankings, promesas no demostradas, repeticiones artificiales ni «actualizado a 2026» sin evidencia.

## 2. Material de descubrimiento preparado, pero no publicado

- [Ficha propuesta es-PE](play-store-es-PE-candidata-1.08.json): título, descripción breve A, alternativa breve B, descripción completa y «Novedades», explícitamente marcadas como **BORRADOR_NO_PUBLICADO** y referidas a 1.08 (13).
- [Prototipo de landing informativa](../../prototypes/discovery/landing-cpc.html): página única responsive, con mensajes de independencia, fuente oficial, fecha y límites, enlaces reales a Play, El Peruano, SPIJ, privacidad y soporte. **No tiene scripts, cookies ni SDKs de seguimiento.** No se ha configurado alojamiento ni dominio; el HTML no es un sitio público operativo.
- **Acerca de la app:** texto de corte de fuente oficial 07/06/2025, advertencia de revisión con salvedades y botón adicional para consultar el catálogo de normas actualizadas de El Peruano.
- [Registro de alertas jurídicas](../continuidad/cpc-registro-alertas.csv) y [procedimiento periódico](../continuidad/PROCEDIMIENTO-MONITOREO-CPC.md): responsabilidades, verificación en fuentes primarias, fechas, dictamen, aprobación y PR. **Todos los asuntos iniciales siguen pendientes.**
- [`tools/validate_discovery_assets.py`](../../tools/validate_discovery_assets.py) y [sus pruebas](../../tools/test_validate_discovery_assets.py), integrados en GitHub CI: barrera reproducible de copy, URLs, transparencia, versionado y trazabilidad. **Validar metadatos no verifica vigencia jurídica real.**

## 3. Experimento de ficha de Play propuesto

**Solo después de desplegar 1.08 mediante pista interna y aprobar su publicación**, revisar los experimentos de ficha disponibles en Play Console. Propuesta con una sola variable por test:

| Elemento | Control A | Variante B | Qué comparar |
|---|---|---|---|
| Descripción breve es-PE | `short_description` del JSON | `short_description_variant_b` | Conversión de ficha a instalación en segmento comparable |
| Capturas (etapa posterior) | Set real de candidata 1.08 aprobado | Set real alternativo con primer pantallazo de búsqueda | Conversión, sin mezclar cambios de texto |

No hay resultados medidos todavía. Registrar ventana, países/idiomas, distribución de usuarios, impresiones, adquisiciones y fiabilidad estadística del experimento tal como los presenta la herramienta. No atribuir diferencias a la variante sin suficiente tráfico o si se modificaron campañas/versión/precios durante la comparación.

**Orden propuesto de capturas genuinas**, a obtener en dispositivo con la candidata validada: 1) Inicio y navegación; 2) búsqueda por número y texto; 3) lectura de artículo; 4) favoritos, resaltados y notas; 5) prácticas de cuestionarios y resultados; 6) transparencia de fuentes. No renderizar imágenes falsas de interfaz ni certificar vigencia en artes. La ficha publica solo el conjunto expresamente aprobado.

**Métricas disponibles:** estadísticas agregadas de adquisición, visitantes/conversión de ficha y calificación en Play Console, según acceso y disponibilidad del propietario. Comparar fechas de referencia y después de cambios; no fabricar valores. Los contadores opt-in personales de la Misión 4 **no equivalen** a métricas de instalaciones, retención o usuarios activos de la tienda.

## 4. Continuidad normativa y avisos de actualización

Frecuencia propuesta: **revisión mensual operativa** y revisión adicional cuando se publique una posible reforma; **sin automatización que altere normas o anuncie vigencia**. El procedimiento exige:
1. Fuente primaria, fecha de publicación y entrada en vigor; considerar disposiciones complementarias, transitorias, derogaciones y fe de erratas.
2. Artículo(s) afectado(s) y comparación específica con `articles.json`; evidencia de que no es solo proyecto.
3. Aprobación explícita antes de editar textos jurídicos, preguntas o avisos. PR separado con tests y regresión.
4. Actualización del contenido de app **solamente en una versión de distribución autorizada**; el índice oficial web permanece externo.
5. Cierre del registro sin fingir que los campos vacíos significan «no hay reformas».

Bloqueantes iniciales conservados: búsqueda integral de publicaciones post-07/06/2025, adecuación del TUO por Ley 32377, conflicto del artículo 551, JUR-016, JUR-031 y JUR-037.

## 5. Puertas antes de publicar

- [ ] CI íntegramente verde para el PR de Misión 5 y los artefactos propuestos.
- [ ] Misión 5 revisada y PR fusionado por el propietario.
- [ ] Pruebas físicas de las misiones 3–5 (incluidos enlaces externos, tamaños de letra, modo oscuro y funcionamiento sin red).
- [ ] Revisar el checklist de **MAN-01 a MAN-12** de la [candidata 1.08](../releases/2026-11-fase2-1.08-candidata.md); ninguna ejecución virtual sustituye teléfono real.
- [ ] Salvedades jurídicas materiales abordadas y transparencia de vigencia revisada por persona competente, sin declarar certificación no acreditada.
- [ ] Revisar el contenido **real** de Play Console: ficha, política pública, Seguridad de los datos, avisos y estado de la versión.
- [ ] AAB release firmado con credenciales fuera de Git; prueba interna 1.07 → 1.08 y conservación de notas/favoritos/privacidad.
- [ ] Autorización **explícita separada** del propietario para alterar ficha pública y otra para publicar en producción.

**Misión 5 cierra la preparación de propuestas de descubrimiento y la capacidad de revisión periódica; no cierra la auditoría jurídica pendiente ni valida una publicación.**
