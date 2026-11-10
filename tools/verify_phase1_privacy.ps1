param(
    [string]$PolicyPath = (Join-Path $PSScriptRoot "..\docs\privacy\politica-privacidad-es.html"),
    [string]$AuditPath = (Join-Path $PSScriptRoot "..\docs\audits\2026-11-07-seguridad-de-los-datos.md"),
    [string]$InventoryPath = (Join-Path $PSScriptRoot "..\docs\audits\2026-11-07-inventario-datos.csv")
)

$ErrorActionPreference = "Stop"

if (-not (Test-Path -LiteralPath $PolicyPath -PathType Leaf)) {
    Write-Error "No existe la politica canonica: $PolicyPath"
    exit 1
}

$html = (Get-Content -Raw -Encoding UTF8 -LiteralPath $PolicyPath).ToLowerInvariant()
$required = [ordered]@{
    "nombre de la app" = "c\u00f3digo procesal civil"
    "paquete Android" = "com\.jobalistudios\.codigoprocesalcivilpe"
    "version publicada" = "versi\u00f3n\s+1\.06"
    "responsable" = "jobali studios"
    "correo" = "studiosjobali@gmail\.com"
    "sin cuenta" = "(?:no (?:necesitas|requiere) crear una cuenta|sin cuenta)"
    "sin servidor propio" = "(?:no (?:opera|utiliza|mantiene|dispone de) (?:un )?servidor propio|sin servidor propio)"
    "busquedas" = "b\u00fasquedas"
    "favoritos" = "favoritos"
    "historial" = "historial de lectura"
    "preferencias" = "preferencias"
    "quiz" = "(?:quiz|cuestionario)"
    "resaltados" = "resaltados"
    "notas" = "notas"
    "Android Keystore" = "android keystore"
    "eliminacion por categoria" = "(?:por categor\u00eda|cada categor\u00eda)"
    "eliminacion total" = "borrar todos los datos locales"
    "copias de seguridad" = "copias? de seguridad"
    "exclusion de busquedas" = "b\u00fasquedas recientes.{0,220}(?:excluid|no se incluyen|no se copia)"
    "exclusion de notas y resaltados" = "(?:notas y resaltados|resaltados y notas).{0,220}(?:excluid|no se incluyen|no se copia)"
    "Google Mobile Ads" = "google mobile ads"
    "Google UMP" = "google ump"
    "publicidad personalizada" = "publicidad personalizada"
    "consentimiento y region" = "consentimiento.{0,180}regi\u00f3n|regi\u00f3n.{0,180}consentimiento"
    "direccion IP" = "direcci\u00f3n ip"
    "ubicacion aproximada" = "ubicaci\u00f3n aproximada"
    "interacciones publicitarias" = "interacci(?:\u00f3n|ones).{0,120}anuncios"
    "diagnosticos" = "diagn\u00f3sticos"
    "identificadores" = "identificadores.{0,120}(?:dispositivo|publicidad)"
    "transito cifrado" = "(?:tr\u00e1nsito|transmit).{0,100}cifrad"
    "privacidad de Google" = "https://policies\.google\.com/privacy"
    "terminos de Google" = "https://policies\.google\.com/terms"
    "publico general" = "p\u00fablico general"
    "no dirigida a ninos" = "no est\u00e1 dirigida espec\u00edficamente (?:a|para) (?:los )?ni\u00f1os"
    "independencia gubernamental" = "(?:independiente|no (?:pertenece|representa|est\u00e1 afiliada)).{0,120}(?:gobierno|entidad gubernamental|estado peruano)"
    "sin asesoria legal" = "no (?:constituye|ofrece).{0,80}asesor\u00eda legal"
    "fuentes oficiales" = "fuentes (?:jur\u00eddicas )?oficiales"
    "actualizacion por versiones" = "actualizado peri\u00f3dicamente mediante nuevas versiones de la aplicaci\u00f3n"
    "fecha noviembre 2026" = "noviembre de 2026"
}

$missing = @()
foreach ($entry in $required.GetEnumerator()) {
    if ($html -notmatch $entry.Value) {
        $missing += $entry.Key
    }
}

$prohibited = [ordered]@{
    "promesa de actualizacion automatica" = "actualizaci\u00f3n autom\u00e1tica|se actualiza autom\u00e1ticamente"
    "promesa de vigencia permanente" = "siempre actualizado"
}
$violations = @()
foreach ($entry in $prohibited.GetEnumerator()) {
    if ($html -match $entry.Value) {
        $violations += $entry.Key
    }
}

if ($missing.Count -gt 0 -or $violations.Count -gt 0) {
    if ($missing.Count -gt 0) {
        Write-Host "Faltan requisitos: $($missing -join ', ')" -ForegroundColor Red
    }
    if ($violations.Count -gt 0) {
        Write-Host "Se encontraron afirmaciones prohibidas: $($violations -join ', ')" -ForegroundColor Red
    }
    exit 1
}

Write-Host "PASS: la politica canonica cubre el contrato de privacidad de la Fase 1."

if (-not (Test-Path -LiteralPath $AuditPath -PathType Leaf)) {
    Write-Error "No existe la auditoria de Seguridad de los datos: $AuditPath"
    exit 1
}
if (-not (Test-Path -LiteralPath $InventoryPath -PathType Leaf)) {
    Write-Error "No existe el inventario de datos: $InventoryPath"
    exit 1
}

$audit = (Get-Content -Raw -Encoding UTF8 -LiteralPath $AuditPath).ToLowerInvariant()
$auditRequired = [ordered]@{
    "paquete y version" = "com\.jobalistudios\.codigoprocesalcivilpe.{0,160}1\.06"
    "permisos" = "internet.{0,120}access_network_state.{0,120}ad_id"
    "SDK de anuncios" = "play-services-ads.{0,120}25\.4\.0"
    "SDK de consentimiento" = "user-messaging-platform.{0,120}4\.0\.0"
    "distincion desarrollador Google" = "jobali studios.{0,260}google|google.{0,260}jobali studios"
    "categorias Play" = "categor(?:i|\u00ed)as de google play"
    "recopilacion y uso compartido" = "recopilaci(?:o|\u00f3)n.{0,180}uso compartido"
    "finalidad" = "finalidad"
    "opcionalidad" = "opcionalidad"
    "tratamiento efimero" = "ef(?:i|\u00ed)mero"
    "cifrado en transito" = "cifrado en tr(?:a|\u00e1)nsito"
    "eliminacion" = "eliminaci(?:o|\u00f3)n"
    "publico objetivo" = "p(?:u|\u00fa)blico objetivo"
    "tratamiento infantil" = "tratamiento infantil"
    "respuesta actual" = "respuesta actual"
    "respuesta recomendada" = "respuesta recomendada"
    "evidencia" = "evidencia"
    "ubicacion aproximada" = "ubicaci(?:o|\u00f3)n aproximada"
    "actividad publicitaria" = "interacciones con (?:la app y )?los anuncios"
    "diagnosticos" = "diagn(?:o|\u00f3)sticos"
    "identificadores" = "identificadores"
    "fraude y seguridad" = "fraude.{0,80}seguridad|seguridad.{0,80}fraude"
}

$auditMissing = @()
foreach ($entry in $auditRequired.GetEnumerator()) {
    if ($audit -notmatch $entry.Value) {
        $auditMissing += $entry.Key
    }
}

$inventoryHeader = (Get-Content -Encoding UTF8 -LiteralPath $InventoryPath -TotalCount 1).ToLowerInvariant()
$expectedColumns = @(
    "categoria_play", "dato", "actor", "recopilado", "compartido", "finalidad",
    "opcionalidad", "efimero", "cifrado_transito", "eliminacion", "respuesta_actual",
    "respuesta_recomendada", "evidencia"
)
$inventoryMissing = @($expectedColumns | Where-Object { $inventoryHeader -notmatch "(^|,)$_(,|$)" })

if ($auditMissing.Count -gt 0 -or $inventoryMissing.Count -gt 0) {
    if ($auditMissing.Count -gt 0) {
        Write-Host "Faltan secciones de auditoria: $($auditMissing -join ', ')" -ForegroundColor Red
    }
    if ($inventoryMissing.Count -gt 0) {
        Write-Host "Faltan columnas de inventario: $($inventoryMissing -join ', ')" -ForegroundColor Red
    }
    exit 1
}

Write-Host "PASS: la auditoria y el inventario cubren el contrato de Seguridad de los datos."
