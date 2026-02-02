"use client";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Text } from "@/components/ui/text";
import { User } from "@/services/types";
import { useEffect, useState } from "react";

export type UserForm = {
  name: string;
  email?: string | null;
  role: User["role"];
  managerId?: string | null;
  balanceDays: number;
  active: boolean;
  password?: string;
};

type Props = {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (data: UserForm) => void;
  managers: Array<{ id: string; name: string }>;
  initial?: (Partial<User> & { id?: string }) | null;
};

export function UserModal({
  open,
  onOpenChange,
  onSubmit,
  managers,
  initial,
}: Props) {
  const [form, setForm] = useState<UserForm>({
    name: "",
    email: "",
    role: "COLLABORATOR",
    managerId: null,
    balanceDays: 22,
    active: true,
    password: "",
  });

  useEffect(() => {
    if (open) {
      setForm({
        name: initial?.name ?? "",
        email: initial?.email ?? "",
        role: (initial?.role as User["role"]) ?? "COLLABORATOR",
        managerId:
          initial?.role === "COLLABORATOR"
            ? (initial?.managerId ?? null)
            : null,
        balanceDays:
          typeof initial?.balanceDays === "number" ? initial.balanceDays : 22,
        active: typeof initial?.active === "boolean" ? initial.active : true,
        password: "",
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, initial?.id]);

  const isCollaborator = form.role === "COLLABORATOR";

  function handleChange<K extends keyof UserForm>(key: K, value: UserForm[K]) {
    setForm((prev) => ({ ...prev, [key]: value }));
  }

  function handleSave() {
    if (!form.name.trim()) return;
    onSubmit({
      ...form,
      name: form.name.trim(),
      managerId: isCollaborator ? (form.managerId ?? null) : null,
    });
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-160 -mt-16">
        <DialogHeader>
          <DialogTitle>
            {initial?.id ? "Editar Usuário" : "Novo Usuário"}
          </DialogTitle>
          <DialogDescription>
            Gerencie dados, role, gestor e saldo de férias.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-2">
          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Nome
            </Text>
            <Input
              value={form.name}
              onChange={(e) => handleChange("name", e.target.value)}
              placeholder="Ex.: João Silva"
            />
          </div>

          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Email
            </Text>
            <Input
              type="email"
              value={form.email ?? ""}
              onChange={(e) => handleChange("email", e.target.value)}
              placeholder="email@exemplo.com"
            />
          </div>

          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Role
            </Text>
            <select
              value={form.role}
              onChange={(e) =>
                handleChange("role", e.target.value as User["role"])
              }
              className="h-9 rounded border border-slate-300 bg-white px-3 text-sm outline-none focus:ring-2 focus:ring-slate-200"
            >
              <option value="COLLABORATOR">COLLABORATOR</option>
              <option value="MANAGER">MANAGER</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>

          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Gestor
            </Text>
            <select
              value={form.managerId ?? ""}
              onChange={(e) =>
                handleChange("managerId", e.target.value || null)
              }
              className="h-9 rounded border border-slate-300 bg-white px-3 text-sm outline-none focus:ring-2 focus:ring-slate-200"
              disabled={!isCollaborator}
            >
              <option value="">Sem gestor</option>
              {managers.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name}
                </option>
              ))}
            </select>
          </div>

          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Saldo de Dias
            </Text>
            <Input
              type="number"
              value={form.balanceDays}
              onChange={(e) =>
                handleChange("balanceDays", Number(e.target.value))
              }
              min={0}
            />
          </div>

          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              {initial?.id ? "Nova Senha (opcional)" : "Senha (opcional)"}
            </Text>
            <Input
              type="password"
              value={form.password ?? ""}
              onChange={(e) => handleChange("password", e.target.value)}
              placeholder={
                initial?.id ? "Deixe em branco para manter" : "Defina uma senha"
              }
            />
          </div>

          <div className="flex items-center gap-2">
            <input
              id="active"
              type="checkbox"
              checked={form.active}
              onChange={(e) => handleChange("active", e.target.checked)}
            />
            <label htmlFor="active" className="text-sm text-slate-700">
              Ativo
            </label>
          </div>
        </div>

        <DialogFooter>
          <DialogClose asChild>
            <Button variant="outline">Cancelar</Button>
          </DialogClose>
          <Button
            onClick={handleSave}
            className="bg-blue-600! hover:bg-blue-700! text-white!"
          >
            Salvar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
