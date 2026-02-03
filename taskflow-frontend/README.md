# Taskflow Vacation — Frontend

Aplicação Next.js (React + Tailwind) para gestão de férias: solicitação, aprovação, rejeição, administração de usuários e configurações.

## 🚀 Início Rápido com Docker

A forma mais simples de rodar todo o sistema é via Docker Compose no backend:

```bash
cd ../taskflow-backend
docker-compose up --build
```

Isso irá subir:

| Serviço | Porta | URL |
|---------|-------|-----|
| PostgreSQL | 5432 | `localhost:5432` |
| Backend (Java) | 8080 | http://localhost:8080/taskflow |
| Frontend (Next.js) | 3000 | http://localhost:3000 |

### Primeiro Acesso

1. Acesse http://localhost:3000
2. Faça login com:
   - **Email**: `iago.admin@taskflow.pt`
   - **Senha**: `123456`

---

## 🛠️ Desenvolvimento Local

### Pré-requisitos
- Node.js 20+ (recomendado: 20 LTS)
- npm 10+
- Backend rodando em `http://localhost:8080/taskflow`

### Instalação

```bash
npm install
```

### Configuração (Opcional)

Crie `.env.local` se o backend estiver em outra URL:

```
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/taskflow
```

### Executando

```bash
# Desenvolvimento
npm run dev

# Build de produção
npm run build
npm start
```

### Testes

```bash
npm test
```

---

## 📁 Estrutura do Projeto

```
taskflow-frontend/
├── app/                    # Páginas (App Router)
│   ├── dashboard/          # Dashboard principal
│   ├── requests/           # Pedidos de férias
│   ├── team/               # Visão da equipe
│   ├── users/              # Gestão de usuários
│   ├── admin/settings/     # Configurações (ADMIN)
│   └── login/              # Autenticação
├── components/
│   ├── ui/                 # Componentes base (shadcn/ui)
│   ├── auth/               # Login
│   ├── header/             # Cabeçalho
│   ├── sidebar/            # Menu lateral
│   └── requests/           # Componentes de férias
├── hooks/                  # Hooks customizados
│   ├── useAuth.ts          # Autenticação
│   ├── useGetData.ts       # Fetch de dados
│   ├── useApiMutation.ts   # Mutations
│   └── useToast.tsx        # Notificações
├── services/               # Comunicação com API
│   ├── api.ts              # Funções REST
│   ├── http.ts             # Axios configurado
│   ├── endpoints.ts        # Mapa de rotas
│   └── types.ts            # Tipos TypeScript
└── lib/                    # Utilitários
```

---

## 👥 Perfis de Acesso

| Perfil | Acesso |
|--------|--------|
| **ADMIN** | Acesso completo: usuários, configurações, todos os pedidos |
| **MANAGER** | Gerencia pedidos da sua equipe |
| **COLLABORATOR** | Solicita férias e acompanha status |

### Usuários de Exemplo

| Perfil | Email | Senha |
|--------|-------|-------|
| ADMIN | iago.admin@taskflow.pt | 123456 |
| MANAGER | iago.gestor@taskflow.pt | 123456 |
| COLLABORATOR | iago.colab1@taskflow.pt | 123456 |

---

## 🔐 Autenticação

O sistema usa JWT armazenado em `localStorage`:

1. Usuário faz login em `/login`
2. Token é salvo automaticamente
3. Interceptor do Axios adiciona `Authorization: Bearer` em todas as requisições
4. Em caso de 401, usuário é redirecionado para `/login`

### Hook useAuth

```typescript
const { user, token, login, logout, isAuthenticated } = useAuth();
```

---

## 📡 Endpoints Utilizados

| Endpoint | Descrição |
|----------|-----------|
| `POST /auth/login` | Login |
| `GET /auth/me` | Dados do usuário |
| `GET /vacations` | Lista pedidos |
| `POST /vacations` | Cria pedido |
| `POST /vacations/:id/approve` | Aprova |
| `POST /vacations/:id/reject` | Rejeita |
| `GET /users` | Lista usuários |
| `POST /users` | Cria usuário |
| `PUT /users/:id` | Atualiza usuário |
| `DELETE /users/:id` | Remove usuário |
| `GET /settings` | Configurações |
| `PUT /settings` | Atualiza configurações |

---

## 🎨 Componentes UI

Baseados em [shadcn/ui](https://ui.shadcn.com/):

- `Button`, `Input`, `Card`
- `Dialog`, `Select`, `Badge`
- `DataTable`, `Calendar`
- `Toast` (notificações)

---

## ⚙️ Scripts Disponíveis

| Comando | Descrição |
|---------|-----------|
| `npm run dev` | Desenvolvimento (hot reload) |
| `npm run build` | Build de produção |
| `npm start` | Executa build de produção |
| `npm run lint` | Verifica código |
| `npm test` | Executa testes |

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| CORS bloqueado | Verifique se backend está rodando |
| 401 Unauthorized | Faça login novamente |
| Porta 3000 ocupada | `lsof -i :3000` e mate o processo |
| API não encontrada | Verifique `NEXT_PUBLIC_API_BASE_URL` |
