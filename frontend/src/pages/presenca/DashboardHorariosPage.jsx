import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { buscarDashboardHorarios } from '../../services/dashboard/dashboardService';
import DashboardPageHeader from '../../components/DashboardPageHeader';
import './RelatorioSalasPage.css';
import './DashboardGeralPage.css';

export default function DashboardHorariosPage() {
  const { token } = useAuth();
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    carregar();
  }, []);

  async function carregar() {
    setIsLoading(true);
    try {
      const response = await buscarDashboardHorarios({ token });
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Falha ao carregar dashboard de horários.');
    } finally {
      setIsLoading(false);
    }
  }

  // Função para calcular cor do heatmap (quantidade de checkins)
  const getHeatmapColor = (checkins, max) => {
    if (max === 0) return '#f5f5f5';
    const intensity = checkins / max;
    if (intensity > 0.8) return '#c8e6c9'; // Verde forte
    if (intensity > 0.6) return '#a5d6a7'; // Verde médio
    if (intensity > 0.4) return '#81c784'; // Verde claro
    if (intensity > 0.2) return '#fff9c4'; // Amarelo
    return '#ffe0b2'; // Laranja claro
  };

  // Construir matrix para heatmap
  const heatmapData = data?.heatmap ?? [];
  const maxCheckins = Math.max(...heatmapData.map((h) => h.checkins), 1);

  // Agrupar por dia da semana
  const porDia = {};
  heatmapData.forEach((item) => {
    if (!porDia[item.diaNome]) {
      porDia[item.diaNome] = {};
    }
    porDia[item.diaNome][item.hora] = item.checkins;
  });

  const horas = Array.from({ length: 24 }, (_, i) => i);
  const dias = ['Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado', 'Domingo'];

  return (
    <section className="dashboard-geral-page">
      <DashboardPageHeader
        title="Dashboard de Horários"
        actions={(
          <button type="button" className="btn-primary" onClick={carregar} disabled={isLoading}>
            Atualizar
          </button>
        )}
      />

      {/* KPIs de Horários */}
      <div className="dashboard-kpis geral">
        <h2 style={{ gridColumn: '1 / -1', marginBottom: '1rem' }}>📌 Horários de Pico & Baixa Ocupação</h2>
        {(data?.kpis ?? []).map((kpi) => (
          <article key={`${kpi.descricao}-${kpi.hora}`} className="kpi-card">
            <h4>
              {kpi.descricao} {kpi.hora}
            </h4>
            <p>{kpi.checkins} checkins</p>
            <small>{kpi.percentual.toFixed(2)}% do total</small>
          </article>
        ))}
      </div>

      {/* Heatmap: Dia x Hora */}
      <article className="panel-card">
        <h3>🔥 Heatmap: Dia da Semana x Hora (últimos 7 dias)</h3>
        <div style={{ overflowX: 'auto', marginTop: '1rem' }}>
          <table style={{ borderCollapse: 'collapse', width: '100%', fontSize: '0.85rem' }}>
            <thead>
              <tr>
                <th style={{ border: '1px solid #ddd', padding: '0.5rem', background: '#f5f5f5' }}>Hora</th>
                {dias.map((dia) => (
                  <th key={dia} style={{ border: '1px solid #ddd', padding: '0.5rem', background: '#f5f5f5' }}>
                    {dia.substring(0, 3)}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {horas.map((hora) => (
                <tr key={hora}>
                  <td
                    style={{
                      border: '1px solid #ddd',
                      padding: '0.5rem',
                      fontWeight: 'bold',
                      background: '#f9f9f9',
                      textAlign: 'center',
                    }}
                  >
                    {String(hora).padStart(2, '0')}:00
                  </td>
                  {dias.map((dia) => {
                    const checkins = porDia[dia]?.[hora] ?? 0;
                    const color = getHeatmapColor(checkins, maxCheckins);
                    return (
                      <td
                        key={`${dia}-${hora}`}
                        style={{
                          border: '1px solid #ddd',
                          padding: '0.5rem',
                          textAlign: 'center',
                          background: color,
                          fontWeight: checkins > 0 ? 'bold' : 'normal',
                        }}
                        title={`${checkins} checkins`}
                      >
                        {checkins > 0 ? checkins : '-'}
                      </td>
                    );
                  })}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <div style={{ marginTop: '1rem', fontSize: '0.85rem' }}>
          <p>
            <span style={{ display: 'inline-block', width: '20px', height: '20px', background: '#c8e6c9', border: '1px solid #ddd', marginRight: '0.5rem' }} />
            Alta atividade
          </p>
          <p>
            <span style={{ display: 'inline-block', width: '20px', height: '20px', background: '#fff9c4', border: '1px solid #ddd', marginRight: '0.5rem' }} />
            Atividade moderada
          </p>
          <p>
            <span style={{ display: 'inline-block', width: '20px', height: '20px', background: '#ffe0b2', border: '1px solid #ddd', marginRight: '0.5rem' }} />
            Baixa atividade
          </p>
        </div>
      </article>

      {/* Ocupação por Faixa Horária */}
      <article className="panel-card">
        <h3>📊 Ocupação por Faixa Horária</h3>
        <div className="bars-list">
          {(data?.ocupacaoFaixa ?? []).map((faixa) => (
            <div className="bar-item" key={faixa.faixa}>
              <div className="bar-label-row">
                <span>{faixa.faixa}</span>
                <strong>{faixa.percentual.toFixed(2)}%</strong>
              </div>
              <div className="bar-track">
                <div className="bar-fill" style={{ width: `${Math.min(faixa.percentual, 100)}%` }} />
              </div>
              <small>{faixa.checkins} check-ins</small>
            </div>
          ))}
        </div>
      </article>

      {/* Ocupação por Turno */}
      <article className="panel-card">
        <h3>🕐 Ocupação por Turno</h3>
        <div className="bars-list">
          {(data?.ocupacaoTurno ?? []).map((turno) => (
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
    </section>
  );
}

