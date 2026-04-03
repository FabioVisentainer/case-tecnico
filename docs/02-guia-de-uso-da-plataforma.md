# Guia de Uso da Plataforma

Este documento mostra como usar a plataforma por perfil de usuario.

## 1) Acesso

1. Acesse `http://localhost:5173`.
2. Faça login na tela inicial.
3. O sistema direciona a navegacao conforme o perfil autenticado.

## 2) Perfis de acesso

- `ADMINISTRADOR`
  - gestao de usuarios
  - gestao de blocos, andares e salas
  - acesso aos dashboards
- `PROFESSOR`
  - check-in/checkout em sala
  - consulta de salas
  - visualizacao de alunos presentes na sala atual
- `ALUNO`
  - check-in/checkout em sala
  - consulta de salas

## 3) Usuarios de teste (dataloader)

Exemplos de usernames:

- `fabio.costa` (administrador)
- `mariana.alves` (professor)
- `ricardo.nogueira` (professor)
- `ana.beatriz` ate `gabriela.teixeira` (alunos)

Senhas padrao no dataloader:

- administrador: `admin123`
- professores: `prof123`
- alunos: `aluno123`

Referencia: `backend/src/main/java/com/pucpr/casetecnico/backend/usuarios/bootstrap/UsuarioDataLoader.java`

## 4) Fluxo recomendado - administrador

1. Abrir `Cadastro de Usuarios` e validar usuarios ativos/inativos.
2. Abrir `Blocos/Andares/Salas` e manter estrutura do campus.
3. Acessar `Dashboards` para acompanhar ocupacao atual e indicadores.

## 5) Fluxo recomendado - professor/aluno

1. Acessar `Check-in de Salas`.
2. Buscar sala por bloco/andar/sala ou navegar pela estrutura.
3. Realizar check-in.
4. Finalizar com checkout ao sair da sala.

## 6) Rotas principais no frontend

- `/login` - autenticacao
- `/` - pagina inicial
- `/about` - sobre o sistema
- `/usuarios` - gestao de usuarios (admin)
- `/blocos` - gestao de espacos (admin)
- `/presenca` - check-in/checkout
- `/relatorios` - dashboards (admin)

## 7) API - pontos de entrada

- Login: `POST /api/auth/login`
- Usuario autenticado: `GET /api/auth/me`
- Busca de salas para presenca: `GET /api/presencas/salas`
- Check-in: `POST /api/presencas/checkin`
- Checkout: `PATCH /api/presencas/checkout`
- Dashboards: `GET /api/dashboards/*`

Para explorar todos os endpoints: `http://localhost:8080/swagger-ui.html`

## 8) Validacao rapida do sistema

1. Fazer login com perfil administrador.
2. Conferir listagem de usuarios e espacos.
3. Fazer login com professor ou aluno.
4. Realizar check-in e checkout.
5. Voltar com administrador e validar dashboards.

