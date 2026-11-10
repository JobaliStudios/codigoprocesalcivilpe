param([switch]$RequireClosed)

$ErrorActionPreference = 'Stop'
$repoRoot = Split-Path $PSScriptRoot -Parent
$reportPath = Join-Path $repoRoot 'docs/audits/2026-11-07-auditoria-contenido-juridico.md'
$findingsPath = Join-Path $repoRoot 'docs/audits/2026-11-07-hallazgos-juridicos.csv'
$corpusPath = Join-Path $repoRoot 'app/src/main/assets/articles.json'
$reportText = Get-Content -LiteralPath $reportPath -Raw -Encoding UTF8
$findings = @(Import-Csv -LiteralPath $findingsPath -Encoding UTF8)
$corpus = Get-Content -LiteralPath $corpusPath -Raw -Encoding UTF8 | ConvertFrom-Json
$currentHash = (Get-FileHash -LiteralPath $corpusPath -Algorithm SHA256).Hash
$baselineHash = 'BC9EE587FA6ECCA923DD0A163B8739E8CE9976C09A79C7551E88661B50B946D9'

foreach ($required in @($baselineHash, $currentHash, '2026', '8 de agosto de 2026',
        'elperuano.pe', 'spij.minjus.gob.pe', 'congreso.gob.pe',
        'Poder Judicial', 'no acredita')) {
    if ($reportText.IndexOf($required, [StringComparison]::OrdinalIgnoreCase) -lt 0) {
        throw "Falta evidencia o limite requerido en el informe: $required"
    }
}
if ($findings.Count -eq 0) { throw 'No hay hallazgos ni decisiones trazables.' }
$seenIds = @{}
foreach ($finding in $findings) {
    if ($finding.id -notmatch '^JUR-\d{3}$' -or $seenIds.ContainsKey($finding.id)) {
        throw "Identificador invalido o duplicado: $($finding.id)"
    }
    $seenIds[$finding.id] = $true
    if ($finding.estado -notin @('PENDIENTE', 'APROBADO', 'RECHAZADO', 'OBSERVADO')) {
        throw "Estado no permitido: $($finding.id)"
    }
    foreach ($field in @('articulo', 'impacto', 'propuesta', 'fuente', 'decision_propietario')) {
        if ([string]::IsNullOrWhiteSpace($finding.$field)) {
            throw "Campo vacio: $($finding.id) / $field"
        }
    }
    $sourceUri = [uri]$finding.fuente
    if ($sourceUri.Scheme -ne 'https' -or $sourceUri.Host -notmatch '(^|\.)(elperuano\.pe|congreso\.gob\.pe|minjus\.gob\.pe|pj\.gob\.pe)$') {
        throw "Fuente no oficial: $($finding.id)"
    }
    if (-not $reportText.Contains($finding.id)) { throw "Hallazgo sin desarrollo: $($finding.id)" }
    if ($finding.estado -eq 'APROBADO' -and $finding.decision_propietario -notmatch 'Aprobacion expresa') {
        throw "Falta evidencia de aprobacion expresa: $($finding.id)"
    }
}
$articles = @($corpus.blocks | ForEach-Object { $_.articles })
if (@($corpus.blocks).Count -ne 104 -or $articles.Count -ne 862 -or
        @($articles.number | Sort-Object -Unique).Count -ne 862) {
    throw 'Cambio no justificado en el inventario de bloques o articulos.'
}
$openFindings = @($findings | Where-Object { $_.estado -in @('PENDIENTE', 'OBSERVADO') })
Write-Output "PASS documental: inventario y trazabilidad; SHA-256 $currentHash"
Write-Output "Hallazgos abiertos: $($openFindings.Count). Este control no certifica vigencia ni cotejo integral."
if ($RequireClosed -and ($openFindings.Count -gt 0 -or $reportText -match 'Estado: EN CURSO')) {
    throw 'Auditoria abierta: no se cumple el cierre juridico necesario para release.'
}
