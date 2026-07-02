# TaskFlow 🚀

> A production-grade, AI-powered project management backend built with Spring Boot — inspired by Jira.  
> Featuring real-time WebSockets, async messaging, Redis caching, Google Gemini AI, and full JWT security.

<p align="center">
  <img src="https://img.shields.io/badge/Status-Live%20on%20Render-brightgreen" />
  <img src="https://img.shields.io/badge/Java-24-orange" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-green" />
  <img src="https://img.shields.io/badge/AI-Google%20Gemini-blue" />
  <img src="https://img.shields.io/badge/Database-PostgreSQL-blue" />
  <img src="https://img.shields.io/badge/Cache-Redis%20%28Upstash%29-red" />
  <img src="https://img.shields.io/badge/MQ-RabbitMQ%20%28CloudAMQP%29-orange" />
  <img src="https://img.shields.io/badge/License-MIT-lightgrey" />
</p>

---

## 🌐 Live Demo

| Resource | URL |
|---|---|
| 🚀 Frontend | `https://task-flow-frontend-sandy.vercel.app/` |
| Swagger docs | `https://taskflow-backend-6f6y.onrender.com/swagger-ui/index.html` |
| Video Demo | `https://www.youtube.com/watch?v=JWQzmWfb2C0` | 


---

## 🧠 About

TaskFlow is a backend REST API for managing teams, projects, sprints, and tasks collaboratively — built to demonstrate production-grade Spring Boot architecture.

It covers the full stack of backend concerns: JWT security with refresh token rotation, WebSocket real-time updates, async messaging via RabbitMQ, role-based access control scoped per workspace, AOP-based permission checks, Redis caching with rate limiting, scheduled jobs, Gemini AI automation, and full Docker + cloud deployment.

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 4.0.3 |
| Language | Java 24 |
| Security | Spring Security + JWT + Refresh Tokens |
| Database | PostgreSQL (Render) |
| Caching | Redis (Upstash) |
| Messaging | RabbitMQ (CloudAMQP) |
| Real-time | WebSockets + STOMP |
| Scheduling | Spring Scheduler |
| AI | Google Gemini 2.5 Flash Lite |
| Email | JavaMailSender (Gmail SMTP) |
| Build Tool | Maven |
| Deployment | Docker + Render |

---

## 🏗️ Architecture

```
src/main/java/com/Project/TaskManager/
├── config/          → Security, Redis, RabbitMQ, Swagger configs
├── controller/      → REST controllers (/api/v1/)
├── service/         → Business logic + interfaces
├── repository/      → JPA repositories
├── model/           → JPA entities (BaseEntity with UUID + timestamps)
├── payload/
│   ├── request/     → Incoming request DTOs
│   └── response/    → Outgoing response DTOs
├── security/        → JWT, refresh tokens, UserDetails, rate limiting
├── exceptions/      → Custom exceptions + GlobalExceptionHandler
├── enums/           → Domain enums (Status, Priority, Role, etc.)
├── aop/             → @RequiresRole + audit logging aspect
├── scheduler/       → Scheduled jobs (overdue tasks, health checks)
├── ai/              → Gemini integration + AI services
└── websocket/       → STOMP WebSocket configuration
```

---

## 🔐 Role System

Roles are **scoped per workspace** — a user can be ADMIN in Workspace A and DEVELOPER in Workspace B simultaneously.

| Role | Permissions |
|---|---|
| `OWNER` | Full control — created automatically on workspace creation |
| `ADMIN` | Manage members, projects, sprints |
| `MANAGER` | Manage projects, sprints, assign tasks |
| `DEVELOPER` | Work on assigned tasks, log time, add comments |
| `VIEWER` | Read-only access |

---

## ✅ Features

### 🔐 Auth & Security
- JWT Access Token (24hr) + Refresh Token (7 days, DB-stored, revocable)
- Token rotation on every refresh
- Stateless Spring Security with BCrypt password hashing
- Rate limiting per IP via Redis (`/api/v1/auth/**`)
- AOP-based `@RequiresRole` permission checks

### 🏢 Workspace & Projects
- Full Workspace CRUD + member invite/remove
- Project CRUD scoped to workspace
- Role-based access at workspace level

### 🏃 Sprints
- Sprint lifecycle: `PLANNED → ACTIVE → COMPLETED`
- Start/complete sprint endpoints
- Incomplete tasks auto-move to backlog on sprint completion
- Scheduled job to flag overdue active sprints

### ✅ Tasks
- Task CRUD with taskKey generation (e.g. `TEST-1`)
- Status FSM: `TODO → IN_PROGRESS → IN_REVIEW → DONE`
- Task assignment, priority, story points, due dates
- Sprint assignment/reassignment
- Task watching — get notified on any change
- Time tracking — log hours per task, get totals
- Comments on tasks
- Activity log (full change history via AOP)

### 🤖 AI Features (Google Gemini)
- **Task Description Generator** — expand a brief title into a full description
- **Priority Suggester** — auto-suggest priority based on task title
- **Sprint Summary Generator** — generate a sprint retrospective summary

### 🔔 Notifications
- Notification system with read/unread tracking
- Mark single or all notifications as read
- Unread count endpoint

### 🔍 Search & Analytics
- Global search across tasks and projects (`?q=keyword`)
- Personal dashboard — assigned tasks, status breakdown, overdue
- Workspace dashboard — members, projects, active sprints, completion %

### ⚙️ Production Ready
- Redis caching on hot endpoints (10min TTL)
- RabbitMQ async notification delivery
- Audit logging via Spring AOP
- Scheduled jobs (overdue flagging, health checks)
- Dockerized with multi-stage build
- Deployed on Render with PostgreSQL + Upstash Redis + CloudAMQP

---

## 🌐 API Reference

All endpoints live under `/api/v1/`. Every response follows a consistent envelope:

```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {},
  "timestamp": "2026-03-12T04:15:30.634499"
}
```

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login, get access + refresh tokens |
| POST | `/auth/refresh` | Rotate refresh token |
| POST | `/auth/logout` | Revoke refresh token |

### Workspaces
| Method | Endpoint | Description |
|---|---|---|
| POST | `/workspaces` | Create workspace |
| GET | `/workspaces` | List user's workspaces |
| GET | `/workspaces/{slug}` | Get workspace by slug |
| PUT | `/workspaces/{workspaceId}` | Update workspace |
| DELETE | `/workspaces/{workspaceId}` | Delete workspace |
| POST | `/workspaces/{workspaceId}/members` | Add member |
| DELETE | `/workspaces/{workspaceId}/members/{memberId}` | Remove member |

### Projects
| Method | Endpoint | Description |
|---|---|---|
| POST | `/workspaces/{workspaceId}/projects` | Create project |
| GET | `/workspaces/{workspaceId}/projects` | List projects |
| GET | `/workspaces/{workspaceId}/projects/{projectId}` | Get project |
| PUT | `/workspaces/{workspaceId}/projects/{projectId}` | Update project |
| DELETE | `/workspaces/{workspaceId}/projects/{projectId}` | Delete project |

### Sprints
| Method | Endpoint | Description |
|---|---|---|
| POST | `/projects/{projectId}/sprints` | Create sprint |
| GET | `/projects/{projectId}/sprints` | List sprints |
| PUT | `/projects/{projectId}/sprints/{sprintId}` | Update sprint |
| POST | `/projects/{projectId}/sprints/{sprintId}/start` | Start sprint |
| POST | `/projects/{projectId}/sprints/{sprintId}/complete` | Complete sprint |

### Tasks
| Method | Endpoint | Description |
|---|---|---|
| POST | `/projects/{projectId}/tasks` | Create task |
| GET | `/projects/{projectId}/tasks` | List tasks |
| GET | `/projects/{projectId}/tasks/{taskId}` | Get task |
| PUT | `/projects/{projectId}/tasks/{taskId}` | Update task (incl. sprint assignment) |
| DELETE | `/projects/{projectId}/tasks/{taskId}` | Delete task |
| PATCH | `/projects/{projectId}/tasks/{taskId}/status` | Update task status |

### Task Extras
| Method | Endpoint | Description |
|---|---|---|
| POST | `/tasks/{taskId}/comments` | Add comment |
| GET | `/tasks/{taskId}/comments` | List comments |
| DELETE | `/tasks/{taskId}/comments/{commentId}` | Delete comment |
| POST | `/tasks/{taskId}/time` | Log time |
| GET | `/tasks/{taskId}/time` | Get time logs |
| GET | `/tasks/{taskId}/time/total` | Get total time logged |
| POST | `/tasks/{taskId}/watch` | Watch task |
| DELETE | `/tasks/{taskId}/watch` | Unwatch task |

### AI
| Method | Endpoint | Description |
|---|---|---|
| POST | `/ai/task/description` | Generate task description |
| POST | `/ai/task/priority` | Suggest task priority |
| POST | `/ai/sprint/summary` | Generate sprint summary |

### Dashboard & Search
| Method | Endpoint | Description |
|---|---|---|
| GET | `/dashboard/me` | Personal dashboard |
| GET | `/dashboard/workspace/{workspaceId}` | Workspace dashboard |
| GET | `/search?q={keyword}` | Global search |

### Notifications
| Method | Endpoint | Description |
|---|---|---|
| GET | `/notifications` | List notifications |
| GET | `/notifications/unread` | Unread notifications |
| GET | `/notifications/unread/count` | Unread count |
| PATCH | `/notifications/{notificationId}/read` | Mark as read |
| PATCH | `/notifications/read-all` | Mark all as read |

---

## 🚀 Running Locally

### Prerequisites
- Java 24+
- Maven
- Docker (optional)
- PostgreSQL
- Redis
- Google Gemini API Key ([free at ai.google.dev](https://ai.google.dev))

### Steps

```bash
# Clone the repo
git clone https://github.com/Spectraa28/TaskManager.git
cd TaskManager

# Copy env file
cp .env.example .env
# Fill in your values in .env

# Run with Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

App runs on `http://localhost:8080`  
Swagger UI → `http://localhost:8080/swagger-ui.html`

### Docker

```bash
# Build and run with Docker Compose
docker-compose up --build
```

---

## 🗓️ Build Log

| Day | What Was Built |
|---|---|
| Day 1 | Project setup, package structure, all domain enums, `BaseEntity` with UUID PKs, `ApiResponse<T>` wrapper, `GlobalExceptionHandler` |
| Day 2 | Full JWT security layer — `JwtUtils`, `AuthTokenFilter`, `AuthEntryPointJwt`, `UserDetailsImpl`, `WebSecurityConfig` |
| Day 3 | `User` entity, `UserRepository`, Register/Login DTOs, `AuthService`, `AuthController` — fully tested |
| Day 4 | RefreshToken entity, token rotation, revocation, full auth flow |
| Day 5 | Workspace entity, CRUD, member management, role system |
| Day 6 | Project entity, CRUD scoped to workspace, project member roles |
| Day 7 | Sprint entity, lifecycle management (PLANNED → ACTIVE → COMPLETED) |
| Day 8 | Task entity, CRUD, taskKey generation, status FSM |
| Day 9 | Task extras — comments, time logging, task watching |
| Day 10 | Activity log via AOP, audit trail on all task mutations |
| Day 11 | Notification system — RabbitMQ async delivery, read/unread |
| Day 12 | WebSocket + STOMP real-time task updates |
| Day 13 | Email notifications via JavaMailSender + Thymeleaf templates |
| Day 14 | Scheduled jobs — overdue task flagging, sprint health checks |
| Day 15 | Google Gemini AI integration — description, priority, sprint summary |
| Day 16 | Global search + personal/workspace dashboards |
| Day 17 | Redis caching (10min TTL) + rate limiting on auth endpoints |
| Day 18 | Docker multi-stage build, Swagger/OpenAPI config, deployed to Render |

---

## 🗺️ Roadmap

### ✅ Phase 1 — Monolith (Days 1–18) — COMPLETE
Production-grade Spring Boot backend with AI features, fully deployed.

### 🔄 Phase 2 — React Frontend (Days 19–26)
Full React + TypeScript frontend with Tailwind CSS, connected to the live API.

### 🔜 Phase 3 — Microservices (Post Day 27)
Migration using Spring Cloud Gateway, Eureka Service Discovery, OpenFeign, Resilience4j circuit breakers, and Kubernetes deployment.

---

## 👨‍💻 Author

Built by [Sonu Verma](https://github.com/Spectraa28)

> If you find this helpful, drop a ⭐ on the repo!
