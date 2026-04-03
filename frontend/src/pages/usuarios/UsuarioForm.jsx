import { useEffect, useState } from 'react';

export const DEFAULT_USUARIO_FORM = {
  nome: '',
  username: '',
  cpf: '',
  papel: 'ALUNO',
  senha: '',
  ativo: true,
};

export default function UsuarioForm({
  initialData = DEFAULT_USUARIO_FORM,
  onSubmit,
  onCancel,
  submitLabel,
  isSubmitting,
  isEdit,
}) {
  const [form, setForm] = useState(initialData);

  useEffect(() => {
    setForm(initialData);
  }, [initialData]);

  function onChangeField(event) {
    const { name, value, type, checked } = event.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value,
    }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    onSubmit(form);
  }

  return (
    <form className="user-form polished" onSubmit={handleSubmit}>
      <div className="form-intro">
        <p>Campos com * sao obrigatorios. Revise username e CPF com atencao para evitar duplicidades.</p>
      </div>
      <div className="form-grid polished">
        <div className="field-group">
          <label htmlFor="nome">Nome *</label>
          <input id="nome" name="nome" value={form.nome} onChange={onChangeField} placeholder="Ex: Gabriela Teixeira" required />
          <small className="field-hint">Use o nome completo. Espacos extras sao ajustados automaticamente.</small>
        </div>

        <div className="field-group">
          <label htmlFor="username">Username *</label>
          <input
            id="username"
            name="username"
            value={form.username}
            onChange={onChangeField}
            placeholder="Ex: gabriela.teixeira"
            required
            disabled={isEdit}
          />
          <small className="field-hint">
            {isEdit
              ? 'Username nao pode ser alterado apos o cadastro.'
              : 'Somente letras, numeros, ponto, underline e hifen (sem espacos).'}
          </small>
        </div>

        <div className="field-group">
          <label htmlFor="cpf">CPF *</label>
          <input
            id="cpf"
            name="cpf"
            value={form.cpf}
            onChange={onChangeField}
            placeholder="Ex: 123.456.789-09"
            required
            disabled={isEdit}
          />
          <small className="field-hint">
            {isEdit
              ? 'CPF nao pode ser alterado apos o cadastro.'
              : 'Aceita formato com ou sem mascara; validado automaticamente.'}
          </small>
        </div>

        <div className="field-group">
          <label htmlFor="papel">Papel *</label>
          <select id="papel" name="papel" value={form.papel} onChange={onChangeField}>
            <option value="ALUNO">Aluno(a)</option>
            <option value="PROFESSOR">Professor(a)</option>
          </select>
          <small className="field-hint">Define o perfil de acesso no sistema.</small>
        </div>

        <div className="field-group">
          <label htmlFor="senha">Senha {isEdit ? '(opcional)' : '*'}</label>
          <input
            id="senha"
            name="senha"
            type="password"
            value={form.senha}
            onChange={onChangeField}
            placeholder={isEdit ? 'Nova senha (opcional)' : 'Senha'}
            required={!isEdit}
          />
          <small className="field-hint">Minimo de 4 caracteres. Em edicao, deixe em branco para manter a senha atual.</small>
        </div>

        <div className="field-group">
          <label htmlFor="ativo">Status</label>
          <label className="switch-control form-switch">
            <span>{form.ativo ? 'Usuario ativo' : 'Usuario inativo'}</span>
            <button
              id="ativo"
              type="button"
              className={`switch ${form.ativo ? 'on' : ''}`}
              onClick={() => setForm((prev) => ({ ...prev, ativo: !prev.ativo }))}
              aria-pressed={form.ativo}
              aria-label="Alternar status do usuario"
            >
              <span className="switch-knob" />
            </button>
          </label>
          <small className="field-hint">Usuários inativos não conseguem autenticar no sistema.</small>
        </div>
      </div>

      <div className="form-actions">
        <button type="submit" className="btn-primary" disabled={isSubmitting}>
          {isSubmitting ? 'Salvando...' : submitLabel}
        </button>
        {onCancel ? (
          <button type="button" className="btn-neutral" onClick={onCancel}>
            Cancelar
          </button>
        ) : null}
      </div>
    </form>
  );
}


