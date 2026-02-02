"use client";

import Container from "@/components/layout/container";
import { columns, type RequestRow } from "@/components/requests/columns";
import { NewRequestModal } from "@/components/requests/new-request-modal";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Text } from "@/components/ui/text";
import { useApiMutation } from "@/hooks/useApiMutation";
import { useAuth } from "@/hooks/useAuth";
import { useGetData } from "@/hooks/useGetData";
import { useToast } from "@/hooks/useToast";
import {
  approveVacationRequest,
  createVacationRequest,
  getUsers,
  getVacationRequests,
  rejectVacationRequest,
} from "@/services/api";
import { useEffect, useMemo, useState } from "react";

export default function RequestsPage() {
  const [open, setOpen] = useState(false);
  const [submitError, setSubmitError] = useState("");
  const { show } = useToast();
  const { user } = useAuth();
  const canManage = user?.role === "ADMIN" || user?.role === "MANAGER";
  const canPickCollaborator = canManage;

  // Seleção de colaborador é tratada dentro do próprio modal quando permitido.

  // Carrega a lista de pedidos via hook de leitura
  const {
    data: vacations,
    error,
    refetch,
  } = useGetData<RequestRow[]>(
    async () => {
      const data = await getVacationRequests();
      // Assume que o backend já retorna no formato esperado; se necessário, mapear aqui.
      return data as unknown as RequestRow[];
    },
    { deps: [], cacheKey: "vacationRequests" },
  );

  // Toast no erro de carregamento
  useEffect(() => {
    if (error) {
      const e = error as { message?: string } | undefined;
      show({
        title: "Erro ao carregar férias",
        description: e?.message || "Verifique o backend",
        variant: "destructive",
      });
    }
  }, [error, show]);

  // Mutação para criar novo pedido
  const { mutate: createReq, loading: creating } = useApiMutation(
    async (input: {
      colaborador: string;
      inicio: string;
      fim: string;
      collaboratorId?: string | null;
    }) => {
      const payload = {
        startDate: input.inicio,
        endDate: input.fim,
        collaboratorId: input.collaboratorId ?? undefined,
      };
      const res = await createVacationRequest(payload);
      return res as unknown as RequestRow;
    },
    {
      onSuccess: async () => {
        await refetch();
        show({
          title: "Pedido criado",
          description: "Sua requisição de férias foi enviada.",
        });
      },
      onError: (err: unknown) => {
        const e = err as { message?: string } | undefined;
        const msg = e?.message || "Falha ao criar pedido";
        show({
          title: "Erro ao criar",
          description: msg,
          variant: "destructive",
        });
      },
    },
  );

  function handleNewRequest() {
    setSubmitError("");
    setOpen(true);
  }

  async function handleSubmit(form: {
    colaborador: string;
    inicio: string;
    fim: string;
    observacoes: string;
    collaboratorId?: string | null;
  }) {
    // 1) Validar datas
    const start = new Date(form.inicio);
    const end = new Date(form.fim);
    if (Number.isNaN(start.getTime()) || Number.isNaN(end.getTime())) {
      setSubmitError("Datas inválidas.");
      show({
        title: "Datas inválidas",
        description: "Corrija as datas e tente novamente.",
        variant: "destructive",
      });
      return;
    }
    if (start > end) {
      setSubmitError("Data inicial não pode ser após a final.");
      show({
        title: "Período inválido",
        description: "A data inicial não pode ser após a final.",
        variant: "destructive",
      });
      return;
    }
    // Dias solicitados (inclusivo)
    const MS_PER_DAY = 24 * 60 * 60 * 1000;
    const requestedDays =
      Math.floor((end.getTime() - start.getTime()) / MS_PER_DAY) + 1;
    if (requestedDays <= 0) {
      setSubmitError("Período deve ter pelo menos 1 dia.");
      show({
        title: "Período inválido",
        description: "Selecione pelo menos 1 dia.",
        variant: "destructive",
      });
      return;
    }

    // 2) Resolver colaborador alvo e saldo disponível
    let targetName: string = user?.name || form.colaborador;
    let availableDays: number | undefined =
      typeof user?.balanceDays === "number" ? user.balanceDays : undefined;

    if (canPickCollaborator && form.collaboratorId) {
      try {
        const users = await getUsers();
        const found = users.find(
          (u) => String(u.id) === String(form.collaboratorId),
        );
        if (found?.name) targetName = found.name;
        if (typeof found?.balanceDays === "number") {
          availableDays = found.balanceDays;
        }
      } catch {
        // ignora falha de lookup
      }
    }

    if (
      !canPickCollaborator &&
      typeof availableDays === "undefined" &&
      user?.id != null
    ) {
      try {
        const users = await getUsers();
        const meUser = users.find((u) => String(u.id) === String(user.id));
        if (typeof meUser?.balanceDays === "number") {
          availableDays = meUser.balanceDays;
        }
      } catch {
        // ignora falha
      }
    }

    if (typeof availableDays === "number" && requestedDays > availableDays) {
      const msg =
        canPickCollaborator && form.collaboratorId
          ? `Período excede o saldo de ${availableDays} dias do colaborador.`
          : `Período excede o seu saldo de ${availableDays} dias.`;
      setSubmitError(msg);
      show({
        title: "Saldo insuficiente",
        description: msg,
        variant: "destructive",
      });
      return;
    }

    // 3) Checar sobreposição com pedidos existentes do mesmo colaborador (exceto rejeitados)
    const rangesOverlap = (
      aStart: string,
      aEnd: string,
      bStart: string,
      bEnd: string,
    ) => {
      const aS = new Date(aStart).getTime();
      const aE = new Date(aEnd).getTime();
      const bS = new Date(bStart).getTime();
      const bE = new Date(bEnd).getTime();
      if (
        Number.isNaN(aS) ||
        Number.isNaN(aE) ||
        Number.isNaN(bS) ||
        Number.isNaN(bE)
      )
        return false;
      return aS <= bE && bS <= aE;
    };

    const conflict = (vacations ?? []).some((v) => {
      return (
        v.colaborador === targetName &&
        v.statusAtual !== "REJECTED" &&
        rangesOverlap(form.inicio, form.fim, v.periodoInicio, v.periodoFim)
      );
    });

    if (conflict) {
      setSubmitError("Já existe férias nesse período para este colaborador.");
      show({
        title: "Conflito de período",
        description: "Já existe férias neste intervalo para este colaborador.",
        variant: "destructive",
      });
      return;
    }

    // 3.1) Conflito global: qualquer colaborador no período (não rejeitado)
    const globalConflict = (vacations ?? []).some((v) => {
      return (
        v.statusAtual !== "REJECTED" &&
        rangesOverlap(form.inicio, form.fim, v.periodoInicio, v.periodoFim)
      );
    });
    if (globalConflict) {
      setSubmitError("Já existe alguém de férias neste período.");
      show({
        title: "Conflito de período",
        description: "Ninguém pode marcar férias nesse intervalo.",
        variant: "destructive",
      });
      return;
    }

    // 4) Prosseguir com criação
    const created = await createReq({
      colaborador: form.colaborador,
      inicio: form.inicio,
      fim: form.fim,
      collaboratorId: form.collaboratorId ?? null,
    });
    if (created) {
      setSubmitError("");
      setOpen(false);
    }
  }

  const rows = useMemo(() => vacations ?? [], [vacations]);
  return (
    <Container>
      <div className="text-xs text-slate-500 mb-2"></div>
      <div className="flex justify-between items-center mb-6">
        <Text size="2xl" weight="semibold">
          Histórico de Pedidos
        </Text>
        <Button
          onClick={() => handleNewRequest()}
          className="bg-blue-600! hover:bg-blue-700! text-white! shadow px-4 py-2 rounded-md font-medium text-sm"
          disabled={creating}
        >
          {creating ? "Salvando..." : "Novo Pedido"}
        </Button>
      </div>

      <DataTable<RequestRow>
        columns={[
          ...columns,
          {
            id: "actions",
            header: "Ações",
            cell: ({ row }) => (
              <div className="flex items-center gap-2">
                {canManage && row.original.statusAtual === "PENDING" && (
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={async () => {
                      try {
                        await approveVacationRequest(row.original.id);
                        await refetch();
                        show({ title: "Pedido aprovado" });
                      } catch (err) {
                        const e = err as { message?: string } | undefined;
                        show({
                          title: "Erro ao aprovar",
                          description: e?.message || "Tente novamente",
                          variant: "destructive",
                        });
                      }
                    }}
                  >
                    Aprovar
                  </Button>
                )}
                {canManage && row.original.statusAtual === "PENDING" && (
                  <Button
                    size="sm"
                    variant="outline"
                    className="border-red-600 text-red-600 hover:bg-red-50"
                    onClick={async () => {
                      try {
                        await rejectVacationRequest(row.original.id);
                        await refetch();
                        show({ title: "Pedido rejeitado" });
                      } catch (err) {
                        const e = err as { message?: string } | undefined;
                        show({
                          title: "Erro ao rejeitar",
                          description: e?.message || "Tente novamente",
                          variant: "destructive",
                        });
                      }
                    }}
                  >
                    Rejeitar
                  </Button>
                )}
              </div>
            ),
          },
        ]}
        data={rows}
        initialPageSize={10}
      />

      <NewRequestModal
        open={open}
        onOpenChange={setOpen}
        onSubmit={handleSubmit}
        canPickCollaborator={!!canPickCollaborator}
        errorText={submitError || undefined}
      />
      {error ? (
        <p className="text-xs text-red-600 mt-2">Falha ao carregar férias</p>
      ) : null}
    </Container>
  );
}
