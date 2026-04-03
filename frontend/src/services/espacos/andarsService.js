import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function createAndar(token, nome, blocoId) {
  return requestJson(API_ROUTES.andares.base, {
    method: 'POST',
    token,
    body: { nome, blocoId },
  });
}

export async function listAndarPorBloco(token, blocoId, page = 0, size = 10) {
  const params = new URLSearchParams({
    blocoId,
    page,
    size,
  });
  return requestJson(`${API_ROUTES.andares.base}?${params}`, { token });
}

export async function getAndarComDetalhes(token, id) {
  return requestJson(API_ROUTES.andares.byId(id), { token });
}

export async function updateAndar(token, id, nome, blocoId) {
  return requestJson(API_ROUTES.andares.byId(id), {
    method: 'PUT',
    token,
    body: { nome, blocoId },
  });
}

export async function deleteAndar(token, id) {
  return requestJson(API_ROUTES.andares.byId(id), {
    method: 'DELETE',
    token,
  });
}

export async function updateAndarStatus(token, id, ativo) {
  return requestJson(API_ROUTES.andares.status(id), {
    method: 'PATCH',
    token,
    body: { ativo },
  });
}

