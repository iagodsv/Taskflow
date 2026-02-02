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
import { useToast } from "@/hooks/useToast";
import { createUser, deleteUser, getUsers, updateUser } from "@/services/api";
import type { ColumnDef } from "@tanstack/react-table";
import { useCallback, useEffect, useMemo, useState } from "react";

export default function AdminUsersPage() {
  const [rows, setRows] = useState<AdminUserRow[]>([]);
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState<AdminUserRow | null>(null);
  const { show } = useToast();

  const load = useCallback(async () => {
    try {
      const data = await getUsers();
      const idToName = new Map<string, string>();
      // Normaliza IDs para string para evitar mismatch (number vs string)
      data.forEach((u) => idToName.set(String(u.id), u.name));
      const mapped: AdminUserRow[] = data.map((u) => {
        const maybeManager = (
          u as unknown as {
            manager?: { id?: string | number; name?: string } | null;
          }
        ).manager;
        const mgrNameFromObj = maybeManager?.name ?? null;
        const mgrNameFromId = u.managerId
          ? (idToName.get(String(u.managerId)) ?? null)
          : null;
        return {
          ...u,
          managerName: mgrNameFromObj ?? mgrNameFromId,
        };
      });
      setRows(mapped);
    } catch (err) {
      const e = err as { message?: string } | undefined;
      show({
        title: "Erro ao carregar usuários",
        description: e?.message || "Verifique o backend",
        variant: "destructive",
      });
    } finally {
      // noop
    }
  }, [show]);

  useEffect(() => {
    load();
  }, [load]);

  const managers = useMemo(
    () =>
      rows
        .filter((r) => r.role === "MANAGER")
        .map((m) => ({ id: m.id, name: m.name })),
    [rows],
  );

  function openCreate() {
    setEditing(null);
    setOpen(true);
  }

  const openEdit = useCallback((row: AdminUserRow) => {
    setEditing(row);
    setOpen(true);
  }, []);

  async function handleSubmit(form: UserForm) {
    try {
      if (editing?.id) {
        await updateUser(editing.id, {
          name: form.name,
          email: form.email ?? undefined,
          role: form.role,
          managerId: form.managerId ?? undefined,
          balanceDays: form.balanceDays,
          active: form.active,
          password:
            form.password && form.password.length > 0
              ? form.password
              : undefined,
        });
        show({ title: "Usuário atualizado", description: form.name });
      } else {
        await createUser({
          name: form.name,
          email: form.email ?? undefined,
          role: form.role,
          managerId: form.managerId ?? undefined,
          balanceDays: form.balanceDays,
          active: form.active,
          password:
            form.password && form.password.length > 0
              ? form.password
              : undefined,
        });
        show({ title: "Usuário criado", description: form.name });
      }
      setOpen(false);
      await load();
    } catch (err) {
      const e = err as { message?: string } | undefined;
      show({
        title: editing?.id ? "Erro ao atualizar" : "Erro ao criar",
        description: e?.message || "Operação não concluída",
        variant: "destructive",
      });
      // não fecha o modal em caso de erro
    }
  }

  const handleDelete = useCallback(
    async (row: AdminUserRow) => {
      try {
        await deleteUser(row.id);
        show({ title: "Usuário excluído", description: row.name });
        await load();
      } catch (err) {
        const e = err as { message?: string } | undefined;
        show({
          title: "Erro ao excluir",
          description: e?.message || "Não foi possível excluir",
          variant: "destructive",
        });
      }
    },
    [load, show],
  );

  const columns: ColumnDef<AdminUserRow>[] = useMemo(() => {
    // Append actions column
    return [
      ...adminUserColumns,
      {
        id: "actions",
        header: "Ações",
        cell: ({ row }) => (
          <div className="flex gap-2">
            <button
              className="text-blue-600 hover:underline"
              onClick={() => openEdit(row.original)}
            >
              Editar
            </button>
            <button
              className="text-red-600 hover:underline"
              onClick={() => handleDelete(row.original)}
            >
              Excluir
            </button>
          </div>
        ),
      },
    ];
  }, [handleDelete, openEdit]);

  return (
    <Container>
      <div className="flex items-center justify-between mb-6">
        <Text size="2xl" weight="semibold">
          Admin / Usuários
        </Text>
        <Button
          onClick={openCreate}
          className="bg-blue-600! hover:bg-blue-700! text-white!"
        >
          Novo Usuário
        </Button>
      </div>

      <DataTable<AdminUserRow>
        columns={columns}
        data={rows}
        initialPageSize={10}
      />

      <UserModal
        open={open}
        onOpenChange={setOpen}
        onSubmit={handleSubmit}
        managers={managers}
        initial={editing}
      />
    </Container>
  );
}
