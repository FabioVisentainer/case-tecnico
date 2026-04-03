import { useNavigate } from 'react-router-dom';
import './DashboardsPage.css';

const CARDS = [
  {
    id: 'ocupacao',
    title: 'Dashboard de Ocupacao',
    description: 'Visao atual de salas em uso, blocos mais ocupados e top salas.',
    route: '/relatorios/salas',
    icon: '📍',
  },
  {
    id: 'geral',
    title: 'Dashboard Geral',
    description: 'Visao executiva com eficiencia do campus por bloco, turno e semestre.',
    route: '/relatorios/geral',
    icon: '📊',
  },
  {
    id: 'blocos',
    title: 'Dashboard de Blocos',
    description: 'Análise detalhada de ocupação, ranking e heatmap de blocos.',
    route: '/relatorios/blocos',
    icon: '🏢',
  },
  {
    id: 'salas',
    title: 'Dashboard de Salas',
    description: 'KPIs individuais, rankings de utilização e análise de dispersão.',
    route: '/relatorios/salas-detalhado',
    icon: '🚪',
  },
  {
    id: 'horarios',
    title: 'Dashboard de Horários',
    description: 'Horários de pico, heatmap temporal e ocupação por faixa horária.',
    route: '/relatorios/horarios',
    icon: '🕐',
  },
];

export default function DashboardsPage() {
  const navigate = useNavigate();

  return (
    <section className="dashboards-page">
      <div className="page-header-inline">
        <h1>Dashboards</h1>
      </div>

      <div className="dashboards-grid">
        {CARDS.map((card) => (
          <article key={card.id} className="dashboard-card" onClick={() => navigate(card.route)}>
            <div className="dashboard-card-icon">{card.icon}</div>
            <h3>{card.title}</h3>
            <p>{card.description}</p>
            <span className="dashboard-card-link">Abrir →</span>
          </article>
        ))}
      </div>
    </section>
  );
}

