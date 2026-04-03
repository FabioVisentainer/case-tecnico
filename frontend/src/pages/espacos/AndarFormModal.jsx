import { useEffect, useState } from 'react';
import { createPortal } from 'react-dom';
import toast from 'react-hot-toast';
import { createAndar, updateAndar } from '../../services/espacos/andarsService';
import { useAuth } from '../../auth/AuthContext';

export default function AndarFormModal({ blocoId, blocoNome, isOpen, onClose, onSuccess, mode = 'create', initialData = null }) {
  const { token } = useAuth();
  const [nome, setNome] = useState(initialData?.nome ?? '');
  const [isLoading, setIsLoading] = useState(false);
  const isEdit = mode === 'edit';

  useEffect(() => {
    setNome(initialData?.nome ?? '');
  }, [initialData, isOpen, mode]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      if (isEdit) {
        await updateAndar(token, initialData.id, nome, blocoId);
        toast.success('Andar atualizado com sucesso!');
      } else {
        await createAndar(token, nome, blocoId);
        toast.success('Andar cadastrado com sucesso!');
        setNome('');
      }
      onSuccess?.();
      onClose();
    } catch (error) {
      toast.error(error.message || `Erro ao ${isEdit ? 'atualizar' : 'cadastrar'} andar`);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return createPortal(
    <div className="modal-overlay modal-overlay-front" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{isEdit ? 'Editar Andar' : `Cadastrar Andar em ${blocoNome}`}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="form-group">
          <div className="form-field">
            <label htmlFor="nomeAndar">Nome do Andar</label>
            <input
              id="nomeAndar"
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex: Térreo, Andar 1, 2º andar"
              required
            />
          </div>

          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              onClick={onClose}
              disabled={isLoading}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={isLoading}
            >
              {isLoading ? (isEdit ? 'Salvando...' : 'Cadastrando...') : (isEdit ? 'Salvar' : 'Cadastrar')}
            </button>
          </div>
        </form>
      </div>
    </div>
  , document.body);
}



