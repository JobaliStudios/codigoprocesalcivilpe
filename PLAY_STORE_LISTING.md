# Texto verificado para Google Play

## Actualización del contenido jurídico

Usar en la ficha de Google Play:

> Contenido jurídico disponible sin conexión y actualizado periódicamente mediante
> nuevas versiones de la aplicación.

No indicar que la aplicación descarga automáticamente cambios legislativos. El contenido
se distribuye dentro del APK/AAB (`assets/articles.json` y recursos compilados), por lo que
una modificación jurídica requiere generar, revisar y publicar una nueva versión.

## Lista de comprobación antes de publicar

1. Contrastar los textos modificados con una fuente oficial.
2. Actualizar los recursos fuente y regenerar `assets/articles.json`.
3. Ejecutar las pruebas de integridad del catálogo legal.
4. Documentar las normas incorporadas en las notas de la versión.
5. Publicar el nuevo AAB con un `versionCode` superior.

Si en el futuro se implementan actualizaciones remotas, no se debe aceptar contenido solo
porque sea JSON válido. El diseño tendrá que exigir HTTPS, firma digital verificada con una
clave pública incorporada en la aplicación, versión monotónica, fecha de publicación, hash
SHA-256, fuente oficial identificada y recuperación segura de la última versión válida.
