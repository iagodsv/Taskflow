import type { PedidoFerias } from "@/services/types";
import { eachDayOfInterval, endOfMonth, startOfMonth } from "date-fns";

export function getMonthDays(month: Date) {
  const start = startOfMonth(month);
  const end = endOfMonth(month);
  return eachDayOfInterval({ start, end });
}

export function vacationColor(status: PedidoFerias["statusAtual"]) {
  if (status === "APPROVED") return "bg-emerald-200";
  if (status === "PENDING") return "bg-amber-200";
  if (status === "REJECTED") return "bg-red-200";
  return "bg-slate-200";
}

export const NAME_COL_WIDTH = 160;
export const DAY_COL_WIDTH = 28;
export const CONTAINER_MAX_H = "calc(100dvh - 260px)";
