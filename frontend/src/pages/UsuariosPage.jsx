import { useEffect, useState } from 'react';
import { useAuth } from '../auth/AuthContext';

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador',
  PROFESSOR: 'Professor',
  ALUNO: 'Aluno',
};

export default function UsuariosPage() {
  const { token } = useAuth();
  const [usuarios, setUsuarios] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    async function loadUsuarios() {
      try {
        const response = await fetch('/api/usuarios', {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          throw new Error(response.status === 403 ? 'Acesso restrito ao administrador.' : 'Falha ao carregar usuarios.');
        }

        const data = await response.json();
        if (active) {
          setUsuarios(data);
          setError('');
        }
      } catch (loadError) {
        if (active) {
          setError(loadError.message || 'Falha ao carregar usuarios.');
        }
      }
    }

    loadUsuarios();
    return () => {
      active = false;
    };
  }, [token]);

  return (
    <section>
      <h1>Cadastro de Usuarios</h1>
      {error ? <p className="error">{error}</p> : null}
      {!error ? (
        <div className="users-table-wrap">
          <table className="users-table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>Username</th>
                <th>Papel</th>
                <th>Ativo</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.id}>
                  <td>{usuario.nome}</td>
                  <td>{usuario.username}</td>
                  <td>{ROLE_LABELS[usuario.papel] ?? usuario.papel}</td>
                  <td>{usuario.ativo ? 'Sim' : 'Nao'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </section>
  );
}

