# Taskflow Vacation — Monorepo

Sistema de gestão de férias com autenticação JWT, controle por perfis (ADMIN, MANAGER, COLLABORATOR), aprovação/rejeição de pedidos e administração de usuários.

## 🏗️ Arquitetura

```
Taskflow/
├── taskflow-backend/    # API REST (Spring Boot + Java 21)
├── taskflow-frontend/   # Interface Web (Next.js + React)
└── README.md
```

## 🚀 Início Rápido com Docker (Recomendado)

A forma mais simples de rodar todo o sistema é usando Docker Compose:

```bash
cd taskflow-backend
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
2. Faça login com as credenciais padrão:
   - **Admin**: `iago.admin@taskflow.pt` / `123456`

> O admin é criado automaticamente na primeira execução.

### Comandos Docker Úteis

```bash
# Subir todos os serviços
docker-compose up --build

# Subir em background
docker-compose up -d --build

# Ver logs
docker-compose logs -f

# Parar todos os serviços
docker-compose down

# Parar e remover volumes (limpa banco)
docker-compose down -v
```

---

## 🛠️ Desenvolvimento Local (Sem Docker)

### Pré-requisitos
- JDK 21
- Node.js 20+
- PostgreSQL 16 (ou use Docker só para o banco)

### 1. Banco de Dados

```bash
cd taskflow-backend
docker-compose up -d postgres
```

### 2. Backend

```bash
cd taskflow-backend
./mvnw spring-boot:run
```
- API: http://localhost:8080/taskflow
- Swagger: http://localhost:8080/taskflow/swagger-ui/index.html

### 3. Frontend

```bash
cd taskflow-frontend
npm install
npm run dev
```
- App: http://localhost:3000

---

## 👥 Usuários de Exemplo

| Perfil | Email | Senha |
|--------|-------|-------|
| ADMIN | iago.admin@taskflow.pt | 123456 |
| MANAGER | iago.gestor@taskflow.pt | 123456 |
| COLLABORATOR | iago.colab1@taskflow.pt | 123456 |

> Para criar usuários de exemplo, use o endpoint `/maintenance/seed` (requer ADMIN).

---

## 📚 Documentação Detalhada

- [Backend README](taskflow-backend/README.md) — API, endpoints, autenticação JWT
- [Frontend README](taskflow-frontend/README.md) — Componentes, hooks, páginas

---

## 🔐 Autenticação

O sistema usa JWT (JSON Web Token):

1. Faça login via `POST /taskflow/auth/login`
2. Receba o token JWT
3. Use `Authorization: Bearer <token>` nas requisições

O frontend gerencia isso automaticamente após o login.

---

## 📋 Funcionalidades

- ✅ Autenticação JWT com 3 perfis
- ✅ CRUD de usuários (ADMIN)
- ✅ Solicitação de férias
- ✅ Aprovação/Rejeição por gestor
- ✅ Configurações globais
- ✅ Dashboard por perfil
- ✅ Logs nas operações
