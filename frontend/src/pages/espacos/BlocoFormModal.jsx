import { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import toast from 'react-hot-toast';
import { createBloco, updateBloco } from '../../services/espacos/blocosService';
import { useAuth } from '../../auth/AuthContext';

export default function BlocoFormModal({ isOpen, onClose, onSuccess, mode = 'create', initialData = null }) {
  const { token } = useAuth();
  const [nome, setNome] = useState(initialData?.nome ?? '');
  const [isLoading, setIsLoading] = useState(false);

  const isEdit = mode === 'edit';

  useEffect(() => {
    setNome(initialData?.nome ?? '');
  }, [initialData, isOpen, mode]);

  async function handleSubmit(event) {
    event.preventDefault();
    const nomeLimpo = nome.trim();

    if (!nomeLimpo) {
      toast.error('Nome do bloco e obrigatório.');
      return;
    }

    setIsLoading(true);
    try {
      if (isEdit) {
        await updateBloco(token, initialData.id, nomeLimpo);
        toast.success('Bloco atualizado com sucesso!');
      } else {
        await createBloco(token, nomeLimpo);
        toast.success('Bloco cadastrado com sucesso!');
        setNome('');
      }
      onSuccess?.();
      onClose();
    } catch (error) {
      toast.error(error.message || `Erro ao ${isEdit ? 'atualizar' : 'cadastrar'} bloco`);
    } finally {
      setIsLoading(false);
    }
  }

  if (!isOpen) return null;

  return createPortal(
    <div className="modal-overlay modal-overlay-front" onClick={onClose}>
      <div className="modal-content" onClick={(event) => event.stopPropagation()}>
        <div className="modal-header">
          <h2>{isEdit ? 'Editar Bloco' : 'Cadastrar Bloco'}</h2>
          <button className="modal-close" onClick={onClose}>x</button>
        </div>

        <form onSubmit={handleSubmit} className="form-group">
          <div className="form-field">
            <label htmlFor="nomeBloco">Nome do Bloco</label>
            <input
              id="nomeBloco"
              type="text"
              value={nome}
              onChange={(event) => setNome(event.target.value)}
              placeholder="Ex: Bloco 10, Predio Administrativo"
              required
            />
          </div>

          <div className="modal-footer">
            <button type="button" className="btn btn-secondary" onClick={onClose} disabled={isLoading}>
              Cancelar
            </button>
            <button type="submit" className="btn btn-primary" disabled={isLoading}>
              {isLoading ? (isEdit ? 'Salvando...' : 'Cadastrando...') : (isEdit ? 'Salvar' : 'Cadastrar')}
            </button>
          </div>
        </form>
      </div>
    </div>
  , document.body);
}

