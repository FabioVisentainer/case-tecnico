import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function loginAuth({ username, senha }) {
  return requestJson(API_ROUTES.auth.login, {
    method: 'POST',
    body: { username, senha },
  });
}

export async function fetchCurrentUser(token) {
  return requestJson(API_ROUTES.auth.me, { token });
}



