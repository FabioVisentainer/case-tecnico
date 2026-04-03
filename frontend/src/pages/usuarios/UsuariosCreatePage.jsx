import { useState } from 'react';
import toast from 'react-hot-toast';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../auth/AuthContext';
import { createUsuario } from '../../services/usuarios/usuariosService';
import UsuarioForm, { DEFAULT_USUARIO_FORM } from './UsuarioForm';
import './UsuariosPage.css';

export default function UsuariosCreatePage() {
  const { token } = useAuth();
  const navigate = useNavigate();
  const [isSubmitting, setIsSubmitting] = useState(false);

  async function handleCreate(form) {
    setIsSubmitting(true);
    try {
      await createUsuario({ token, payload: form });

      toast.success('Usuario cadastrado com sucesso.');
      navigate('/usuarios');
    } catch (error) {
      toast.error(error.message || 'Falha ao cadastrar usuario.');
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <section className="usuarios-page form-page usuarios-form-page">
      <div className="usuarios-header">
        <h1>Criar usuário</h1>
        <p className="page-subtitle">Preencha os campos abaixo para cadastrar um novo aluno(a) ou professor(a).</p>
      </div>
      <UsuarioForm
        initialData={DEFAULT_USUARIO_FORM}
        onSubmit={handleCreate}
        onCancel={() => navigate('/usuarios')}
        submitLabel="Cadastrar usuário"
        isSubmitting={isSubmitting}
        isEdit={false}
      />
    </section>
  );
}



