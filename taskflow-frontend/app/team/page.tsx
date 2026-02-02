"use client";

import Container from "@/components/layout/container";
import { AvailabilityBoard } from "@/components/team/availability";
import { employeeColumns, type EmployeeRow } from "@/components/team/columns";
import { Button } from "@/components/ui/button";
import { DataTable } from "@/components/ui/data-table";
import { Text } from "@/components/ui/text";
import { useToast } from "@/hooks/useToast";
import { getUsers, getVacationRequests } from "@/services/api";
import type { PedidoFerias, User } from "@/services/types";
import { addMonths, format } from "date-fns";
import { useEffect, useMemo, useState } from "react";

export default function TeamPage() {
  const { show } = useToast();
  const [rows, setRows] = useState<EmployeeRow[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [vacations, setVacations] = useState<PedidoFerias[]>([]);
  const [currentMonth, setCurrentMonth] = useState<Date>(new Date());
  const [managerId, setManagerId] = useState<string>("");
  const [showList, setShowList] = useState(false);

  useEffect(() => {
    let active = true;
    (async () => {
      try {
        const [usersRes, vacationsRes] = await Promise.all([
          getUsers(),
          getVacationRequests(),
        ]);
        if (active) {
          setUsers(usersRes);
          setVacations(vacationsRes);
          setRows(
            usersRes.map((u) => ({
              id: u.id,
              name: u.name,
              role:
                (u.role as EmployeeRow["role"]) || ("COLLABORATOR" as const),
            })),
          );
        }
      } catch (e) {
        const err = e as { message?: string } | undefined;
        show({
          title: "Erro ao carregar equipe",
          description: err?.message || "Verifique o backend",
          variant: "destructive",
        });
        if (active) {
          setUsers([]);
          setVacations([]);
          setRows([]);
        }
      }
    })();
    return () => {
      active = false;
    };
  }, []);

  return (
    <Container>
      <div className="flex items-center justify-between mb-6">
        <Text size="2xl" weight="semibold">
          Equipe
        </Text>
      </div>

      <div className="mb-4 flex flex-wrap items-center gap-3">
        <div className="flex items-center gap-2">
          <span className="text-sm text-slate-600">Gestor:</span>
          <select
            className="h-9 rounded border border-slate-300 bg-white px-3 text-sm"
            value={managerId}
            onChange={(e) => setManagerId(e.target.value)}
          >
            <option value="">Todos</option>
            {users
              .filter((u) => u.role === "MANAGER")
              .map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name}
                </option>
              ))}
          </select>
        </div>

        <div className="ml-auto flex items-center gap-2">
          <Button
            variant="outline"
            onClick={() => setCurrentMonth((m) => addMonths(m, -1))}
            size="sm"
          >
            Mês anterior
          </Button>
          <div className="text-sm text-slate-700 w-32 text-center">
            {format(currentMonth, "MMMM yyyy")}
          </div>
          <Button
            variant="outline"
            onClick={() => setCurrentMonth((m) => addMonths(m, 1))}
            size="sm"
          >
            Próximo mês
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowList((v) => !v)}
          >
            {showList ? "Esconder lista" : "Mostrar lista"}
          </Button>
        </div>
      </div>

      <AvailabilityBoard
        users={useMemo(() => {
          const pool = users;
          const filtered = managerId
            ? pool.filter(
                (u) => u.id === managerId || u.managerId === managerId,
              )
            : pool;
          return filtered.map((u) => ({ id: u.id, name: u.name }));
        }, [users, managerId])}
        vacations={vacations}
        month={currentMonth}
      />

      {showList && (
        <div className="mt-6">
          <Text size="lg" weight="semibold" className="mb-2 block">
            Lista de Colaboradores
          </Text>
          <div className="rounded border bg-white max-h-[25vh] overflow-auto">
            <div className="p-2">
              <DataTable<EmployeeRow>
                columns={employeeColumns}
                data={rows}
                initialPageSize={10}
              />
            </div>
          </div>
        </div>
      )}
    </Container>
  );
}
