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
};



