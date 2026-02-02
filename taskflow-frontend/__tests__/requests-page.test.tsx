import { render, screen } from "@testing-library/react";

vi.mock("@/hooks/useAuth", () => ({
  useAuth: () => ({ user: { role: "ADMIN" } }),
}));

vi.mock("@/hooks/useGetData", () => ({
  useGetData: () => ({ data: [] }),
}));

vi.mock("@/services/api", () => ({
  getUsers: vi.fn(async () => []),
  getVacationRequests: vi.fn(async () => []),
  approveVacationRequest: vi.fn(async () => {}),
  rejectVacationRequest: vi.fn(async () => {}),
  createVacationRequest: vi.fn(async () => ({})),
}));

describe("RequestsPage role gating", () => {
  it("mostra botão Novo Pedido para ADMIN", async () => {
    const Page = (await import("@/app/requests/page")).default;
    render(<Page />);
    expect(screen.getByText("Novo Pedido")).toBeInTheDocument();
  });

  it("mostra botão Novo Pedido para COLLABORATOR", async () => {
    vi.resetModules();
    vi.doMock("@/hooks/useAuth", () => ({
      useAuth: () => ({ user: { role: "COLLABORATOR" } }),
    }));
    const Page = (await import("@/app/requests/page")).default;
    render(<Page />);
    expect(screen.getByText("Novo Pedido")).toBeInTheDocument();
  });
});
