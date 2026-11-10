# Getting started

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.9+** (or use the included `./mvnw` wrapper)
- **Node.js 20+** and **npm 10+** — [Download](https://nodejs.org/)
- **Docker & Docker Compose** (production only)

---

### Run Locally (Development)

The development setup uses an **in-memory H2 database**, no external database required. The backend and frontend run as two separate processes.

#### 1. Start the Backend

```bash
./mvnw spring-boot:run
```

The Spring Boot application starts on **http://localhost:8080**.

- H2 Console (database inspector): **http://localhost:8080/h2-console**
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa` · Password: *(empty)*

#### 2. Start the Frontend

**Environment Variables**
Some features require API keys that are not committed to the repository.
Before starting the frontend, create the following file:
`frontend/.env.local`
```
VITE_UNSPLASH_KEY=your_key_here
```
This file is gitignored and must never be committed. Obtain the key from a team member or generate one at [unsplash.com/developers](https://unsplash.com/developers).
In CI/CD the key is injected automatically via the `VITE_UNSPLASH_KEY` GitHub Secret.

```bash
cd frontend
npm install
npm run dev
```

The React dev server starts on **http://localhost:5173** with hot-reload enabled. API requests are proxied to the backend automatically.

> **Note:** Vite's dev server is pre-configured to forward `/api/*` requests to `http://localhost:8080`. No extra configuration is needed.

---

### Run with Docker (Production)

The production setup runs four containers orchestrated by Docker Compose:

| Service | Image | Role |
|---|---|---|
| `reverse-proxy` | `traefik:v2.1.3` | HTTPS termination, HTTP→HTTPS redirect |
| `app` | `openjdk` | Spring Boot backend (`app-0.1.0.jar`) |
| `db` | `mysql` | Persistent MySQL database |
| `frontend` | `nginx:alpine` | Serves the built React SPA + proxies `/api/` |
| `adminer` | `adminer` | Database admin UI |

#### 1. Build the backend JAR

```bash
./mvnw clean package -DskipTests
```

#### 2. Build the frontend

```bash
cd frontend
npm install
npm run build
```

Copy the build output to the deployment directory:

```bash
cp -r frontend/dist ../frontend-dist
```

#### 3. Configure environment

Set the public IP of your server:

```bash
export PUB_IP=<your-server-ip>
```

Create the Let's Encrypt storage file:

```bash
mkdir -p letsencrypt
touch letsencrypt/acme.json
chmod 600 letsencrypt/acme.json
```

Switch `application.properties` to use MySQL (uncomment the MySQL lines, comment out H2):

```properties
spring.datasource.url=jdbc:mysql://<DB_HOST>:3306/carlease
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=CHANGE_ME_DB_PASSWORD
```

#### 4. Start all services

```bash
docker-compose up -d
```

The application will be available at:
- **Frontend:** `https://app.<PUB_IP>.se.scs.unisg.ch`
- **Database Admin:** `https://dbadmin.<PUB_IP>.se.scs.unisg.ch`

---

[← Back to the README](../README.md) · [Documentation index](README.md)
