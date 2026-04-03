import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarDashboardSalas } from '../../services/dashboard/dashboardService';
import DashboardPageHeader from '../../components/DashboardPageHeader';
import './RelatorioSalasPage.css';
import './DashboardGeralPage.css';

export default function DashboardSalasPage() {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [tabAtiva, setTabAtiva] = useState('kpis');

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setIsLoading(true);
    try {
      const response = await buscarDashboardSalas({ token });
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar dashboard de salas.');
    } finally {
      setIsLoading(false);
    }
  }

  // Calcular min/max para normalizar o gráfico de dispersão
  const capacidades = (data?.dispersao ?? []).map((s) => s.capacidade);
  const minCapacidade = Math.min(...capacidades);
  const maxCapacidade = Math.max(...capacidades);

  return (
    <section className="dashboard-geral-page">
      <DashboardPageHeader
        title="Dashboard de Salas"
        actions={(
          <button type="button" className="btn-primary" onClick={carregar} disabled={isLoading}>
            Atualizar
          </button>
        )}
      />

      {/* Tabs de navegação */}
      <div style={{ display: 'flex', gap: '1rem', marginBottom: '1.5rem', borderBottom: '2px solid #e0e0e0' }}>
        <button
          type="button"
          onClick={() => setTabAtiva('kpis')}
          style={{
            padding: '0.75rem 1.5rem',
            border: 'none',
            background: tabAtiva === 'kpis' ? '#4A90E2' : 'transparent',
            color: tabAtiva === 'kpis' ? 'white' : '#333',
            cursor: 'pointer',
            fontWeight: 'bold',
            borderBottom: tabAtiva === 'kpis' ? '3px solid #4A90E2' : 'none',
          }}
        >
          KPIs por Sala
        </button>
        <button
          type="button"
          onClick={() => setTabAtiva('rankings')}
          style={{
            padding: '0.75rem 1.5rem',
            border: 'none',
            background: tabAtiva === 'rankings' ? '#4A90E2' : 'transparent',
            color: tabAtiva === 'rankings' ? 'white' : '#333',
            cursor: 'pointer',
            fontWeight: 'bold',
            borderBottom: tabAtiva === 'rankings' ? '3px solid #4A90E2' : 'none',
          }}
        >
          Rankings
        </button>
        <button
          type="button"
          onClick={() => setTabAtiva('dispersao')}
          style={{
            padding: '0.75rem 1.5rem',
            border: 'none',
            background: tabAtiva === 'dispersao' ? '#4A90E2' : 'transparent',
            color: tabAtiva === 'dispersao' ? 'white' : '#333',
            cursor: 'pointer',
            fontWeight: 'bold',
            borderBottom: tabAtiva === 'dispersao' ? '3px solid #4A90E2' : 'none',
          }}
        >
          Dispersão (Capacidade x Ocupação)
        </button>
      </div>

      {/* Tab: KPIs */}
      {tabAtiva === 'kpis' && (
        <article className="panel-card">
          <h3>KPIs por Sala (últimas 12 meses)</h3>
          <div className="users-table-wrap refined">
            <table className="users-table">
              <thead>
                <tr>
                  <th>Sala</th>
                  <th>Bloco</th>
                  <th>Andar</th>
                  <th>Taxa Ocupação (%)</th>
                  <th>Taxa Capacidade (%)</th>
                  <th>Horas Utilizadas</th>
                  <th>Horas Disponíveis</th>
                </tr>
              </thead>
              <tbody>
                {(data?.kpis ?? []).map((sala) => (
                  <tr key={sala.salaId}>
                    <td>
                      <strong>{sala.salaNome}</strong>
                    </td>
                    <td>{sala.blocoNome}</td>
                    <td>{sala.andarNome}</td>
                    <td>{sala.taxaOcupacao.toFixed(2)}%</td>
                    <td>{sala.taxaCapacidade.toFixed(2)}%</td>
                    <td>{sala.horasUtilizadas}</td>
                    <td>{sala.horasDisponiveis}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </article>
      )}

      {/* Tab: Rankings */}
      {tabAtiva === 'rankings' && (
        <div className="dashboard-grid-two">
          <article className="panel-card">
            <h3>🏆 Salas Mais Utilizadas (top 10)</h3>
            <div className="bars-list">
              {(data?.rankingUtilizadas ?? []).map((sala) => (
                <div className="bar-item" key={sala.salaId}>
                  <div className="bar-label-row">
                    <span>
                      {sala.salaNome} ({sala.blocoNome})
                    </span>
                    <strong>{sala.percentual.toFixed(2)}%</strong>
                  </div>
                  <div className="bar-track">
                    <div className="bar-fill" style={{ width: `${Math.min(sala.percentual, 100)}%` }} />
                  </div>
                  <small>
                    {sala.checkins} checkins - {(sala.minutosPresenca / 60).toFixed(1)} horas
                  </small>
                </div>
              ))}
            </div>
          </article>

          <article className="panel-card">
            <h3>😴 Salas Mais Ociosas (top 10)</h3>
            <div className="bars-list">
              {(data?.rankingOciosas ?? []).map((sala) => (
                <div className="bar-item" key={sala.salaId}>
                  <div className="bar-label-row">
                    <span>
                      {sala.salaNome} ({sala.blocoNome})
                    </span>
                    <strong>{sala.percentualOciosidade.toFixed(2)}%</strong>
                  </div>
                  <div className="bar-track">
                    <div
                      className="bar-fill"
                      style={{
                        width: `${Math.min(sala.percentualOciosidade, 100)}%`,
                        background: '#ff9800',
                      }}
                    />
                  </div>
                  <small>{sala.checkins} checkins</small>
                </div>
              ))}
            </div>
          </article>
        </div>
      )}

      {/* Tab: Dispersão */}
      {tabAtiva === 'dispersao' && (
        <article className="panel-card">
          <h3>📊 Dispersão: Capacidade x Ocupação Atual</h3>
          <div style={{ overflow: 'auto', marginTop: '1rem' }}>
            <svg width="100%" height="400" viewBox="0 0 800 400" style={{ border: '1px solid #ddd' }}>
              {/* Eixos */}
              <line x1="50" y1="350" x2="750" y2="350" stroke="#333" strokeWidth="2" />
              <line x1="50" y1="350" x2="50" y2="30" stroke="#333" strokeWidth="2" />

              {/* Labels dos eixos */}
              <text x="750" y="370" fontSize="12" fill="#333">
                Capacidade
              </text>
              <text x="20" y="30" fontSize="12" fill="#333">
                Ocupação (%)
              </text>

              {/* Pontos de dados */}
              {(data?.dispersao ?? []).map((sala, idx) => {
                const range = maxCapacidade - minCapacidade || 1;
                const x = 50 + ((sala.capacidade - minCapacidade) / range) * 700;
                const y = 350 - (sala.percentualOcupacao / 100) * 320;
                const color = sala.percentualOcupacao > 80 ? '#4CAF50' : sala.percentualOcupacao > 50 ? '#FF9800' : '#F44336';

                return (
                  <g key={idx}>
                    <circle cx={x} cy={y} r="5" fill={color} opacity="0.7" />
                    <title>
                      {sala.salaNome} - Cap: {sala.capacidade}, Ocupação: {sala.percentualOcupacao.toFixed(1)}%
                    </title>
                  </g>
                );
              })}

              {/* Linhas de grid */}
              {[0, 25, 50, 75, 100].map((val) => (
                <line
                  key={`h-${val}`}
                  x1="50"
                  y1={350 - (val / 100) * 320}
                  x2="750"
                  y2={350 - (val / 100) * 320}
                  stroke="#e0e0e0"
                  strokeDasharray="4"
                />
              ))}
            </svg>
            <div style={{ marginTop: '1rem', fontSize: '0.85rem' }}>
              <p>
                <span style={{ display: 'inline-block', width: '12px', height: '12px', background: '#4CAF50', marginRight: '0.5rem' }} />
                Alta ocupação (&gt;80%)
              </p>
              <p>
                <span style={{ display: 'inline-block', width: '12px', height: '12px', background: '#FF9800', marginRight: '0.5rem' }} />
                Ocupação média (50-80%)
              </p>
              <p>
                <span style={{ display: 'inline-block', width: '12px', height: '12px', background: '#F44336', marginRight: '0.5rem' }} />
                Baixa ocupação (&lt;50%)
              </p>
            </div>
          </div>
        </article>
      )}
    </section>
  );
}

