"use client";

import { cn } from "@/lib/utils";
import { ptBR } from "date-fns/locale";
import * as React from "react";
import { DayPicker } from "react-day-picker";

export type CalendarProps = React.ComponentProps<typeof DayPicker>;

export function Calendar({ className, ...props }: CalendarProps) {
  return (
    <DayPicker
      locale={ptBR}
      showOutsideDays
      className={cn("p-3", className)}
      {...props}
      classNames={{
        months: "flex flex-col sm:flex-row space-y-4 sm:space-x-4 sm:space-y-0",
        month: "space-y-4",
        caption: "flex justify-center pt-1 relative items-center",
        caption_label: "text-sm font-medium",
        nav: "space-x-1 flex items-center",
        nav_button:
          "h-7 w-7 bg-transparent p-0 opacity-50 hover:opacity-100 hover:bg-slate-100 rounded",
        table: "w-full border-collapse",
        head_row: "grid grid-cols-7",
        head_cell:
          "text-slate-500 rounded-md w-9 font-normal text-[0.8rem] text-center",
        row: "grid grid-cols-7 w-full mt-2",
        cell: "text-center text-sm p-0 relative [&:has([aria-selected].day-outside)]:bg-slate-50 first:[&:has([aria-selected])]:rounded-l-md last:[&:has([aria-selected])]:rounded-r-md focus-within:relative focus-within:z-20",
        day: "h-9 w-9 p-0 font-normal rounded hover:bg-slate-100 aria-selected:bg-slate-900 aria-selected:text-white",
        day_range_end: "day-range-end",
        day_selected:
          "bg-slate-900 text-white hover:bg-slate-900 hover:text-white",
        day_today: "bg-slate-200 text-slate-900",
        day_outside: "day-outside text-slate-400 opacity-50",
        day_disabled: "text-slate-400 opacity-50",
        day_range_middle:
          "aria-selected:bg-slate-100 aria-selected:text-slate-900",
        day_hidden: "invisible",
      }}
    />
  );
}
