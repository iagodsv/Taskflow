# 🚀 Guia de Deploy Gratuito - TaskFlow

Este guia detalha como hospedar o TaskFlow **100% gratuitamente** usando:

| Serviço | Componente | Limite Gratuito |
|---------|------------|-----------------|
| **Vercel** | Frontend (Next.js) | Ilimitado para projetos pessoais |
| **Render** | Backend (Spring Boot) | 750h/mês (suficiente 24/7) |
| **Neon** | PostgreSQL | 500MB + 0.5 GB-hours/mês |

---

## 📋 Pré-requisitos

1. Conta no [GitHub](https://github.com) (para hospedar o código)
2. Conta na [Vercel](https://vercel.com) (login com GitHub)
3. Conta no [Render](https://render.com) (login com GitHub)
4. Conta no [Neon](https://neon.tech) (banco PostgreSQL gratuito)

---

## 🗄️ Parte 1: Configurar o Banco de Dados (Neon)

### Passo 1.1: Criar conta e projeto no Neon

1. Acesse [neon.tech](https://neon.tech) e clique em **"Sign Up"**
2. Faça login com sua conta GitHub
3. Clique em **"Create a project"**
4. Configure:
   - **Project name:** `taskflow`
   - **Database name:** `taskflow`
   - **Region:** Escolha o mais próximo (ex: `AWS São Paulo`)
5. Clique em **"Create project"**

### Passo 1.2: Obter credenciais de conexão

1. No dashboard do Neon, vá para **"Connection Details"**
2. Selecione **"Connection string"** → **"Java/JDBC"**
3. Anote as informações (você vai precisar):
   ```
   jdbc:postgresql://ep-xxx.sa-east-1.aws.neon.tech/taskflow?sslmode=require
   User: seu-usuario
   Password: sua-senha
   ```

> ⚠️ **Importante:** O Neon usa SSL. A URL já inclui `?sslmode=require`.

---

## ☕ Parte 2: Deploy do Backend (Render)

### Passo 2.1: Preparar o repositório

1. Certifique-se de que seu código está no GitHub
2. O projeto já está configurado com variáveis de ambiente flexíveis

### Passo 2.2: Criar o serviço no Render

1. Acesse [render.com](https://render.com) e faça login
2. Clique em **"New +"** → **"Web Service"**
3. Conecte seu repositório GitHub
4. Configure:
   - **Name:** `taskflow-backend`
   - **Region:** `Oregon (US West)` ou o mais próximo
   - **Branch:** `main`
   - **Root Directory:** `taskflow-backend`
   - **Runtime:** `Docker`
   - **Instance Type:** `Free`

### Passo 2.3: Configurar variáveis de ambiente

Na seção **"Environment Variables"**, adicione:

| Variável | Valor |
|----------|-------|
| `DATABASE_URL` | `jdbc:postgresql://ep-xxx.neon.tech/taskflow?sslmode=require` |
| `DATABASE_USERNAME` | `seu-usuario-neon` |
| `DATABASE_PASSWORD` | `sua-senha-neon` |
| `JWT_SECRET` | `gere-uma-string-aleatoria-de-32-caracteres` |
| `CORS_ALLOWED_ORIGINS` | `https://seu-app.vercel.app` (preencha depois) |
| `ADMIN_EMAIL` | `admin@seudominio.com` |
| `ADMIN_PASSWORD` | `senha-segura-inicial` |
| `PORT` | `8080` |

> 💡 **Dica:** Para gerar um JWT_SECRET seguro, use: `openssl rand -base64 32`

### Passo 2.4: Deploy

1. Clique em **"Create Web Service"**
2. Aguarde o build (pode levar 5-10 minutos)
3. Quando terminar, você terá uma URL como:
   ```
   https://taskflow-backend.onrender.com
   ```

### Passo 2.5: Verificar se está funcionando

Acesse no navegador:
```
https://taskflow-backend.onrender.com/taskflow/actuator/health
```

Deve retornar:
```json
{"status":"UP"}
```

---

## ⚡ Parte 3: Deploy do Frontend (Vercel)

### Passo 3.1: Importar projeto na Vercel

1. Acesse [vercel.com](https://vercel.com) e faça login com GitHub
2. Clique em **"Add New..."** → **"Project"**
3. Selecione seu repositório
4. Configure:
   - **Framework Preset:** `Next.js`
   - **Root Directory:** `taskflow-frontend`

### Passo 3.2: Configurar variáveis de ambiente

Na seção **"Environment Variables"**, adicione:

| Variável | Valor |
|----------|-------|
| `NEXT_PUBLIC_API_BASE_URL` | `https://taskflow-backend.onrender.com/taskflow` |

### Passo 3.3: Deploy

1. Clique em **"Deploy"**
2. Aguarde o build (geralmente 2-3 minutos)
3. Sua aplicação estará disponível em:
   ```
   https://seu-projeto.vercel.app
   ```

---

## 🔗 Parte 4: Conectar Frontend e Backend

### Passo 4.1: Atualizar CORS no Render

1. Volte ao dashboard do Render
2. Vá no serviço `taskflow-backend`
3. Aba **"Environment"**
4. Atualize a variável:
   ```
   CORS_ALLOWED_ORIGINS=https://seu-projeto.vercel.app
   ```
5. O serviço reiniciará automaticamente

### Passo 4.2: Testar a integração

1. Acesse seu frontend na Vercel
2. Faça login com as credenciais admin que você definiu
3. Se tudo estiver correto, você conseguirá acessar o sistema!

---

## 🛠️ Solução de Problemas

### Erro de CORS
- Verifique se `CORS_ALLOWED_ORIGINS` está correto (sem barra final)
- Certifique-se de que o backend reiniciou após a mudança

### Erro de conexão com banco
- Verifique se a URL do Neon inclui `?sslmode=require`
- Confira usuário e senha

### Backend demora para responder
- No plano gratuito do Render, o serviço "dorme" após 15min de inatividade
- A primeira requisição pode levar 30-60 segundos para "acordar"

### Logs do Backend
- No Render, vá em **"Logs"** para ver erros em tempo real

---

## 📊 Limitações do Plano Gratuito

| Serviço | Limitação | Impacto |
|---------|-----------|---------|
| **Render** | Serviço "dorme" após 15min | Primeira requisição lenta |
| **Render** | 750h/mês de uptime | Suficiente para uso leve |
| **Neon** | 500MB de storage | ~50.000 registros |
| **Vercel** | 100GB bandwidth/mês | Suficiente para pequenas equipes |

---

## 🔄 Comandos Úteis (Local)

```bash
# Rodar backend localmente
cd taskflow-backend
./mvnw spring-boot:run

# Rodar frontend localmente
cd taskflow-frontend
npm install
npm run dev

# Build do backend
./mvnw clean package -DskipTests
```

---

## 🎉 Pronto!

Seu TaskFlow está agora hospedado gratuitamente na nuvem!

- **Frontend:** https://seu-projeto.vercel.app
- **Backend:** https://taskflow-backend.onrender.com
- **Banco:** Neon PostgreSQL

Para fazer atualizações, basta fazer push para o GitHub - os serviços farão deploy automático!
