import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function buscarSalasAtivas({ token, q = '', page = 0, size = 12 }) {
  const params = new URLSearchParams({ page: String(page), size: String(size) });
  if (q && q.trim()) params.set('q', q.trim());
  return requestJson(`${API_ROUTES.presencas.salas}?${params.toString()}`, { token });
}

export async function checkinSala({ token, salaId }) {
  return requestJson(API_ROUTES.presencas.checkin, {
    method: 'POST',
    token,
    body: { salaId },
  });
}

export async function checkoutSala({ token }) {
  return requestJson(API_ROUTES.presencas.checkout, {
    method: 'PATCH',
    token,
  });
}

export async function buscarMinhaPresenca({ token }) {
  return requestJson(API_ROUTES.presencas.minha, { token });
}

export async function buscarAlunosPresentesMinhaSala({ token }) {
  return requestJson('/api/presencas/minha/alunos-presentes', { token });
}

