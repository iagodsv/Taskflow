"use client";

import { UserModal, type UserForm } from "@/components/admin/users/user-modal";
import Container from "@/components/layout/container";
import { Button } from "@/components/ui/button";
import { Calendar } from "@/components/ui/calendar";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Text } from "@/components/ui/text";
import { useApiMutation } from "@/hooks/useApiMutation";
import { useGetData } from "@/hooks/useGetData";
import { useToast } from "@/hooks/useToast";
import {
  createUser,
  getSettings,
  getUsers,
  updateSettings,
  updateUser,
} from "@/services/api";
import type { Settings } from "@/services/types";
import { formatISO } from "date-fns";
import { useEffect, useMemo, useState } from "react";

export default function AdminSettingsPage() {
  const [emailInput, setEmailInput] = useState("");
  const [users, setUsers] = useState<
    Array<{
      id: string;
      name: string;
      role: "ADMIN" | "MANAGER" | "COLLABORATOR";
      managerId?: string | null;
      email?: string | null;
      balanceDays: number;
      active: boolean;
    }>
  >([]);
  const [usersLoading, setUsersLoading] = useState(false);
  const [selectedUserId, setSelectedUserId] = useState<string>("");
  const [openUserModal, setOpenUserModal] = useState(false);
  const [editingUser, setEditingUser] = useState<{
    id: string;
    name: string;
    email?: string | null;
    role: "ADMIN" | "MANAGER" | "COLLABORATOR";
    managerId?: string | null;
    balanceDays: number;
    active: boolean;
  } | null>(null);
  const { show } = useToast();

  const {
    data: form,
    loading,
    error,
    setData,
    refetch,
  } = useGetData<Settings>(getSettings);

  const { mutate: saveSettings, loading: saving } = useApiMutation<
    Settings,
    Settings
  >(
    async (payload) => {
      // Normaliza datas para yyyy-mm-dd
      const normalized: Settings = {
        ...payload,
        blackoutDays: (payload.blackoutDays || []).map((d) =>
          formatISO(new Date(d), { representation: "date" }),
        ),
      };
      const saved = await updateSettings(normalized);
      return saved;
    },
    {
      onSuccess: async (saved) => {
        setData(saved);
        await refetch();
        show({ title: "Configurações salvas" });
      },
      onError: (err) => {
        const e = err as { message?: string } | undefined;
        show({
          title: "Erro ao salvar configurações",
          description: e?.message || "Verifique os campos e tente novamente",
          variant: "destructive",
        });
      },
    },
  );

  const blackoutDates = useMemo(() => {
    if (!form) return [] as Date[];
    return (form.blackoutDays || []).map((d) => new Date(d));
  }, [form]);

  // Managers para o modal de usuário
  const managers = useMemo(
    () =>
      users
        .filter((u) => u.role === "MANAGER")
        .map((m) => ({ id: m.id, name: m.name })),
    [users],
  );

  async function loadUsers() {
    setUsersLoading(true);
    try {
      const data = await getUsers();
      setUsers(data);
    } finally {
      setUsersLoading(false);
    }
  }

  useEffect(() => {
    loadUsers();
  }, []);

  function update<K extends keyof Settings>(key: K, value: Settings[K]) {
    setData((prev) => (prev ? { ...prev, [key]: value } : prev));
  }

  async function handleSave() {
    if (!form) return;
    await saveSettings(form);
  }

  function handleAddEmail() {
    if (!emailInput.trim()) return;
    const email = emailInput.trim();
    if (!form) return;
    if (form.notificationEmails.includes(email)) return;
    update("notificationEmails", [...form.notificationEmails, email]);
    setEmailInput("");
  }

  function handleRemoveEmail(email: string) {
    if (!form) return;
    update(
      "notificationEmails",
      form.notificationEmails.filter((e) => e !== email),
    );
  }

  if (loading || !form) {
    return (
      <Container>
        <Text size="xl" weight="semibold">
          Admin / Configurações
        </Text>
        <p className="text-slate-600 mt-2">Carregando configurações...</p>
        {error ? (
          <p className="text-sm text-red-600 mt-2">
            Falha ao carregar configurações.
          </p>
        ) : null}
      </Container>
    );
  }

  return (
    <Container>
      <div className="mb-6 flex items-center justify-between">
        <Text size="2xl" weight="semibold">
          Admin / Configurações
        </Text>
        <Button
          onClick={handleSave}
          disabled={saving}
          className="bg-blue-600! hover:bg-blue-700! text-white!"
        >
          {saving ? "Salvando..." : "Salvar"}
        </Button>
      </div>

      <div className="grid gap-8 md:grid-cols-2">
        {/* Seletor de Usuário para configuração rápida */}
        <section className="space-y-3 md:col-span-2">
          <Text size="lg" weight="semibold">
            Configurar Usuário
          </Text>
          <p className="text-sm text-slate-600">
            Selecione um usuário para editar seus dados.
          </p>
          <div className="flex items-center gap-3">
            <div className="min-w-64">
              <Select
                value={selectedUserId}
                onValueChange={(v) => setSelectedUserId(v)}
              >
                <SelectTrigger disabled={usersLoading}>
                  <SelectValue placeholder="Selecione um usuário" />
                </SelectTrigger>
                <SelectContent>
                  {users.map((u) => (
                    <SelectItem key={u.id} value={u.id}>
                      {u.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </section>

        <section className="space-y-3">
          <Text size="lg" weight="semibold">
            Regras de Antecedência e Períodos
          </Text>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-1">
              <label className="text-sm text-slate-600">
                Limite de Antecedência (dias)
              </label>
              <Input
                type="number"
                value={form.minLeadDays}
                onChange={(e) => update("minLeadDays", Number(e.target.value))}
                min={0}
              />
            </div>
            <div className="grid gap-1">
              <label className="text-sm text-slate-600">
                Período Mínimo (dias)
              </label>
              <Input
                type="number"
                value={form.minPeriodDays}
                onChange={(e) =>
                  update("minPeriodDays", Number(e.target.value))
                }
                min={1}
              />
            </div>
            <div className="grid gap-1">
              <label className="text-sm text-slate-600">
                Período Máximo (dias)
              </label>
              <Input
                type="number"
                value={form.maxPeriodDays}
                onChange={(e) =>
                  update("maxPeriodDays", Number(e.target.value))
                }
                min={1}
              />
            </div>
          </div>
        </section>

        <section className="space-y-3">
          <Text size="lg" weight="semibold">
            Blackout Days
          </Text>
          <p className="text-sm text-slate-600">
            Selecione as datas indisponíveis:
          </p>
          <div className="rounded border bg-white p-3">
            <Calendar
              className="w-full"
              mode="multiple"
              selected={blackoutDates}
              onSelect={(dates) =>
                update(
                  "blackoutDays",
                  (dates || []).map((d) =>
                    formatISO(d!, { representation: "date" }),
                  ),
                )
              }
            />
          </div>
        </section>

        <section className="space-y-3 md:col-span-2">
          <Text size="lg" weight="semibold">
            Notificações
          </Text>
          <p className="text-sm text-slate-600">
            Emails que receberão as aprovações automaticamente:
          </p>
          <div className="flex gap-2">
            <Input
              placeholder="email@empresa.com"
              value={emailInput}
              onChange={(e) => setEmailInput(e.target.value)}
              className="max-w-80"
            />
            <Button variant="outline" onClick={handleAddEmail}>
              Adicionar
            </Button>
          </div>
          <div className="flex flex-wrap gap-2">
            {form.notificationEmails.map((email) => (
              <span
                key={email}
                className="inline-flex items-center gap-2 rounded-full border bg-white px-3 py-1 text-sm"
              >
                {email}
                <button
                  className="text-slate-500 hover:text-red-600"
                  onClick={() => handleRemoveEmail(email)}
                >
                  ×
                </button>
              </span>
            ))}
            {form.notificationEmails.length === 0 && (
              <span className="text-sm text-slate-500">
                Nenhum email adicionado.
              </span>
            )}
          </div>
        </section>
      </div>

      {/* Modal para criar/editar usuário direto nas Configurações */}
      <UserModal
        open={openUserModal}
        onOpenChange={(v) => {
          setOpenUserModal(v);
          if (!v) {
            setEditingUser(null);
          }
        }}
        managers={managers}
        initial={editingUser}
        onSubmit={async (formData: UserForm) => {
          try {
            if (editingUser?.id) {
              await updateUser(editingUser.id, {
                name: formData.name,
                email: formData.email ?? undefined,
                role: formData.role,
                managerId: formData.managerId ?? undefined,
                balanceDays: formData.balanceDays,
                active: formData.active,
                password:
                  formData.password && formData.password.length > 0
                    ? formData.password
                    : undefined,
              });
              show({ title: "Usuário atualizado", description: formData.name });
            } else {
              await createUser({
                name: formData.name,
                email: formData.email ?? undefined,
                role: formData.role,
                managerId: formData.managerId ?? undefined,
                balanceDays: formData.balanceDays,
                active: formData.active,
                password:
                  formData.password && formData.password.length > 0
                    ? formData.password
                    : undefined,
              });
              show({ title: "Usuário criado", description: formData.name });
            }
            setOpenUserModal(false);
            await loadUsers();
          } catch (err) {
            const e = err as { message?: string } | undefined;
            show({
              title: editingUser?.id ? "Erro ao atualizar" : "Erro ao criar",
              description: e?.message || "Operação não concluída",
              variant: "destructive",
            });
            // não fecha o modal em caso de erro
          }
        }}
      />
    </Container>
  );
}
