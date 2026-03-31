import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function getPrivateHello(token) {
  return requestJson(API_ROUTES.hello.private, { token });
}

