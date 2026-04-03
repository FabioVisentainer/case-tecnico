import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function createSala(token, nome, lotacaoAlunos, lotacaoProfessores, andarId) {
  return requestJson(API_ROUTES.salas.base, {
    method: 'POST',
    token,
    body: { nome, lotacaoAlunos, lotacaoProfessores, andarId },
  });
}

export async function listSalasPorAndar(token, andarId, page = 0, size = 10) {
  const params = new URLSearchParams({
    andarId,
    page,
    size,
  });
  return requestJson(`${API_ROUTES.salas.base}?${params}`, { token });
}

export async function getSala(token, id) {
  return requestJson(API_ROUTES.salas.byId(id), { token });
}

export async function updateSala(token, id, nome, lotacaoAlunos, lotacaoProfessores, andarId) {
  return requestJson(API_ROUTES.salas.byId(id), {
    method: 'PUT',
    token,
    body: { nome, lotacaoAlunos, lotacaoProfessores, andarId },
  });
}

export async function deleteSala(token, id) {
  return requestJson(API_ROUTES.salas.byId(id), {
    method: 'DELETE',
    token,
  });
}

export async function updateSalaStatus(token, id, ativo) {
  return requestJson(API_ROUTES.salas.status(id), {
    method: 'PATCH',
    token,
    body: { ativo },
  });
}

