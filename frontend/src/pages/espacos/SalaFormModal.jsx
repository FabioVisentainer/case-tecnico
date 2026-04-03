import { useState, useEffect } from 'react';
import { createPortal } from 'react-dom';
import toast from 'react-hot-toast';
import { createSala, listSalasPorAndar, updateSala } from '../../services/espacos/salasService';
import { useAuth } from '../../auth/AuthContext';

export default function SalaFormModal({ andarId, andarNome, blocoNome, isOpen, onClose, onSuccess, mode = 'create', initialData = null }) {
  const { token } = useAuth();
  const [nome, setNome] = useState(initialData?.nome ?? '');
  const [lotacaoAlunos, setLotacaoAlunos] = useState(initialData?.lotacaoAlunos ?? '');
  const [lotacaoProfessores, setLotacaoProfessores] = useState(initialData?.lotacaoProfessores ?? '');
  const [isLoading, setIsLoading] = useState(false);
  const [existingRoomNames, setExistingRoomNames] = useState([]);
  const isEdit = mode === 'edit';

  useEffect(() => {
    setNome(initialData?.nome ?? '');
    setLotacaoAlunos(initialData?.lotacaoAlunos ?? '');
    setLotacaoProfessores(initialData?.lotacaoProfessores ?? '');
  }, [initialData, isOpen, mode]);

  useEffect(() => {
    if (isOpen && andarId) {
      loadExistingRooms();
    }
  }, [isOpen, andarId]);

  const loadExistingRooms = async () => {
    try {
      const response = await listSalasPorAndar(token, andarId);
      setExistingRoomNames((response.content || []).map(s => s.nome.toLowerCase()));
    } catch (error) {
      console.error('Erro ao carregar salas:', error);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validação de nome repetido
    if (existingRoomNames.includes(nome.toLowerCase()) && (!isEdit || nome.toLowerCase() !== (initialData?.nome ?? '').toLowerCase())) {
      toast.error('Já existe uma sala com este nome neste andar');
      return;
    }

    setIsLoading(true);
    try {
      if (isEdit) {
        await updateSala(token, initialData.id, nome, parseInt(lotacaoAlunos), parseInt(lotacaoProfessores), andarId);
        toast.success('Sala atualizada com sucesso!');
      } else {
        await createSala(token, nome, parseInt(lotacaoAlunos), parseInt(lotacaoProfessores), andarId);
        toast.success('Sala cadastrada com sucesso!');
        setNome('');
        setLotacaoAlunos('');
        setLotacaoProfessores('');
      }
      onSuccess?.();
      onClose();
    } catch (error) {
      toast.error(error.message || `Erro ao ${isEdit ? 'atualizar' : 'cadastrar'} sala`);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isOpen) return null;

  return createPortal(
    <div className="modal-overlay modal-overlay-front" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{isEdit ? 'Editar Sala' : `Cadastrar Sala em ${andarNome} - ${blocoNome}`}</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>

        <form onSubmit={handleSubmit} className="form-group">
          <div className="form-field">
            <label htmlFor="nomeSala">Nome da Sala</label>
            <input
              id="nomeSala"
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex: Sala 101, Auditório, Lab de Informática"
              required
            />
          </div>

          <div className="form-row">
            <div className="form-field">
              <label htmlFor="lotacaoAlunos">Lotação de Alunos</label>
              <input
                id="lotacaoAlunos"
                type="number"
                value={lotacaoAlunos}
                onChange={(e) => setLotacaoAlunos(e.target.value)}
                placeholder="Ex: 30"
                min="1"
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="lotacaoProfessores">Lotação de Professores</label>
              <input
                id="lotacaoProfessores"
                type="number"
                value={lotacaoProfessores}
                onChange={(e) => setLotacaoProfessores(e.target.value)}
                placeholder="Ex: 2"
                min="1"
                required
              />
            </div>
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



