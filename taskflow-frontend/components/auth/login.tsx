"use client";

import logo from "@/assets/taskflow_logo.png";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { useAuth } from "@/hooks/useAuth";
import { useToast } from "@/hooks/useToast";
import Image from "next/image";
import { useRouter } from "next/navigation";
import React, { useState } from "react";

/**
 * Tela de Login mínima: pede e-mail e autentica.
 * Exibida enquanto não houver usuário autenticado.
 */
export function Login() {
  const { login } = useAuth();
  const router = useRouter();
  const { show } = useToast();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const ok = await login(email.trim(), password);
      if (!ok) {
        const msg = "Falha ao autenticar. Verifique suas credenciais.";
        setError(msg);
        show({
          title: "Erro de login",
          description: msg,
          variant: "destructive",
        });
      } else {
        router.replace("/dashboard");
      }
    } catch (err) {
      const e = err as { message?: string } | undefined;
      const msg = e?.message || "Não foi possível autenticar";
      setError(msg);
      show({
        title: "Erro de login",
        description: msg,
        variant: "destructive",
      });
    }
    setSubmitting(false);
  }

  return (
    <div className="min-h-screen grid place-items-center bg-card">
      <Card className="w-full max-w-sm p-6 space-y-4">
        <div className="flex flex-col items-center gap-3">
          <Image src={logo} alt="TaskFlow" width={128} height={128} priority />
          <h1 className="text-lg font-semibold">Entrar</h1>
          <p className="text-xs text-slate-600">Acesse para continuar</p>
        </div>
        <form onSubmit={onSubmit} className="space-y-3">
          <Input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
            required
          />
          <Input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="sua senha"
            required
          />
          {error ? <p className="text-xs text-red-600">{error}</p> : null}
          <Button type="submit" disabled={submitting}>
            {submitting ? "Entrando..." : "Entrar"}
          </Button>
        </form>
      </Card>
    </div>
  );
}
