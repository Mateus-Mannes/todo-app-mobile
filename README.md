# todo-app-mobile
Aplicativo Todo para Android desenvolvido em Java

Rodar base postgres:
docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres postgres

Gerar certificado https (rodar na pasta do projeto da API -> todo-api/):
keytool -genkeypair -alias todoapp -keyalg RSA -keystore ./src/main/resources/keystore.jks -keysize 2048 -dname "CN=localhost, OU=Dev, O=My Company, L=City, ST=State, C=US" -noprompt -storepass "123456"