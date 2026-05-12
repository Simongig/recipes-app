# Kochbuch.io

## Bonjour mon ami

My name is Simon and I am a computer science masters student from Berlin. <br>
Kochbuch.io is a hobby project that tries to simplify my
efforts to digitalise my mother's (and also friend's) recipes.
I know it's shitty in some ways and if you try to delete my database by any kind of exploit do as you like. I would feel proud if you found the website and interacted with it in any way <3. <br>
But if you like the project and would like to contribute in any way hit me up on ICQ.<br>

If you are an employer who checks out my github out, I appreciate the effort! I hope at least you like the README :cat:

The site is reachable via <a href="https://kochbuch.io">kochbuch.io</a>

For all of you unemployed people that found this repo and want to recreate it, here is the guide:

---

## Development Setup

### Prerequisites

- **Java 17** — [Download](https://adoptium.net/)
- **Maven** — usually bundled with your IDE, or install via your package manager
- **Node.js + npm** — [Download](https://nodejs.org/) (LTS is fine)
- **Docker + Docker Compose** — [Download](https://www.docker.com/)

### 1. Start the database

The dev setup uses a local MongoDB instance via Docker:

```bash
docker compose up -d
```

This starts MongoDB on port `27017` and seeds it with the init scripts in `scripts/`.

### 2. Start the backend

I use the VS Code Spring Boot extension for development. You just need to click "Start with Profile" and select the "dev" profile. 
Alternatively you can use this in the cmd:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The backend runs on `http://localhost:8080`. The dev profile connects to the local MongoDB and uses a dummy JWT secret — no extra config needed.

### 3. Start the frontend

```bash
cd frontend
npm install
npm run serve
```

The frontend dev server runs on `http://localhost:8081` and proxies all `/api` requests to the backend, so both need to be running. Changes to `.vue` files reload automatically.

---

## Production Setup (Digital Ocean)

On the server you only need **Docker** and **Docker Compose** installed. The images are pulled from Docker Hub.

Create a `.env` file next to `docker-compose.prod.yml` with the following variables (you can also copy the .env.example file for reference):

```env
MONGODB_URI=mongodb+srv://<user>:<password>@<cluster>/kochbuch
MONGODB_DATABASE=kochbuch
JWT_SECRET=<your-secret>
```

Then start everything with:

```bash
docker compose -f docker-compose.prod.yml up -d
```

The frontend container handles SSL via Certbot/Let's Encrypt and listens on ports `80` and `443`.
