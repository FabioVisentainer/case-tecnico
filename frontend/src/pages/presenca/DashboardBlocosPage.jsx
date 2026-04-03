import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarDashboardBlocos } from '../../services/dashboard/dashboardService';
import DashboardPageHeader from '../../components/DashboardPageHeader';
import './RelatorioSalasPage.css';
import './DashboardGeralPage.css';

export default function DashboardBlocosPage() {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setIsLoading(true);
    try {
      const response = await buscarDashboardBlocos({ token });
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar dashboard de blocos.');
    } finally {
      setIsLoading(false);
    }
  }

  // Group heatmap data by bloco
  const heatmapPorBloco = {};
  (data?.heatmap ?? []).forEach((item) => {
    if (!heatmapPorBloco[item.blocoNome]) {
      heatmapPorBloco[item.blocoNome] = {};
    }
    heatmapPorBloco[item.blocoNome][item.turno] = item.checkins;
  });

  const turnos = ['Manha', 'Tarde', 'Noite'];

  return (
    <section className="dashboard-geral-page">
      <DashboardPageHeader
        title="Dashboard de Blocos"
        actions={(
          <button type="button" className="btn-primary" onClick={carregar} disabled={isLoading}>
            Atualizar
          </button>
        )}
      />

      <div className="dashboard-kpis geral">
        <h2 style={{ gridColumn: '1 / -1', marginBottom: '1rem' }}>KPIs por Bloco</h2>
        {(data?.kpis ?? []).map((bloco) => (
          <article key={bloco.blocoNome} className="kpi-card">
            <h4>{bloco.blocoNome}</h4>
            <div style={{ fontSize: '0.9rem', marginTop: '0.5rem' }}>
              <div>
                <strong>Ocupação:</strong> {bloco.ocupacaoMedia.toFixed(2)}%
              </div>
              <div>
                <strong>Capacidade:</strong> {bloco.capacidadeTotal}
              </div>
              <div>
                <strong>Salas:</strong> {bloco.numeroSalas}
              </div>
            </div>
          </article>
        ))}
      </div>

      <div className="dashboard-grid-two">
        <article className="panel-card">
          <h3>Ranking de Blocos Mais Utilizados (12 meses)</h3>
          <div className="bars-list">
            {(data?.ranking ?? []).map((bloco) => (
              <div className="bar-item" key={bloco.blocoNome}>
                <div className="bar-label-row">
                  <span>{bloco.blocoNome}</span>
                  <strong>{bloco.percentual.toFixed(2)}%</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.min(bloco.percentual, 100)}%` }} />
                </div>
                <small>{bloco.utilizacoes} utilizações</small>
              </div>
            ))}
          </div>
        </article>

        <article className="panel-card">
          <h3>Ocupação Atual vs Capacidade</h3>
          <div className="bars-list">
            {(data?.comparativo ?? []).map((bloco) => (
              <div className="bar-item" key={bloco.blocoNome}>
                <div className="bar-label-row">
                  <span>{bloco.blocoNome}</span>
                  <strong>{bloco.percentualOcupacao.toFixed(2)}%</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.min(bloco.percentualOcupacao, 100)}%` }} />
                </div>
                <small>
                  {bloco.presentes} / {bloco.capacidadeTotal}
                </small>
              </div>
            ))}
          </div>
        </article>
      </div>

      <article className="panel-card">
        <h3>Heatmap: Blocos x Turnos (12 meses)</h3>
        <div className="users-table-wrap refined">
          <table className="users-table">
            <thead>
              <tr>
                <th>Bloco</th>
                {turnos.map((turno) => (
                  <th key={turno}>{turno}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {Object.entries(heatmapPorBloco).map(([bloco, turnoCounts]) => (
                <tr key={bloco}>
                  <td>
                    <strong>{bloco}</strong>
                  </td>
                  {turnos.map((turno) => (
                    <td key={`${bloco}-${turno}`}>{turnoCounts[turno] ?? 0}</td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </article>
    </section>
  );
}

