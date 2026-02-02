"use client";

import { useToast } from "@/hooks/useToast";

export default function ToastContainer() {
  const { toasts, dismiss } = useToast();
  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-10000 flex flex-col items-center gap-2">
      {toasts.map((t) => (
        <div
          key={t.id}
          className={
            "min-w-64 max-w-96 rounded-md border shadow-lg bg-white text-slate-900 px-3 py-2 " +
            (t.variant === "destructive"
              ? "border-red-300 bg-red-50 text-red-700"
              : "border-slate-200")
          }
        >
          <div className="flex items-start gap-3">
            <div className="flex-1">
              {t.title ? (
                <div className="text-sm font-medium">{t.title}</div>
              ) : null}
              {t.description ? (
                <div className="text-sm leading-snug">{t.description}</div>
              ) : null}
            </div>
            <button
              className="text-xs text-slate-500 hover:text-slate-700"
              onClick={() => dismiss(t.id)}
            >
              fechar
            </button>
          </div>
        </div>
      ))}
    </div>
  );
}
