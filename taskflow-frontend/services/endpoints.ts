/**
 * Endpoints centralizados para facilitar manutenção de paths e métodos.
 */
export const endpoints = {
  vacations: {
    name: "vacations",
    path: "/vacations",
    method: "GET",
    description: "Lista pedidos de férias",
  },
  createVacation: {
    name: "createVacation",
    path: "/vacations",
    method: "POST",
    description: "Cria um novo pedido de férias",
  },
  approveVacation: {
    name: "approveVacation",
    path: "/vacations/:id/approve",
    method: "POST",
    description: "Aprova um pedido de férias",
  },
  rejectVacation: {
    name: "rejectVacation",
    path: "/vacations/:id/reject",
    method: "POST",
    description: "Rejeita um pedido de férias",
  },
  users: {
    name: "users",
    path: "/users",
    method: "GET",
    description: "Lista usuários",
  },
  userById: {
    name: "userById",
    path: "/users/:id",
    method: "GET",
    description: "Detalhe de usuário por id",
  },
  createUser: {
    name: "createUser",
    path: "/users",
    method: "POST",
    description: "Cria usuário",
  },
  updateUser: {
    name: "updateUser",
    path: "/users/:id",
    method: "PUT",
    description: "Atualiza usuário",
  },
  deleteUser: {
    name: "deleteUser",
    path: "/users/:id",
    method: "DELETE",
    description: "Remove usuário",
  },
  settings: {
    name: "settings",
    path: "/settings",
    method: "GET",
    description: "Obtém configurações globais",
  },
  updateSettings: {
    name: "updateSettings",
    path: "/settings",
    method: "PUT",
    description: "Atualiza configurações globais",
  },
} as const;

/**
 * Substitui parâmetros nomeados no path (ex.: /users/:id) pelos valores informados.
 * Mantém o placeholder se o valor não for fornecido.
 */
export function buildPath(
  path: string,
  params?: Record<string, string | number>,
) {
  if (!params) return path;
  return path.replace(/:([a-zA-Z_][a-zA-Z0-9_]*)/g, (_, key: string) => {
    const value = params[key];
    if (value === undefined || value === null) return `:${key}`;
    return String(value);
  });
}
