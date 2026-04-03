import { useNavigate } from 'react-router-dom';

export default function DashboardPageHeader({ title, backTo = '/relatorios', backLabel = 'Voltar', actions = null }) {
  const navigate = useNavigate();

  return (
    <div className="page-header-inline dashboard-page-header">
      <div>
        <h1>{title}</h1>
      </div>

      <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', flexWrap: 'wrap' }}>
        <button type="button" className="btn-primary" onClick={() => navigate(backTo)}>
          {backLabel}
        </button>
        {actions}
      </div>
    </div>
  );
}

