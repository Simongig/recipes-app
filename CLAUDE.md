# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Frontend (Vue 3)
```bash
cd frontend
npm run serve   # Dev server on port 8081, proxies API to localhost:8080
npm run build   # Production build (output goes to frontend/dist)
npm run lint    # ESLint with auto-fix
```

### Backend (Spring Boot)
```bash
cd backend
mvn spring-boot:run          # Run backend on port 8080
mvn clean install            # Full build: compiles backend + copies frontend dist into backend/src/main/resources/public
```

### Full production build (from root)
```bash
mvn clean install   # Builds both modules; frontend dist is embedded in the JAR
```

## Architecture

This is a monorepo with a **Vue 3 SPA** frontend and a **Spring Boot 2.6** backend, deployed as a single unit to Heroku.

- **Frontend** (`/frontend/src/`) — Vue 3 with Vue Router and Vuex. Pages live in `views/`, reusable components in `components/`. Axios calls the backend API directly; no centralized API service layer exists — calls are made inside individual components.
- **Backend** (`/backend/src/main/java/com/simongig/recipesapp/`) — Layered: `api/` (REST controllers) → `service/` (business logic) → `dao/` (data access interfaces + MongoDB implementations). The `dao/` layer uses Spring's `@Qualifier` to switch between implementations.
- **Database** — MongoDB Atlas. The URI is in `backend/src/main/resources/application.yaml`.
- **Auth** — Custom JWT filters (`filter/`). `CustomAuthenticationFilter` handles `POST /api/auth/login` and issues access + refresh tokens. `CustomAuthorizationFilter` validates tokens on incoming requests. Tokens are stored in browser `localStorage`; Vuex only tracks an `isLoggedIn` boolean.
- **Build integration** — The Maven parent POM (`/pom.xml`) builds the frontend via the frontend-maven-plugin and copies `frontend/dist` into `backend/src/main/resources/public`, so Spring Boot serves the SPA.

### Key API routes
| Method | Path | Auth |
|--------|------|------|
| GET | `/api/v1/recipe/all` | Public |
| POST | `/api/v1/recipe/find` | Public — search by ingredients array |
| POST | `/api/v1/recipe/search` | Public — full-text search by term |
| GET | `/api/v1/recipe/id/{id}` | Public |
| POST | `/api/v1/recipe/add` | Required — multipart form (recipe JSON + optional images) |
| DELETE | `/api/v1/recipe/delete/{id}` | Required |
| POST | `/api/auth/login` | — |

### Styling conventions
- All CSS is scoped inside Vue component `<style>` blocks — no external stylesheet files.
- CSS custom properties defined in `App.vue`: `--main-color: #212e2f`, `--secondary-color: #c3a452`, `--tertiary-color: #fdfdfa`.
- Responsive breakpoints: 1500px, 768px, 550px.
- Icons come from Ionicons (loaded via CDN in `public/index.html`).

## Dev proxy
`frontend/vue.config.js` proxies all requests from the frontend dev server (`:8081`) to the backend (`:8080`), so both servers must be running during local development.

<!-- BACKLOG.MD GUIDELINES START -->
<!-- backlog.md-instructions-version: 1.55.3 -->
<CRITICAL_INSTRUCTION>

## Backlog.md Workflow

This project uses Backlog.md for task and project management.

**At the beginning of each conversation in this project, run `backlog instructions overview` before answering or taking action. Re-read it only if you have not read it yet in the current conversation.**

Use the overview to decide whether to search, read, create, or update Backlog tasks.

Before task lifecycle actions, read the matching detailed guide:
- `backlog instructions task-creation` before creating or splitting tasks
- `backlog instructions task-execution` before planning, changing status or assignee, adding a plan or implementation notes, or implementing task work
- `backlog instructions task-finalization` before checking acceptance criteria, writing final summaries, or moving tasks to terminal statuses

Use `backlog <command> --help` before running unfamiliar commands. Help shows options, fields, and examples.

Do not edit Backlog task, draft, document, decision, or milestone markdown files directly. Use the `backlog` CLI so metadata, relationships, and history stay consistent.

</CRITICAL_INSTRUCTION>
<!-- BACKLOG.MD GUIDELINES END -->
