import { useEffect, useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { getUsuarioById, updateUsuario } from '../../services/usuarios/usuariosService';
import UsuarioForm from './UsuarioForm';
import './UsuariosPage.css';

export default function UsuariosEditPage() {
  const { token } = useAuth();
  const { id } = useParams();
  const navigate = useNavigate();
  const [initialData, setInitialData] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    let active = true;

    async function loadUsuario() {
      try {
        const payload = await getUsuarioById({ token, id });

        if (active) {
          setInitialData({
            nome: payload.nome,
            username: payload.username,
            cpf: payload.cpf,
            papel: payload.papel,
            senha: '',
            ativo: payload.ativo,
          });
        }
      } catch (error) {
        toast.error(error.message || 'Falha ao carregar usuario.');
        navigate('/usuarios');
      }
    }

    loadUsuario();
    return () => {
      active = false;
    };
  }, [id, navigate, token]);

  async function handleUpdate(form) {
    setIsSubmitting(true);
    try {
      await updateUsuario({ token, id, payload: form });

      toast.success('Usuario atualizado com sucesso.');
      navigate('/usuarios');
    } catch (error) {
      toast.error(error.message || 'Falha ao atualizar usuario.');
    } finally {
      setIsSubmitting(false);
    }
  }

  if (!initialData) {
    return <section className="usuarios-page form-page usuarios-form-page">Carregando usuario...</section>;
  }

  return (
    <section className="usuarios-page form-page usuarios-form-page">
      <div className="usuarios-header">
        <h1>Editar usuário</h1>
        <p className="page-subtitle">Atualize os dados e salve as alterações para aplicar no sistema.</p>
      </div>
      <UsuarioForm
        initialData={initialData}
        onSubmit={handleUpdate}
        onCancel={() => navigate('/usuarios')}
        submitLabel="Salvar alterações"
        isSubmitting={isSubmitting}
        isEdit
      />
    </section>
  );
}



