# ============================================================
# Multi-Stage Dockerfile for AI Interview Platform
# Stage 1: Build with Maven + JDK 21
# Stage 2: Run with lean JRE 21 image
# ============================================================

# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first (layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable
RUN chmod +x mvnw

# Download dependencies (cached layer — only re-runs when pom.xml changes)
RUN ./mvnw dependency:go-offline -q

# Copy source and build JAR
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# --- Run Stage ---
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy built JAR from builder
COPY --from=builder /app/target/ai-interview-platform-1.0.0.jar app.jar

RUN chown appuser:appgroup app.jar
USER appuser

EXPOSE 8080

# ── Hardcode H2 profile so the app never tries to connect to MySQL ──
# This is the critical fix: Render must use H2 in-memory DB.
# The SPRING_PROFILES_ACTIVE env var from render.yaml sets this,
# but we also bake it in here as an unambiguous default.
ENV SPRING_PROFILES_ACTIVE=h2
ENV SPRING_DATASOURCE_URL=jdbc:h2:mem:ai_interview_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.h2.Driver
ENV SPRING_DATASOURCE_USERNAME=sa
ENV SPRING_DATASOURCE_PASSWORD=

# JVM tuning for container environments
ENV JAVA_OPTS="-Xms128m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
