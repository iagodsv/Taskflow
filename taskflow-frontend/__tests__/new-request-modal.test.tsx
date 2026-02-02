import { NewRequestModal } from "@/components/requests/new-request-modal";
import { render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

vi.mock("@/services/api", () => ({
  getUsers: vi.fn(async () => [
    { id: "1", name: "Alice" },
    { id: "2", name: "Bob" },
  ]),
}));

describe("NewRequestModal", () => {
  it("abre o select e mostra colaboradores quando canPickCollaborator", async () => {
    const user = userEvent.setup({ pointerEventsCheck: false });
    render(
      <NewRequestModal
        open={true}
        onOpenChange={() => {}}
        onSubmit={() => {}}
        canPickCollaborator={true}
      />,
    );

    const trigger = screen.getByRole("combobox");
    await user.click(trigger);

    // Opções devem aparecer
    expect(await screen.findByText("Alice")).toBeInTheDocument();
    expect(await screen.findByText("Bob")).toBeInTheDocument();
  });

  it("usa input de nome quando não pode escolher colaborador", async () => {
    render(
      <NewRequestModal
        open={true}
        onOpenChange={() => {}}
        onSubmit={() => {}}
        canPickCollaborator={false}
      />,
    );

    const input = screen.getByPlaceholderText("Seu nome");
    expect(input).toBeInTheDocument();
  });
});
