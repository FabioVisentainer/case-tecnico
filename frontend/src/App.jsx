import { NavLink, Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';
import HomePage from './pages/home/HomePage';
import AboutPage from './pages/home/AboutPage';
import LoginPage from './pages/auth/LoginPage';
import UsuariosListPage from './pages/usuarios/UsuariosListPage';
import UsuariosCreatePage from './pages/usuarios/UsuariosCreatePage';
import UsuariosEditPage from './pages/usuarios/UsuariosEditPage';
import BlocosListPage from './pages/espacos/BlocosListPage';
import CheckinPage from './pages/presenca/CheckinPage';
import DashboardsPage from './pages/presenca/DashboardsPage';
import RelatorioSalasPage from './pages/presenca/RelatorioSalasPage';
import DashboardGeralPage from './pages/presenca/DashboardGeralPage';
import DashboardBlocosPage from './pages/presenca/DashboardBlocosPage';
import DashboardSalasPage from './pages/presenca/DashboardSalasPage';
import DashboardHorariosPage from './pages/presenca/DashboardHorariosPage';
import logoPuc from './assets/LogoPucQuadrado.png';
import './App.css';

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador(a)',
  PROFESSOR: 'Professor(a)',
  ALUNO: 'Aluno(a)',
};

function PrivateLayout({ user, isAdmin, onLogout }) {
  const papelLabel = ROLE_LABELS[user?.papel] ?? 'Usuario(a)';
  const isAlunoOuProfessor = user?.papel === 'ALUNO' || user?.papel === 'PROFESSOR';

  return (
    <div className="shell">
      <header className="topbar">
        <div className="topbar-brand private">
          <img src={logoPuc} alt="PUCPR" className="brand-logo" />
          <div className="user-identity">
            <h2>Case Tecnico</h2>
            <div className="identity-row">
              <span className="role-chip">{papelLabel}</span>
              <p>{user?.nome ?? 'Usuario logado'}</p>
            </div>
          </div>
        </div>
        <button type="button" onClick={onLogout} className="link-button">
          Sair
        </button>
      </header>

      <div className="shell-body">
        <aside className="sidebar">
          <NavLink to="/" end>
            Inicio
          </NavLink>
          <NavLink to="/about">Sobre</NavLink>
          {isAlunoOuProfessor ? <NavLink to="/presenca">Check-in de Salas</NavLink> : null}
          {isAdmin ? <NavLink to="/blocos">Blocos/Andares/Salas</NavLink> : null}
          {isAdmin ? <NavLink to="/relatorios">Dashboards</NavLink> : null}
          {isAdmin ? <NavLink to="/usuarios">Cadastro de Usuários</NavLink> : null}
        </aside>

        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default function App() {
  const { isAuthenticated, isAdmin, isLoadingUser, logout, user } = useAuth();

  if (isAuthenticated && isLoadingUser) {
    return <div className="app-container">Carregando sessão...</div>;
  }

  return (
    <div className="app-container">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<PrivateLayout user={user} isAdmin={isAdmin} onLogout={logout} />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/blocos" element={<BlocosListPage />} />
            <Route path="/presenca" element={<CheckinPage />} />
            <Route path="/relatorios" element={<DashboardsPage />} />
            <Route path="/relatorios/salas" element={<RelatorioSalasPage />} />
            <Route path="/relatorios/geral" element={<DashboardGeralPage />} />
            <Route path="/relatorios/blocos" element={<DashboardBlocosPage />} />
            <Route path="/relatorios/salas-detalhado" element={<DashboardSalasPage />} />
            <Route path="/relatorios/horarios" element={<DashboardHorariosPage />} />
            <Route path="/usuarios" element={<UsuariosListPage />} />
            <Route path="/usuarios/criar" element={<UsuariosCreatePage />} />
            <Route path="/usuarios/:id/editar" element={<UsuariosEditPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to={isAuthenticated ? '/' : '/login'} replace />} />
      </Routes>
    </div>
  );
}

