import './BlocosListPage.css';

export default function BlocoDetalheModal({ bloco, isOpen, onClose, onCreateAndar, onOpenAndar, onEditBloco, onStatusBloco }) {
  if (!isOpen || !bloco) return null;

  return (
    <div className="modal-overlay modal-overlay-back" onClick={onClose}>
      <div className="modal-content bloco-detalhe-modal" onClick={(event) => event.stopPropagation()}>
        <div className="modal-header bloco-detalhe-header">
          <div>
            <h2>{bloco.nome}</h2>
            <p className="bloco-detalhe-subtitle">
              {bloco.ativo ? 'Bloco ativo' : 'Bloco inativo'}
            </p>
          </div>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <div className="bloco-detalhe-stats">
          <div className="stat-card">
            <span className="stat-value">{bloco.totalAndares ?? 0}</span>
            <span className="stat-label">Andares</span>
          </div>
          <div className="stat-card">
            <span className="stat-value">{bloco.totalSalas ?? 0}</span>
            <span className="stat-label">Salas</span>
          </div>
        </div>

        <div className="bloco-detalhe-actions">
          <button
            type="button"
            className="btn btn-primary"
            onClick={onCreateAndar}
            disabled={!bloco.ativo}
          >
            + Cadastrar andar
          </button>
          <button type="button" className="btn btn-secondary" onClick={onEditBloco}>
            Editar bloco
          </button>
          <button type="button" className="btn btn-danger compact-action-btn" onClick={onStatusBloco}>
            {bloco.ativo ? 'Inativar bloco' : 'Reativar bloco'}
          </button>
        </div>

        <div className="bloco-detalhe-list">
          <h3>Andares</h3>
          {(bloco.andares || []).length === 0 ? (
            <p className="empty-list">Nenhum andar cadastrado.</p>
          ) : (
            <div className="bloco-detalhe-andares">
              {bloco.andares.map((andar) => (
                <article
                  key={andar.id}
                  className="andar-detail-card clickable"
                  onClick={() => onOpenAndar?.(andar)}
                >
                  <div className="andar-detail-main">
                    <strong>
                      {andar.nome}
                      {!andar.ativo ? <span className="status-tag inativo">Inativo</span> : null}
                    </strong>
                    <span>{andar.totalSalas ?? 0} sala(s)</span>
                  </div>
                  <span className="andar-detail-hint">Abrir detalhes</span>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

