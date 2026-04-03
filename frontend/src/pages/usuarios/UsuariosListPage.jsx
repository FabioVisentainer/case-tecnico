import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { getUsuarioById, listUsuarios, updateUsuarioStatus } from '../../services/usuarios/usuariosService';
import './UsuariosPage.css';

const ROLE_LABELS = {
  ADMINISTRADOR: 'Administrador(a)',
  PROFESSOR: 'Professor(a)',
  ALUNO: 'Aluno(a)',
};

export default function UsuariosListPage() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [usuarios, setUsuarios] = useState([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [mostrarInativos, setMostrarInativos] = useState(false);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [papel, setPapel] = useState('');
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [meta, setMeta] = useState({ totalPages: 0, totalElements: 0, first: true, last: true });
  const [localizacaoModal, setLocalizacaoModal] = useState({ isOpen: false, isLoading: false, data: null, error: '' });

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      setDebouncedSearch(search.trim());
      setPage(0);
    }, 300);
    return () => window.clearTimeout(timeoutId);
  }, [search]);

  async function loadUsuarios() {
    setIsLoading(true);
    try {
      const data = await listUsuarios({
        token,
        page,
        size,
        mostrarInativos,
        q: debouncedSearch,
        papel,
      });
      setUsuarios(data.content ?? []);
      setMeta({
        totalPages: data.totalPages ?? 0,
        totalElements: data.totalElements ?? 0,
        first: data.first ?? true,
        last: data.last ?? true,
      });
      setError('');
    } catch (loadError) {
      const message = loadError.message || 'Falha ao carregar usuários.';
      setError(message);
      toast.error(message);
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    loadUsuarios();
  }, [token, page, size, mostrarInativos, debouncedSearch, papel]);

  async function handleToggleStatus(usuario) {
    const nextStatus = !usuario.ativo;
    try {
      await updateUsuarioStatus({ token, id: usuario.id, ativo: nextStatus });

      toast.success(nextStatus ? 'Usuário reativado com sucesso.' : 'Usuário inativado com sucesso.');
      await loadUsuarios();
    } catch (statusError) {
      toast.error(statusError.message || 'Falha ao alterar status.');
    }
  }

  async function handleLocalizar(usuario) {
    setLocalizacaoModal({ isOpen: true, isLoading: true, data: null, error: '' });
    try {
      const detalhe = await getUsuarioById({ token, id: usuario.id });
      setLocalizacaoModal({ isOpen: true, isLoading: false, data: detalhe, error: '' });
    } catch (error) {
      setLocalizacaoModal({ isOpen: true, isLoading: false, data: null, error: error.message || 'Nao foi possivel localizar o usuario.' });
    }
  }

  function closeLocalizacaoModal() {
    setLocalizacaoModal({ isOpen: false, isLoading: false, data: null, error: '' });
  }

  async function handleCopiarLocalizacao() {
    const data = localizacaoModal.data;
    if (!data) return;

    const texto = data.emSala
      ? `${data.nome} - ${data.blocoAtual} / ${data.andarAtual} / ${data.salaAtual}`
      : `${data.nome} - Fora da universidade`;

    try {
      await navigator.clipboard.writeText(texto);
      toast.success('Localização copiada.');
    } catch {
      toast.error('Não foi possível copiar a localização.');
    }
  }

  return (
    <section className="usuarios-page">
      <div className="usuarios-header page-header-inline">
        <div>
          <h1>Usuários</h1>
          <p className="page-subtitle">Gerencie cadastro, perfil e status dos usuários.</p>
        </div>
        <Link to="/usuarios/criar" className="btn-primary link-as-button">
          + Novo usuário
        </Link>
      </div>

      <div className="usuarios-toolbar table-toolbar advanced">
        <input
          className="table-search"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Pesquisar por nome ou username"
        />

        <select
          className="table-filter"
          value={papel}
          onChange={(event) => {
            setPapel(event.target.value);
            setPage(0);
          }}
        >
          <option value="">Todos os papeis</option>
          <option value="ALUNO">Aluno(a)</option>
          <option value="PROFESSOR">Professor(a)</option>
        </select>

        <label className="switch-control compact">
          <span>Inativos</span>
          <button
            type="button"
            className={`switch ${mostrarInativos ? 'on' : ''}`}
            onClick={() => {
              setMostrarInativos((prev) => !prev);
              setPage(0);
            }}
            aria-pressed={mostrarInativos}
            aria-label="Exibir usuários inativos"
          >
            <span className="switch-knob" />
          </button>
        </label>
      </div>

      {error ? <p className="error">{error}</p> : null}
      {!error ? (
        <div className="usuarios-table-card users-table-wrap refined">
          <table className="users-table">
            <thead>
              <tr>
                <th>Usuário</th>
                <th>Username</th>
                <th>Ativo</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {usuarios.map((usuario) => (
                <tr key={usuario.id}>
                  <td>{`${ROLE_LABELS[usuario.papel] ?? 'Usuário(a)'}: ${usuario.nome}`}</td>
                  <td>{usuario.username}</td>
                  <td>{usuario.ativo ? 'Sim' : 'Não'}</td>
                  <td className="table-actions">
                    <button type="button" className="btn-secondary" onClick={() => navigate(`/usuarios/${usuario.id}/editar`)}>
                      Editar
                    </button>
                    <button type="button" className="btn-neutral" onClick={() => handleLocalizar(usuario)}>
                      Localizar
                    </button>
                    <button type="button" className="btn-neutral" onClick={() => handleToggleStatus(usuario)}>
                      {usuario.ativo ? 'Inativar' : 'Reativar'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}

      <div className="usuarios-pagination pagination-wrap">
        <button type="button" className="btn-neutral" onClick={() => setPage((prev) => Math.max(prev - 1, 0))} disabled={meta.first || isLoading}>
          Anterior
        </button>
        <span>
          Página {page + 1} de {Math.max(meta.totalPages, 1)} - {meta.totalElements} registros
        </span>
        <button type="button" className="btn-neutral" onClick={() => setPage((prev) => prev + 1)} disabled={meta.last || isLoading}>
          Próxima
        </button>
      </div>

      {localizacaoModal.isOpen ? (
        <div className="usuarios-modal-overlay" onClick={closeLocalizacaoModal}>
          <div className="usuarios-modal-content" onClick={(event) => event.stopPropagation()}>
            <div className="usuarios-modal-header">
              <h2>Localizar usuário</h2>
              <button type="button" className="modal-close" onClick={closeLocalizacaoModal}>×</button>
            </div>

            <div className="usuarios-modal-body">
              {localizacaoModal.isLoading ? <p>Localizando usuário...</p> : null}
              {!localizacaoModal.isLoading && localizacaoModal.error ? <p>{localizacaoModal.error}</p> : null}
              {!localizacaoModal.isLoading && !localizacaoModal.error && localizacaoModal.data ? (
                <>
                  <p><strong>Nome:</strong> {localizacaoModal.data.nome}</p>
                  <p><strong>Papel:</strong> {ROLE_LABELS[localizacaoModal.data.papel] ?? 'Usuário(a)'}</p>
                  {localizacaoModal.data.emSala ? (
                    <>
                      <p className="status-localizacao em-sala"><strong>Status:</strong> Em sala</p>
                      <p><strong>Local:</strong> {localizacaoModal.data.blocoAtual} / {localizacaoModal.data.andarAtual} / {localizacaoModal.data.salaAtual}</p>
                      <p><strong>Entrada:</strong> {localizacaoModal.data.entradaAtual ? new Date(localizacaoModal.data.entradaAtual).toLocaleString('pt-BR') : '-'}</p>
                    </>
                  ) : (
                    <p className="status-localizacao fora"><strong>Status:</strong> Professor/aluno não se encontra na universidade</p>
                  )}
                </>
              ) : null}
            </div>

            <div className="usuarios-modal-footer">
              {!localizacaoModal.isLoading && !localizacaoModal.error && localizacaoModal.data ? (
                <button type="button" className="btn-secondary" onClick={handleCopiarLocalizacao}>Copiar localização</button>
              ) : null}
              <button type="button" className="btn-neutral" onClick={closeLocalizacaoModal}>Fechar</button>
            </div>
          </div>
        </div>
      ) : null}
    </section>
  );
}




