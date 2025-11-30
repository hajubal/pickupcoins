# Multi-stage build for admin-api
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Copy all modules
COPY pickup-common ./pickup-common
COPY admin-api ./admin-api

# Build the application (pickup-common will be built automatically as dependency)
RUN gradle :admin-api:bootJar --no-daemon -x test

# Runtime stage
FROM bellsoft/liberica-openjdk-alpine-musl:17

LABEL description="admin-api with pickup-common"
LABEL maintainer="hajubal@gmail.com"

WORKDIR /app

ARG VERSION=2.0.0

# Copy the built jar from builder stage
COPY --from=builder /app/admin-api/build/libs/admin-api-${VERSION}.jar /app/admin-api.jar

# Create logs directory
RUN mkdir -p /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/admin-api.jar", "--spring.profiles.active=dev"]
