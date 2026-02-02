import { Badge } from "@/components/ui/badge";
import { ColumnDef } from "@tanstack/react-table";

export type EmployeeRow = {
  id: string;
  name: string;
  role: "ADMIN" | "MANAGER" | "COLLABORATOR" | string;
};

function RoleBadge({ role }: { role: EmployeeRow["role"] }) {
  const map: Record<string, { label: string; className: string }> = {
    ADMIN: {
      label: "Admin",
      className: "text-red-700 border-red-300 bg-red-100",
    },
    MANAGER: {
      label: "Manager",
      className: "text-blue-700 border-blue-300 bg-blue-100",
    },
    COLLABORATOR: {
      label: "Collaborator",
      className: "text-slate-700 border-slate-300 bg-slate-100",
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

export const employeeColumns: ColumnDef<EmployeeRow>[] = [
  {
    accessorKey: "name",
    header: "Nome",
    cell: ({ row }) => <span className="font-medium">{row.original.name}</span>,
  },
  {
    accessorKey: "role",
    header: "Tipo",
    cell: ({ row }) => <RoleBadge role={row.original.role} />,
  },
];
