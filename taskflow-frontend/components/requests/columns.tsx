import { Badge } from "@/components/ui/badge";
import { ColumnDef } from "@tanstack/react-table";

export type RequestRow = {
  id: string;
  colaborador: string;
  periodoInicio: string; // ISO date
  periodoFim: string; // ISO date
  statusAtual: "PENDING" | "APPROVED" | "REJECTED" | string;
  proximoStatus?: string | null;
};

function formatDate(iso: string) {
  if (!iso) return "-";
  const d = new Date(iso);
  if (Number.isNaN(d.getTime())) return iso;
  const dd = String(d.getDate()).padStart(2, "0");
  const mm = String(d.getMonth() + 1).padStart(2, "0");
  const yyyy = d.getFullYear();
  return `${dd}/${mm}/${yyyy}`;
}

function StatusBadge({ status }: { status: RequestRow["statusAtual"] }) {
  const map: Record<string, { label: string; className: string }> = {
    APPROVED: {
      label: "Aprovado",
      className: "text-green-700 border-green-300 bg-green-100",
    },
    PENDING: {
      label: "Pendente",
      className: "text-amber-700 border-amber-300 bg-amber-100",
    },
    REJECTED: {
      label: "Rejeitado",
      className: "text-red-700 border-red-300 bg-red-100",
    },
  };
  const cfg = map[status] ?? {
    label: status,
    className: "text-slate-700 border-slate-300 bg-slate-100",
  };
  return (
    <Badge variant="outline" className={cfg.className}>
      {cfg.label}
    </Badge>
  );
}

export const columns: ColumnDef<RequestRow>[] = [
  {
    accessorKey: "colaborador",
    header: "Colaborador",
    cell: ({ row }) => (
      <span className="font-medium">{row.original.colaborador}</span>
    ),
  },
  {
    accessorKey: "periodoInicio",
    header: "Início",
    cell: ({ row }) => <span>{formatDate(row.original.periodoInicio)}</span>,
  },
  {
    accessorKey: "periodoFim",
    header: "Fim",
    cell: ({ row }) => <span>{formatDate(row.original.periodoFim)}</span>,
  },
  {
    accessorKey: "statusAtual",
    header: "Status",
    cell: ({ row }) => <StatusBadge status={row.original.statusAtual} />,
    enableSorting: true,
  },
];
