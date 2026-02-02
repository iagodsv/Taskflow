"use client";

import { cn } from "@/lib/utils";
import { format, isWeekend } from "date-fns";

/** Props da linha de cabeçalho (nomes dos dias). */
type Props = {
  days: Date[];
  nameColWidth: number;
  dayColWidth: number;
};

/** Cabeçalho com rótulo "Colaborador" e dias do mês. */
export function HeaderRow({ days, nameColWidth, dayColWidth }: Props) {
  return (
    <>
      <div
        className="sticky left-0 z-10 bg-white/90 px-3 py-2 text-sm font-medium border-b"
        style={{ width: nameColWidth }}
      >
        Colaborador
      </div>
      {days.map((d, i) => (
        <div
          key={i}
          className={cn(
            "flex items-center justify-center border-b text-[10px] text-slate-600",
            isWeekend(d) && "bg-slate-50",
          )}
          title={format(d, "dd/MM")}
        >
          {format(d, "d")}
        </div>
      ))}
    </>
  );
}
