import { createContext, useContext, useEffect, useMemo, useState } from 'react';

const AuthContext = createContext(null);
const STORAGE_KEY = 'case-tecnico-auth-token';

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem(STORAGE_KEY) ?? '');
  const [user, setUser] = useState(null);
  const [isLoadingUser, setIsLoadingUser] = useState(Boolean(token));

  async function fetchCurrentUser(nextToken) {
    const response = await fetch('/api/auth/me', {
      headers: {
        Authorization: `Bearer ${nextToken}`,
      },
    });

    if (!response.ok) {
      throw new Error('Sessao invalida. Faca login novamente.');
    }

    return response.json();
  }

  useEffect(() => {
    let cancelled = false;

    async function loadUserFromToken() {
      if (!token) {
        setUser(null);
        setIsLoadingUser(false);
        return;
      }

      setIsLoadingUser(true);
      try {
        const currentUser = await fetchCurrentUser(token);
        if (!cancelled) {
          setUser(currentUser);
        }
      } catch {
        if (!cancelled) {
          localStorage.removeItem(STORAGE_KEY);
          setToken('');
          setUser(null);
        }
      } finally {
        if (!cancelled) {
          setIsLoadingUser(false);
        }
      }
    }

    loadUserFromToken();

    return () => {
      cancelled = true;
    };
  }, [token]);

  const value = useMemo(
    () => ({
      token,
      user,
      isLoadingUser,
      isAuthenticated: Boolean(token),
      isAdmin: user?.papel === 'ADMINISTRADOR',
      async login(username, senha) {
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username, senha }),
        });

        const payload = await response.json().catch(() => ({}));

        if (!response.ok) {
          throw new Error(payload.message ?? 'Nao foi possivel realizar o login.');
        }

        const nextToken = payload.token ?? '';
        if (!nextToken) {
          throw new Error('Resposta de login sem token.');
        }

        const currentUser = await fetchCurrentUser(nextToken);
        localStorage.setItem(STORAGE_KEY, nextToken);
        setUser(currentUser);
        setToken(nextToken);
      },
      logout() {
        localStorage.removeItem(STORAGE_KEY);
        setToken('');
        setUser(null);
      },
    }),
    [isLoadingUser, token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider.');
  }
  return context;
}

