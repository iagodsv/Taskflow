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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Text } from "@/components/ui/text";
import { useGetData } from "@/hooks/useGetData";
import { getUsers } from "@/services/api";
import { useState } from "react";

export type NewRequestData = {
  colaborador: string;
  inicio: string; // ISO date string
  fim: string; // ISO date string
  observacoes: string;
  collaboratorId?: string | null;
};

export function NewRequestModal({
  open,
  onOpenChange,
  onSubmit,
  canPickCollaborator = false,
  errorText,
}: {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSubmit?: (data: NewRequestData) => void;
  canPickCollaborator?: boolean;
  errorText?: string;
}) {
  const [form, setForm] = useState<NewRequestData>({
    colaborador: "",
    inicio: "",
    fim: "",
    observacoes: "",
    collaboratorId: null,
  });

  const { data: collaborators = [] } = useGetData(
    async () => {
      if (!canPickCollaborator) return [];
      const users = await getUsers();
      return users.map((u: { id: string; name: string }) => ({
        id: u.id,
        name: u.name,
      }));
    },
    { deps: [canPickCollaborator] },
  );

  function handleSave() {
    onSubmit?.(form);
  }
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nova Requisição</DialogTitle>
          <DialogDescription>
            Preencha os dados da solicitação
          </DialogDescription>
        </DialogHeader>
        {errorText ? (
          <p className="px-6 text-sm text-red-600">{errorText}</p>
        ) : null}
        <div className="px-6 pb-6 space-y-4">
          {canPickCollaborator ? (
            <div>
              <Text as="label" size="sm" weight="medium" className="block mb-1">
                Colaborador
              </Text>
              <Select
                value={form.collaboratorId ?? ""}
                onValueChange={(val: string) =>
                  setForm((f) => ({ ...f, collaboratorId: val || null }))
                }
              >
                <SelectTrigger className="w-full">
                  <SelectValue placeholder="Selecione um colaborador" />
                </SelectTrigger>
                <SelectContent>
                  {collaborators.map((c) => (
                    <SelectItem key={c.id} value={String(c.id)}>
                      {c.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          ) : (
            <div>
              <Text as="label" size="sm" weight="medium" className="block mb-1">
                Colaborador
              </Text>
              <Input
                placeholder="Seu nome"
                value={form.colaborador}
                onChange={(e) =>
                  setForm((f) => ({ ...f, colaborador: e.target.value }))
                }
              />
            </div>
          )}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div>
              <Text as="label" size="sm" weight="medium" className="block mb-1">
                Início
              </Text>
              <Input
                type="date"
                value={form.inicio}
                onChange={(e) =>
                  setForm((f) => ({ ...f, inicio: e.target.value }))
                }
              />
            </div>
            <div>
              <Text as="label" size="sm" weight="medium" className="block mb-1">
                Fim
              </Text>
              <Input
                type="date"
                value={form.fim}
                onChange={(e) =>
                  setForm((f) => ({ ...f, fim: e.target.value }))
                }
              />
            </div>
          </div>
          <div>
            <Text as="label" size="sm" weight="medium" className="block mb-1">
              Observações
            </Text>
            <Input
              placeholder="Opcional"
              value={form.observacoes}
              onChange={(e) =>
                setForm((f) => ({ ...f, observacoes: e.target.value }))
              }
            />
          </div>
        </div>
        <DialogFooter>
          <DialogClose asChild>
            <Button variant="outline">Cancelar</Button>
          </DialogClose>
          <Button
            onClick={handleSave}
            className="bg-blue-600 hover:bg-blue-700 text-white"
          >
            Salvar
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
