"use client";

import { cn } from "@/lib/utils";
import type { PedidoFerias } from "@/services/types";
import { format, isWeekend, isWithinInterval, parseISO } from "date-fns";
import { Fragment } from "react";
import { vacationColor } from "./utils";

/** Props de uma linha de colaborador no grid. */
type Props = {
  id: string;
  name: string;
  days: Date[];
  vacations: PedidoFerias[];
  nameColWidth: number;
  dayColWidth: number;
};

/**
 * Linha de colaborador com marcação de dias conforme os períodos de férias.
 */
export function UserRow({
  id,
  name,
  days,
  vacations,
  nameColWidth,
  dayColWidth,
}: Props) {
  return (
    <Fragment key={id}>
      <div
        className="sticky left-0 z-10 bg-white/90 px-3 py-2 text-sm border-b"
        style={{ width: nameColWidth }}
        title={name}
      >
        <span className="line-clamp-1">{name}</span>
      </div>
      {days.map((day, idx) => {
        const v = vacations.find((vac) =>
          isWithinInterval(day, {
            start: parseISO(vac.periodoInicio),
            end: parseISO(vac.periodoFim),
          }),
        );
        return (
          <div
            key={`${id}-${idx}`}
            className={cn(
              "border-b",
              isWeekend(day) && "bg-slate-50",
              v && cn("", vacationColor(v.statusAtual)),
            )}
            title={
              v
                ? `${name}: ${format(parseISO(v.periodoInicio), "dd/MM")}–${format(parseISO(v.periodoFim), "dd/MM")} (${v.statusAtual})`
                : undefined
            }
          />
        );
      })}
    </Fragment>
  );
}
