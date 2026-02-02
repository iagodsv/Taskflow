"use client";

import {
  adminUserColumns,
  type AdminUserRow,
} from "@/components/admin/users/columns";
import { UserModal, type UserForm } from "@/components/admin/users/user-modal";
import Container from "@/components/layout/container";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Text } from "@/components/ui/text";
import { useApiMutation } from "@/hooks/useApiMutation";
import { useGetData } from "@/hooks/useGetData";
import { useToast } from "@/hooks/useToast";
import { createUser, deleteUser, getUsers, updateUser } from "@/services/api";
import type { User } from "@/services/types";
import { useMemo, useState } from "react";

export default function UsersPage() {
  function getErrorMessage(err: unknown): string | undefined {
    if (typeof err === "string") return err;
    if (err && typeof err === "object" && "message" in err) {
      const msg = (err as { message?: unknown }).message;
      return typeof msg === "string" ? msg : undefined;
    }
    return undefined;
  }
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<User | null>(null);
  const { show } = useToast();

  const { data, error, refetch } = useGetData<User[]>(getUsers, {
    deps: [],
  });

  const managers = useMemo(
    () =>
      (data ?? [])
        .filter((u) => u.role === "MANAGER")
        .map((m) => ({ id: m.id, name: m.name })),
    [data],
  );

  const rows: AdminUserRow[] = useMemo(() => {
    const users = data ?? [];
    const byId = new Map(users.map((u) => [u.id, u] as const));
    return users.map((u) => ({
      ...u,
      managerName: u.managerId ? (byId.get(u.managerId)?.name ?? null) : null,
    }));
  }, [data]);

  const {
    mutate: createMut,
    loading: creating,
    error: createError,
  } = useApiMutation(
    async (input: UserForm) => {
      const res = await createUser({
        name: input.name,
        email: input.email ?? undefined,
        role: input.role,
        managerId: input.managerId ?? undefined,
        balanceDays: input.balanceDays,
        active: input.active,
      });
      return res;
    },
    {
      onSuccess: () => {
        setOpen(false);
        void refetch();
        show({ title: "Usuário criado" });
      },
      onError: (err) => {
        const e = err as { message?: string } | undefined;
        show({
          title: "Erro ao criar",
          description: e?.message || "Operação não concluída",
          variant: "destructive",
        });
      },
    },
  );

  const {
    mutate: updateMut,
    loading: updating,
    error: updateError,
  } = useApiMutation(
    async (input: { id: string; data: UserForm }) => {
      const res = await updateUser(input.id, {
        name: input.data.name,
        email: input.data.email ?? undefined,
        role: input.data.role,
        managerId: input.data.managerId ?? undefined,
        balanceDays: input.data.balanceDays,
        active: input.data.active,
      });
      return res;
    },
    {
      onSuccess: () => {
        setOpen(false);
        setEditing(null);
        void refetch();
        show({ title: "Usuário atualizado" });
      },
      onError: (err) => {
        const e = err as { message?: string } | undefined;
        show({
          title: "Erro ao atualizar",
          description: e?.message || "Operação não concluída",
          variant: "destructive",
        });
      },
    },
  );

  const { mutate: deleteMut, loading: deleting } = useApiMutation(
    async (id: string) => {
      const res = await deleteUser(id);
      return res;
    },
    {
      onSuccess: () => {
        void refetch();
        show({ title: "Usuário excluído" });
      },
      onError: (err) => {
        const e = err as { message?: string } | undefined;
        show({
          title: "Erro ao excluir",
          description: e?.message || "Operação não concluída",
          variant: "destructive",
        });
      },
    },
  );

  function onAdd() {
    setEditing(null);
    setOpen(true);
  }
  function onEdit(u: User) {
    setEditing(u);
    setOpen(true);
  }
  async function onDelete(u: User) {
    // Impede exclusão de gestor com colaboradores vinculados (evita erro 500)
    const hasTeam =
      u.role === "MANAGER" && (data ?? []).some((x) => x.managerId === u.id);
    if (hasTeam) {
      show({
        title: "Não é possível excluir",
        description: "Este gestor possui colaboradores associados.",
        variant: "destructive",
      });
      return;
    }
    await deleteMut(u.id);
  }

  return (
    <Container>
      <div className="flex items-center justify-between mb-6">
        <Text size="2xl" weight="semibold">
          Usuários
        </Text>
        <div className="flex gap-2">
          <Button onClick={onAdd} disabled={creating || updating}>
            Novo
          </Button>
        </div>
      </div>

      <div className="rounded border bg-white">
        <div className="p-2">
          {createError || updateError ? (
            <p className="text-xs text-red-600 mb-2">
              {getErrorMessage(createError) ||
                getErrorMessage(updateError) ||
                "Falha na operação"}
            </p>
          ) : null}
          <DataTable<AdminUserRow>
            columns={[
              ...adminUserColumns,
              {
                id: "actions",
                header: "Ações",
                cell: ({ row }) => (
                  <div className="flex items-center gap-2">
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => onEdit(row.original as User)}
                    >
                      Editar
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => onDelete(row.original as User)}
                      disabled={deleting}
                    >
                      Excluir
                    </Button>
                  </div>
                ),
              },
            ]}
            data={rows}
            initialPageSize={10}
          />
          {error ? (
            <p className="text-xs text-red-600 mt-2">
              Falha ao carregar usuários
            </p>
          ) : null}
        </div>
      </div>

      <UserModal
        open={open}
        onOpenChange={setOpen}
        onSubmit={(form) => {
          if (editing?.id) {
            void updateMut({ id: editing.id, data: form });
          } else {
            void createMut(form);
          }
        }}
        managers={managers}
        initial={editing ?? undefined}
      />
    </Container>
  );
}
