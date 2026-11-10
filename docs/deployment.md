# Deployment

The live deployment runs on a university-provided VM using Docker Compose with Traefik handling TLS.

**Deployment process:**

1. Build the backend JAR: `./mvnw clean package -DskipTests`
2. Build the React frontend: `cd frontend && npm run build`
3. Copy the JAR and frontend dist to the server
4. Set `PUB_IP` environment variable
5. Run `docker-compose up -d`

Traefik automatically provisions and renews TLS certificates via **Let's Encrypt** using the HTTP-01 ACME challenge. All HTTP traffic is permanently redirected to HTTPS.

**Nginx** serves the compiled React SPA and forwards all `/api/*` requests to the Spring Boot container on port 8080, enabling the single-domain setup without CORS issues.

---

[← Back to the README](../README.md) · [Documentation index](README.md)
