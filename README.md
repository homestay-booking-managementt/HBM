1. Tải Java 17
https://download.oracle.com/java/17/archive/jdk-17.0.12_windows-x64_bin.exe
2. Tải mysql server(8.4.7 LTS)
https://dev.mysql.com/downloads/mysql/
3. Build jwt-core(bằng maven): clean and package(tự tra chat nếu dùng vscode)
4. Vào file application.properties của từng service, đổi spring.datasource.password nếu mật khẩu của mysql không phải là "root"
5. Run
