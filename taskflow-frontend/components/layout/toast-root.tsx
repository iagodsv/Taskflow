"use client";

import ToastContainer from "@/components/ui/toast-container";
import { ToastProvider } from "@/hooks/useToast";

export default function ToastRoot() {
  return (
    <ToastProvider>
      <ToastContainer />
    </ToastProvider>
  );
}
