"use client";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Text } from "@/components/ui/text";
import { useState } from "react";
import { useAuth } from "../../hooks/useAuth";

export function Header() {
  const { user, logout } = useAuth();
  const [openProfile, setOpenProfile] = useState(false);

  return (
    <header className="flex items-center justify-between px-8 py-4 bg-white border-b">
      <div />
      <div className="flex items-center gap-3">
        {user ? (
          <>
            <span className="text-sm text-slate-700">Olá, {user.name}</span>
            <Button
              variant="outline"
              onClick={() => setOpenProfile(true)}
              className="text-slate-800"
            >
              Perfil
            </Button>
          </>
        ) : null}
        <Button variant="ghost" onClick={logout} className="text-slate-800">
          Sair
        </Button>
      </div>

      <Dialog open={openProfile} onOpenChange={setOpenProfile}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Meu Perfil</DialogTitle>
            <DialogDescription>Informações da sua conta</DialogDescription>
          </DialogHeader>
          {user ? (
            <div className="space-y-2 px-6 pb-4">
              <div className="grid grid-cols-2 gap-2">
                <Text as="span" size="sm" className="text-slate-600">
                  Nome
                </Text>
                <Text as="span" size="sm" weight="medium">
                  {user.name}
                </Text>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <Text as="span" size="sm" className="text-slate-600">
                  E-mail
                </Text>
                <Text as="span" size="sm" weight="medium">
                  {user.email}
                </Text>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <Text as="span" size="sm" className="text-slate-600">
                  Perfil
                </Text>
                <Text as="span" size="sm" weight="medium">
                  {user.role}
                </Text>
              </div>
              {typeof user.balanceDays !== "undefined" ? (
                <div className="grid grid-cols-2 gap-2">
                  <Text as="span" size="sm" className="text-slate-600">
                    Saldo de dias
                  </Text>
                  <Text as="span" size="sm" weight="medium">
                    {user.balanceDays}
                  </Text>
                </div>
              ) : null}
            </div>
          ) : null}
        </DialogContent>
      </Dialog>
    </header>
  );
}
