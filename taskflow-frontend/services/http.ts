import axios from "axios";

/**
 * Instância HTTP (Axios) sempre apontando para o backend real (sem mock).
 * - Adiciona automaticamente Authorization Bearer (quando houver token no localStorage).
 * - Normaliza erros para { status, message, original }.
 */

const baseURL =
  process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080/taskflow";

export const http = axios.create({
  baseURL,
  withCredentials: true,
});

http.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("accessToken");
    if (token) {
      config.headers = config.headers ?? {};
      config.headers["Authorization"] = `Bearer ${token}`;
    }
  }
  return config;
});

http.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error?.response?.status;
    const message =
      error?.response?.data?.message || error.message || "Erro desconhecido";
    if (typeof window !== "undefined" && status === 401) {
      try {
        localStorage.removeItem("accessToken");
      } catch {}
      // Redireciona para login em não autorizado
      window.location.replace("/login");
    }
    return Promise.reject({ status, message, original: error });
  },
);

export default http;
