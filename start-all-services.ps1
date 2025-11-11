# Start All HBM Services
# This script starts all microservices in separate PowerShell windows

Write-Host "Starting all HBM services..." -ForegroundColor Green

# Define services with their debug ports (using default ports from application properties)
$services = @(
    @{Name="auth-service"; DebugPort=5005},
    @{Name="admin-service"; DebugPort=5006},
    @{Name="booking-service"; DebugPort=5007},
    @{Name="homestay-service"; DebugPort=5008},
    @{Name="review-service"; DebugPort=5009},
    @{Name="api-gateway"; DebugPort=5010}
)

foreach ($service in $services) {
    $serviceName = $service.Name
    $debugPort = $service.DebugPort
    
    Write-Host "Starting $serviceName (debug port: $debugPort)..." -ForegroundColor Cyan
    
    # Start each service in a new PowerShell window
    $command = "cd '$PSScriptRoot\$serviceName'; .\mvnw.cmd spring-boot:run '-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$debugPort'"
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    
    # Small delay to avoid overwhelming the system
    Start-Sleep -Seconds 2
}

Write-Host "`nAll services started! Each service is running in its own window." -ForegroundColor Green
Write-Host "Service ports (from application.properties/yml):" -ForegroundColor Yellow
Write-Host "  - API Gateway: 8080 (debug: 5010)" -ForegroundColor White
Write-Host "  - Auth Service: 8081 (debug: 5005)" -ForegroundColor White
Write-Host "  - Admin Service: 8083 (debug: 5006)" -ForegroundColor White
Write-Host "  - Homestay Service: 8084 (debug: 5008)" -ForegroundColor White
Write-Host "  - Booking Service: (check app.properties) (debug: 5007)" -ForegroundColor White
Write-Host "  - Review Service: (check app.properties) (debug: 5009)" -ForegroundColor White
