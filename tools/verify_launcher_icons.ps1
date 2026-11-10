$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path $PSScriptRoot -Parent
function Check-Png([string]$relative, [int]$expected, [bool]$transparent) {
    $image = [System.Drawing.Bitmap]::FromFile((Join-Path $root $relative))
    try {
        if ($image.Width -ne $expected -or $image.Height -ne $expected) {
            throw "Dimensiones incorrectas: $relative"
        }
        if ($transparent -and $image.GetPixel(0,0).A -ne 0) {
            throw "Falta transparencia de primer plano: $relative"
        }
        if (-not $transparent -and $image.GetPixel(0,0).A -ne 255) {
            throw "El icono Play debe ser opaco: $relative"
        }
    } finally { $image.Dispose() }
}
Check-Png 'app/src/main/ic_launcher-playstore.png' 512 $false
$foreground = [System.Drawing.Bitmap]::FromFile((Join-Path $root 'app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png'))
try {
    for ($y=0; $y -lt $foreground.Height; $y++) {
        for ($x=0; $x -lt $foreground.Width; $x++) {
            $dx = ($x + 0.5) * 108 / $foreground.Width - 54
            $dy = ($y + 0.5) * 108 / $foreground.Height - 54
            if ($foreground.GetPixel($x,$y).A -gt 16 -and ($dx*$dx+$dy*$dy) -gt (33*33)) {
                throw "Geometria fuera del circulo seguro adaptativo: ($x,$y)"
            }
        }
    }
} finally { $foreground.Dispose() }
$densities = @{mdpi=48;hdpi=72;xhdpi=96;xxhdpi=144;xxxhdpi=192}
foreach ($density in $densities.Keys) {
    Check-Png "app/src/main/res/mipmap-$density/ic_launcher.png" $densities[$density] $false
    Check-Png "app/src/main/res/mipmap-$density/ic_launcher_foreground.png" ([int]($densities[$density]*108/48)) $true
    $round = Join-Path $root "app/src/main/res/mipmap-$density/ic_launcher_round.png"
    $image = [System.Drawing.Bitmap]::FromFile($round)
    try {
        if ($image.Width -ne $densities[$density] -or $image.Height -ne $densities[$density]) { throw "Tamano round incorrecto: $round" }
    } finally { $image.Dispose() }
}
foreach ($name in @('ic_launcher.xml','ic_launcher_round.xml')) {
    [xml]$adaptive = Get-Content (Join-Path $root "app/src/main/res/mipmap-anydpi-v26/$name")
    if (-not $adaptive.'adaptive-icon'.monochrome) { throw "Falta capa monocroma: $name" }
}
Write-Host 'PASS: iconos Play, densidades, transparencia y capas adaptativas.'
