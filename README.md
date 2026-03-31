# Monorepo - Frontend React + Backend Spring Boot

Estrutura do projeto:

- `frontend`: aplicacao React criada com Vite
- `backend`: API Spring Boot (Java 21 + Gradle)

## Requisitos

- Java 21
- Node.js 20+
- Docker e Docker Compose (opcional, para subir tudo junto)

## Backend

API Spring Boot com endpoint:

- `GET /api/hello` -> `{ "message": "Hello from Spring Boot" }`

Banco de dados MySQL (Hibernate/JPA):

- Banco: `bd_casetecnico`
- Usuario: `root` (ajuste se necessario)
- Senha: `C@seTecn!co123`
- Hibernate: `spring.jpa.hibernate.ddl-auto=update`

Executar localmente:

```bash
cd backend
./gradlew bootRun
```

No Windows PowerShell:

```powershell
Set-Location backend
.\gradlew.bat bootRun
```

Backend roda na porta `8080`.

### Autenticacao JWT (backend)

- Login publico: `POST /api/auth/login`
- Endpoint publico: `GET /api/hello`
- Endpoint protegido: `GET /api/private/hello`
- Endpoints de usuarios (apenas ADMINISTRADOR):
  - `POST /api/usuarios`
  - `GET /api/usuarios`

Usuarios de carga inicial (dataloader):

- 7 alunos: `aluno1` ... `aluno7` (senha `aluno123`)
- 2 professores: `professor1`, `professor2` (senha `prof123`)
- 1 administrador: `admin` (senha `admin123`)

Exemplo de login no PowerShell:

```powershell
$body = @{ nome = "admin"; senha = "admin123" } | ConvertTo-Json
$auth = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $body
$auth
```

> O payload de login usa `nome` e `senha`.

Exemplo de chamada protegida:

```powershell
$token = $auth.token
Invoke-RestMethod -Uri "http://localhost:8080/api/private/hello" -Headers @{ Authorization = "Bearer $token" }
```


## Frontend

Aplicacao React com React Router:

- `/`: pagina principal que consome `/api/hello`
- `/about`: pagina de exemplo

O frontend nao usa URL hardcoded do backend; consome `/api/hello` e o Vite redireciona via proxy.

Executar localmente:

```bash
cd frontend
npm install
npm run dev
```

No Windows PowerShell:

```powershell
Set-Location frontend
npm install
npm run dev
```

Frontend roda na porta `5173`.

## Como testar a tela atual

1. Abra um terminal para o backend e rode:

```powershell
Set-Location C:\PUCPR\case-tecnico\backend
.\gradlew.bat bootRun
```

2. Abra outro terminal para o frontend e rode:

```powershell
Set-Location C:\PUCPR\case-tecnico\frontend
npm install
npm run dev
```

3. Abra `http://localhost:5173` no navegador.
4. Na rota `/`, a tela deve exibir a mensagem vinda de `GET /api/hello`.
5. Se o backend estiver desligado, a tela mostra uma mensagem amigavel de erro.

### Teste rapido da seguranca (API)

```powershell
$body = @{ username = "user"; password = "password" } | ConvertTo-Json
$auth = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" -ContentType "application/json" -Body $body
Invoke-RestMethod -Uri "http://localhost:8080/api/private/hello" -Headers @{ Authorization = "Bearer $($auth.token)" }
```

## Proxy Vite

Arquivo: `frontend/vite.config.js`

- Proxy de `/api` para `http://localhost:8080` no ambiente local.
- Em Docker Compose, `VITE_API_PROXY_TARGET` e definido como `http://backend:8080`.

## Docker Compose

Na raiz do projeto:

```bash
docker compose up --build
```

Servicos:

- `backend`: porta `8080`
- `frontend`: porta `5173`

A comunicacao frontend -> backend ocorre pelo proxy `/api`.
