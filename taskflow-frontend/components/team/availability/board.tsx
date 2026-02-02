"use client";

import { cn } from "@/lib/utils";
import type { PedidoFerias } from "@/services/types";
import { addDays, isWithinInterval, parseISO } from "date-fns";
import { useMemo } from "react";
import { CountsRow } from "./counts-row";
import { HeaderRow } from "./header-row";
import { UserRow } from "./user-row";
import {
  CONTAINER_MAX_H,
  DAY_COL_WIDTH,
  getMonthDays,
  NAME_COL_WIDTH,
} from "./utils";

/** Usuário resumido para exibição no board de disponibilidade. */
export type AvailabilityUser = { id: string; name: string };

/** Propriedades do componente `AvailabilityBoard`. */
type Props = {
  users: AvailabilityUser[];
  vacations: PedidoFerias[];
  month: Date;
};

/**
 * Grid de disponibilidade do time por mês.
 * Mantém scroll apenas dentro do próprio componente.
 */
export function AvailabilityBoard({ users, vacations, month }: Props) {
  const days = useMemo(() => getMonthDays(month), [month]);

  const vacationsByUser = useMemo(() => {
    const map = new Map<string, PedidoFerias[]>();
    for (const v of vacations) {
      const key = v.colaborador; // mock atual usa nome
      const list = map.get(key) ?? [];
      list.push(v);
      map.set(key, list);
    }
    return map;
  }, [vacations]);

  const dayCounts = useMemo(() => {
    const counts = new Array(days.length).fill(0) as number[];
    for (const u of users) {
      const list = vacationsByUser.get(u.name) ?? [];
      for (const vac of list) {
        const start = parseISO(vac.periodoInicio);
        const end = parseISO(vac.periodoFim);
        days.forEach((d, idx) => {
          if (
            isWithinInterval(d, {
              start,
              end: addDays(end, 0),
            })
          ) {
            counts[idx] += 1;
          }
        });
      }
    }
    return counts;
  }, [users, days, vacationsByUser]);

  return (
    <div
      className={cn(
        "w-full max-w-full overflow-x-auto overflow-y-auto rounded border bg-white",
      )}
      style={{ maxHeight: CONTAINER_MAX_H }}
    >
      <div
        className="grid"
        style={{
          gridTemplateColumns: `${NAME_COL_WIDTH}px repeat(${days.length}, minmax(${DAY_COL_WIDTH}px, 1fr))`,
        }}
      >
        <HeaderRow
          days={days}
          nameColWidth={NAME_COL_WIDTH}
          dayColWidth={DAY_COL_WIDTH}
        />

        <CountsRow
          days={days}
          counts={dayCounts}
          nameColWidth={NAME_COL_WIDTH}
          dayColWidth={DAY_COL_WIDTH}
        />

        {users.map((u) => (
          <UserRow
            key={u.id}
            id={u.id}
            name={u.name}
            days={days}
            vacations={vacationsByUser.get(u.name) ?? []}
            nameColWidth={NAME_COL_WIDTH}
            dayColWidth={DAY_COL_WIDTH}
          />
        ))}
      </div>
    </div>
  );
}
