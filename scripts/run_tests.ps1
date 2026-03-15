Set-Location $PSScriptRoot

[Console]::OutputEncoding = [System.Text.Encoding]::GetEncoding(850)
$OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "`nCompilando o projeto..." -ForegroundColor Cyan
mvn -f ../pom.xml compile -q

Write-Host "------------------------------------------" -ForegroundColor Yellow
Write-Host "Iniciando Bateria de Testes" -ForegroundColor Yellow
Write-Host "------------------------------------------" -ForegroundColor Yellow

if (Test-Path "test_results") {
    Remove-Item -Path "test_results/*" -Force -ErrorAction SilentlyContinue
} else {
    New-Item -ItemType Directory -Path "test_results" | Out-Null
}

$files = Get-ChildItem "../samples/*.java"
foreach ($file in $files) {
    $fileName = $file.Name
    Write-Host "Testando: $fileName... " -NoNewline

    $cleanOutput = cmd /c "mvn -f ../pom.xml exec:java -q -Dexec.mainClass=br.ufc.minijava.Main -Dexec.args=../samples/$fileName 2>&1" | Out-String

    $cleanOutput | Out-File "test_results/$fileName.log" -Encoding utf8

    $passou = $cleanOutput.Contains("sucesso")

    if ($fileName -match "Erro") {
        if (-not $passou) {
            Write-Host " [OK]" -ForegroundColor Green -NoNewline
            Write-Host " (Erro detectado)" -ForegroundColor Gray
        } else {
            Write-Host " [FALHA]" -ForegroundColor Red -NoNewline
            Write-Host " (Não detectou erro)" -ForegroundColor Red
        }
    } else {
        if ($passou) {
            Write-Host " [OK]" -ForegroundColor Green -NoNewline
            Write-Host " (Sucesso)" -ForegroundColor Gray
        } else {
            Write-Host " [FALHA]" -ForegroundColor Red -NoNewline
            Write-Host " (Deu erro indevido)" -ForegroundColor Red
        }
    }
}

Write-Host "------------------------------------------" -ForegroundColor Yellow
Write-Host "Logs gerados em: scripts/test_results/" -ForegroundColor Yellow