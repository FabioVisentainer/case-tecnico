# Case Técnico - Gestão de Espaços e Ocupação do Campus

Monorepo com frontend em React e backend em Spring Boot para controle de presença em ambientes de ensino e acompanhamento da ocupação do campus.

## O que este projeto entrega

- autenticação por JWT
- autorização por perfil (`ADMINISTRADOR`, `PROFESSOR`, `ALUNO`)
- gestão de usuários, blocos, andares e salas
- check-in e checkout em salas
- dashboards de ocupação
- documentação da API com Swagger

## Estrutura

- `frontend/` - React + Vite
- `backend/` - Spring Boot + Java 21

## Acesso rápido

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

## Documentação

- Setup, execução e configuração: [`docs/01-setup-execucao-configuracao.md`](docs/01-setup-execucao-configuracao.md)
- Guia de uso da plataforma: [`docs/02-guia-de-uso-da-plataforma.md`](docs/02-guia-de-uso-da-plataforma.md)
- Enunciado original do case: [`README-Case.md`](README-Case.md)

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

