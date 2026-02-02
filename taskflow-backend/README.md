# Taskflow Vacation — Backend

API REST em Spring Boot (Java 21) para gestão de férias, usuários e configurações. Utiliza JWT para autenticação, PostgreSQL como banco e documentação via Swagger/OpenAPI.

## Visão Geral
- **Context path**: `/taskflow` (port padrão `8080`).
- **Swagger UI**: `http://localhost:8080/taskflow/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/taskflow/v3/api-docs`
- **Autenticação**: JWT (`Authorization: Bearer <token>`). Rotas públicas: `/auth/**`, `/public/**`, Swagger e recursos correlatos.

## Pré-requisitos
- JDK 21 (Java 21)
- Maven 3.9+ (ou use o Maven Wrapper `mvnw` incluso)
- Docker + Docker Compose (para o PostgreSQL)

## Banco de Dados
A configuração padrão (em `src/main/resources/application.yml`):
```
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/taskflow
    username: taskflow
    password: taskflow
```
Use o `docker-compose.yml` fornecido para subir um PostgreSQL pronto:

```bash
docker compose up -d
```

## Executando a aplicação

Via Maven Wrapper (recomendado):

```bash
./mvnw spring-boot:run
```

Build + jar:

```bash
./mvnw -DskipTests package
java -jar target/Vacation-System-0.0.1-SNAPSHOT.jar
```

## Autenticação e Token
Para obter um token, use o endpoint de login:

```bash
curl -X POST 'http://localhost:8080/taskflow/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"email":"iago.admin@taskflow.pt","password":"123456"}'
```
A resposta conterá um `token` JWT. Utilize-o nas chamadas seguintes:

```bash
curl -H 'Authorization: Bearer <TOKEN>' \
  'http://localhost:8080/taskflow/users'
```

### Usuários de exemplo (seed)
Há um endpoint para popular dados (requer ADMIN autenticado):

```bash
curl -X POST 'http://localhost:8080/taskflow/maintenance/seed' \
  -H 'Authorization: Bearer <TOKEN>'
```
Ele cria:
- Admin: `iago.admin@taskflow.pt` / senha `123456`
- Gestor: `iago.gestor@taskflow.pt` / senha `123456`
- Colaborador: `iago.colab1@taskflow.pt` / senha `123456`

> Observação: Para o primeiro acesso, garanta que exista um **ADMIN** no banco (por exemplo, criando manualmente um usuário com senha Bcrypt). Depois disso, você poderá usar `/maintenance/seed` e os endpoints administrativos normalmente.

### Admin inicial automático
Se ao iniciar a aplicação não existir nenhum usuário com perfil `ADMIN`, um usuário administrativo é criado automaticamente com as credenciais definidas em `application.yml`.

Como gerar o admin inicial (passo a passo):

```bash
# 1) Banco de dados (opcional se já estiver rodando)
docker compose up -d

# 2) Subir a aplicação (usando Maven Wrapper)
./mvnw spring-boot:run

# 3) Fazer login com o admin inicial para obter o token
curl -X POST 'http://localhost:8080/taskflow/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{"email":"iago.admin@taskflow.pt","password":"123456"}'
```

Usuário criado por padrão (se não houver ADMIN):

- Email: `iago.admin@taskflow.pt`
- Senha: `123456`
- Nome: `Administrador`
- Perfil: `ADMIN`

Essas credenciais vêm destas chaves no `application.yml`:

```
app:
  admin:
    email: iago.admin@taskflow.pt
    password: 123456
    name: Administrador
```

Você pode sobrescrever via variáveis de ambiente ou parâmetros JVM:

```bash
APP_ADMIN_EMAIL=meu.admin@empresa.com \
APP_ADMIN_PASSWORD=minhaSenhaForte \
APP_ADMIN_NAME="Admin Taskflow" \
./mvnw spring-boot:run
```

Ou com `-D`:

```bash
./mvnw spring-boot:run \
  -Dspring-boot.run.jvmArguments="-Dapp.admin.email=meu.admin@empresa.com -Dapp.admin.password=minhaSenhaForte -Dapp.admin.name=AdminTaskflow"
```

> Importante: altere a senha padrão em ambientes reais o quanto antes.

## Principais Endpoints
- `POST /auth/login`: autenticação (email/senha). Retorna JWT.
- `GET /auth/me`: dados do usuário autenticado.
- `GET /public/ping`: verificação de saúde pública.
- `GET/POST/PUT/DELETE /users`: gestão de usuários (regras de permissão aplicadas).
- `GET/POST /vacations`, `POST /vacations/:id/approve`, `POST /vacations/:id/reject`: ciclo de pedidos de férias.
- `GET/PUT /settings`: configurações globais.
- `POST /maintenance/clean` e `POST /maintenance/seed`: manutenção (apenas ADMIN).

## CORS
Origens permitidas (no `SecurityConfig`):
- `http://localhost:3000`, `http://127.0.0.1:3000`
- `http://localhost:3001`, `http://127.0.0.1:3001`

## Configurações JWT
No `application.yml`:
```
jwt:
  secret: taskflow-super-secret-jwt-key-32-bytes-2026-XX-YY
  expiration: 86400000
```
Ajuste o `secret` e `expiration` conforme seu ambiente.

## Troubleshooting
- Porta `8080` ocupada: encerre o processo que usa a porta ou ajuste `server.port`.
- Sem ADMIN inicial: crie manualmente um usuário ADMIN com senha Bcrypt para obter o primeiro token.
- Banco sem acesso: confira `docker compose ps` e credenciais (`taskflow`/`taskflow`).
