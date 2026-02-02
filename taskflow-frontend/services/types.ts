import { endpoints } from "./endpoints";

/**
 * Representa um pedido de férias no sistema.
 * Datas devem estar em ISO (yyyy-mm-dd ou ISO completo).
 */
export type PedidoFerias = {
  id: string;
  colaborador: string;
  /** Data inicial do período (ISO). */
  periodoInicio: string;
  /** Data final do período (ISO). */
  periodoFim: string;
  statusAtual: "PENDING" | "APPROVED" | "REJECTED";
  proximoStatus?: "APPROVED" | "REJECTED" | null;
};

/** Métodos HTTP suportados pelos endpoints. */
export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

/**
 * Definição de um endpoint de API.
 * Usado para centralizar path/método e facilitar construção de rotas.
 */
export type EndpointDef = {
  name: string;
  /** Caminho do recurso (pode conter parâmetros ex.: /users/:id). */
  path: string;
  method: HttpMethod;
  description?: string;
};

export type EndpointKey = keyof typeof endpoints;

/** Representa um usuário do sistema. */
export type User = {
  id: string;
  name: string;
  email?: string | null;
  role: "ADMIN" | "MANAGER" | "COLLABORATOR";
  managerId?: string | null;
  balanceDays: number;
  active: boolean;
};

/** Configurações globais que afetam a criação/validação de férias. */
export type Settings = {
  /** Dias mínimos de antecedência para solicitar férias. */
  minLeadDays: number;
  /** Tamanho mínimo (em dias) de um período. */
  minPeriodDays: number;
  /** Tamanho máximo (em dias) de um período. */
  maxPeriodDays: number;
  /** Datas indisponíveis (ISO yyyy-mm-dd). */
  blackoutDays: string[];
  /** Emails que recebem notificações automáticas. */
  notificationEmails: string[];
};
