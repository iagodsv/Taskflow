"use client";

import { Header } from "@/components/header/header.component";
import { Sidebar } from "@/components/sidebar/sidebar.component";
import React from "react";
import { useAuth } from "../../hooks/useAuth";
import { Login } from "../auth/login";

type ContainerProps = {
  children?: React.ReactNode;
};

export default function Container({ children }: ContainerProps) {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="min-h-screen grid place-items-center">
        <p className="text-sm text-slate-600">Carregando...</p>
      </div>
    );
  }

  if (!user) {
    return <Login />;
  }

  return (
    <div className="flex min-h-screen bg-background text-foreground">
      <Sidebar />
      <div className="flex-1 flex flex-col bg-card">
        <Header />
        <main className="flex-1 p-8">{children}</main>
      </div>
    </div>
  );
}
