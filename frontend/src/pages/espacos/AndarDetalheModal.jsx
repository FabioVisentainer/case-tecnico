import './BlocosListPage.css';

export default function AndarDetalheModal({
  andar,
  blocoAtivo = true,
  isOpen,
  onClose,
  onEditAndar,
  onStatusAndar,
  onCreateSala,
  onEditSala,
  onStatusSala,
}) {
  if (!isOpen || !andar) return null;

  const podeAlterarSala = blocoAtivo && andar.ativo;

  return (
    <div className="modal-overlay modal-overlay-back" onClick={onClose}>
      <div className="modal-content andar-detalhe-modal" onClick={(event) => event.stopPropagation()}>
        <div className="modal-header bloco-detalhe-header">
          <div>
            <h2>{andar.nome}</h2>
            <p className="bloco-detalhe-subtitle">
              {andar.ativo ? 'Andar ativo' : 'Andar inativo'}
            </p>
          </div>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="bloco-detalhe-stats">
          <div className="stat-card">
            <span className="stat-value">{andar.salas?.length ?? 0}</span>
            <span className="stat-label">Salas</span>
          </div>
          <div className="stat-card">
            <span className="stat-value">{andar.ativo ? 'Sim' : 'Não'}</span>
            <span className="stat-label">Ativo</span>
          </div>
        </div>

        <div className="bloco-detalhe-actions">
          <button type="button" className="btn btn-primary" onClick={onCreateSala} disabled={!andar.ativo}>
            + Cadastrar sala
          </button>
          <button type="button" className="btn btn-secondary" onClick={onEditAndar}>
            Editar andar
          </button>
          {blocoAtivo ? (
            <button type="button" className="btn btn-danger compact-action-btn" onClick={onStatusAndar}>
              {andar.ativo ? 'Inativar andar' : 'Reativar andar'}
            </button>
          ) : null}
        </div>

        <div className="bloco-detalhe-list">
          <h3>Salas</h3>
          {(andar.salas || []).length === 0 ? (
            <p className="empty-list">Nenhuma sala cadastrada.</p>
          ) : (
            <div className="bloco-detalhe-andares">
              {andar.salas.map((sala) => (
                <article key={sala.id} className="andar-detail-card sala-detail-card">
                  <div className="andar-detail-main">
                    <strong>
                      {sala.nome}
                      {!sala.ativo ? <span className="status-tag inativo">Inativo</span> : null}
                    </strong>
                    <span>Alunos: {sala.lotacaoAlunos} • Professores: {sala.lotacaoProfessores}</span>
                  </div>
                  <div className="sala-detail-actions">
                    <button type="button" className="btn btn-secondary" onClick={() => onEditSala(sala)}>
                      Editar
                    </button>
                    {podeAlterarSala ? (
                      <button type="button" className="btn btn-danger compact-action-btn" onClick={() => onStatusSala(sala)}>
                        {sala.ativo ? 'Inativar' : 'Reativar'}
                      </button>
                    ) : null}
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

