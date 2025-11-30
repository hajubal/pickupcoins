# Multi-stage build for pickup-server
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

# Copy gradle files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Copy all modules
COPY pickup-common ./pickup-common
COPY pickup-server ./pickup-server

# Build the application (pickup-common will be built automatically as dependency)
RUN gradle :pickup-server:bootJar --no-daemon -x test

# Runtime stage
FROM bellsoft/liberica-openjdk-alpine-musl:17

LABEL description="pickup-server with pickup-common"
LABEL maintainer="hajubal@gmail.com"

WORKDIR /app

ARG VERSION=2.0.0

# Copy the built jar from builder stage
COPY --from=builder /app/pickup-server/build/libs/pickup-server-${VERSION}.jar /app/pickup-server.jar

# Create logs directory
RUN mkdir -p /logs

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/pickup-server.jar", "--spring.profiles.active=dev"]
