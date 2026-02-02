import { Badge } from "@/components/ui/badge";
import { User } from "@/services/types";
import { ColumnDef } from "@tanstack/react-table";

export type AdminUserRow = User & { managerName?: string | null };

function RoleBadge({ role }: { role: User["role"] }) {
  const map: Record<string, { label: string; className: string }> = {
    ADMIN: {
      label: "ADMIN",
      className: "text-red-700 border-red-300 bg-red-100",
    },
    MANAGER: {
      label: "MANAGER",
      className: "text-blue-700 border-blue-300 bg-blue-100",
    },
    COLLABORATOR: {
      label: "COLLABORATOR",
      className: "text-emerald-700 border-emerald-300 bg-emerald-100",
    },
  };
  const cfg = map[role] ?? {
    label: role,
    className: "text-slate-700 border-slate-300 bg-slate-100",
  };
  return (
    <Badge variant="outline" className={cfg.className}>
      {cfg.label}
    </Badge>
  );
}

export const adminUserColumns: ColumnDef<AdminUserRow>[] = [
  {
    accessorKey: "name",
    header: "Nome",
    cell: ({ row }) => <span className="font-medium">{row.original.name}</span>,
  },
  {
    accessorKey: "email",
    header: "Email",
  },
  {
    accessorKey: "role",
    header: "Role",
    cell: ({ row }) => <RoleBadge role={row.original.role} />,
  },
  {
    accessorKey: "managerName",
    header: "Gestor",
    cell: ({ row }) => row.original.managerName ?? "-",
  },
  {
    accessorKey: "balanceDays",
    header: "Saldo Dias",
  },
  {
    accessorKey: "active",
    header: "Ativo",
    cell: ({ row }) => (row.original.active ? "Sim" : "Não"),
  },
];
