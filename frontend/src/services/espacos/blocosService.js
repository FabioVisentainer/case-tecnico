import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function createBloco(token, nome) {
  return requestJson(API_ROUTES.blocos.base, {
    method: 'POST',
    token,
    body: { nome },
  });
}

export async function listBlocos(token, page = 0, size = 10, mostrarInativos = false, q = '') {
  const params = new URLSearchParams({
    page,
    size,
    mostrarInativos,
  });
  if (q && q.trim()) {
    params.set('q', q.trim());
  }
  return requestJson(`${API_ROUTES.blocos.base}?${params}`, { token });
}

export async function getBlocoComDetalhes(token, id) {
  return requestJson(API_ROUTES.blocos.byId(id), { token });
}

export async function updateBloco(token, id, nome) {
  return requestJson(API_ROUTES.blocos.byId(id), {
    method: 'PUT',
    token,
    body: { nome },
  });
}

export async function deleteBloco(token, id) {
  return requestJson(API_ROUTES.blocos.byId(id), {
    method: 'DELETE',
    token,
  });
}

export async function updateBlocoStatus(token, id, ativo) {
  return requestJson(API_ROUTES.blocos.status(id), {
    method: 'PATCH',
    token,
    body: { ativo },
  });
}

