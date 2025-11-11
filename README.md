1. Tải Java 17
https://download.oracle.com/java/17/archive/jdk-17.0.12_windows-x64_bin.exe
2. Tải mysql server(8.4.7 LTS)
https://dev.mysql.com/downloads/mysql/
3. Build jwt-core(bằng maven): clean and package(tự tra chat nếu dùng vscode)
4. Vào file application.properties của từng service, đổi spring.datasource.password nếu mật khẩu của mysql không phải là "root"

## 5. Run Services

### Option A: Run All Services (Recommended)
Chạy tất cả services trong các cửa sổ riêng biệt:
```powershell
.\start-all-services.ps1
```

Dừng tất cả services:
```powershell
.\stop-all-services.ps1
```

### Option B: Run All Services as Background Jobs
Chạy tất cả services như background jobs trong cùng một console:
```powershell
.\start-all-services-jobs.ps1
```

### Option C: Run Single Service (Manual)
Chạy một service riêng lẻ:
```powershell
cd .\auth-service\
.\mvnw.cmd spring-boot:run '-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005'
```

### Service Ports:
- **API Gateway**: http://localhost:8080 (debug: 5010)
- **Auth Service**: http://localhost:8081 (debug: 5005)
- **Admin Service**: http://localhost:8083 (debug: 5006)
- **Homestay Service**: http://localhost:8084 (debug: 5008)
- **Booking Service**: (check application.properties) (debug: 5007)
- **Review Service**: (check application.properties) (debug: 5009)