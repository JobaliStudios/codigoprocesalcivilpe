# Guía rápida de Design Tokens (Android XML)

Este proyecto usa tokens en `app/src/main/res/values/` para evitar valores hardcodeados y mantener consistencia visual.

## Dónde están los tokens
- **Colores semánticos:** `colors.xml` (`colorSurface`, `colorOnSurface`, `colorPrimaryContainer`, `colorOutline`, etc.).
- **Tipografía / spacing / radios:** `dimens.xml` (`text_title_l`, `text_body_m`, `space_m`, `radius_l`, etc.).
- **Estilos base reutilizables:** `styles.xml`.

## Guía de uso por componente
- **Título principal (pantalla/card):** `@style/App.Text.Title.L`
- **Subtítulo / heading secundario:** `@style/App.Text.Subtitle.M`
- **Texto body:** `@style/App.Text.Body.M` o `@style/App.Text.Body.S`
- **CTA principal (botón):** `@style/App.Button.Primary`
- **Card recurrente (Home / Configuración / Favoritos / Búsqueda):** `@style/App.Card.Recurrent`
- **Chips de filtro:** `@style/App.Chip.Filter`

## Reglas prácticas
1. No usar colores hex directos en layouts; usar `@color/...` semántico.
2. No usar `dp/sp` hardcodeados en layouts; usar `@dimen/...`.
3. Para cards repetidas, aplicar siempre `@style/App.Card.Recurrent`.
4. Si necesitas una variante, crear estilo derivado en `styles.xml` en lugar de repetir atributos en XML.
