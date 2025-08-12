# Gemini Code Assistant Project Context

This file provides context for the Gemini Code Assistant to understand the `pickupcoins` project.

## Project Overview

`pickupcoins` is a Java-based web application built with the Spring Boot framework. It appears to be a system for "picking up coins," likely related to managing points or virtual currency from different websites. The project is structured as a multi-module Gradle project, separating the core API from the administrative web interface.

The project consists of two main modules:
- `pickup-server`: The core backend server, providing APIs for the coin pickup functionality.
- `admin`: A web application for administrators, which includes a dashboard, user management, and other administrative features. It uses Thymeleaf for server-side rendering of HTML.

## Technology Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.2.0
- **Build Tool:** Gradle
- **Primary Dependencies:**
    - Spring Boot Starter (Web, Data JPA, Security, Actuator)
    - QueryDSL for type-safe database queries
    - Lombok for reducing boilerplate code
    - Thymeleaf for the admin UI template engine
    - Jsoup for HTML parsing
    - MySQL Connector, H2 Database
    - p6spy for SQL logging
    - Spring Cloud Dependencies
    - Micrometer for metrics
- **Containerization:** Docker (via `Dockerfile` and `docker-compose.yml`) and Jib for building container images.

## Project Structure

- **`settings.gradle`**: Defines the multi-module structure (`pickup-server`, `admin`).
- **`build.gradle`**: The root Gradle build file, configuring all subprojects.
- **`pickup-server/`**: Contains the core backend logic, entities, repositories, and services.
- **`admin/`**: Contains the admin-facing web application, including controllers, Thymeleaf templates (`src/main/resources/templates`), and static assets (`src/main/resources/static`).
- **`.github/workflows/`**: Contains GitHub Actions workflows for CI/CD, notably `gradle.yml` which defines the build process.
- **`Dockerfile` & `docker-compose.yml`**: Used for building and running the application in a containerized environment.

## Key Commands

Based on the project configuration, here are the essential commands:

- **Build the entire project:**
  ```bash
  ./gradlew build
  ```
- **Run tests for all modules:**
  ```bash
  ./gradlew test
  ```
- **Clean the build directory:**
  ```bash
  ./gradlew clean
  ```
- **Run a specific module (e.g., the admin app) locally:**
  ```bash
  ./gradlew :admin:bootRun
  ```
- **Run the application using Docker Compose:**
  ```bash
  docker-compose up --build
  ```

## Coding Conventions

- The project uses standard Java coding conventions.
- **Lombok:** The use of `@Data`, `@Getter`, `@Setter`, `@Builder`, etc., is expected for model and entity classes to reduce boilerplate.
- **QueryDSL:** Database queries should be written using QueryDSL's generated classes for type safety. Q-classes are generated into the `$buildDir/generated/querydsl` directory during the build process.
- **Dependency Management:** Dependencies are managed centrally in the root `build.gradle` and `io.spring.dependency-management` plugin. Subproject dependencies are defined in their respective `build.gradle` files.
