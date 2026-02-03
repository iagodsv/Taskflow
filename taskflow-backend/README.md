# Taskflow Vacation — Backend

API REST em Spring Boot (Java 21) para gestão de férias, usuários e configurações. Utiliza JWT para autenticação, PostgreSQL como banco e documentação via Swagger/OpenAPI.

## 🚀 Início Rápido com Docker

A forma mais simples de rodar todo o sistema (backend + frontend + banco):

```bash
docker-compose up --build
```

Isso irá subir:

| Serviço | Porta | URL |
|---------|-------|-----|
| PostgreSQL | 5432 | `localhost:5432` |
| Backend (Java) | 8080 | http://localhost:8080/taskflow |
| Frontend (Next.js) | 3000 | http://localhost:3000 |

### Comandos Docker Úteis

```bash
# Subir todos os serviços em background
docker-compose up -d --build

# Ver logs do backend
docker-compose logs -f backend

# Parar todos os serviços
docker-compose down

# Limpar tudo (incluindo banco)
docker-compose down -v
```

---

## 🛠️ Desenvolvimento Local

### Pré-requisitos
- JDK 21 (Java 21)
- Maven 3.9+ (ou use o Maven Wrapper `mvnw`)
- Docker + Docker Compose (para o PostgreSQL)

### Banco de Dados

```bash
docker-compose up -d postgres
```

Configuração em `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskflow
    username: taskflow
    password: taskflow
```

### Executando a Aplicação

```bash
# Via Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Ou build + jar
./mvnw -DskipTests package
java -jar target/Vacation-System-0.0.1-SNAPSHOT.jar
```

### Testes

```bash
./mvnw test
```

---

## 📚 Documentação da API

- **Swagger UI**: http://localhost:8080/taskflow/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/taskflow/v3/api-docs

---

## 🔐 Autenticação

### Login

```bash
curl -X POST 'http://localhost:8080/taskflow/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"email":"iago.admin@taskflow.pt","password":"123456"}'
```

### Usando o Token

```bash
curl -H 'Authorization: Bearer <TOKEN>' \
  'http://localhost:8080/taskflow/users'
```

### Admin Inicial

Se não existir ADMIN no banco, um é criado automaticamente:

| Campo | Valor |
|-------|-------|
| Email | iago.admin@taskflow.pt |
| Senha | 123456 |
| Nome | Administrador |
| Perfil | ADMIN |

Configurável via `application.yml`:
```yaml
app:
  admin:
    email: iago.admin@taskflow.pt
    password: 123456
    name: Administrador
```

---

## 👥 Usuários de Exemplo

Use o endpoint de seed (requer ADMIN autenticado):

```bash
curl -X POST 'http://localhost:8080/taskflow/maintenance/seed' \
  -H 'Authorization: Bearer <TOKEN>'
```

Usuários criados:

| Perfil | Email | Senha |
|--------|-------|-------|
| ADMIN | iago.admin@taskflow.pt | 123456 |
| MANAGER | iago.gestor@taskflow.pt | 123456 |
| COLLABORATOR | iago.colab1@taskflow.pt | 123456 |

---

## 📋 Principais Endpoints

### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/auth/login` | Login (retorna JWT) |
| GET | `/auth/me` | Dados do usuário autenticado |

### Usuários
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/users` | Lista usuários |
| GET | `/users/{id}` | Busca usuário |
| POST | `/users` | Cria usuário (ADMIN) |
| PUT | `/users/{id}` | Atualiza usuário (ADMIN) |
| DELETE | `/users/{id}` | Remove usuário (ADMIN) |

### Férias
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/vacations` | Lista pedidos |
| POST | `/vacations` | Cria pedido |
| POST | `/vacations/{id}/approve` | Aprova pedido |
| POST | `/vacations/{id}/reject` | Rejeita pedido |

### Configurações
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/settings` | Busca configurações |
| PUT | `/settings` | Atualiza configurações (ADMIN) |

### Manutenção
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/maintenance/seed` | Popula dados de exemplo |
| POST | `/maintenance/clean` | Limpa todos os dados |

---

## ⚙️ Configurações

### JWT
```yaml
jwt:
  secret: taskflow-super-secret-jwt-key-32-bytes-2026-XX-YY
  expiration: 86400000  # 24 horas
```

### CORS
Origens permitidas:
- `http://localhost:3000`
- `http://localhost:3001`
- `http://127.0.0.1:3000`
- `http://127.0.0.1:3001`

---

## 🏗️ Arquitetura

```
src/main/java/pt/com/LBC/Vacation_System/
├── controller/     # REST Controllers (recebem/retornam DTOs)
├── service/        # Regras de negócio
├── repository/     # Acesso a dados (JPA)
├── model/          # Entidades JPA
├── dto/            # Data Transfer Objects
├── mapper/         # Conversão Entity ↔ DTO
├── security/       # JWT, filtros, configuração
└── exception/      # Exceções e handler global
```

### Boas Práticas Implementadas

| Camada | Responsabilidade |
|--------|------------------|
| Controller | Recebe/retorna DTOs, chama Service, faz logs |
| Service | Regras de negócio, usa Mapper, lança exceções |
| Mapper | Conversão Entity ↔ DTO |
| Repository | Acesso a dados |

---

## 🐛 Troubleshooting

| Problema | Solução |
|----------|---------|
| Porta 8080 ocupada | `lsof -i :8080` e mate o processo |
| Banco sem acesso | Verifique `docker-compose ps` |
| Token inválido | Faça login novamente |
| CORS bloqueado | Verifique origem permitida |
| Login não funciona | Limpe o banco: `docker-compose down -v` e suba novamente |
