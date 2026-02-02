import { buildPath, endpoints } from "./endpoints";
import http from "./http";
import { PedidoFerias, Settings, User } from "./types";

export type CreateUserPayload = {
  name: string;
  email?: string | null;
  role: "ADMIN" | "MANAGER" | "COLLABORATOR";
  managerId?: string | null;
  balanceDays?: number;
  active?: boolean;
  password?: string;
};

export type UpdateUserPayload = Partial<CreateUserPayload>;

/** Obtém a lista de pedidos de férias. */
export async function getVacationRequests() {
  const { data } = await http.get<
    Array<{
      id: number | string;
      collaborator?: { id: number | string; name: string } | null;
      startDate: string;
      endDate: string;
      status: "PENDING" | "APPROVED" | "REJECTED" | string;
    }>
  >(endpoints.vacations.path);
  // Mapeia para o formato usado no front (PedidoFerias)
  return data.map((v) => ({
    id: String(v.id),
    colaborador: v.collaborator?.name ?? "",
    periodoInicio: v.startDate,
    periodoFim: v.endDate,
    statusAtual: v.status as PedidoFerias["statusAtual"],
    proximoStatus: null,
  }));
}

/** Cria um novo pedido de férias. */
export async function createVacationRequest(payload: {
  startDate: string;
  endDate: string;
  collaboratorId?: string | number;
}) {
  const { data } = await http.post<{
    id: number | string;
    collaborator?: { id: number | string; name: string } | null;
    startDate: string;
    endDate: string;
    status: "PENDING" | "APPROVED" | "REJECTED" | string;
  }>(endpoints.createVacation.path, payload);
  const mapped: PedidoFerias = {
    id: String(data.id),
    colaborador: data.collaborator?.name ?? "",
    periodoInicio: data.startDate,
    periodoFim: data.endDate,
    statusAtual: data.status as PedidoFerias["statusAtual"],
    proximoStatus: null,
  };
  return mapped;
}

/** Aprova um pedido de férias existente. */
export async function approveVacationRequest(id: string | number) {
  const path = buildPath(endpoints.approveVacation.path, { id });
  const { data } = await http.post<{
    id: number | string;
    collaborator?: { id: number | string; name: string } | null;
    startDate: string;
    endDate: string;
    status: "PENDING" | "APPROVED" | "REJECTED" | string;
  }>(path);
  const mapped: PedidoFerias = {
    id: String(data.id),
    colaborador: data.collaborator?.name ?? "",
    periodoInicio: data.startDate,
    periodoFim: data.endDate,
    statusAtual: data.status as PedidoFerias["statusAtual"],
    proximoStatus: null,
  };
  return mapped;
}

/** Rejeita um pedido de férias existente. */
export async function rejectVacationRequest(id: string | number) {
  const path = buildPath(endpoints.rejectVacation.path, { id });
  const { data } = await http.post<{
    id: number | string;
    collaborator?: { id: number | string; name: string } | null;
    startDate: string;
    endDate: string;
    status: "PENDING" | "APPROVED" | "REJECTED" | string;
  }>(path);
  const mapped: PedidoFerias = {
    id: String(data.id),
    colaborador: data.collaborator?.name ?? "",
    periodoInicio: data.startDate,
    periodoFim: data.endDate,
    statusAtual: data.status as PedidoFerias["statusAtual"],
    proximoStatus: null,
  };
  return mapped;
}

/** Lista todos os usuários. */
export async function getUsers() {
  const { data } = await http.get<User[]>(endpoints.users.path);
  return data;
}

/** Cria um usuário. */
export async function createUser(payload: CreateUserPayload) {
  const { data } = await http.post<User>(endpoints.createUser.path, payload);
  return data;
}

/** Atualiza um usuário existente. */
export async function updateUser(id: string, payload: UpdateUserPayload) {
  const path = buildPath(endpoints.updateUser.path, { id });
  const { data } = await http.put<User>(path, payload);
  return data;
}

/** Remove um usuário. Retorna void para aceitar 204/200 sem corpo. */
export async function deleteUser(id: string): Promise<void> {
  const path = buildPath(endpoints.deleteUser.path, { id });
  await http.delete(path);
}

/** Faz um ping público para checar saúde do backend. */
export async function pingBackend() {
  const { data } = await http.get<{ status: string }>("/public/ping");
  return data;
}

/** Autentica um usuário (ambiente dev/mock). */
export async function login(payload: {
  email: string;
  password?: string;
  name?: string;
}) {
  const { data } = await http.post<{
    token: string;
    id: number;
    name: string;
    email: string;
    role: string;
  }>("/auth/login", payload);
  return data;
}

/** Retorna dados do usuário autenticado. */
export async function me() {
  const { data } = await http.get<{
    id: number;
    name: string;
    email: string;
    role: string;
  }>("/auth/me");
  return data;
}

/** Obtém as configurações globais. */
export async function getSettings() {
  const { data } = await http.get<Settings>(endpoints.settings.path);
  return data;
}

/** Atualiza as configurações globais. */
export async function updateSettings(payload: Settings) {
  const { data } = await http.put<Settings>(
    endpoints.updateSettings.path,
    payload,
  );
  return data;
}
