# TaskFlow 🚀

> A production-grade, real-time collaborative task management system  
> built with Spring Boot — inspired by Jira. Built in public over 20 days.

![Status](https://img.shields.io/badge/Status-In%20Development-yellow)
![Java](https://img.shields.io/badge/Java-25-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## 🧠 About

TaskFlow is a backend-heavy REST API for managing teams, projects,
sprints and tasks collaboratively in real time.

Built to demonstrate production-grade Spring Boot architecture —
JWT security with refresh tokens, WebSockets, async messaging via
RabbitMQ, role-based access control scoped per workspace, AOP-based
permission checks, rate limiting, scheduled jobs and more.

> 🚧 Actively building in public — Day 1 of 20 complete.

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.3 |
| Language | Java 25 |
| Security | Spring Security + JWT + Refresh Tokens |
| Database | H2 (dev) → PostgreSQL (prod) |
| Caching | Redis *(coming soon)* |
| Messaging | RabbitMQ *(coming soon)* |
| Real-time | WebSockets + STOMP *(coming soon)* |
| Storage | AWS S3 *(coming soon)* |
| Scheduling | Spring Scheduler *(coming soon)* |
| Build Tool | Maven |
| Deployment | Docker + Railway *(coming soon)* |

---

## 🏗️ Architecture
```
src/main/java/com/project/taskmanager/
├── config/          → Security config, constants
├── controller/      → REST controllers (/api/v1/)
├── service/         → Business logic + interfaces
├── repository/      → JPA repositories
├── model/           → JPA entities
├── payload/
│   ├── request/     → Incoming request DTOs
│   └── response/    → Outgoing response DTOs
├── security/        → JWT, refresh tokens, UserDetails
├── exceptions/      → Custom exceptions + global handler
├── enums/           → Domain enums
├── aop/             → @RequiresRole + audit aspect
├── scheduler/       → Scheduled jobs
└── util/            → Utility classes
```

---

## 🔐 Role System

| Role | Permissions |
|---|---|
| ADMIN | Full control over workspace |
| MANAGER | Manages projects, sprints, labels |
| DEVELOPER | Works on assigned tasks, logs time |

> Roles are **scoped per workspace** — a user can be Admin  
> in Workspace A and Developer in Workspace B simultaneously.

---

## 📦 Features

### ✅ Foundation
- [x] Project structure and package setup
- [x] Base entity with UUID primary keys + auto timestamps
- [x] Global exception handling via `@RestControllerAdvice`
- [x] Consistent `ApiResponse<T>` wrapper on every endpoint
- [x] All domain enums defined

### 🔐 Auth & Security
- [x] JWT Access Token (short lived — 24hr)
- [ ] Refresh Token System (7 days, DB stored, revocable)
- [x] Role-based access control (scoped per workspace)
- [ ] AOP permission checks via `@RequiresRole` annotation
- [ ] Rate limiting per user via Redis
- [x] API versioning (`/api/v1/`)

### 🏢 Workspace & Projects
- [ ] Workspace CRUD + member management
- [ ] Project CRUD + member management
- [ ] Pagination + filtering + sorting on all list endpoints

### 🏃 Sprints
- [ ] Sprint lifecycle (PLANNED → ACTIVE → COMPLETED)
- [ ] Sprint backlog (incomplete tasks move here on completion)
- [ ] Scheduled job — auto flag overdue active sprints

### ✅ Tasks
- [ ] Task CRUD with strict status FSM
- [ ] Task assignment + reassignment
- [ ] Task dependencies (blocking / blocked by)
- [ ] Task watching (get notified on any change)
- [ ] Time tracking (log hours, sprint reports)
- [ ] Comments with @mentions
- [ ] File attachments via AWS S3
- [ ] Task activity feed (full change history)

### 🔔 Real-time & Notifications
- [ ] WebSocket live task updates (STOMP)
- [ ] RabbitMQ async notification system
- [ ] Email notifications (JavaMailSender)
- [ ] @Mention notifications in comments
- [ ] Daily digest email (Spring Scheduler)
- [ ] Scheduled job — auto mark overdue tasks

### 🔍 Search & Analytics
- [ ] Global search API (tasks, projects, workspaces)
- [ ] Dashboard stats per project
- [ ] Sprint velocity and burndown data
- [ ] Time tracking reports per sprint

### ⚙️ Production Ready
- [ ] Redis caching on hot endpoints
- [ ] Audit logging via Spring AOP
- [ ] Docker + PostgreSQL setup
- [ ] Deployed on Railway

---

## 🌐 API Overview

All endpoints live under `/api/v1/`
```
Auth          POST   /api/v1/auth/register
              POST   /api/v1/auth/login
              POST   /api/v1/auth/refresh
              POST   /api/v1/auth/logout
              GET    /api/v1/auth/me

Workspaces    POST   /api/v1/workspaces
              GET    /api/v1/workspaces
              PUT    /api/v1/workspaces/{id}
              DELETE /api/v1/workspaces/{id}
              POST   /api/v1/workspaces/{id}/members

Projects      POST   /api/v1/workspaces/{id}/projects
              GET    /api/v1/workspaces/{id}/projects
              PUT    /api/v1/projects/{id}
              DELETE /api/v1/projects/{id}

Sprints       POST   /api/v1/projects/{id}/sprints
              PATCH  /api/v1/sprints/{id}/start
              PATCH  /api/v1/sprints/{id}/complete
              GET    /api/v1/projects/{id}/backlog

Tasks         POST   /api/v1/sprints/{id}/tasks
              PATCH  /api/v1/tasks/{id}/status
              PATCH  /api/v1/tasks/{id}/assign
              POST   /api/v1/tasks/{id}/watch
              POST   /api/v1/tasks/{id}/dependencies
              POST   /api/v1/tasks/{id}/time-logs
              GET    /api/v1/tasks/{id}/activity

Comments      POST   /api/v1/tasks/{id}/comments
              DELETE /api/v1/tasks/{id}/comments/{commentId}

Search        GET    /api/v1/search?q=keyword&type=task,project

Notifications GET    /api/v1/notifications
              PATCH  /api/v1/notifications/read-all

Dashboard     GET    /api/v1/projects/{id}/dashboard
              GET    /api/v1/sprints/{id}/burndown
```

---

## 🚀 Running Locally

### Prerequisites
- Java 25
- Maven

### Steps
```bash
# Clone the repo
git clone https://github.com/yourusername/taskflow.git

# Navigate into project
cd taskflow

# Run the app
./mvnw spring-boot:run
```

App runs on `http://localhost:8080`

H2 Console → `http://localhost:8080/h2-console`
```
JDBC URL  : jdbc:h2:mem:taskmanagerdb
Username  : sa
Password  : (leave blank)
```

---

## 🗓️ Build Log

| Day | What was built |
|---|---|
| Day 1 | Project setup, enums, BaseEntity, ApiResponse, GlobalExceptionHandler, AppConstants |
| Day 2 | Full JWT security layer — `JwtUtils` (generate/validate tokens), `AuthTokenFilter` (intercepts every request), `AuthEntryPointJwt` (clean 401 responses), `UserDetailsImpl`, `UserDetailsServiceImpl`, `WebSecurityConfig` (stateless, route rules, BCrypt) |
| Day 3 | `User` entity, `UserRepository`, request DTOs (`RegisterRequest`, `LoginRequest`), `AuthResponse` DTO, `AuthService` interface + `AuthServiceImpl`, `AuthController` — register and login APIs fully working and tested in Postman |

---

## 👨‍💻 Author

Built by Sonu Verma(https://github.com/Spectraa28)

> If you find this helpful, drop a ⭐ on the repo!