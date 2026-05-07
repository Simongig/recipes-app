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
