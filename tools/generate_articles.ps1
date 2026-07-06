# Genera app/src/main/assets/articles.json a partir de strings.xml y LegalContentCatalog.java.
#
#   Uso:  powershell -ExecutionPolicy Bypass -File tools\generate_articles.ps1
#
# Cada bloque del JSON reconstruye por concatenacion (preamble + articles[].text) EXACTAMENTE
# el mismo texto que Resources#getString() devuelve en runtime; ArticleRepositoryTest
# (Robolectric) verifica esa identidad byte a byte para todos los bloques del catalogo.
# Ejecutar de nuevo este script tras cualquier cambio en los strings de contenido.
#
# Nota: archivo ASCII puro a proposito (PowerShell 5.1 interpreta como ANSI los .ps1 sin BOM);
# la "i" acentuada de los patrones se construye con [char]0x00ED.

$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$stringsPath = Join-Path $root 'app\src\main\res\values\strings.xml'
$catalogPath = Join-Path $root 'app\src\main\java\com\jobalistudios\codigoprocesalcivilpe\navigation\LegalContentCatalog.java'
$outPath     = Join-Path $root 'app\src\main\assets\articles.json'

# --- 1) Claves de bloques, en el orden del catalogo (ultimo R.string de cada entry(...)) ---
$catalogSrc = Get-Content $catalogPath -Raw -Encoding utf8
$keys = New-Object System.Collections.Generic.List[string]
foreach ($m in [regex]::Matches($catalogSrc, 'entry\(([^\)]+)\)')) {
    $refs = [regex]::Matches($m.Groups[1].Value, 'R\.string\.(\w+)')
    if ($refs.Count -gt 0) { $keys.Add($refs[$refs.Count - 1].Groups[1].Value) }
}
if ($keys.Count -eq 0) { throw "No se encontraron entradas en $catalogPath" }
$dupKeys = $keys | Group-Object | Where-Object { $_.Count -gt 1 }
if ($dupKeys) { throw "Claves duplicadas en el catalogo: $(($dupKeys | ForEach-Object Name) -join ', ')" }

# --- 2) Valores crudos del XML (el parser ya resuelve entidades XML, igual que aapt) ---
$xml = [xml](Get-Content $stringsPath -Raw -Encoding utf8)
$rawByName = @{}
foreach ($s in $xml.resources.string) { $rawByName[$s.name] = $s.'#text' }

# --- 3) Emulacion del procesamiento de strings de aapt ---
# - los escapes (\n, \t, \', \", \\, \@, \?) se insertan literales
# - una comilla doble sin escapar alterna el "modo comillas" y se elimina
# - fuera de comillas, las rachas de blancos crudos colapsan a UN espacio
# - los espacios colapsados al inicio/fin se recortan (los escapes nunca se recortan)
function Convert-AaptString([string]$raw) {
    $sb = New-Object System.Text.StringBuilder
    $collapsed = New-Object System.Collections.Generic.List[bool]
    $quote = $false
    $lastCollapsed = $false
    $i = 0
    while ($i -lt $raw.Length) {
        $c = $raw[$i]
        if ($c -eq '\' -and $i + 1 -lt $raw.Length) {
            $e = $raw[$i + 1]
            switch ($e) {
                'n' { [void]$sb.Append([char]10) }
                't' { [void]$sb.Append([char]9) }
                default { [void]$sb.Append($e) }
            }
            $collapsed.Add($false)
            $lastCollapsed = $false
            $i += 2
            continue
        }
        if ($c -eq '"') {
            $quote = -not $quote
            $lastCollapsed = $false
            $i++
            continue
        }
        if (-not $quote -and ($c -eq ' ' -or $c -eq "`t" -or $c -eq "`n" -or $c -eq "`r")) {
            if (-not $lastCollapsed) {
                [void]$sb.Append(' ')
                $collapsed.Add($true)
                $lastCollapsed = $true
            }
            $i++
            continue
        }
        [void]$sb.Append($c)
        $collapsed.Add($false)
        $lastCollapsed = $false
        $i++
    }
    $s = $sb.ToString()
    $start = 0
    while ($start -lt $s.Length -and $collapsed[$start]) { $start++ }
    $end = $s.Length
    while ($end -gt $start -and $collapsed[$end - 1]) { $end-- }
    return $s.Substring($start, $end - $start)
}

# --- 4) Segmentacion por encabezado de articulo ---
# Acepta "Articulo 647", "Articulo 647-A", "Articulo 647 A." (letra suelta solo si le sigue . o -)
$ia = [char]0x00ED
$headPat = "(?m)^[ \t]*Art[i$ia]culo[ \t]+(\d{1,4})(?:[ \t]*-[ \t]*([A-Fa-f])(?![\w])|[ \t]+([A-Fa-f])(?=[ \t]*[\.\-]))?"

function ConvertTo-JsonEscaped([string]$s) {
    $e = $s.Replace('\', '\\').Replace('"', '\"').Replace("`r", '\r').Replace("`n", '\n').Replace("`t", '\t')
    if ($e -match '[\x00-\x1F]') { throw 'Caracter de control inesperado en el contenido' }
    return $e
}

$json = New-Object System.Text.StringBuilder
[void]$json.Append("{`n`"version`": 1,`n`"blocks`": [`n")

$totalArticles = 0
$preambleCount = 0
$emptyBlocks = @()
$warnings = @()
$numberCount = @{}
$firstBlock = $true

foreach ($key in $keys) {
    $raw = $rawByName[$key]
    if ($null -eq $raw) { throw "El catalogo referencia R.string.$key pero no existe en strings.xml" }
    $txt = Convert-AaptString $raw

    $heads = @([regex]::Matches($txt, $headPat))

    # numeros canonicos + titulos
    $articles = New-Object System.Collections.Generic.List[object]
    for ($h = 0; $h -lt $heads.Count; $h++) {
        $m = $heads[$h]
        $numDigits = $m.Groups[1].Value
        $letter = ''
        if ($m.Groups[2].Success) { $letter = $m.Groups[2].Value }
        elseif ($m.Groups[3].Success) { $letter = $m.Groups[3].Value }
        $number = if ($letter) { "$numDigits-$($letter.ToUpperInvariant())" } else { $numDigits }

        $eol = $txt.IndexOf("`n", $m.Index)
        if ($eol -lt 0) { $eol = $txt.Length }
        $line = $txt.Substring($m.Index, $eol - $m.Index)
        $title = $line.Substring($m.Length).TrimStart([char[]]@(' ', "`t", '.', '-', ':')).Trim()

        $start = $m.Index
        $end = if ($h + 1 -lt $heads.Count) { $heads[$h + 1].Index } else { $txt.Length }
        $articles.Add(@{ Number = $number; Title = $title; Text = $txt.Substring($start, $end - $start) })

        if (-not $numberCount.ContainsKey($number)) { $numberCount[$number] = 0 }
        $numberCount[$number]++
        $totalArticles++
    }

    $preamble = if ($heads.Count -gt 0) { $txt.Substring(0, $heads[0].Index) } else { $txt }
    if ($heads.Count -eq 0) { $emptyBlocks += $key }
    elseif ($preamble.Length -gt 0) { $preambleCount++ }

    # auto-verificacion: la concatenacion debe reproducir el texto completo
    $rebuilt = New-Object System.Text.StringBuilder
    [void]$rebuilt.Append($preamble)
    foreach ($a in $articles) { [void]$rebuilt.Append($a.Text) }
    if ($rebuilt.ToString() -cne $txt) { throw "La segmentacion de '$key' no reconstruye el texto original" }

    # advertencia si los numeros no crecen dentro del bloque
    for ($h = 1; $h -lt $articles.Count; $h++) {
        $prev = $articles[$h - 1].Number; $cur = $articles[$h].Number
        $prevN = [int]($prev -replace '-.*', ''); $curN = [int]($cur -replace '-.*', '')
        if ($curN -lt $prevN -or ($curN -eq $prevN -and $cur -le $prev)) {
            $warnings += "Orden no creciente en ${key}: $prev -> $cur"
        }
    }

    # emitir bloque
    if (-not $firstBlock) { [void]$json.Append(",`n") }
    $firstBlock = $false
    [void]$json.Append("{`"key`": `"$key`",")
    if ($preamble.Length -gt 0) { [void]$json.Append("`n`"preamble`": `"$(ConvertTo-JsonEscaped $preamble)`",") }
    [void]$json.Append("`n`"articles`": [")
    for ($h = 0; $h -lt $articles.Count; $h++) {
        $a = $articles[$h]
        if ($h -gt 0) { [void]$json.Append(',') }
        [void]$json.Append("`n{`"number`": `"$($a.Number)`", `"title`": `"$(ConvertTo-JsonEscaped $a.Title)`", `"text`": `"$(ConvertTo-JsonEscaped $a.Text)`"}")
    }
    [void]$json.Append("`n]}")
}

[void]$json.Append("`n]`n}`n")

$outDir = Split-Path -Parent $outPath
if (-not (Test-Path $outDir)) { New-Item -ItemType Directory -Force $outDir | Out-Null }
[System.IO.File]::WriteAllText($outPath, $json.ToString(), (New-Object System.Text.UTF8Encoding($false)))

$dups = $numberCount.GetEnumerator() | Where-Object { $_.Value -gt 1 }
$sizeKb = [Math]::Round((Get-Item $outPath).Length / 1KB)

Write-Output "Bloques: $($keys.Count)"
Write-Output "Articulos: $totalArticles (numeros distintos: $($numberCount.Count))"
Write-Output "Bloques con preambulo: $preambleCount | sin articulos: $($emptyBlocks.Count) $($emptyBlocks -join ',')"
Write-Output "Salida: $outPath ($sizeKb KB)"
if ($dups) { foreach ($d in $dups) { Write-Output "DUPLICADO GLOBAL: Art. $($d.Key) x$($d.Value)" } }
if ($warnings) { $warnings | ForEach-Object { Write-Output "AVISO: $_" } }
