# Checklist automatico de prepublicacion para Windows.
#
# Uso:
#   powershell -NoProfile -ExecutionPolicy Bypass -File tools\pre_release_check.ps1
#
# Este script solo valida y genera artefactos locales. No publica ni sube archivos.

[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$script:StepNumber = 0
$script:TotalSteps = 10
$expectedApplicationId = 'com.jobalistudios.codigoprocesalcivilpe'
$androidNamespace = 'http://schemas.android.com/apk/res/android'
$root = Split-Path -Parent $PSScriptRoot
$gradleWrapper = Join-Path $root 'gradlew.bat'
$appGradle = Join-Path $root 'app\build.gradle'
$articlesGenerator = Join-Path $PSScriptRoot 'generate_articles.ps1'
$articlesJson = Join-Path $root 'app\src\main\assets\articles.json'
$releaseManifest = Join-Path $root 'app\build\intermediates\packaged_manifests\release\processReleaseManifestForPackage\AndroidManifest.xml'
$bundleMetadata = Join-Path $root 'app\build\intermediates\bundle_ide_model\release\produceReleaseBundleIdeListingFile\output-metadata.json'

function Write-Step([string]$Message) {
    $script:StepNumber++
    Write-Host ''
    Write-Host ("[{0}/{1}] {2}" -f $script:StepNumber, $script:TotalSteps, $Message) -ForegroundColor Cyan
}

function Write-Ok([string]$Message) {
    Write-Host ("OK: {0}" -f $Message) -ForegroundColor Green
}

function Invoke-Gradle([string]$Task, [string]$Description) {
    & $gradleWrapper $Task '--console=plain'
    if ($LASTEXITCODE -ne 0) {
        throw ("Fallo {0}. Gradle termino con codigo {1}." -f $Description, $LASTEXITCODE)
    }
}

function Get-SingleGradleValue([string]$Content, [string]$Pattern, [string]$Name) {
    $found = [regex]::Matches($Content, $Pattern)
    if ($found.Count -ne 1) {
        throw ("No se pudo obtener un unico valor literal para {0} en app/build.gradle." -f $Name)
    }
    return $found[0].Groups[1].Value
}

function Test-Placeholder([string]$Value) {
    if ([string]::IsNullOrWhiteSpace($Value)) { return $true }
    $trimmed = $Value.Trim()
    if ($trimmed -match '^<[^>]+>$') { return $true }
    if ($trimmed -match '^\$\{[^}]+\}$') { return $true }
    return $trimmed -match '(?i)(change.?me|replace.?me|example|sample|dummy|your[-_ ]|placeholder|path[/\\]to)'
}

function Add-SecurityIssue(
    [System.Collections.Generic.List[string]]$Issues,
    [string]$Path,
    [string]$Reason
) {
    $description = "{0} ({1})" -f $Path, $Reason
    if (-not $Issues.Contains($description)) {
        $Issues.Add($description)
    }
}

if (-not (Test-Path -LiteralPath $gradleWrapper -PathType Leaf)) {
    throw "No se encontro el wrapper de Gradle: $gradleWrapper"
}

Write-Host 'Checklist de prepublicacion - Codigo Procesal Civil PE' -ForegroundColor White
Write-Host 'Este proceso no realiza ninguna publicacion en Google Play.' -ForegroundColor Yellow

Push-Location $root
try {
    Write-Step 'Limpieza del proyecto'
    Invoke-Gradle 'clean' 'la limpieza del proyecto'
    Write-Ok 'Proyecto limpio.'

    Write-Step 'Pruebas unitarias'
    Invoke-Gradle 'test' 'la ejecucion de pruebas unitarias'
    Write-Ok 'Todas las pruebas unitarias finalizaron correctamente.'

    Write-Step 'Android Lint'
    Invoke-Gradle 'lint' 'Android Lint'
    Write-Ok 'Android Lint no encontro errores bloqueantes.'

    Write-Step 'Regeneracion y validacion de articles.json'
    if (-not (Test-Path -LiteralPath $articlesGenerator -PathType Leaf)) {
        throw "No se encontro el generador de articulos: $articlesGenerator"
    }
    $hashBefore = $null
    if (Test-Path -LiteralPath $articlesJson -PathType Leaf) {
        $hashBefore = (Get-FileHash -LiteralPath $articlesJson -Algorithm SHA256).Hash
    }
    & $articlesGenerator
    if (-not (Test-Path -LiteralPath $articlesJson -PathType Leaf)) {
        throw "El generador no produjo articles.json."
    }
    try {
        $articlesData = Get-Content -LiteralPath $articlesJson -Raw -Encoding UTF8 | ConvertFrom-Json
    } catch {
        throw "articles.json no es JSON valido: $($_.Exception.Message)"
    }
    $articleBlocks = @($articlesData.blocks)
    if ($articleBlocks.Count -eq 0) {
        throw 'articles.json no contiene bloques juridicos.'
    }
    $missingBlockKeys = @($articleBlocks | Where-Object { [string]::IsNullOrWhiteSpace([string]$_.key) })
    if ($missingBlockKeys.Count -gt 0) {
        throw "articles.json contiene $($missingBlockKeys.Count) bloques sin clave."
    }
    $duplicateBlockKeys = @($articleBlocks | Group-Object -Property key | Where-Object { $_.Count -gt 1 })
    if ($duplicateBlockKeys.Count -gt 0) {
        throw "articles.json contiene claves de bloque duplicadas."
    }
    $emptyBlocks = @($articleBlocks | Where-Object { @($_.articles).Count -eq 0 })
    if ($emptyBlocks.Count -gt 0) {
        $emptyBlockSummary = ($emptyBlocks | ForEach-Object { [string]$_.key }) -join ', '
        throw "articles.json contiene bloques sin articulos: $emptyBlockSummary"
    }
    $allArticles = New-Object System.Collections.Generic.List[object]
    foreach ($block in $articleBlocks) {
        foreach ($article in @($block.articles)) {
            $allArticles.Add($article)
        }
    }
    if ($allArticles.Count -eq 0) {
        throw 'articles.json no contiene articulos.'
    }
    $missingNumbers = @($allArticles | Where-Object { [string]::IsNullOrWhiteSpace([string]$_.number) })
    if ($missingNumbers.Count -gt 0) {
        throw "articles.json contiene $($missingNumbers.Count) articulos sin numero."
    }
    $duplicateNumbers = @($allArticles | Group-Object -Property number | Where-Object { $_.Count -gt 1 })
    if ($duplicateNumbers.Count -gt 0) {
        $duplicateSummary = ($duplicateNumbers | ForEach-Object { "Art. $($_.Name) x$($_.Count)" }) -join ', '
        throw "articles.json contiene numeros duplicados: $duplicateSummary"
    }
    $hashAfter = (Get-FileHash -LiteralPath $articlesJson -Algorithm SHA256).Hash
    if ($null -ne $hashBefore -and $hashBefore -eq $hashAfter) {
        Write-Ok "articles.json ya estaba sincronizado ($($allArticles.Count) articulos)."
    } else {
        Write-Host 'articles.json cambio; ejecutando ArticleRepositoryTest sobre el archivo nuevo.'
        & $gradleWrapper 'testDebugUnitTest' '--tests' `
                'com.jobalistudios.codigoprocesalcivilpe.contenido.ArticleRepositoryTest' '--console=plain'
        if ($LASTEXITCODE -ne 0) {
            throw "ArticleRepositoryTest fallo despues de regenerar articles.json (codigo $LASTEXITCODE)."
        }
        Write-Ok "articles.json fue regenerado y validado ($($allArticles.Count) articulos)."
    }

    Write-Step 'Compilacion release'
    Invoke-Gradle 'assembleRelease' 'la compilacion release'
    Write-Ok 'La variante release compilo correctamente.'

    Write-Step 'Generacion del AAB release'
    $bundleStartedUtc = [DateTime]::UtcNow
    Invoke-Gradle 'bundleRelease' 'la generacion del AAB release'
    Write-Ok 'Gradle genero el bundle release.'

    $gradleContent = Get-Content -LiteralPath $appGradle -Raw -Encoding UTF8
    $configuredApplicationId = Get-SingleGradleValue $gradleContent '(?m)^\s*applicationId\s+["'']([^"'']+)["'']\s*$' 'applicationId'
    $configuredVersionCode = Get-SingleGradleValue $gradleContent '(?m)^\s*versionCode\s+(\d+)\s*$' 'versionCode'
    $configuredVersionName = Get-SingleGradleValue $gradleContent '(?m)^\s*versionName\s+["'']([^"'']+)["'']\s*$' 'versionName'

    if (-not (Test-Path -LiteralPath $releaseManifest -PathType Leaf)) {
        throw "No existe el manifiesto release generado: $releaseManifest"
    }
    [xml]$manifestXml = Get-Content -LiteralPath $releaseManifest -Raw -Encoding UTF8
    $manifestApplicationId = [string]$manifestXml.manifest.package
    $manifestVersionCode = $manifestXml.manifest.GetAttribute('versionCode', $androidNamespace)
    $manifestVersionName = $manifestXml.manifest.GetAttribute('versionName', $androidNamespace)

    Write-Step 'Verificacion del applicationId'
    if ($configuredApplicationId -ne $expectedApplicationId) {
        throw "applicationId incorrecto en app/build.gradle. Esperado: $expectedApplicationId. Encontrado: $configuredApplicationId."
    }
    if ($manifestApplicationId -ne $configuredApplicationId) {
        throw "El manifiesto release usa '$manifestApplicationId', pero app/build.gradle configura '$configuredApplicationId'."
    }
    Write-Ok "applicationId verificado: $configuredApplicationId"

    Write-Step 'Verificacion de versionCode y versionName'
    if ([string]$manifestVersionCode -ne [string]$configuredVersionCode) {
        throw "versionCode no coincide. Gradle: $configuredVersionCode. Manifiesto release: $manifestVersionCode."
    }
    if ([string]$manifestVersionName -cne [string]$configuredVersionName) {
        throw "versionName no coincide. Gradle: $configuredVersionName. Manifiesto release: $manifestVersionName."
    }
    Write-Ok "Version verificada: versionCode $configuredVersionCode, versionName $configuredVersionName"

    Write-Step 'Confirmacion de la version del AAB generado'
    if (-not (Test-Path -LiteralPath $bundleMetadata -PathType Leaf)) {
        throw "No existen los metadatos del bundle release: $bundleMetadata"
    }
    $metadata = Get-Content -LiteralPath $bundleMetadata -Raw -Encoding UTF8 | ConvertFrom-Json
    if ([string]$metadata.variantName -cne 'release') {
        throw "Los metadatos encontrados no corresponden a la variante release."
    }
    if ([string]$metadata.applicationId -ne $configuredApplicationId) {
        throw "El AAB fue registrado para '$($metadata.applicationId)' y no para '$configuredApplicationId'."
    }
    $bundleElements = @($metadata.elements)
    if ($bundleElements.Count -ne 1 -or [string]::IsNullOrWhiteSpace([string]$bundleElements[0].outputFile)) {
        throw 'Los metadatos del bundle no describen un unico AAB.'
    }
    $metadataDirectory = Split-Path -Parent $bundleMetadata
    $aabPath = [System.IO.Path]::GetFullPath((Join-Path $metadataDirectory ([string]$bundleElements[0].outputFile)))
    if (-not (Test-Path -LiteralPath $aabPath -PathType Leaf)) {
        throw "El AAB indicado por Gradle no existe: $aabPath"
    }
    $aabFile = Get-Item -LiteralPath $aabPath
    if ($aabFile.Length -le 0) {
        throw "El AAB generado esta vacio: $aabPath"
    }
    if ($aabFile.LastWriteTimeUtc -lt $bundleStartedUtc.AddSeconds(-2)) {
        throw "El AAB no fue generado por la ejecucion actual: $aabPath"
    }
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $archive = [System.IO.Compression.ZipFile]::OpenRead($aabPath)
    try {
        if ($null -eq $archive.GetEntry('base/manifest/AndroidManifest.xml')) {
            throw 'El AAB no contiene el manifiesto base esperado.'
        }
    } finally {
        $archive.Dispose()
    }
    $aabHash = (Get-FileHash -LiteralPath $aabPath -Algorithm SHA256).Hash
    Write-Ok "AAB confirmado para versionCode $configuredVersionCode / versionName $configuredVersionName"
    Write-Host "AAB: $aabPath"
    Write-Host "SHA-256: $aabHash"

    Write-Step 'Comprobacion de claves y contrasenas en el repositorio'
    & git -C $root rev-parse --is-inside-work-tree *> $null
    if ($LASTEXITCODE -ne 0) {
        throw 'No se pudo verificar el repositorio Git.'
    }
    $securityIssues = New-Object System.Collections.Generic.List[string]
    $protectedPathPattern = '(?i)(^|/)(keystore\.properties|[^/]+\.(jks|keystore|p12|pfx))$'

    $trackedFiles = @(& git -C $root -c core.quotePath=false ls-files)
    if ($LASTEXITCODE -ne 0) {
        throw 'No se pudo obtener la lista de archivos versionados.'
    }
    foreach ($relativePath in $trackedFiles) {
        $normalizedPath = ([string]$relativePath).Replace('\', '/')
        if ($normalizedPath -match $protectedPathPattern) {
            Add-SecurityIssue $securityIssues $normalizedPath 'material de firma versionado'
            continue
        }
        $absolutePath = Join-Path $root ([string]$relativePath)
        if (-not (Test-Path -LiteralPath $absolutePath -PathType Leaf)) { continue }
        $file = Get-Item -LiteralPath $absolutePath
        if ($file.Length -gt 2MB) { continue }
        try {
            $content = [System.IO.File]::ReadAllText($file.FullName)
        } catch {
            continue
        }
        if ($content -match '-----BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY-----') {
            Add-SecurityIssue $securityIssues $normalizedPath 'clave privada incrustada'
        }
        $signingProperties = [regex]::Matches(
                $content,
                '(?im)^\s*(RELEASE_STORE_FILE|RELEASE_STORE_PASSWORD|RELEASE_KEY_ALIAS|RELEASE_KEY_PASSWORD)\s*[:=]\s*(.*?)\s*$')
        foreach ($property in $signingProperties) {
            $propertyName = $property.Groups[1].Value
            $propertyValue = $property.Groups[2].Value
            if (-not (Test-Placeholder $propertyValue)) {
                Add-SecurityIssue $securityIssues $normalizedPath "valor real para $propertyName"
            }
        }
        $hardcodedGradleValues = [regex]::Matches(
                $content,
                '(?im)\b(storeFile|storePassword|keyAlias|keyPassword)\s+(["''])(.*?)\2')
        foreach ($hardcoded in $hardcodedGradleValues) {
            if (-not (Test-Placeholder $hardcoded.Groups[3].Value)) {
                Add-SecurityIssue $securityIssues $normalizedPath "credencial de firma escrita directamente"
            }
        }
    }

    $historyObjects = @(& git -C $root rev-list --objects --all)
    if ($LASTEXITCODE -ne 0) {
        throw 'No se pudo revisar el historial de nombres de archivos de Git.'
    }
    foreach ($objectLine in $historyObjects) {
        $separator = ([string]$objectLine).IndexOf(' ')
        if ($separator -lt 0) { continue }
        $historicalPath = ([string]$objectLine).Substring($separator + 1).Replace('\', '/')
        if ($historicalPath -match $protectedPathPattern) {
            Add-SecurityIssue $securityIssues $historicalPath 'material de firma presente en el historial de Git'
        }
    }

    foreach ($probe in @('tools/__signing_probe/keystore.properties', 'tools/__signing_probe/key.jks',
            'tools/__signing_probe/key.keystore', 'tools/__signing_probe/key.p12', 'tools/__signing_probe/key.pfx')) {
        & git -C $root check-ignore --no-index -q -- $probe
        if ($LASTEXITCODE -ne 0) {
            Add-SecurityIssue $securityIssues $probe 'no esta protegido por .gitignore'
        }
    }

    if ($securityIssues.Count -gt 0) {
        Write-Host 'Se encontraron riesgos de firma (los valores sensibles no se muestran):' -ForegroundColor Red
        foreach ($issue in $securityIssues) {
            Write-Host " - $issue" -ForegroundColor Red
        }
        throw "La comprobacion de seguridad encontro $($securityIssues.Count) problema(s)."
    }
    Write-Ok 'No hay claves, almacenes ni credenciales reales en los archivos versionados.'

    Write-Host ''
    Write-Host 'CHECKLIST COMPLETADO CORRECTAMENTE.' -ForegroundColor Green
    Write-Host 'No se ha subido ningun archivo a Google Play.' -ForegroundColor Yellow
} catch {
    Write-Host ''
    Write-Host ("ERROR DE PREPUBLICACION: {0}" -f $_.Exception.Message) -ForegroundColor Red
    Write-Host ("El proceso se detuvo en el paso {0} de {1}." -f $script:StepNumber, $script:TotalSteps) -ForegroundColor Red
    exit 1
} finally {
    Pop-Location
}
