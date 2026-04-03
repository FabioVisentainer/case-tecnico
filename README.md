# Case Técnico - Gestão de Espaços e Ocupação do Campus

Aplicação em monorepo para controlar o uso de espaços de ensino, registrar presença em salas e acompanhar a ocupação do campus em tempo real.

O projeto atende ao enunciado do case com:

- autenticação via token
- autorização por perfis de usuário
- cadastro de usuários
- registro de entrada e saída em ambientes de ensino
- dashboards de ocupação
- persistência em MySQL com Hibernate/JPA

## Estrutura do monorepo

- `frontend/` → aplicação React criada com Vite
- `backend/` → API Spring Boot em Java 21 com Gradle

## Stack utilizada

### Frontend
- React
- Vite
- React Router
- fetch para integração com a API

### Backend
- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA / Hibernate
- Spring Validation
- JWT
- MySQL
- Lombok
- Springdoc OpenAPI / Swagger

## Requisitos para executar

- Java 21
- Node.js 20+
- npm
- MySQL 8+
- Docker e Docker Compose, se quiser subir tudo junto

## Banco de dados

O backend utiliza MySQL com o banco:

- `bd_casetecnico`

As configurações de banco e JWT são lidas por variáveis de ambiente:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `APP_JWT_SECRET`
- `APP_JWT_EXPIRATION_MS`

O Hibernate está configurado para gerenciar as tabelas automaticamente conforme a estratégia definida no projeto.

## Perfis da aplicação

O backend trabalha com perfis de execução:

- `dev` → execução local
- `docker` → execução no Docker Compose

O perfil ativo é definido por `SPRING_PROFILES_ACTIVE`.

## Funcionalidades principais

### Autenticação e autorização
- login com JWT
- acesso por perfil:
  - `ADMINISTRADOR`
  - `PROFESSOR`
  - `ALUNO`
- endpoints públicos e protegidos conforme regra de negócio

### Administração
- cadastro, edição, listagem e inativação de usuários
- gestão de blocos, andares e salas
- dashboards para acompanhamento da ocupação

### Presença
- check-in em salas
- checkout de sala atual
- consulta da presença atual
- consulta de alunos presentes na mesma sala para professor

## Swagger / OpenAPI

A documentação da API está disponível no backend via Swagger.

Endpoints:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/v3/api-docs`

## Como executar localmente

### 1. Backend

Configure as variáveis de ambiente antes de subir o backend.

#### Windows PowerShell

```powershell
Set-Location C:\PUCPR\case-tecnico\backend
Set-Item Env:DB_HOST localhost
Set-Item Env:DB_PORT 3306
Set-Item Env:DB_NAME bd_casetecnico
Set-Item Env:DB_USER root
Set-Item Env:DB_PASSWORD "C@seTecn!co123"
Set-Item Env:APP_JWT_SECRET "sua-chave-jwt-aqui"
.\gradlew.bat bootRun
```

#### Linux / macOS

```bash
cd backend
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=bd_casetecnico
export DB_USER=root
export DB_PASSWORD='C@seTecn!co123'
export APP_JWT_SECRET='sua-chave-jwt-aqui'
./gradlew bootRun
```

O backend sobe na porta `8080`.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

O frontend sobe na porta `5173`.

## Como testar

### 1. Testar o backend

Com o backend rodando, acesse no navegador ou via cliente HTTP:

- `GET http://localhost:8080/api/hello`
- `GET http://localhost:8080/swagger-ui.html`

Exemplo no PowerShell:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/hello"
```

### 2. Testar o login

Faça login com um usuário de teste para obter o token JWT.

Exemplo:

```powershell
$body = @{ username = "admin"; senha = "admin123" } | ConvertTo-Json
$auth = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $body
$auth
```

### 3. Testar um endpoint protegido

Use o token retornado no login:

```powershell
Invoke-RestMethod -Uri "http://localhost:8080/api/private/hello" -Headers @{ Authorization = "Bearer $($auth.token)" }
```

### 4. Testar o frontend

Com o frontend rodando, abra:

- `http://localhost:5173`

Valide se:

- a página carrega corretamente
- o login funciona
- o redirecionamento por perfil ocorre corretamente
- os cards e dashboards aparecem conforme o usuário autenticado

### 5. Testar via Docker

Após subir com `docker compose up --build`, valide:

- frontend: `http://localhost:5173`
- backend: `http://localhost:8080`
- swagger: `http://localhost:8080/swagger-ui.html`

### 6. Testar o banco de dados

Verifique se o MySQL criou o banco `bd_casetecnico` e se as tabelas foram geradas pelo Hibernate ao iniciar a aplicação.

## Como executar com Docker Compose

Na raiz do projeto, defina as variáveis de ambiente e suba os serviços:

#### Windows PowerShell

```powershell
Set-Location C:\PUCPR\case-tecnico
Set-Item Env:DB_PASSWORD "C@seTecn!co123"
Set-Item Env:APP_JWT_SECRET "sua-chave-jwt-aqui"
docker compose up --build
```

#### Linux / macOS

```bash
cd case-tecnico
export DB_PASSWORD='C@seTecn!co123'
export APP_JWT_SECRET='sua-chave-jwt-aqui'
docker compose up --build
```

Serviços expostos:

- backend: `http://localhost:8080`
- frontend: `http://localhost:5173`
- MySQL: porta interna `3306`

No Docker, o frontend se comunica com o backend via proxy para `http://backend:8080`.

## Fluxo de uso

1. O usuário acessa o frontend e é redirecionado para o login.
2. Após autenticação, o sistema direciona conforme o perfil.
3. O administrador acessa cadastros e dashboards.
4. Professores e alunos usam check-in, checkout e navegação por salas.

## Usuários de teste

O projeto possui dados iniciais para facilitar a validação da aplicação.

Exemplos:

- `admin`
- `professor1`
- `professor2`
- `aluno1` até `aluno7`

As senhas podem ser consultadas no dataloader do backend.

## Rotas principais do frontend

- `/login` → autenticação
- `/` → página inicial institucional
- `/about` → apresentação do sistema
- `/usuarios` → cadastro de usuários
- `/blocos` → gestão de blocos, andares e salas
- `/presenca` → check-in e checkout
- `/relatorios` → dashboards

## Observações importantes

- O frontend não usa URL hardcoded da API; a integração acontece por proxy.
- O backend usa autenticação via token JWT.
- O projeto foi estruturado para manter separação entre interface, regras de negócio e persistência.
- O case original exigia CRUD de alunos e registro de entrada/saída; o projeto foi expandido para suportar o gerenciamento institucional completo do campus.

## Comandos rápidos

### Backend

```bash
cd backend
./gradlew bootRun
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Docker

```bash
docker compose up --build
```

