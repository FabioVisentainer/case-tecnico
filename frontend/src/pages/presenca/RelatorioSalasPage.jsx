import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarRelatorioUsoAtualDashboard } from '../../services/dashboard/dashboardService';
import DashboardPageHeader from '../../components/DashboardPageHeader';
import './RelatorioSalasPage.css';

function resumirPorBloco(salas) {
  const blocos = new Map();

  salas.forEach((sala) => {
    const blocoNome = sala.blocoNome ?? 'Sem bloco';
    if (!blocos.has(blocoNome)) {
      blocos.set(blocoNome, {
        blocoNome,
        professoresPresentes: 0,
        alunosPresentes: 0,
        capacidadeTotal: 0,
      });
    }

    const acumulado = blocos.get(blocoNome);
    acumulado.professoresPresentes += sala.professoresPresentes ?? 0;
    acumulado.alunosPresentes += sala.alunosPresentes ?? 0;
    acumulado.capacidadeTotal += sala.capacidadeTotal ?? 0;
  });

  return [...blocos.values()]
    .map((bloco) => {
      const totalPresentes = bloco.professoresPresentes + bloco.alunosPresentes;
      return {
        ...bloco,
        percentualOcupacaoAtual: bloco.capacidadeTotal > 0 ? (totalPresentes * 100) / bloco.capacidadeTotal : 0,
      };
    })
    .sort((a, b) => a.blocoNome.localeCompare(b.blocoNome));
}

function ordenarPorOcupacao(salas) {
  return [...salas].sort((a, b) => (b.percentualOcupacaoAtual ?? 0) - (a.percentualOcupacaoAtual ?? 0));
}

export default function RelatorioSalasPage() {
  const { token } = useAuth();

  const [snapshot, setSnapshot] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    carregarTudo();
  }, []);

  async function carregarTudo() {
    setIsLoading(true);
    try {
      const snapshotData = await buscarRelatorioUsoAtualDashboard({ token });
      setSnapshot(snapshotData);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar dashboard de ocupação.');
    } finally {
      setIsLoading(false);
    }
  }

  const salasEmUso = snapshot?.salasEmUso ?? [];
  const ocupacaoPorBloco = resumirPorBloco(salasEmUso);
  const topSalas = ordenarPorOcupacao(salasEmUso).slice(0, 10);

  return (
    <section className="relatorio-salas-page dashboard-page">
      <DashboardPageHeader
        title="Dashboard de Ocupação Atual"
        actions={(
          <button type="button" className="btn-primary" onClick={carregarTudo} disabled={isLoading}>
            Atualizar
          </button>
        )}
      />

      <div className="dashboard-kpis">
        <article className="kpi-card">
          <h4>Ocupação campus (agora)</h4>
          <p>{snapshot?.percentualOcupacaoMedia?.toFixed?.(2) ?? '0.00'}%</p>
        </article>
        <article className="kpi-card">
          <h4>Salas em uso</h4>
          <p>{snapshot?.totalSalasEmUso ?? 0}</p>
        </article>
        <article className="kpi-card">
          <h4>Professores presentes</h4>
          <p>{snapshot?.totalProfessoresPresentes ?? 0}</p>
        </article>
        <article className="kpi-card">
          <h4>Alunos presentes</h4>
          <p>{snapshot?.totalAlunosPresentes ?? 0}</p>
        </article>
      </div>

      <div className="dashboard-grid-two">
        <article className="panel-card">
          <h3>Ocupação por bloco (agora)</h3>
          <div className="bars-list">
            {ocupacaoPorBloco.map((bloco) => (
              <div className="bar-item" key={bloco.blocoNome}>
                <div className="bar-label-row">
                  <span>{bloco.blocoNome}</span>
                  <strong>{bloco.percentualOcupacaoAtual.toFixed(2)}%</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.min(bloco.percentualOcupacaoAtual, 100)}%` }} />
                </div>
                <small>{bloco.professoresPresentes} prof(s) • {bloco.alunosPresentes} aluno(s)</small>
              </div>
            ))}
          </div>
        </article>

        <article className="panel-card">
          <h3>Top salas por ocupação (agora)</h3>
          <div className="users-table-wrap refined">
            <table className="users-table">
              <thead>
                <tr>
                  <th>Bloco</th>
                  <th>Andar</th>
                  <th>Sala</th>
                  <th>Ocupação</th>
                </tr>
              </thead>
              <tbody>
                {topSalas.map((sala, index) => (
                  <tr key={`${sala.blocoNome}-${sala.andarNome}-${sala.salaNome}-${index}`}>
                    <td>{sala.blocoNome}</td>
                    <td>{sala.andarNome}</td>
                    <td>{sala.salaNome}</td>
                    <td>{sala.percentualOcupacaoAtual.toFixed(2)}%</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </article>
      </div>

      <article className="panel-card">
        <h3>Momento atual: salas sendo utilizadas</h3>
        <div className="users-table-wrap refined">
          <table className="users-table">
            <thead>
              <tr>
                <th>Bloco</th>
                <th>Andar</th>
                <th>Sala</th>
                <th>Professores / espaço</th>
                <th>Alunos / espaço</th>
                <th>% ocupação atual</th>
              </tr>
            </thead>
            <tbody>
              {salasEmUso.map((sala, index) => (
                <tr key={`${sala.blocoNome}-${sala.andarNome}-${sala.salaNome}-${index}`}>
                  <td>{sala.blocoNome}</td>
                  <td>{sala.andarNome}</td>
                  <td>{sala.salaNome}</td>
                  <td>{sala.professoresPresentes}</td>
                  <td>{sala.alunosPresentes}</td>
                  <td>{sala.percentualOcupacaoAtual.toFixed(2)}%</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </article>
    </section>
  );
}
