import './AboutPage.css';

export default function AboutPage() {
  return (
    <section className="about-page">
      <div className="about-container">
        <div className="about-header">
          <h1>Sobre o Sistema</h1>
          <p className="subtitle">Plataforma para gestão de usuários, espaços e ocupação do campus</p>
        </div>

        <div className="about-content">
          <div className="about-section">
            <div className="section-icon">🎯</div>
            <h2>Visão Geral</h2>
            <p>
              Este sistema foi desenvolvido para apoiar a administração acadêmica do campus, reunindo em uma
              única aplicação o cadastro de usuários, a organização dos blocos, andares e salas, o controle de
              check-in e checkout e a visualização de indicadores de ocupação em tempo real.
            </p>
          </div>

          <div className="about-section">
            <div className="section-icon">✨</div>
            <h2>Principais Funcionalidades</h2>
            <div className="features-list">
              <div className="feature">
                <span className="feature-icon">👥</span>
                <div>
                  <strong>Cadastro de Usuários</strong>
                  <p>Gerenciar administradores, professores e alunos com regras de acesso por papel</p>
                </div>
              </div>
              <div className="feature">
                <span className="feature-icon">🏢</span>
                <div>
                  <strong>Gestão de Blocos</strong>
                  <p>Organizar os blocos do campus com visualização em cards e controle de status</p>
                </div>
              </div>
              <div className="feature">
                <span className="feature-icon">🪜</span>
                <div>
                  <strong>Andares e Salas</strong>
                  <p>Cadastrar andares e salas com lotação, integridade de nomes e histórico operacional</p>
                </div>
              </div>
              <div className="feature">
                <span className="feature-icon">🟢</span>
                <div>
                  <strong>Check-in e Checkout</strong>
                  <p>Registrar a presença atual de alunos e professores com navegação por blocos e salas</p>
                </div>
              </div>
              <div className="feature">
                <span className="feature-icon">📊</span>
                <div>
                  <strong>Dashboards</strong>
                  <p>Visualizar ocupação do campus, dos blocos, das salas e dos horários de maior uso</p>
                </div>
              </div>
            </div>
          </div>

          <div className="about-section">
            <div className="section-icon">🎓</div>
            <h2>Papéis de Usuário</h2>
            <div className="roles-grid">
              <div className="role-card">
                <div className="role-badge admin">ADMINISTRADOR</div>
                <p>Cadastre usuários, gerencie blocos, andares e salas e acompanhe os dashboards de ocupação.</p>
              </div>
              <div className="role-card">
                <div className="role-badge professor">PROFESSOR</div>
                <p>Realize check-in e checkout, consulte salas e acompanhe a presença dos alunos na sala atual.</p>
              </div>
              <div className="role-card">
                <div className="role-badge aluno">ALUNO</div>
                <p>Consulte espaços ativos, faça check-in na sala atual e finalize o checkout ao sair.</p>
              </div>
            </div>
          </div>

          <div className="about-section">
            <div className="section-icon">⚙️</div>
            <h2>Tecnologia Utilizada</h2>
            <div className="tech-stack">
              <div className="tech-column">
                <h3>Frontend</h3>
                <ul>
                  <li>React</li>
                  <li>Vite</li>
                  <li>React Router</li>
                  <li>Fetch e componentes reutilizáveis</li>
                </ul>
              </div>
              <div className="tech-column">
                <h3>Backend</h3>
                <ul>
                  <li>Spring Boot</li>
                  <li>Java 21</li>
                  <li>Spring Data JPA / Hibernate</li>
                  <li>MySQL 8.0</li>
                </ul>
              </div>
            </div>
          </div>

          <div className="about-section">
            <div className="section-icon">🧭</div>
            <h2>Fluxo de Uso</h2>
            <div className="usage-steps">
              <div className="usage-step">
                <span className="usage-step-number">1</span>
                <div>
                  <strong>Entrar no sistema</strong>
                  <p>O usuário autentica-se e é direcionado para a área correspondente ao seu perfil.</p>
                </div>
              </div>
              <div className="usage-step">
                <span className="usage-step-number">2</span>
                <div>
                  <strong>Encontrar o espaço</strong>
                  <p>Blocos, andares e salas podem ser consultados por navegação ou pesquisa.</p>
                </div>
              </div>
              <div className="usage-step">
                <span className="usage-step-number">3</span>
                <div>
                  <strong>Registrar presença</strong>
                  <p>O check-in e o checkout alimentam os indicadores de ocupação e o acompanhamento operacional.</p>
                </div>
              </div>
            </div>
          </div>

          <div className="about-section">
            <div className="section-icon">💻</div>
            <h2>Projeto e Estrutura</h2>
            <p>
              O projeto foi organizado como um monorepo, com frontend em React e backend em Spring Boot,
              para manter a separação entre interface, regras de negócio e persistência de dados.
            </p>
          </div>
        </div>

        <div className="about-footer">
          <p>Case técnico desenvolvido para demonstrar gestão de acesso, espaços e ocupação do campus.</p>
        </div>
      </div>
    </section>
  );
}

