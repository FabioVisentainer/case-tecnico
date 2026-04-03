import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { listBlocos, getBlocoComDetalhes, updateBlocoStatus } from '../../services/espacos/blocosService';
import { getAndarComDetalhes, updateAndarStatus } from '../../services/espacos/andarsService';
import { updateSalaStatus } from '../../services/espacos/salasService';
import BlocoFormModal from './BlocoFormModal';
import BlocoDetalheModal from './BlocoDetalheModal';
import AndarDetalheModal from './AndarDetalheModal';
import AndarFormModal from './AndarFormModal';
import SalaFormModal from './SalaFormModal';
import './BlocosListPage.css';

export default function BlocosListPage() {
  const { token } = useAuth();
  const [blocos, setBlocos] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [size] = useState(12);
  const [meta, setMeta] = useState({ totalPages: 0, totalElements: 0, first: true, last: true });
  const [mostrarInativos, setMostrarInativos] = useState(false);
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [showBlocoModal, setShowBlocoModal] = useState(false);
  const [blocoFormMode, setBlocoFormMode] = useState('create');
  const [blocoFormData, setBlocoFormData] = useState(null);
  const [selectedBloco, setSelectedBloco] = useState(null);
  const [selectedAndar, setSelectedAndar] = useState(null);
  const [showAndarFormModal, setShowAndarFormModal] = useState(false);
  const [andarFormMode, setAndarFormMode] = useState('create');
  const [andarFormData, setAndarFormData] = useState(null);
  const [showSalaFormModal, setShowSalaFormModal] = useState(false);
  const [salaFormMode, setSalaFormMode] = useState('create');
  const [salaFormData, setSalaFormData] = useState(null);
  const [salaFormAndar, setSalaFormAndar] = useState(null);
  const [salaFormBloco, setSalaFormBloco] = useState(null);
  const [confirmDialog, setConfirmDialog] = useState(null);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      setDebouncedSearch(search.trim());
      setPage(0);
    }, 300);
    return () => window.clearTimeout(timeoutId);
  }, [search]);

  useEffect(() => {
    setPage(0);
  }, [mostrarInativos]);

  useEffect(() => {
    loadBlocos();
  }, [token, mostrarInativos, debouncedSearch, page, size]);

  async function loadBlocos() {
    setIsLoading(true);
    try {
      const data = await listBlocos(token, page, size, mostrarInativos, debouncedSearch);
      setBlocos(data.content ?? []);
      setMeta({
        totalPages: data.totalPages ?? 0,
        totalElements: data.totalElements ?? 0,
        first: data.first ?? true,
        last: data.last ?? true,
      });
    } catch (error) {
      toast.error(error.message || 'Erro ao carregar blocos');
    } finally {
      setIsLoading(false);
    }
  }

  async function loadBlocoDetalheCompleto(blocoBase) {
    if (!blocoBase?.id) return null;

    const detalhe = await getBlocoComDetalhes(token, blocoBase.id);
    const andaresDetalhados = await Promise.all(
      (detalhe.andares ?? []).map(async (andar) => {
        const andarDetalhe = await getAndarComDetalhes(token, andar.id);
        return {
          ...andar,
          ...andarDetalhe,
          totalSalas: andarDetalhe.salas?.length ?? 0,
        };
      }),
    );

    return {
      ...blocoBase,
      ...detalhe,
      andares: andaresDetalhados,
      totalAndares: andaresDetalhados.length,
      totalSalas: andaresDetalhados.reduce((acc, andar) => acc + (andar.totalSalas ?? 0), 0),
    };
  }

  function openCreateBloco() {
    setBlocoFormMode('create');
    setBlocoFormData(null);
    setShowBlocoModal(true);
  }

  function openBlockEdit(bloco) {
    setBlocoFormMode('edit');
    setBlocoFormData(bloco);
    window.setTimeout(() => setShowBlocoModal(true), 0);
  }

  async function openBlockDetail(bloco) {
    try {
      setSelectedBloco(await loadBlocoDetalheCompleto(bloco));
      setSelectedAndar(null);
    } catch (error) {
      toast.error(error.message || 'Erro ao carregar detalhes do bloco');
    }
  }

  function openCreateAndar() {
    if (!selectedBloco) return;
    setAndarFormMode('create');
    setAndarFormData(null);
    setShowAndarFormModal(true);
  }

  async function openAndarDetail(andar) {
    try {
      const detalhe = await getAndarComDetalhes(token, andar.id);
      setSelectedAndar(detalhe);
    } catch (error) {
      toast.error(error.message || 'Erro ao carregar detalhes do andar');
    }
  }

  function openAndarEdit(andar) {
    if (!selectedBloco || !andar) return;
    setAndarFormMode('edit');
    setAndarFormData(andar);
    window.setTimeout(() => setShowAndarFormModal(true), 0);
  }

  function openCreateSala() {
    if (!selectedBloco || !selectedAndar) return;
    setSalaFormMode('create');
    setSalaFormData(null);
    setSalaFormAndar(selectedAndar);
    setSalaFormBloco(selectedBloco);
    setShowSalaFormModal(true);
  }

  function openEditSala(sala) {
    if (!selectedBloco || !selectedAndar) return;
    setSalaFormMode('edit');
    setSalaFormData(sala);
    setSalaFormAndar(selectedAndar);
    setSalaFormBloco(selectedBloco);
    window.setTimeout(() => setShowSalaFormModal(true), 0);
  }

  function requestConfirm(entityLabel, entityName, onConfirm) {
    setConfirmDialog({
      title: `${entityLabel} ${entityName}`,
      message: `Tem certeza que deseja ${entityLabel.toLowerCase()}?`,
      onConfirm,
    });
  }

  async function toggleStatusBloco(bloco) {
    requestConfirm(
      bloco.ativo ? 'Inativar bloco' : 'Reativar bloco',
      `"${bloco.nome}"`,
      async () => {
        try {
          await updateBlocoStatus(token, bloco.id, !bloco.ativo);
          toast.success(bloco.ativo ? 'Bloco inativado com sucesso!' : 'Bloco reativado com sucesso!');
          if (selectedBloco?.id === bloco.id) {
            setSelectedBloco(await loadBlocoDetalheCompleto(bloco.id, bloco));
          }
          if (selectedAndar) {
            setSelectedAndar(await getAndarComDetalhes(token, selectedAndar.id));
          }
          await loadBlocos();
        } catch (error) {
          toast.error(error.message || 'Erro ao alterar status do bloco');
        }
      },
    );
  }

  async function toggleStatusAndar(andar) {
    requestConfirm(
      andar.ativo ? 'Inativar andar' : 'Reativar andar',
      `"${andar.nome}"`,
      async () => {
        try {
          await updateAndarStatus(token, andar.id, !andar.ativo);
          toast.success(andar.ativo ? 'Andar inativado com sucesso!' : 'Andar reativado com sucesso!');
          if (selectedBloco?.id) {
            setSelectedBloco(await loadBlocoDetalheCompleto(selectedBloco.id, selectedBloco));
          }
          const refreshedAndar = await getAndarComDetalhes(token, andar.id);
          setSelectedAndar(refreshedAndar);
          await loadBlocos();
        } catch (error) {
          toast.error(error.message || 'Erro ao alterar status do andar');
        }
      },
    );
  }

  async function toggleStatusSala(sala) {
    requestConfirm(
      sala.ativo ? 'Inativar sala' : 'Reativar sala',
      `"${sala.nome}"`,
      async () => {
        try {
          await updateSalaStatus(token, sala.id, !sala.ativo);
          toast.success(sala.ativo ? 'Sala inativada com sucesso!' : 'Sala reativada com sucesso!');
          const refreshedAndar = selectedAndar ? await getAndarComDetalhes(token, selectedAndar.id) : null;
          if (refreshedAndar) {
            setSelectedAndar(refreshedAndar);
          }
          if (selectedBloco?.id) {
            setSelectedBloco(await loadBlocoDetalheCompleto(selectedBloco.id, selectedBloco));
          }
          await loadBlocos();
        } catch (error) {
          toast.error(error.message || 'Erro ao alterar status da sala');
        }
      },
    );
  }

  return (
    <div className="blocos-page">
      <div className="page-header">
        <h1>Gerenciar Blocos</h1>
        <p>Organize andares e salas por bloco</p>
      </div>

      <div className="create-bloco-section">
        <button className="btn btn-primary" onClick={openCreateBloco}>
          + Novo Bloco
        </button>
      </div>

      <div className="table-toolbar advanced bloco-toolbar">
        <input
          className="table-search"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Pesquisar por nome do bloco"
        />

        <label className="switch-control compact">
          <span>Mostrar inativos</span>
          <button
            type="button"
            className={`switch ${mostrarInativos ? 'on' : ''}`}
            onClick={() => setMostrarInativos((prev) => !prev)}
            aria-pressed={mostrarInativos}
            aria-label="Exibir blocos inativos"
          >
            <span className="switch-knob" />
          </button>
        </label>
      </div>

      {isLoading ? (
        <div className="loading">Carregando blocos...</div>
      ) : blocos.length === 0 ? (
        <div className="empty-state">
          <p>Nenhum bloco cadastrado ainda.</p>
          <p>Clique em "Novo Bloco" para comecar.</p>
        </div>
      ) : (
        <div className="blocos-grid">
          {blocos.map((bloco) => (
            <article key={bloco.id} className="bloco-card bloco-card-summary" onClick={() => openBlockDetail(bloco)}>
              <div className="card-header">
                <h2>
                  {bloco.nome}
                  {!bloco.ativo ? <span className="status-tag inativo">Inativo</span> : null}
                </h2>
              </div>

              <div className="bloco-card-metrics">
                <div className="metric-pill">
                  <strong>{bloco.totalAndares ?? 0}</strong>
                  <span>Andares</span>
                </div>
                <div className="metric-pill">
                  <strong>{bloco.totalSalas ?? 0}</strong>
                  <span>Salas</span>
                </div>
              </div>

              <div className="card-actions bloco-card-actions">
                <button
                  type="button"
                  className="btn btn-small btn-primary"
                  onClick={(event) => {
                    event.stopPropagation();
                    openBlockDetail(bloco);
                  }}
                >
                  Ver detalhes
                </button>
                <button
                  type="button"
                  className="btn btn-small btn-danger"
                  onClick={(event) => {
                    event.stopPropagation();
                    toggleStatusBloco(bloco);
                  }}
                >
                  {bloco.ativo ? 'Inativar' : 'Reativar'}
                </button>
              </div>
            </article>
          ))}
        </div>
      )}

      <div className="pagination-wrap blocos-pagination">
        <button
          type="button"
          className="btn-neutral"
          onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
          disabled={meta.first || isLoading}
        >
          Anterior
        </button>
        <span>
          Pagina {page + 1} de {Math.max(meta.totalPages, 1)} - {meta.totalElements} registros
        </span>
        <button
          type="button"
          className="btn-neutral"
          onClick={() => setPage((prev) => prev + 1)}
          disabled={meta.last || isLoading}
        >
          Proxima
        </button>
      </div>

      <BlocoFormModal
        key={`bloco-${blocoFormMode}-${blocoFormData?.id ?? 'new'}`}
        isOpen={showBlocoModal}
        mode={blocoFormMode}
        initialData={blocoFormData}
        onClose={() => {
          setShowBlocoModal(false);
          setBlocoFormData(null);
          setBlocoFormMode('create');
        }}
        onSuccess={async () => {
          await loadBlocos();
          if (selectedBloco?.id) setSelectedBloco(await loadBlocoDetalheCompleto(selectedBloco));
        }}
      />

      <BlocoDetalheModal
        bloco={selectedBloco}
        isOpen={Boolean(selectedBloco)}
        onClose={() => {
          setSelectedBloco(null);
          setSelectedAndar(null);
        }}
        onCreateAndar={openCreateAndar}
        onOpenAndar={openAndarDetail}
        onEditBloco={() => selectedBloco && openBlockEdit(selectedBloco)}
        onStatusBloco={() => selectedBloco && toggleStatusBloco(selectedBloco)}
      />

      <AndarDetalheModal
        andar={selectedAndar}
        blocoAtivo={selectedBloco?.ativo ?? true}
        isOpen={Boolean(selectedAndar)}
        onClose={() => setSelectedAndar(null)}
        onEditAndar={() => openAndarEdit(selectedAndar)}
        onStatusAndar={() => selectedAndar && toggleStatusAndar(selectedAndar)}
        onCreateSala={openCreateSala}
        onEditSala={openEditSala}
        onStatusSala={toggleStatusSala}
      />

      {showAndarFormModal && selectedBloco && (
        <AndarFormModal
          key={`andar-${andarFormMode}-${andarFormData?.id ?? 'new'}`}
          blocoId={selectedBloco.id}
          blocoNome={selectedBloco.nome}
          mode={andarFormMode}
          initialData={andarFormData}
          isOpen={showAndarFormModal}
          onClose={() => {
            setShowAndarFormModal(false);
            setAndarFormData(null);
            setAndarFormMode('create');
          }}
          onSuccess={async () => {
            await loadBlocos();
            if (selectedBloco?.id) setSelectedBloco(await loadBlocoDetalheCompleto(selectedBloco));
            if (selectedAndar?.id) setSelectedAndar(await getAndarComDetalhes(token, selectedAndar.id));
          }}
        />
      )}

      {showSalaFormModal && salaFormAndar && salaFormBloco && (
        <SalaFormModal
          key={`sala-${salaFormMode}-${salaFormData?.id ?? 'new'}`}
          andarId={salaFormAndar.id}
          andarNome={salaFormAndar.nome}
          blocoNome={salaFormBloco.nome}
          mode={salaFormMode}
          initialData={salaFormData}
          isOpen={showSalaFormModal}
          onClose={() => {
            setShowSalaFormModal(false);
            setSalaFormData(null);
            setSalaFormMode('create');
          }}
          onSuccess={async () => {
            await loadBlocos();
            if (salaFormBloco?.id) setSelectedBloco(await loadBlocoDetalheCompleto(salaFormBloco));
            if (salaFormAndar?.id) setSelectedAndar(await getAndarComDetalhes(token, salaFormAndar.id));
          }}
        />
      )}

      {confirmDialog ? (
        <div className="modal-overlay modal-overlay-front" onClick={() => setConfirmDialog(null)}>
          <div className="modal-content confirm-dialog" onClick={(event) => event.stopPropagation()}>
            <div className="modal-header">
              <h2>{confirmDialog.title}</h2>
              <button className="modal-close" onClick={() => setConfirmDialog(null)}>×</button>
            </div>
            <div className="confirm-dialog-body">
              <p>{confirmDialog.message}</p>
              <p>Essa ação pode alterar a disponibilidade de andares e salas vinculadas.</p>
            </div>
            <div className="confirm-dialog-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setConfirmDialog(null)}>
                Cancelar
              </button>
              <button
                type="button"
                className="btn btn-danger"
                onClick={async () => {
                  const action = confirmDialog.onConfirm;
                  setConfirmDialog(null);
                  await action?.();
                }}
              >
                Confirmar
              </button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

