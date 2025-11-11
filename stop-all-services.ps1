# Stop All HBM Services
# This script stops all running Java processes (Spring Boot services)

Write-Host "Stopping all HBM services..." -ForegroundColor Red

# Find all java.exe processes running Spring Boot
$javaProcesses = Get-Process -Name "java" -ErrorAction SilentlyContinue

if ($javaProcesses) {
    Write-Host "Found $($javaProcesses.Count) Java process(es). Stopping..." -ForegroundColor Yellow
    
    foreach ($process in $javaProcesses) {
        try {
            Stop-Process -Id $process.Id -Force
            Write-Host "  Stopped process ID: $($process.Id)" -ForegroundColor Cyan
        } catch {
            Write-Host "  Failed to stop process ID: $($process.Id)" -ForegroundColor Red
        }
    }
    
    Write-Host "`nAll services stopped!" -ForegroundColor Green
} else {
    Write-Host "No Java processes found running." -ForegroundColor Yellow
}
