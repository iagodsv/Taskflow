"use client";

import Container from "@/components/layout/container";
import { Card } from "@/components/ui/card";
import { Text } from "@/components/ui/text";
import { getUsers, getVacationRequests } from "@/services/api";
import type { PedidoFerias, User } from "@/services/types";
// Removido cálculo por mês; vamos usar contagem direta do backend
import { useToast } from "@/hooks/useToast";
import { useEffect, useMemo, useState } from "react";

/**
 * Dashboard simples com métricas do mês e atalhos.
 * Foco em código enxuto e fácil de alterar.
 */
export default function DashboardPage() {
  const { show } = useToast();
  const [users, setUsers] = useState<User[]>([]);
  const [vacations, setVacations] = useState<PedidoFerias[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let active = true;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const [u, v] = await Promise.all([getUsers(), getVacationRequests()]);
        if (active) {
          setUsers(u);
          setVacations(v);
        }
      } catch {
        if (active) {
          setError("Falha ao carregar dados do dashboard");
          show({
            title: "Erro no dashboard",
            description: "Verifique o backend",
            variant: "destructive",
          });
        }
      } finally {
        if (active) setLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  const { approvedCount, pendingCount, rejectedCount } = useMemo(() => {
    let approved = 0;
    let pending = 0;
    let rejected = 0;
    for (const v of vacations) {
      if (v.statusAtual === "APPROVED") approved += 1;
      if (v.statusAtual === "PENDING") pending += 1;
      if (v.statusAtual === "REJECTED") rejected += 1;
    }
    return {
      approvedCount: approved,
      pendingCount: pending,
      rejectedCount: rejected,
    };
  }, [vacations]);

  return (
    <Container>
      <div className="mb-6">
        <Text size="2xl" weight="semibold">
          Dashboard
        </Text>
        <p className="text-slate-600 mt-2">
          Resumo rápido do mês e atalhos úteis.
        </p>
      </div>

      {error && (
        <Card className="p-4 mb-4">
          <p className="text-red-600">{error}</p>
        </Card>
      )}

      <div className="grid gap-4 md:grid-cols-3">
        <Card className="p-4">
          <p className="text-sm text-slate-600">Colaboradores</p>
          <p className="text-2xl font-semibold">{users.length}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-slate-600">Férias aprovadas</p>
          <p className="text-2xl font-semibold">{approvedCount}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-slate-600">Pedidos pendentes</p>
          <p className="text-2xl font-semibold">{pendingCount}</p>
        </Card>
        <Card className="p-4">
          <p className="text-sm text-slate-600">Pedidos recusados</p>
          <p className="text-2xl font-semibold">{rejectedCount}</p>
        </Card>
      </div>

      {loading && <p className="text-sm text-slate-500 mt-4">Carregando...</p>}
    </Container>
  );
}
