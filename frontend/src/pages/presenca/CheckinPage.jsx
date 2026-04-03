import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarMinhaPresenca, buscarSalasAtivas, checkinSala, checkoutSala, buscarAlunosPresentesMinhaSala } from '../../services/presenca/presencaService';
import { getBlocoComDetalhes, listBlocos } from '../../services/espacos/blocosService';
import { listSalasPorAndar } from '../../services/espacos/salasService';
import './CheckinPage.css';

const BLOCO_PAGE_SIZE = 9;
const SALA_PAGE_SIZE = 12;

export default function CheckinPage() {
  const { token, user } = useAuth();
  const [activeCard, setActiveCard] = useState('atual');

  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [salasBusca, setSalasBusca] = useState([]);
  const [salasBuscaPage, setSalasBuscaPage] = useState(0);
  const [salasBuscaMeta, setSalasBuscaMeta] = useState({ first: true, last: true, totalPages: 0, totalElements: 0 });

  const [blocosNav, setBlocosNav] = useState([]);
  const [blocoSearch, setBlocoSearch] = useState('');
  const [debouncedBlocoSearch, setDebouncedBlocoSearch] = useState('');
  const [blocosPage, setBlocosPage] = useState(0);
  const [blocosMeta, setBlocosMeta] = useState({ first: true, last: true, totalPages: 0, totalElements: 0 });

  const [selectedBlocoNav, setSelectedBlocoNav] = useState(null);
  const [andaresNav, setAndaresNav] = useState([]);

  const [selectedAndarNav, setSelectedAndarNav] = useState(null);
  const [salasNav, setSalasNav] = useState([]);
  const [salasNavPage, setSalasNavPage] = useState(0);
  const [salasNavMeta, setSalasNavMeta] = useState({ first: true, last: true, totalPages: 0, totalElements: 0 });

  const [minhaPresenca, setMinhaPresenca] = useState(null);
  const [alunosPresentes, setAlunosPresentes] = useState([]);
  const [showAlunosModal, setShowAlunosModal] = useState(false);
  const [alunoSearch, setAlunoSearch] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      setDebouncedSearch(search.trim());
      setSalasBuscaPage(0);
    }, 300);
    return () => window.clearTimeout(timeoutId);
  }, [search]);

  useEffect(() => {
    const timeoutId = window.setTimeout(() => {
      setDebouncedBlocoSearch(blocoSearch.trim());
      setBlocosPage(0);
    }, 300);
    return () => window.clearTimeout(timeoutId);
  }, [blocoSearch]);

  useEffect(() => {
    carregarPresencaAtual();
  }, [token]);

  useEffect(() => {
    if (activeCard !== 'buscar') return;
    carregarBuscaSalas();
  }, [activeCard, token, debouncedSearch, salasBuscaPage]);

  useEffect(() => {
    if (activeCard !== 'navegar') return;
    carregarBlocosNavegacao();
  }, [activeCard, token, blocosPage, debouncedBlocoSearch]);

  useEffect(() => {
    if (activeCard !== 'navegar' || !selectedAndarNav) return;
    carregarSalasNavegacao();
  }, [activeCard, token, selectedAndarNav, salasNavPage]);

  async function carregarPresencaAtual() {
    try {
      const resposta = await buscarMinhaPresenca({ token });
      setMinhaPresenca(resposta);
      if (!resposta?.atual) {
        setAlunosPresentes([]);
        setShowAlunosModal(false);
        setAlunoSearch('');
      }
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar presença atual.');
    }
  }

  async function carregarBuscaSalas() {
    setIsLoading(true);
    try {
      const resposta = await buscarSalasAtivas({ token, q: debouncedSearch, page: salasBuscaPage, size: SALA_PAGE_SIZE });
      setSalasBusca(resposta.content ?? []);
      setSalasBuscaMeta({
        first: resposta.first ?? true,
        last: resposta.last ?? true,
        totalPages: resposta.totalPages ?? 0,
        totalElements: resposta.totalElements ?? 0,
      });
    } catch (error) {
      toast.error(error.message || 'Falha ao buscar salas.');
    } finally {
      setIsLoading(false);
    }
  }

  async function carregarBlocosNavegacao() {
    setIsLoading(true);
    try {
      const data = await listBlocos(token, blocosPage, BLOCO_PAGE_SIZE, false, debouncedBlocoSearch);
      setBlocosNav(data.content ?? []);
      setBlocosMeta({
        first: data.first ?? true,
        last: data.last ?? true,
        totalPages: data.totalPages ?? 0,
        totalElements: data.totalElements ?? 0,
      });
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar blocos.');
    } finally {
      setIsLoading(false);
    }
  }

  async function carregarSalasNavegacao() {
    if (!selectedAndarNav) return;

    setIsLoading(true);
    try {
      const data = await listSalasPorAndar(token, selectedAndarNav.id, salasNavPage, SALA_PAGE_SIZE);
      const ativas = (data.content ?? []).filter((sala) => sala.ativo);
      setSalasNav(ativas);
      setSalasNavMeta({
        first: data.first ?? true,
        last: data.last ?? true,
        totalPages: data.totalPages ?? 0,
        totalElements: data.totalElements ?? 0,
      });
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar salas do andar.');
    } finally {
      setIsLoading(false);
    }
  }

  async function entrarBloco(bloco) {
    setIsLoading(true);
    try {
      const detalhe = await getBlocoComDetalhes(token, bloco.id);
      setSelectedBlocoNav(bloco);
      setSelectedAndarNav(null);
      setAndaresNav((detalhe.andares ?? []).filter((andar) => andar.ativo));
      setSalasNav([]);
      setSalasNavPage(0);
      setSalasNavMeta({ first: true, last: true, totalPages: 0, totalElements: 0 });
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar andares do bloco.');
    } finally {
      setIsLoading(false);
    }
  }

  function entrarAndar(andar) {
    setSelectedAndarNav(andar);
    setSalasNavPage(0);
  }

  async function handleCheckin(salaId) {
    try {
      await checkinSala({ token, salaId });
      toast.success('Check-in realizado com sucesso!');
      await carregarPresencaAtual();
      setActiveCard('atual');
    } catch (error) {
      toast.error(error.message || 'Nao foi possivel realizar check-in.');
    }
  }

  async function handleCheckout() {
    try {
      await checkoutSala({ token });
      toast.success('Checkout realizado com sucesso!');
      await carregarPresencaAtual();
    } catch (error) {
      toast.error(error.message || 'Nao foi possivel realizar checkout.');
    }
  }

  async function handleOpenAlunosPresentesModal() {
    try {
      const alunos = await buscarAlunosPresentesMinhaSala({ token });
      setAlunosPresentes(alunos ?? []);
      setAlunoSearch('');
      setShowAlunosModal(true);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar alunos presentes.');
    }
  }

  const temCheckinAtivo = Boolean(minhaPresenca?.atual);
  const isProfessor = user?.papel === 'PROFESSOR';
  const alunosPresentesFiltrados = (alunosPresentes ?? []).filter((aluno) => {
    const termo = alunoSearch.trim().toLowerCase();
    if (!termo) return true;
    return (
      (aluno.nome ?? '').toLowerCase().includes(termo)
      || (aluno.username ?? '').toLowerCase().includes(termo)
    );
  });

  return (
    <section className="checkin-page">
      <div className="page-header-inline">
        <h1>Check-in de Sala</h1>
      </div>

      <div className="checkin-option-cards">
        <button type="button" className={`option-card ${activeCard === 'atual' ? 'active' : ''}`} onClick={() => setActiveCard('atual')}>
          <h3>1. Sala atual / Checkout</h3>
          <p>Veja sua sala atual e finalize sua presença.</p>
        </button>
        <button type="button" className={`option-card ${activeCard === 'buscar' ? 'active' : ''}`} onClick={() => setActiveCard('buscar')}>
          <h3>2. Buscar sala</h3>
          <p>Busque por bloco, andar ou sala e faça check-in.</p>
        </button>
        <button type="button" className={`option-card ${activeCard === 'navegar' ? 'active' : ''}`} onClick={() => setActiveCard('navegar')}>
          <h3>3. Navegar até a sala</h3>
          <p>Percorra blocos, andares e salas para check-in.</p>
        </button>
      </div>

      {activeCard === 'atual' ? (
        <div className="checkin-current-card">
          <h3>Minha situação atual</h3>
          {temCheckinAtivo ? (
            <>
              <p>
                Você está em: <strong>{minhaPresenca.atual.blocoNome} / {minhaPresenca.atual.andarNome} / {minhaPresenca.atual.salaNome}</strong>
              </p>
              {isProfessor ? <p>Alunos presentes: <strong>{minhaPresenca.atual.quantidadeAlunosPresentes ?? 0}</strong></p> : null}
              <div className="checkin-current-actions">
                {isProfessor ? (
                  <button type="button" className="btn btn-secondary" onClick={handleOpenAlunosPresentesModal}>
                    Ver alunos presentes
                  </button>
                ) : null}
                <button type="button" className="btn btn-primary" onClick={handleCheckout}>
                  Fazer checkout
                </button>
              </div>
            </>
          ) : (
            <p>Você não possui check-in ativo.</p>
          )}
        </div>
      ) : null}

      {showAlunosModal ? (
        <div className="checkin-modal-overlay" onClick={() => setShowAlunosModal(false)}>
          <div className="checkin-modal-content" onClick={(event) => event.stopPropagation()}>
            <div className="checkin-modal-header">
              <h3>Alunos presentes ({alunosPresentesFiltrados.length})</h3>
              <button type="button" className="modal-close" onClick={() => setShowAlunosModal(false)}>×</button>
            </div>
            <div className="alunos-presentes-list">
              <input
                type="text"
                className="alunos-search"
                value={alunoSearch}
                onChange={(event) => setAlunoSearch(event.target.value)}
                placeholder="Pesquisar aluno por nome ou username"
              />

              {alunosPresentesFiltrados.length === 0 ? (
                <p>Nenhum aluno presente no momento.</p>
              ) : (
                <ul>
                  {alunosPresentesFiltrados.map((aluno) => (
                    <li key={aluno.id}>{aluno.nome} ({aluno.username})</li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>
      ) : null}

      {activeCard === 'buscar' ? (
        <>
          <div className="table-toolbar advanced checkin-toolbar">
            <input
              className="table-search"
              value={search}
              onChange={(event) => setSearch(event.target.value)}
              placeholder="Pesquisar por bloco, andar ou sala"
            />
          </div>

          {isLoading ? <p>Carregando salas...</p> : null}

          <div className="checkin-results">
            {salasBusca.map((sala) => (
              <article key={sala.salaId} className="checkin-room-card">
                <h4>{sala.salaNome}</h4>
                <p>{sala.blocoNome} / {sala.andarNome}</p>
                <p>Capacidade: alunos {sala.lotacaoAlunos} | professores {sala.lotacaoProfessores}</p>
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => handleCheckin(sala.salaId)}
                  disabled={temCheckinAtivo}
                >
                  Fazer check-in
                </button>
              </article>
            ))}
          </div>

          <div className="pagination-wrap">
            <button type="button" className="btn-neutral" onClick={() => setSalasBuscaPage((prev) => Math.max(prev - 1, 0))} disabled={salasBuscaMeta.first || isLoading}>
              Anterior
            </button>
            <span>Página {salasBuscaPage + 1} de {Math.max(salasBuscaMeta.totalPages, 1)} - {salasBuscaMeta.totalElements} salas</span>
            <button type="button" className="btn-neutral" onClick={() => setSalasBuscaPage((prev) => prev + 1)} disabled={salasBuscaMeta.last || isLoading}>
              Próxima
            </button>
          </div>
        </>
      ) : null}

      {activeCard === 'navegar' ? (
        <div className="navigate-wrap">
          {!selectedBlocoNav ? (
            <>
              <div className="table-toolbar advanced checkin-toolbar">
                <input
                  className="table-search"
                  value={blocoSearch}
                  onChange={(event) => setBlocoSearch(event.target.value)}
                  placeholder="Pesquisar nome do bloco"
                />
              </div>

              <div className="checkin-results">
                {blocosNav.map((bloco) => (
                  <article key={bloco.id} className="checkin-room-card">
                    <h4>{bloco.nome}</h4>
                    <p>{bloco.totalAndares ?? 0} andares • {bloco.totalSalas ?? 0} salas</p>
                    <button type="button" className="btn btn-primary" onClick={() => entrarBloco(bloco)}>
                      Entrar no bloco
                    </button>
                  </article>
                ))}
              </div>

              <div className="pagination-wrap">
                <button type="button" className="btn-neutral" onClick={() => setBlocosPage((prev) => Math.max(prev - 1, 0))} disabled={blocosMeta.first || isLoading}>
                  Anterior
                </button>
                <span>Página {blocosPage + 1} de {Math.max(blocosMeta.totalPages, 1)} - {blocosMeta.totalElements} blocos</span>
                <button type="button" className="btn-neutral" onClick={() => setBlocosPage((prev) => prev + 1)} disabled={blocosMeta.last || isLoading}>
                  Próxima
                </button>
              </div>
            </>
          ) : null}

          {selectedBlocoNav && !selectedAndarNav ? (
            <>
              <div className="navigate-header">
                <button type="button" className="btn btn-secondary" onClick={() => setSelectedBlocoNav(null)}>Voltar aos blocos</button>
                <h3>{selectedBlocoNav.nome}</h3>
              </div>
              <div className="checkin-results">
                {andaresNav.map((andar) => (
                  <article key={andar.id} className="checkin-room-card">
                    <h4>{andar.nome}</h4>
                    <button type="button" className="btn btn-primary" onClick={() => entrarAndar(andar)}>
                      Entrar no andar
                    </button>
                  </article>
                ))}
              </div>
            </>
          ) : null}

          {selectedBlocoNav && selectedAndarNav ? (
            <>
              <div className="navigate-header">
                <button type="button" className="btn btn-secondary" onClick={() => setSelectedAndarNav(null)}>Voltar aos andares</button>
                <h3>{selectedBlocoNav.nome} / {selectedAndarNav.nome}</h3>
              </div>
              <div className="checkin-results">
                {salasNav.map((sala) => (
                  <article key={sala.id} className="checkin-room-card">
                    <h4>{sala.nome}</h4>
                    <p>Capacidade: alunos {sala.lotacaoAlunos} | professores {sala.lotacaoProfessores}</p>
                    <button
                      type="button"
                      className="btn btn-primary"
                      onClick={() => handleCheckin(sala.id)}
                      disabled={temCheckinAtivo}
                    >
                      Fazer check-in
                    </button>
                  </article>
                ))}
              </div>

              <div className="pagination-wrap">
                <button type="button" className="btn-neutral" onClick={() => setSalasNavPage((prev) => Math.max(prev - 1, 0))} disabled={salasNavMeta.first || isLoading}>
                  Anterior
                </button>
                <span>Página {salasNavPage + 1} de {Math.max(salasNavMeta.totalPages, 1)} - {salasNavMeta.totalElements} salas</span>
                <button type="button" className="btn-neutral" onClick={() => setSalasNavPage((prev) => prev + 1)} disabled={salasNavMeta.last || isLoading}>
                  Próxima
                </button>
              </div>
            </>
          ) : null}
        </div>
      ) : null}
    </section>
  );
}

