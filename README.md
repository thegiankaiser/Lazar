# Lazar — AI Productivity Companion

Revive your thoughts. Rediscover your focus.

Lazar is a full-stack portfolio project: an Android (Java) client backed by a Spring Boot REST API, PostgreSQL persistence, JWT authentication, daily usage limits, and server-side Gemini integration.

## Purpose
Give Lazar your mess. Get back a plan

## Features
- Register and authenticate with JWT access tokens.
- Three guided workflows: `ORGANIZE_TASKS`, `ACTION_PLAN`, and `DRAFT_EMAIL`.
- Server-side AI integration. The Gemini key is never shipped to Android.
- PostgreSQL-backed users, generated responses, and daily usage accounting.
- Ten free AI actions per user per UTC calendar day.
- Validation, consistent API errors, and service/controller tests.
- Android Java starter client using Retrofit and OkHttp.

## Architecture

```text
Android (Java) ── Retrofit / HTTPS ──> Spring Boot REST API
                                          │
                         ┌────────────────┼────────────────┐
                         │                │                │
                    Spring Security   PostgreSQL       Gemini API
                       JWT auth       JPA entities    server-side key
                         │                │                │
                         └──────────── Usage + history ────┘
```

## Repository layout

```text
lazar/
├── backend/                 Spring Boot application
│   ├── src/main/java/com/lazar/
│   │   ├── config/           Security and application configuration
│   │   ├── controller/       REST endpoints
│   │   ├── dto/              Request/response contracts
│   │   ├── exception/        API errors and exception mapping
│   │   ├── model/            JPA entities and workflow enum
│   │   ├── repository/       Spring Data repositories
│   │   ├── security/         JWT filter and token service
│   │   └── service/          Auth, chat, AI, and usage logic
│   ├── src/main/resources/
│   ├── src/test/java/
│   ├── pom.xml
│   └── .env.example
├── android/                 Android Java client starter
│   ├── app/src/main/java/com/lazar/mobile/
│   ├── app/src/main/res/
│   ├── app/build.gradle
│   └── settings.gradle
├── docker-compose.yml
└── .gitignore
```

## Requirements
- Java 17 or newer
- Maven 3.9+
- Docker Desktop (recommended for PostgreSQL)
- Android Studio for the mobile client
- A Gemini API key from Google AI Studio

## Run locally

### 1. Start PostgreSQL

From the repository root:

```bash
docker compose up -d db
```

PostgreSQL listens on `localhost:5432`. The development database is `lazar`, username `lazar`, password `lazar_dev_password`.

### 2. Configure backend environment

```bash
cd backend
cp .env.example .env
```

Set `AI_API_KEY` in your shell or your IDE run configuration. Do not commit `.env`.

macOS/Linux:

```bash
export AI_API_KEY="YOUR_GEMINI_API_KEY"
export JWT_SECRET="$(openssl rand -base64 48)"
```

Windows PowerShell:

```powershell
$env:AI_API_KEY="YOUR_GEMINI_API_KEY"
$env:JWT_SECRET="replace-with-a-long-random-secret-at-least-32-bytes"
```

The backend reads environment variables directly. Spring Boot does not automatically load `.env`; use shell variables or configure them in your IDE.

### 3. Run the backend

```bash
cd backend
mvn spring-boot:run
```

API base URL: `http://localhost:8080`

Health check:

```bash
curl http://localhost:8080/actuator/health
```

### 4. Try the API

Register:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"ChangeMe123!"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@example.com","password":"ChangeMe123!"}'
```

Copy `accessToken` from the response, then call chat:

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":"I have a meeting, three overdue tasks, and need to email my manager. Help me organize this.","workflow":"ORGANIZE_TASKS"}'
```

Get saved history:

```bash
curl http://localhost:8080/api/chat/history \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### 5. Run tests

```bash
cd backend
mvn test
```

Tests use an isolated H2 in-memory database through the test profile; PostgreSQL is not required to execute the unit tests.

## Android client

Open the `android/` directory in Android Studio and allow Gradle sync.

Set the API base URL in `ApiClient.java`:
- Android Emulator → `http://10.0.2.2:8080/`
- Physical device → `http://YOUR_COMPUTER_LAN_IP:8080/` (same Wi-Fi; allow the port through your firewall)
- Production → your HTTPS deployment URL.

The included Android app is a deliberately small networking starter: it demonstrates Retrofit login and chat calls. Build your own screens and token persistence before treating it as a production mobile app.

For local HTTP only, the manifest enables cleartext traffic for development. Disable it and use HTTPS in production.

## API overview

| Method | Endpoint | Auth | Purpose |
|---|---|---:|---|
| POST | `/api/auth/register` | No | Create account |
| POST | `/api/auth/login` | No | Obtain JWT |
| POST | `/api/chat` | Yes | Run a guided AI workflow |
| GET | `/api/chat/history` | Yes | List saved outputs |
| GET | `/api/usage` | Yes | View today's usage |
| GET | `/actuator/health` | No | Health check |

### Chat request

```json
{
  "message": "My notes are all over the place...",
  "workflow": "ORGANIZE_TASKS"
}
```

Supported workflow values: `ORGANIZE_TASKS`, `ACTION_PLAN`, `DRAFT_EMAIL`.

## Configuration

| Variable | Description |
|---|---|
| `DB_URL` | JDBC URL; defaults to local PostgreSQL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Base64-encoded secret, at least 256 bits |
| `JWT_EXPIRATION_MS` | Token lifetime; defaults to 24 hours |
| `AI_API_KEY` | Gemini API key |
| `AI_MODEL` | Gemini model identifier |
| `FREE_DAILY_LIMIT` | Free actions per UTC day; defaults to 10 |

## Before production
This is a portfolio-ready learning baseline, not a fully audited commercial service. Before public launch:
- Use HTTPS and a managed PostgreSQL service.
- Use Flyway/Liquibase migrations instead of `ddl-auto=update`.
- Add refresh-token rotation, email verification, password reset, account deletion, and stronger abuse controls.
- Add request timeouts, retry/backoff policy, provider error mapping, and observability.
- Store secrets in your deployment provider's secret manager.
- Add subscription billing only after defining entitlement, webhook verification, and refund logic.
- Review privacy, data retention, and AI-provider data handling.
- Replace the development Android cleartext configuration with HTTPS.

## GitHub publishing

From the repository root:

```bash
git init
git add .
git commit -m "Initial commit: build Lazar AI productivity companion"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/lazar.git
git push -u origin main
```

Create an empty GitHub repository named `lazar` first. Do not initialize it with a README if you already have this README locally.

## Suggested next milestones
1. Add Android screens for register, login, workflow selection, result, and history.
2. Add pagination and filtering to history.
3. Add integration tests with Testcontainers PostgreSQL.
4. Add GitHub Actions CI for Maven tests.
5. Deploy the API and database, then configure the Android production URL.
6. Add billing and paid-tier entitlements only after the core product is stable.
