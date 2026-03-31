import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import logoPuc from '../../assets/LogoPucQuadrado.png';

const TEST_USERS = [
  { nome: 'Fabio Costa', username: 'fabio.costa', senha: 'admin123', papel: 'ADMINISTRADOR' },
  { nome: 'Mariana Alves', username: 'mariana.alves', senha: 'prof123', papel: 'PROFESSOR' },
  { nome: 'Ricardo Nogueira', username: 'ricardo.nogueira', senha: 'prof123', papel: 'PROFESSOR' },
  { nome: 'Ana Beatriz Lima', username: 'ana.beatriz', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Bruno Henrique Souza', username: 'bruno.henrique', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Carolina Martins', username: 'carolina.martins', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Daniel Rocha', username: 'daniel.rocha', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Eduarda Fernandes', username: 'eduarda.fernandes', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Felipe Araujo', username: 'felipe.araujo', senha: 'aluno123', papel: 'ALUNO' },
  { nome: 'Gabriela Teixeira', username: 'gabriela.teixeira', senha: 'aluno123', papel: 'ALUNO' },
];

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador',
  PROFESSOR: 'Professor',
  ALUNO: 'Aluno',
};

const ROLE_ORDER = ['ADMINISTRADOR', 'PROFESSOR', 'ALUNO'];

export default function LoginPage() {
  const { login, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [senha, setSenha] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const usersByRole = TEST_USERS.reduce((acc, user) => {
    if (!acc[user.papel]) {
      acc[user.papel] = [];
    }
    acc[user.papel].push(user);
    return acc;
  }, {});

  if (isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  async function handleLogin(event, credentials = null) {
    event?.preventDefault();
    const nextUsername = credentials?.username ?? username;
    const nextSenha = credentials?.senha ?? senha;

    if (!nextUsername || !nextSenha) {
      toast.error('Informe username e senha.');
      return;
    }

    setIsLoading(true);

    try {
      await login(nextUsername, nextSenha);
      toast.success('Login realizado com sucesso.');
      navigate('/', { replace: true });
    } catch (loginError) {
      toast.error(loginError.message || 'Falha ao autenticar.');
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section className="login-screen">
      <header className="login-topbar">
        <div className="topbar-brand">
          <img src={logoPuc} alt="PUCPR" className="brand-logo" />
          <h1>Case Tecnico - Acesso</h1>
        </div>
      </header>

      <div className="login-body">
        <aside className="login-sidebar">
          <h2>Ambiente de testes</h2>
          <p>Selecione um usuario para entrar rapidamente no sistema.</p>
        </aside>

        <div className="login-content">
          <div className="login-card professional">
            <h2>Entrar</h2>
            <p className="hint">Use suas credenciais ou escolha um card de usuario de teste.</p>

            <form onSubmit={handleLogin}>
              <label htmlFor="username">Username</label>
              <input
                id="username"
                value={username}
                onChange={(event) => setUsername(event.target.value)}
                autoComplete="username"
                placeholder="admin"
              />

              <label htmlFor="senha">Senha</label>
              <input
                id="senha"
                type="password"
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
                autoComplete="current-password"
                placeholder="********"
              />


              <button type="submit" className="btn-primary" disabled={isLoading}>
                {isLoading ? 'Entrando...' : 'Entrar no sistema'}
              </button>
            </form>
          </div>

          <aside className="users-panel">
            <h2>Usuarios de teste</h2>
            {ROLE_ORDER.map((role) => (
              <section key={role} className="users-group">
                <h3>{ROLE_LABELS[role]}</h3>
                <div className="users-grid">
                  {(usersByRole[role] ?? []).map((user) => (
                    <article className="test-user-card" key={user.username}>
                      <h4>{user.nome}</h4>
                      <p>
                        <strong>Username:</strong> {user.username}
                      </p>
                      <p>
                        <strong>Papel:</strong> {ROLE_LABELS[user.papel]}
                      </p>
                      <button
                        type="button"
                        className="btn-secondary"
                        onClick={(event) => handleLogin(event, user)}
                        disabled={isLoading}
                      >
                        Entrar com este usuario
                      </button>
                    </article>
                  ))}
                </div>
              </section>
            ))}
          </aside>
        </div>
      </div>
    </section>
  );
}



