export const API_ROUTES = {
  auth: {
    login: '/api/auth/login',
    me: '/api/auth/me',
  },
  hello: {
    private: '/api/private/hello',
  },
  usuarios: {
    base: '/api/usuarios',
    byId: (id) => `/api/usuarios/${id}`,
    status: (id) => `/api/usuarios/${id}/status`,
  },
  blocos: {
    base: '/api/blocos',
    byId: (id) => `/api/blocos/${id}`,
    status: (id) => `/api/blocos/${id}/status`,
  },
  andares: {
    base: '/api/andares',
    byId: (id) => `/api/andares/${id}`,
    status: (id) => `/api/andares/${id}/status`,
  },
  salas: {
    base: '/api/salas',
    byId: (id) => `/api/salas/${id}`,
    status: (id) => `/api/salas/${id}/status`,
  },
  presencas: {
    base: '/api/presencas',
    salas: '/api/presencas/salas',
    checkin: '/api/presencas/checkin',
    checkout: '/api/presencas/checkout',
    minha: '/api/presencas/minha',
  },
  dashboards: {
    base: '/api/dashboards',
    geral: '/api/dashboards/geral',
    blocos: '/api/dashboards/blocos',
    salas: '/api/dashboards/salas',
    horarios: '/api/dashboards/horarios',
    relatorio: '/api/dashboards/relatorio',
    relatorioAtual: '/api/dashboards/relatorio/atual',
  },
};



