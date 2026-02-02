"use client";

import logo from "@/assets/taskflow_logo.png";
import { useAuth } from "@/hooks/useAuth";
import { routes } from "@/lib/routes";
import { Home, List, Settings, Users } from "lucide-react";
import Image from "next/image";

export function Sidebar() {
  const { user } = useAuth();
  const isAdmin = user?.role === "ADMIN";
  const canSeeTeam = user?.role === "ADMIN" || user?.role === "MANAGER";
  return (
    <aside className="w-60 bg-slate-800 text-white min-h-screen flex flex-col py-6 px-4 gap-2">
      <div className="text-xl font-bold mb-8 flex items-center gap-2">
        <Image
          src={logo}
          alt="TaskFlow"
          width={200}
          height={40}
          className="rounded"
          priority
        />
      </div>
      <nav className="flex flex-col gap-2">
        <a
          className="flex items-center gap-2 px-3 py-2 rounded hover:bg-slate-700"
          href={routes.dashboard}
        >
          <Home size={18} /> Dashboard
        </a>
        <a
          className="flex items-center gap-2 px-3 py-2 rounded hover:bg-slate-700"
          href={routes.myRequests}
        >
          <List size={18} /> Meus Pedidos
        </a>
        {canSeeTeam && (
          <a
            className="flex items-center gap-2 px-3 py-2 rounded hover:bg-slate-700"
            href={routes.team}
          >
            <Users size={18} /> Equipe
          </a>
        )}
        {isAdmin && (
          <>
            <div className="mt-4 text-xs text-slate-400">ADMIN ACTIONS</div>
            <a
              className="flex items-center gap-2 px-3 py-2 rounded hover:bg-slate-700"
              href={routes.admin.users}
            >
              <Users size={18} /> Usuários
            </a>
            <a
              className="flex items-center gap-2 px-3 py-2 rounded hover:bg-slate-700"
              href={routes.admin.settings}
            >
              <Settings size={18} /> Configurações
            </a>
          </>
        )}
      </nav>
    </aside>
  );
}
