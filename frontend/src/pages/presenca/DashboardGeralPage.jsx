import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarDashboardGeral } from '../../services/dashboard/dashboardService';
import DashboardPageHeader from '../../components/DashboardPageHeader';
import './RelatorioSalasPage.css';
import './DashboardGeralPage.css';

export default function DashboardGeralPage() {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setIsLoading(true);
    try {
      const response = await buscarDashboardGeral({ token });
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar dashboard geral.');
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <section className="dashboard-geral-page">
      <DashboardPageHeader
        title="Dashboard Geral"
        actions={(
          <button type="button" className="btn-primary" onClick={carregar} disabled={isLoading}>
            Atualizar
          </button>
        )}
      />

      <div className="dashboard-kpis geral">
        <article className="kpi-card">
          <h4>Taxa media de ocupacao</h4>
          <p>{data?.taxaMediaOcupacao?.toFixed?.(2) ?? '0.00'}%</p>
        </article>
        <article className="kpi-card">
          <h4>Taxa de ociosidade</h4>
          <p>{data?.taxaOciosidade?.toFixed?.(2) ?? '0.00'}%</p>
        </article>
        <article className="kpi-card">
          <h4>Total de salas ativas</h4>
          <p>{data?.totalSalasAtivas ?? 0}</p>
        </article>
        <article className="kpi-card">
          <h4>Capacidade total vs alunos</h4>
          <p>{data?.capacidadeTotal ?? 0} / {data?.totalAlunosPresentes ?? 0}</p>
        </article>
      </div>

      <div className="dashboard-grid-two">
        <article className="panel-card">
          <h3>Ocupacao por bloco</h3>
          <div className="bars-list">
            {(data?.ocupacaoPorBloco ?? []).map((bloco) => (
              <div className="bar-item" key={bloco.blocoNome}>
                <div className="bar-label-row">
                  <span>{bloco.blocoNome}</span>
                  <strong>{bloco.percentualOcupacao.toFixed(2)}%</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.min(bloco.percentualOcupacao, 100)}%` }} />
                </div>
              </div>
            ))}
          </div>
        </article>

        <article className="panel-card">
          <h3>Ocupacao por turno</h3>
          <div className="bars-list">
            {(data?.ocupacaoPorTurno ?? []).map((turno) => (
              <div className="bar-item" key={turno.turno}>
                <div className="bar-label-row">
                  <span>{turno.turno}</span>
                  <strong>{turno.percentualOcupacao.toFixed(2)}%</strong>
                </div>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${Math.min(turno.percentualOcupacao, 100)}%` }} />
                </div>
                <small>{turno.totalCheckins} check-ins</small>
              </div>
            ))}
          </div>
        </article>
      </div>

      <article className="panel-card">
        <h3>Evolucao da ocupacao (por semestre)</h3>
        <div className="users-table-wrap refined">
          <table className="users-table">
            <thead>
              <tr>
                <th>Semestre</th>
                <th>Ocupacao (%)</th>
                <th>Salas utilizadas</th>
                <th>Total salas ativas</th>
              </tr>
            </thead>
            <tbody>
              {(data?.evolucaoSemestral ?? []).map((item) => (
                <tr key={item.semestre}>
                  <td>{item.semestre}</td>
                  <td>{item.percentualOcupacao.toFixed(2)}%</td>
                  <td>{item.salasUtilizadas}</td>
                  <td>{item.totalSalasAtivas}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </article>
    </section>
  );
}

