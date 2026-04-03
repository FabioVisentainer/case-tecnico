import { API_ROUTES } from '../core/apiRoutes';
import { requestJson } from '../core/httpClient';

export async function buscarDashboardGeral({ token }) {
  return requestJson(API_ROUTES.dashboards.geral, { token });
}

export async function buscarDashboardBlocos({ token }) {
  return requestJson(API_ROUTES.dashboards.blocos, { token });
}

export async function buscarDashboardSalas({ token }) {
  return requestJson(API_ROUTES.dashboards.salas, { token });
}

export async function buscarDashboardHorarios({ token }) {
  return requestJson(API_ROUTES.dashboards.horarios, { token });
}

export async function buscarRelatorioUsoAtualDashboard({ token }) {
  return requestJson(API_ROUTES.dashboards.relatorioAtual, { token });
}

export async function buscarRelatorioUsoDashboard({ token, inicio, fim }) {
  const params = new URLSearchParams({ inicio, fim });
  return requestJson(`${API_ROUTES.dashboards.relatorio}?${params.toString()}`, { token });
}

