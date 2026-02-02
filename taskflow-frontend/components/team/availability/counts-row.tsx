"use client";

import { cn } from "@/lib/utils";
import { isWeekend } from "date-fns";

/** Props da linha de contagem de colaboradores em férias por dia. */
type Props = {
  days: Date[];
  counts: number[];
  nameColWidth: number;
  dayColWidth: number;
};

/** Exibe a quantidade de pessoas em férias por dia. */
export function CountsRow({ days, counts, nameColWidth, dayColWidth }: Props) {
  return (
    <>
      <div
        className="sticky left-0 z-10 bg-white/90 px-3 py-1 text-xs text-slate-600 border-b"
        style={{ width: nameColWidth }}
      >
        Em férias
      </div>
      {days.map((d, i) => (
        <div
          key={`count-${i}`}
          className={cn(
            "flex items-center justify-center border-b text-[10px]",
            counts[i] > 0 ? "text-slate-800" : "text-slate-400",
            isWeekend(d) && "bg-slate-50",
          )}
        >
          {counts[i] || "-"}
        </div>
      ))}
    </>
  );
}
