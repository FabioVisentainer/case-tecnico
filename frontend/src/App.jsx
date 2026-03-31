import { NavLink, Navigate, Outlet, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';
import HomePage from './pages/HomePage';
import AboutPage from './pages/AboutPage';
import LoginPage from './pages/LoginPage';
import UsuariosPage from './pages/UsuariosPage';
import './App.css';

function PrivateLayout({ user, isAdmin, onLogout }) {
  return (
    <div className="shell">
      <header className="topbar">
        <div>
          <h2>Case Tecnico</h2>
          <p>{user?.nome ?? 'Usuário logado'}</p>
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
          {isAdmin ? <NavLink to="/usuarios">Cadastro de Usuarios</NavLink> : null}
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
    return <div className="app-container">Carregando sessao...</div>;
  }

  return (
    <div className="app-container">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route element={<ProtectedRoute />}>
          <Route element={<PrivateLayout user={user} isAdmin={isAdmin} onLogout={logout} />}>
            <Route path="/" element={<HomePage />} />
            <Route path="/about" element={<AboutPage />} />
            <Route path="/usuarios" element={<UsuariosPage />} />
          </Route>
        </Route>
        <Route path="*" element={<Navigate to={isAuthenticated ? '/' : '/login'} replace />} />
      </Routes>
    </div>
  );
}

