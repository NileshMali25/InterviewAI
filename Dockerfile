# ============================================================
# Multi-Stage Dockerfile for AI Interview Platform
# Stage 1: Build with Maven + JDK 21
# Stage 2: Run with lean JRE 21 image
# ============================================================

# --- Build Stage ---
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first (layer caching — only re-downloads
# dependencies when pom.xml changes, not on every source code change)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -q

# Copy source code and build the fat JAR
COPY src/ src/
RUN ./mvnw package -DskipTests -q

# --- Run Stage ---
FROM eclipse-temurin:21-jre-alpine AS runner

WORKDIR /app

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy only the built JAR from builder stage
COPY --from=builder /app/target/ai-interview-platform-1.0.0.jar app.jar

# Set ownership
RUN chown appuser:appgroup app.jar

USER appuser

# Expose the application port
EXPOSE 8080

# JVM tuning for container environments (low memory)
ENV JAVA_OPTS="-Xms128m -Xmx512m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
