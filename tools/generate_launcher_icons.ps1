$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$root = Split-Path $PSScriptRoot -Parent
function Render-Icon([string]$path, [int]$size, [bool]$foreground, [bool]$round) {
    $bitmap = New-Object System.Drawing.Bitmap($size, $size)
    $g = [System.Drawing.Graphics]::FromImage($bitmap)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.Clear([System.Drawing.Color]::Transparent)
    $g.ScaleTransform(($size / 108.0), ($size / 108.0))
    $burgundy = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#641D32'))
    $ivory = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#FFF4DE'))
    $gold = New-Object System.Drawing.SolidBrush([System.Drawing.ColorTranslator]::FromHtml('#D9AE64'))
    $pen = New-Object System.Drawing.Pen($ivory, 3)
    if (-not $foreground) {
        if ($round) { $g.FillEllipse($burgundy, 0, 0, 108, 108) }
        else { $g.Clear([System.Drawing.ColorTranslator]::FromHtml('#641D32')) }
    }
    $g.TranslateTransform(54, 54)
    $g.ScaleTransform(0.85, 0.85)
    $g.TranslateTransform(-54, -54)
    $g.FillRectangle($ivory, 51, 32, 6, 43)
    $g.FillRectangle($ivory, 40, 75, 28, 5)
    $g.FillRectangle($ivory, 35, 81, 38, 4)
    $g.DrawLine($pen, 29, 42, 79, 42)
    $g.DrawLine($pen, 33, 43, 24, 61)
    $g.DrawLine($pen, 33, 43, 42, 61)
    $g.DrawLine($pen, 75, 43, 66, 61)
    $g.DrawLine($pen, 75, 43, 84, 61)
    $g.FillPie($ivory, 23, 51, 20, 20, 0, 180)
    $g.FillPie($ivory, 65, 51, 20, 20, 0, 180)
    $g.FillEllipse($gold, 49, 25, 10, 10)
    $bitmap.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
    $pen.Dispose(); $gold.Dispose(); $ivory.Dispose(); $burgundy.Dispose(); $g.Dispose(); $bitmap.Dispose()
}
$densities = @{mdpi=48;hdpi=72;xhdpi=96;xxhdpi=144;xxxhdpi=192}
foreach ($density in $densities.Keys) {
    $folder = Join-Path $root "app/src/main/res/mipmap-$density"
    Render-Icon (Join-Path $folder 'ic_launcher.png') $densities[$density] $false $false
    Render-Icon (Join-Path $folder 'ic_launcher_round.png') $densities[$density] $false $true
    $foregroundSize = [int]($densities[$density] * 108 / 48)
    Render-Icon (Join-Path $folder 'ic_launcher_foreground.png') $foregroundSize $true $false
}
Render-Icon (Join-Path $root 'app/src/main/ic_launcher-playstore.png') 512 $false $false
Write-Host 'Iconos generados.'
