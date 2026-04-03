# Setup, Execução e Configuração

Este guia descreve como configurar e executar o projeto localmente ou com Docker.

## 1) Pre-requisitos

- Java 21
- Node.js 20+
- npm
- Docker e Docker Compose (opcional)

## 2) Variáveis de ambiente

O projeto usa as seguintes variáveis:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`

### Exemplo de `.env` (raiz do projeto)

```dotenv
DB_NAME=bd_casetecnico
DB_USER=casetecnico
DB_PASSWORD=C@seTecn!co123
DB_PORT=3306
APP_JWT_SECRET=troque-por-um-segredo-grande
APP_JWT_EXPIRATION_MS=3600000
```

## 3) Execução local (sem Docker)

### 3.1 Backend

No PowerShell:

```powershell
Set-Location C:\PUCPR\case-tecnico\backend
Set-Item Env:DB_HOST localhost
Set-Item Env:DB_PORT 3306
Set-Item Env:DB_NAME bd_casetecnico
Set-Item Env:DB_USER casetecnico
Set-Item Env:DB_PASSWORD "C@seTecn!co123"
Set-Item Env:APP_JWT_SECRET "troque-por-um-segredo-grande"
.\gradlew.bat bootRun
```

No Linux/macOS:

```bash
cd backend
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=bd_casetecnico
export DB_USER=casetecnico
export DB_PASSWORD='C@seTecn!co123'
export APP_JWT_SECRET='troque-por-um-segredo-grande'
./gradlew bootRun
```

Backend: `http://localhost:8080`

### 3.2 Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`

## 4) Execução com Docker Compose

Na raiz do projeto:

```powershell
Set-Location C:\PUCPR\case-tecnico
docker compose up --build
```

Serviços:

- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- swagger: `http://localhost:8080/swagger-ui.html`

## 5) Banco de dados no Docker

- O container MySQL cria banco `bd_casetecnico`.
- O backend usa o usuário definido em `DB_USER` (padrão `casetecnico`).

Se houver erro de autenticação por mudança de usuário/senha, recrie os volumes:

```powershell
docker compose down -v
docker compose up --build
```

## 6) Swagger

- UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 7) Referências rápidas

- Compose: `docker-compose.yml`
- Perfil docker backend: `backend/src/main/resources/application-docker.properties`
- Perfil default backend: `backend/src/main/resources/application.properties`

