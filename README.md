# Todo REST API

> A production-ready REST API for task management, built with Spring Boot and containerized with Docker.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=flat-square&logo=springboot)
![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=flat-square&logo=docker)
![H2](https://img.shields.io/badge/H2-in--memory-blue?style=flat-square)

---

## Overview

This project is a fully functional **Todo REST API** demonstrating a clean three-layer backend architecture using Spring Boot. It is designed as a learning reference for backend developers looking to understand how **Maven**, **Git**, and **Docker** work together in a real project.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Database](#database)
- [Maven vs Docker](#maven-vs-docker)
- [Project Structure](#project-structure)
- [API Reference](#api-reference)
- [Getting Started](#getting-started)
- [Running with Docker](#running-with-docker)
- [Pushing to GitHub](#pushing-to-github)

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.3 |
| Build Tool | Apache Maven 3.9 |
| Persistence | Spring Data JPA + Hibernate |
| Database | H2 (in-memory) |
| Containerization | Docker |
| Version Control | Git + GitHub |

---

## Architecture

This API follows a strict **three-layer architecture**, separating concerns across Controller, Service, and Repository layers.

```
Client (Postman / Browser)
         │
         ▼
┌─────────────────────┐
│     Controller      │  ← Handles HTTP requests and responses
│  TodoController.java│    Maps URLs to Java methods
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│      Service        │  ← Contains all business logic
│  TodoService.java   │    Validation, transformation, rules
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│     Repository      │  ← Communicates with the database
│ TodoRepository.java │    Powered by Spring Data JPA
└────────┬────────────┘
         │
         ▼
┌─────────────────────┐
│      Database       │
│    H2 (in-memory)   │
└─────────────────────┘
```

**Why this separation matters:**
Each layer has a single responsibility. You can swap the database, change the business logic, or redesign the API endpoints — all without touching the other layers.

---

## Database

This project uses **H2** — a lightweight, in-memory relational database that runs entirely inside the JVM.

## Maven vs Docker

One of the most common points of confusion for new backend developers is understanding where Maven ends and Docker begins.

### Maven — The Build Tool

Maven is responsible for everything that happens **before** the application runs:

- Reading `pom.xml` and resolving all declared dependencies
- Downloading libraries from Maven Central into your local `.m2` cache
- Compiling `.java` source files into `.class` bytecode
- Packaging everything into a single executable `.jar` file
- Running automated tests

Maven produces one output: a `.jar` file. It knows nothing about deployment or containers.

### Docker — The Containerization Tool

Docker is responsible for everything that happens **after** Maven has done its job:

- Wrapping the `.jar` file in a portable container image
- Defining the exact runtime environment (OS, Java version, file system)
- Ensuring the application runs identically on any machine
- Exposing the application port to the outside world

### The Full Pipeline

```
Source Code  ──▶  Maven  ──▶  .jar file  ──▶  Docker  ──▶  Container (live API)
                 (builds)                    (packages)      (runs anywhere)
```

### Multi-Stage Dockerfile

The `Dockerfile` in this project runs both steps automatically:

```dockerfile
# Stage 1: Maven builds the JAR
FROM maven:3.9-eclipse-temurin-17 AS builder
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Docker packages only the JAR into a lean image
FROM eclipse-temurin:17-jre-alpine
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

The final image contains only the JRE and the JAR — no Maven, no source code, no build tools. This keeps the image small and secure.

---

## Project Structure

```
todo-api/
├── src/
│   └── main/
│       ├── java/com/example/todo/
│       │   ├── TodoApplication.java        ← Entry point (@SpringBootApplication)
│       │   ├── controller/
│       │   │   └── TodoController.java     ← REST endpoints
│       │   ├── service/
│       │   │   └── TodoService.java        ← Business logic
│       │   ├── repository/
│       │   │   └── TodoRepository.java     ← JPA repository interface
│       │   └── model/
│       │       └── Todo.java               ← Entity / database table definition
│       └── resources/
│           └── application.properties      ← Runtime configuration
├── Dockerfile                              ← Multi-stage container build
├── docker-compose.yml                      ← Single-command runner
├── pom.xml                                 ← Maven dependencies and build config
└── .gitignore                              ← Excludes target/, .idea/, *.jar
```

---

## API Reference

### Base URL
```
http://localhost:8080/api/todos
```

### Endpoints

| Method | Path | Description | Response |
|--------|------|-------------|----------|
| `GET` | `/api/todos` | Retrieve all todos | `200 OK` |
| `GET` | `/api/todos/{id}` | Retrieve a single todo | `200 OK` / `404 Not Found` |
| `POST` | `/api/todos` | Create a new todo | `201 Created` |
| `PUT` | `/api/todos/{id}` | Update an existing todo | `200 OK` / `404 Not Found` |
| `DELETE` | `/api/todos/{id}` | Delete a todo | `204 No Content` / `404 Not Found` |

### Todo Object Schema

```json
{
  "id": 1,
  "title": "string (required)",
  "description": "string (optional)",
  "completed": false
}
```

### Request Examples

**Create a todo**
```http
POST /api/todos
Content-Type: application/json

{
  "title": "Learn Docker",
  "description": "Understand multi-stage builds",
  "completed": false
}
```

**Update a todo**
```http
PUT /api/todos/1
Content-Type: application/json

{
  "title": "Learn Docker",
  "description": "Understand multi-stage builds",
  "completed": true
}
```

**Delete a todo**
```http
DELETE /api/todos/1
```

---

## Getting Started

### Prerequisites

Verify all required tools are installed:

```bash
java -version    # 17 or higher
mvn -version     # 3.8 or higher
git --version    # any recent version
docker --version # any recent version
```

### Clone and Run

```bash
git clone https://github.com/YOUR_USERNAME/todo-api.git
cd todo-api
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api/todos`.

---

## Running with Docker

### Build and run manually

```bash
# Build the Docker image
docker build -t todo-api .

# Run the container
docker run -p 8080:8080 todo-api
```

### Or use Docker Compose

```bash
# Build and start
docker-compose up --build

# Run in the background
docker-compose up -d --build

# Stop
docker-compose down
```

---
