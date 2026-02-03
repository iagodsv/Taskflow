# ==================== BUILD STAGE ====================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copia arquivos de configuração do Maven primeiro (cache de dependências)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Download de dependências (camada cacheável)
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Build do projeto (skip tests para acelerar)
RUN mvn clean package -DskipTests -B

# ==================== RUNTIME STAGE ====================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root para segurança
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Copia o JAR do estágio de build
COPY --from=build /app/target/*.jar app.jar

# Altera proprietário do arquivo
RUN chown appuser:appgroup app.jar

# Usa usuário não-root
USER appuser

# Expõe a porta do Spring Boot
EXPOSE 8080

# Configurações de JVM para containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Comando de execução
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
