# Guia de Uso da Plataforma

Este documento mostra como usar a plataforma por perfil de usuário.

## 1) Acesso

1. Acesse `http://localhost:5173`.
2. Faça login na tela inicial.
3. O sistema direciona a navegação conforme o perfil autenticado.

## 2) Perfis de acesso

- `ADMINISTRADOR`
  - gestão de usuários
  - gestão de blocos, andares e salas
  - acesso aos dashboards
- `PROFESSOR`
  - check-in/checkout em sala
  - consulta de salas
  - visualização de alunos presentes na sala atual
- `ALUNO`
  - check-in/checkout em sala
  - consulta de salas

## 3) Usuários de teste (dataloader)

Exemplos de usernames:

- `fabio.costa` (administrador)
- `mariana.alves` (professor)
- `ricardo.nogueira` (professor)
- `ana.beatriz` até `gabriela.teixeira` (alunos)

Senhas padrão no dataloader:

- administrador: `admin123`
- professores: `prof123`
- alunos: `aluno123`

Referência: `backend/src/main/java/com/pucpr/casetecnico/backend/usuarios/bootstrap/UsuarioDataLoader.java`

## 4) Fluxo recomendado - administrador

1. Abrir `Cadastro de Usuários` e validar usuários ativos/inativos.
2. Abrir `Blocos/Andares/Salas` e manter estrutura do campus.
3. Acessar `Dashboards` para acompanhar ocupação atual e indicadores.

## 5) Fluxo recomendado - professor/aluno

1. Acessar `Check-in de Salas`.
2. Buscar sala por bloco/andar/sala ou navegar pela estrutura.
3. Realizar check-in.
4. Finalizar com checkout ao sair da sala.

## 6) Rotas principais no frontend

- `/login` - autenticação
- `/` - página inicial
- `/about` - sobre o sistema
- `/usuarios` - gestão de usuários (admin)
- `/blocos` - gestão de espaços (admin)
- `/presenca` - check-in/checkout
- `/relatorios` - dashboards (admin)

## 7) API - pontos de entrada

- Login: `POST /api/auth/login`
- Usuário autenticado: `GET /api/auth/me`
- Busca de salas para presença: `GET /api/presencas/salas`
- Check-in: `POST /api/presencas/checkin`
- Checkout: `PATCH /api/presencas/checkout`
- Dashboards: `GET /api/dashboards/*`

Para explorar todos os endpoints: `http://localhost:8080/swagger-ui.html`

## 8) Validação rápida do sistema

1. Fazer login com perfil administrador.
2. Conferir listagem de usuários e espaços.
3. Fazer login com professor ou aluno.
4. Realizar check-in e checkout.
5. Voltar com administrador e validar dashboards.

