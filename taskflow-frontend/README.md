# Taskflow Vacation — Frontend

Uma aplicação Next.js (React + Tailwind) que fornece a interface para gestão de férias: solicitação, aprovação, rejeição, administração de usuários e configurações.

## Visão Geral
- **Páginas**: Dashboard, Pedidos (Requests), Equipe (Team), Usuários, Admin/Configurações, Login.
- **Arquitetura**: Roteamento via `app/`, componentes reutilizáveis em `components/ui`, hooks em `hooks/`, serviços REST em `services/`.
- **Autenticação**: JWT armazenado em `localStorage` e inserido automaticamente em `Authorization: Bearer` nas requisições.
- **Perfis de acesso**: `ADMIN`, `MANAGER` (Gestor) e `COLLABORATOR` (Colaborador).

## Pré-requisitos
- Node.js 20+ (recomendado: 20 LTS)
- npm 10+
- Backend rodando (veja o README do backend) acessível em `http://localhost:8080/taskflow` (padrão)

## Configuração
- Variáveis de ambiente do Front:
	- `NEXT_PUBLIC_API_BASE_URL`: URL base da API. Padrão: `http://localhost:8080/taskflow`

Crie um arquivo `.env.local` (opcional):

```
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/taskflow
```

## Instalação e execução

Instale dependências, suba em modo dev e rode testes:

```bash
npm install
npm run dev
npm test
```

Build e produção:

```bash
npm run build
npm start
```

## Estrutura principal
- `app/`: páginas (`/dashboard`, `/requests`, `/team`, `/users`, `/admin/settings`, `/login`).
- `components/ui/`: componentes base (botões, inputs, cards, tabela, diálogo, etc.).
- `hooks/`: `useAuth` (autenticação), `useToast`, `useGetData`, `useApiMutation`.
- `services/`: `http` (Axios com interceptors), `api` (funções REST), `endpoints` (mapa de rotas), `types`.

## Autenticação — passo a passo
A tela de login está em `components/auth/login.tsx`. O fluxo usa o hook `useAuth` e o endpoint `/auth/login` do backend.

1. Acesse `/login`.
2. Informe seu e-mail e senha.
3. Ao autenticar, o token é salvo e você é redirecionado para `/dashboard`.

### Perfis ("3 tipos de login")
Os três perfis suportados são:
- **ADMIN**: acesso completo; pode criar/editar usuários e atualizar configurações.
- **MANAGER (Gestor)**: gerencia pedidos da sua equipe.
- **COLLABORATOR (Colaborador)**: solicita férias e acompanha status.

Se você utilizar o endpoint de seed (veja o backend), existem usuários de exemplo:
- Admin: `iago.admin@taskflow.pt` / senha `123456`
- Gestor: `iago.gestor@taskflow.pt` / senha `123456`
- Colaborador: `iago.colab1@taskflow.pt` / senha `123456`

## Chamadas de API principais
Alguns endpoints utilizados (veja `services/endpoints.ts`):
- `GET /vacations`: lista pedidos
- `POST /vacations`: cria pedido
- `POST /vacations/:id/approve`: aprova pedido
- `POST /vacations/:id/reject`: rejeita pedido
- `GET /users`, `POST /users`, `PUT /users/:id`, `DELETE /users/:id`
- `GET /settings`, `PUT /settings`
- `POST /auth/login`, `GET /auth/me`

## Dicas de desenvolvimento
- Em 401, o interceptor remove o token e redireciona para `/login`.
- Ajuste `NEXT_PUBLIC_API_BASE_URL` caso rode o backend em outra porta/host.
- Testes com Vitest e Testing Library já configurados.
