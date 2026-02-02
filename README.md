# Taskflow Vacation — Monorepo

Este repositório contém dois projetos:
- **Frontend** (Next.js) em `taskflow-frontend`
- **Backend** (Spring Boot) em `taskflow-backend`

## Visão Geral do Sistema
O Taskflow Vacation é um sistema de gestão de férias com autenticação JWT, controle por perfis (ADMIN, MANAGER, COLLABORATOR), aprovação/rejeição de pedidos e administração de usuários e configurações.

## Links e Documentação
- Frontend: veja o README em [`taskflow-frontend/README.md`](taskflow-frontend/README.md)
- Backend: veja o README em [`taskflow-backend/README.md`](taskflow-backend/README.md)

## Início Rápido
1. **Backend**
   - Suba o Postgres:
     ```bash
     docker compose -f taskflow-backend/docker-compose.yml up -d
     ```
   - Rode a API:
     ```bash
     cd taskflow-backend
     ./mvnw spring-boot:run
     ```
   - Swagger: `http://localhost:8080/taskflow/swagger-ui/index.html`

2. **Frontend**
   - Configure a base da API (opcional):
     ```bash
     echo "NEXT_PUBLIC_API_BASE_URL=http://localhost:8080/taskflow" > taskflow-frontend/.env.local
     ```
   - Rode o app:
     ```bash
     cd taskflow-frontend
     npm install
     npm run dev
     ```
   - Acesse: `http://localhost:3000`

## Fluxo de Login
- Obtenha um token via `POST /taskflow/auth/login` (veja o README do backend para usuários de exemplo).
- O frontend salva o token e usa automaticamente `Authorization: Bearer` nas chamadas.

## Estrutura
- `taskflow-frontend/`: app Next.js, componentes UI, hooks e serviços.
- `taskflow-backend/`: API Spring Boot, segurança JWT, controladores e documentação.
