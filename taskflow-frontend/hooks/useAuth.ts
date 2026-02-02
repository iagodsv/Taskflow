"use client";

import { login as apiLogin, me } from "@/services/api";
import { useRouter } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
// tipos locais suficientes para autenticação

type AuthUser = {
  id: string | number;
  name: string;
  email: string;
  role: string;
  balanceDays?: number;
  active?: boolean;
};

/**
 * Hook simples de autenticação baseado em token no localStorage.
 * - Na montagem, tenta carregar `me()` se houver token.
 * - `login(email)` chama API, salva token e carrega usuário.
 * - `logout()` remove token e limpa estado.
 */
export function useAuth() {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  const loadUser = useCallback(async () => {
    try {
      const u = await me();
      setUser(u);
    } catch {
      setUser(null);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    const token =
      typeof window !== "undefined"
        ? localStorage.getItem("accessToken")
        : null;
    if (token) {
      loadUser();
    } else {
      setLoading(false);
      // Sem token: o Container exibirá a tela de Login.
      // Evitamos redirecionar aqui para não criar loops em /login.
    }
  }, [loadUser]);

  const login = useCallback(
    async (email: string, password: string) => {
      setLoading(true);
      try {
        const res = await apiLogin({ email, password });
        if (res?.token) {
          localStorage.setItem("accessToken", res.token);
          await loadUser();
          return true;
        }
        return false;
      } catch {
        setLoading(false);
        return false;
      }
    },
    [loadUser],
  );

  const logout = useCallback(() => {
    localStorage.removeItem("accessToken");
    setUser(null);
    router.replace("/login");
  }, []);

  return { user, loading, login, logout };
}
