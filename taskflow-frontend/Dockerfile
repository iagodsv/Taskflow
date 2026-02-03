# ==================== BUILD STAGE ====================
FROM node:20-alpine AS build

WORKDIR /app

# Copia arquivos de dependências primeiro (cache)
COPY package.json package-lock.json* ./

# Instala dependências
RUN npm ci

# Copia o código fonte
COPY . .

# Build da aplicação Next.js
RUN npm run build

# ==================== RUNTIME STAGE ====================
FROM node:20-alpine AS runner

WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -g 1001 -S nodejs && \
    adduser -u 1001 -S nextjs -G nodejs

# Copia arquivos necessários do estágio de build
COPY --from=build /app/public ./public
COPY --from=build /app/.next/standalone ./
COPY --from=build /app/.next/static ./.next/static

# Altera proprietário dos arquivos
RUN chown -R nextjs:nodejs /app

# Usa usuário não-root
USER nextjs

# Expõe a porta do Next.js
EXPOSE 3000

# Variáveis de ambiente
ENV NODE_ENV=production
ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

# Comando de execução
CMD ["node", "server.js"]
