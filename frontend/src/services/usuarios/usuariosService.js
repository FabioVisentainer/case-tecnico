import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function listUsuarios({ token, page, size, mostrarInativos, q, papel }) {
  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
    mostrarInativos: String(mostrarInativos),
  });

  if (q) query.set('q', q);
  if (papel) query.set('papel', papel);

  return requestJson(`${API_ROUTES.usuarios.base}?${query.toString()}`, { token });
}

export async function getUsuarioById({ token, id }) {
  return requestJson(API_ROUTES.usuarios.byId(id), { token });
}

export async function createUsuario({ token, payload }) {
  return requestJson(API_ROUTES.usuarios.base, {
    method: 'POST',
    token,
    body: payload,
  });
}

export async function updateUsuario({ token, id, payload }) {
  return requestJson(API_ROUTES.usuarios.byId(id), {
    method: 'PUT',
    token,
    body: payload,
  });
}

export async function updateUsuarioStatus({ token, id, ativo }) {
  return requestJson(API_ROUTES.usuarios.status(id), {
    method: 'PATCH',
    token,
    body: { ativo },
  });
}



