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
import { useState } from "react";

type Props = {
  open: boolean;
  onOpenChange: (v: boolean) => void;
  onSubmit: (data: {
    name: string;
    role: "ADMIN" | "MANAGER" | "COLLABORATOR";
  }) => void;
};

export function NewEmployeeModal({ open, onOpenChange, onSubmit }: Props) {
  const [name, setName] = useState("");
  const [role, setRole] = useState<"ADMIN" | "MANAGER" | "COLLABORATOR">(
    "COLLABORATOR",
  );

  function handleSave() {
    if (!name.trim()) return;
    onSubmit({ name: name.trim(), role });
  }

  return (
    <Dialog
      open={open}
      onOpenChange={(v) => {
        if (v) {
          setName("");
          setRole("COLLABORATOR");
        }
        onOpenChange(v);
      }}
    >
      <DialogContent className="sm:max-w-120 -mt-16">
        <DialogHeader>
          <DialogTitle>Novo Funcionário</DialogTitle>
          <DialogDescription>
            Cadastre um colaborador e defina o tipo.
          </DialogDescription>
        </DialogHeader>

        <div className="grid gap-4 py-2">
          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Nome
            </Text>
            <Input
              placeholder="Ex.: João Silva"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="grid gap-1">
            <Text size="sm" className="text-slate-600">
              Tipo
            </Text>
            <select
              value={role}
              onChange={(e) =>
                setRole(e.target.value as "ADMIN" | "MANAGER" | "COLLABORATOR")
              }
              className="h-9 rounded border border-slate-300 bg-white px-3 text-sm outline-none focus:ring-2 focus:ring-slate-200"
            >
              <option value="COLLABORATOR">Collaborator</option>
              <option value="MANAGER">Manager</option>
              <option value="ADMIN">Admin</option>
            </select>
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
