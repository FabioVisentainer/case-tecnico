import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../auth/AuthContext';
import { useNavigate } from 'react-router-dom';
import { buscarMinhaPresenca, checkoutSala } from '../../services/presenca/presencaService';
import './HomePage.css';

export default function HomePage() {
  const { user, isAdmin, token } = useAuth();
  const navigate = useNavigate();
  const [hoveredCard, setHoveredCard] = useState(null);
  const [minhaPresenca, setMinhaPresenca] = useState(null);
  const [isCheckingOut, setIsCheckingOut] = useState(false);

  const isAlunoOuProfessor = user?.papel === 'ALUNO' || user?.papel === 'PROFESSOR';

  const papelLabel =
    user?.papel === 'ADMINISTRADOR'
      ? 'Administrador(a)'
      : user?.papel === 'PROFESSOR'
        ? 'Professor(a)'
        : user?.papel === 'ALUNO'
          ? 'Aluno(a)'
          : 'Usuário(a)';

  useEffect(() => {
    if (!isAlunoOuProfessor) return;
    carregarPresencaAtual();
  }, [isAlunoOuProfessor, token]);

  async function carregarPresencaAtual() {
    try {
      const response = await buscarMinhaPresenca({ token });
      setMinhaPresenca(response);
    } catch {
      // Se falhar, apenas nao exibe o checkout rapido.
    }
  }

  async function handleCheckoutRapido() {
    setIsCheckingOut(true);
    try {
      await checkoutSala({ token });
      toast.success('Checkout realizado com sucesso!');
      await carregarPresencaAtual();
    } catch (error) {
      toast.error(error.message || 'Não foi possível realizar checkout.');
    } finally {
      setIsCheckingOut(false);
    }
  }

  const adminCards = [
    {
      id: 'usuarios',
      title: 'Cadastro de Usuários',
      description: 'Gerenciar usuários, papéis e status de acesso do sistema.',
      icon: '👥',
      route: '/usuarios',
      color: '#4A90E2',
    },
    {
      id: 'blocos',
      title: 'Blocos/Andares/Salas',
      description: 'Administrar a estrutura física do campus de forma organizada.',
      icon: '🏢',
      route: '/blocos',
      color: '#7ED321',
    },
    {
      id: 'dashboards',
      title: 'Dashboards',
      description: 'Acompanhar ocupação, disponibilidade e indicadores operacionais.',
      icon: '📈',
      route: '/relatorios',
      color: '#FF8A00',
    },
  ];

  return (
    <section className="home-page">
      <div className="home-hero">
        <p className="home-eyebrow">Portal Acadêmico</p>
        <h1>Painel inicial do sistema</h1>
        <p className="home-subtitle">
          Ambiente institucional para gestão de usuários, espaços físicos e acompanhamento da ocupação do campus.
        </p>
        <div className="home-user-summary">
          <span className="home-role-badge">{papelLabel}</span>
          <strong>{user?.nome || 'Usuário'}</strong>
        </div>
      </div>

      {isAdmin ? (
        <div className="admin-section">
          <h2>Painel Administrativo</h2>
          <div className="cards-grid">
            {adminCards.map(card => (
              <div
                key={card.id}
                className="admin-card"
                style={{ transform: hoveredCard === card.id ? 'translateY(-8px)' : 'translateY(0)' }}
                onMouseEnter={() => setHoveredCard(card.id)}
                onMouseLeave={() => setHoveredCard(null)}
                onClick={() => navigate(card.route)}
              >
                <div className="card-icon">{card.icon}</div>
                <h3>{card.title}</h3>
                <p>{card.description}</p>
                <div className="card-arrow">→</div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <div className="user-section">
          <h2>Acesso do Usuário</h2>
          <p>
            Sua área foi configurada conforme o seu perfil de acesso. Utilize as opções abaixo para consultar salas
            e registrar presença quando aplicável.
          </p>

          {isAlunoOuProfessor ? (
            <div className="cards-grid user-cards-grid">
              <div
                className="admin-card"
                onClick={() => navigate('/presenca')}
              >
                <div className="card-icon">📍</div>
                <h3>Check-in de Salas</h3>
                {minhaPresenca?.atual ? (
                  <p>
                    Em sala: {minhaPresenca.atual.blocoNome} / {minhaPresenca.atual.andarNome} / {minhaPresenca.atual.salaNome}
                  </p>
                ) : (
                  <p>Acesse para realizar check-in por busca ou navegação.</p>
                )}
                {minhaPresenca?.atual ? (
                  <button
                    type="button"
                    className="btn btn-secondary home-checkout-btn"
                    onClick={(event) => {
                      event.stopPropagation();
                      handleCheckoutRapido();
                    }}
                    disabled={isCheckingOut}
                  >
                    {isCheckingOut ? 'Processando...' : 'Checkout rápido'}
                  </button>
                ) : null}
                <div className="card-arrow">→</div>
              </div>
            </div>
          ) : null}
        </div>
      )}

      <div className="info-section">
        <h2>Orientação institucional</h2>
        <p>
          As funcionalidades exibidas nesta página variam de acordo com o perfil autenticado. Para conhecer a
          proposta do sistema, acesse a seção <strong>Sobre</strong> no menu lateral.
        </p>
      </div>
    </section>
  );
}



