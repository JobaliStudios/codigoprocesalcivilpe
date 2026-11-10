# Revisión independiente del código — 8 de noviembre de 2026

Rango: `3cf767e..c30fa7f`. Revisión de solo lectura.

No se identificaron problemas críticos. Requiere corrección antes del release:

## Seguimiento de correcciones

- Caché publicitaria: corregida en `c37d346`; se invalida el anuncio y se descartan callbacks de generaciones anteriores al cerrar opciones UMP. Pruebas de generación e intersticiales pasan. Prueba real de SDK en dispositivo pendiente.
- Zona segura: geometría escalada al 85 % alrededor del centro en vector, monocromo y generador. Verificador de píxeles detectó el defecto antes y pasó después; ningún píxel significativo del primer plano xxxhdpi supera el radio 33 del viewport 108. Pendiente inspección visual en dispositivo.

1. **Importante — anuncio almacenado tras cambiar consentimiento.** `canRequestAds()` puede seguir en true después de cambiar personalización. El anuncio ya cargado y una solicitud en vuelo pueden conservar la configuración anterior. Invalidar caché y solicitudes pendientes al cambiar opciones UMP; recargar bajo las opciones nuevas. Agregar prueba que rechace callbacks de la generación anterior.
2. **Menor — zona segura del ícono.** Esquinas del pedestal a aproximadamente 36,36 unidades del centro superan el círculo garantizado de radio 33 en el viewport de 108. Pendiente ajustar geometría y validar máscaras.

Fortalezas verificadas: navegación de contenido sin gate; solo dos call sites naturales; borrado mediante QuizErrorStore; retorno al índice sin SINGLE_TOP; política pública coincide con canónico y HTTP 200; corpus jurídico intacto.

Fuera del alcance de este dictamen: auditoría jurídica integral, respuestas definitivas de consola, prueba instalada de SDK y estética a 48 px. No constituye aprobación para publicar el AAB.
