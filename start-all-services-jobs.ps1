# Start All Services as Background Jobs
# Services run in the same console but as background jobs

Write-Host "Starting all HBM services as background jobs..." -ForegroundColor Green

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
    
    Write-Host "Starting $serviceName..." -ForegroundColor Cyan
    
    $job = Start-Job -ScriptBlock {
        param($serviceDir, $debugPort)
        Set-Location $serviceDir
        $jvmArgs = "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=$debugPort"
        & .\mvnw.cmd spring-boot:run "-Dspring-boot.run.jvmArguments=$jvmArgs"
    } -ArgumentList "$PSScriptRoot\$serviceName", $debugPort -Name $serviceName
    
    Write-Host "  Job ID: $($job.Id)" -ForegroundColor Gray
}

Write-Host "`nAll services started as background jobs!" -ForegroundColor Green
Write-Host "Use 'Get-Job' to see job status" -ForegroundColor Yellow
Write-Host "Use 'Receive-Job -Id <id>' to see job output" -ForegroundColor Yellow
Write-Host "Use 'Stop-Job -Name <service-name>' to stop a specific service" -ForegroundColor Yellow
Write-Host "Use 'Get-Job | Stop-Job' to stop all services" -ForegroundColor Yellow
