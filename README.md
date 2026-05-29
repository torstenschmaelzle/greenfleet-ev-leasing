# GreenFleet — EV Fleet Leasing Platform

> A Domain-Driven Design implementation of an electric vehicle fleet leasing system, built with Hexagonal Architecture (Ports & Adapters) across three bounded contexts.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Bounded Contexts](#bounded-contexts)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Run Locally (Development)](#run-locally-development)
  - [Run with Docker (Production)](#run-with-docker-production)
- [Authentication](#authentication)
- [API Reference](#api-reference)
- [Testing](#testing)
- [Deployment](#deployment)
- [Team](#team)

---

## Overview

GreenFleet is a web-based EV fleet leasing platform developed as part of the Software Engineering course at the University of St. Gallen (HSG). The system allows fleet managers to discover, compare, and lease electric vehicles through a data-driven recommendation engine, while internal staff can manage the vehicle catalogue and oversee the full contract lifecycle.

The project is implemented following **Domain-Driven Design (DDD)** principles with a strict **Hexagonal Architecture** (Ports & Adapters) to ensure bounded contexts remain decoupled and the domain model stays free of infrastructure concerns.

---

## Architecture

The application is structured around **three independent bounded contexts**, each with its own domain model, application services, and adapter layer. All communication between bounded contexts goes through explicit ports.

```
┌─────────────────────────────────────────────────────────────┐
│                        React Frontend                        │
│         (TypeScript · Vite · React Router · Fetch API)      │
└───────────────────────┬─────────────────────────────────────┘
                        │ HTTP / REST  ·  SSE
┌───────────────────────▼─────────────────────────────────────┐
│                     Spring Boot Backend                      │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  EV Catalogue│  │   Fleet      │  │      Sales       │  │
│  │  Bounded     │  │  Consulting  │  │   Bounded        │  │
│  │  Context     │  │  Bounded     │  │   Context        │  │
│  │              │  │  Context     │  │                  │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘  │
│         │                 │                    │            │
│  ┌──────▼─────────────────▼────────────────────▼─────────┐  │
│  │              Shared Domain (Amount, LeaseTerm, ...)    │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────┐
│              H2 (dev)  /  MySQL (production)                 │
└─────────────────────────────────────────────────────────────┘
```

Each bounded context follows the same internal layering:

```
adapter/in/web      ←  REST controllers, DTOs, exception handlers
application/port    ←  Use case interfaces (in) and output port interfaces (out)
application/service ←  Application services implementing use cases
adapter/out         ←  Persistence adapters, inter-BC adapters, SSE adapters
domain              ←  Aggregate roots, entities, value objects, domain services
```

---

## Bounded Contexts

### 1. EV Catalogue

Manages the inventory of electric vehicles available for leasing.

**Responsibilities:**
- Add, update, and remove electric vehicles with full technical specifications
- Track vehicle lifecycle status: `AVAILABLE` → `RESERVED` → `UNDER_CONTRACT` → `REMOVED`
- Manage battery health records (state of health, measured range)
- Broadcast real-time status changes to connected clients via Server-Sent Events (SSE)
- Automatically release expired reservation locks via a scheduled job

**Key domain objects:** `Vehicle` (Aggregate Root), `VehicleId`, `VehicleSpecification`, `BatterySpecification`, `ReservationLock`, `VehicleStatus`

---

### 2. Fleet Consulting

The core advisory engine: translates fleet operational requirements into ranked EV bundle recommendations.

**Responsibilities:**
- Accept fleet requirements (daily route km, number of vehicles, lease term, charging setup, optional budget, brand/type filters)
- Score every available vehicle across three dimensions: range suitability, CO₂ savings, and cost
- Allow custom scoring weight allocation (range / CO₂ / cost, always summing to 100%)
- Generate up to five parallel bundle recommendations:

| Bundle Type | Strategy |
|---|---|
| `BALANCED` | Best overall score across all three dimensions |
| `ECO` | Maximum annual CO₂ savings vs. diesel |
| `ECONOMIC` | Lowest total cost of ownership |
| `PREMIUM` | Maximum range and sustainability, cost unconstrained |
| `CUSTOM` | Optimised for the user's own weight settings |

- Calculate full TCO per bundle: monthly lease cost, monthly charging cost, monthly total, annual total
- Compute annual CO₂ savings per vehicle compared to an equivalent ICE fleet

**Key domain objects:** `FleetRequirements`, `FleetBundle`, `ScoredVehicle`, `IndividualVehicleScore`, `ScoringWeights`, `BundleOptimizer`, `TotalCostOfOwnership`, `Co2Impact`

---

### 3. Sales

Manages the full fleet contract lifecycle from bundle reservation through to digital signing.

**Responsibilities:**
- Reserve all vehicles in a selected bundle atomically
- Create fleet leasing contracts with customer details (name, company, email)
- Present the full contract document (parties, lease summary, vehicle schedule, T&C)
- Accept digital handwritten signatures via a canvas-based signature pad
- Transition reserved vehicles to `UNDER_CONTRACT` status on signing
- Automatically release vehicle reservations and delete unsigned contracts on abandonment
- Allow fleet managers (customers) to view and print their own contracts
- Allow GreenFleet employees to view all contracts across all customers

**Key domain objects:** `FleetContract` (Aggregate Root), `VehicleContract`, `Customer`, `ContractNumber`, `SignDate`

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Runtime |
| Spring Boot | 3.2.2 | Application framework |
| Spring Data JPA / Hibernate | — | Persistence |
| H2 Database | 2.2.224 | In-memory database (development) |
| MySQL | 8+ | Relational database (production) |
| jMolecules DDD | 1.9.0 | DDD stereotype annotations |
| dddbits | 0.0.1 | DDD base types |
| Lombok | 1.18.30 | Boilerplate reduction |
| MapStruct | 1.6.3 | Object mapping |
| Bean Validation | — | REST input validation |
| Vaadin | 24.2.2 | Server-side UI (legacy, partially retained) |

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| React | 18.3 | UI framework |
| TypeScript | 5.6 | Type-safe JavaScript |
| Vite | 5.4 | Build tool and dev server |
| React Router | 6.28 | Client-side routing |

### Testing
| Technology | Purpose |
|---|---|
| JUnit 5 | Unit and integration tests |
| Cucumber 7 | BDD / acceptance tests |
| jqwik 1.8 | Property-based tests |
| Spring Boot Test | System and integration tests |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker Compose | Container orchestration |
| Traefik v2 | Reverse proxy, TLS termination, HTTP→HTTPS redirect |
| Nginx | Static frontend serving, API proxy |
| Let's Encrypt (ACME) | Automatic TLS certificates |

---

## Project Structure

```
BSC_FS26_SE_GROUP04/
├── src/
│   ├── main/
│   │   ├── java/io/carlease/
│   │   │   ├── auth/                    # Session-based authentication
│   │   │   ├── config/                  # CORS and web configuration
│   │   │   ├── shared/domain/           # Shared value objects (Amount, LeaseTerm, ...)
│   │   │   ├── EV_Catalogue/            # EV Catalogue bounded context
│   │   │   │   ├── adapter/in/web/      # REST controllers + DTOs
│   │   │   │   ├── adapter/out/         # Persistence + SSE adapters
│   │   │   │   ├── application/         # Use cases (ports) and services
│   │   │   │   └── domain/              # Vehicle aggregate + value objects
│   │   │   ├── fleet_consulting/        # Fleet Consulting bounded context
│   │   │   │   ├── adapter/in/web/      # REST controller + DTOs
│   │   │   │   ├── adapter/out/         # EV Catalogue + Installment adapters
│   │   │   │   ├── application/         # GetRecommendation use case + service
│   │   │   │   └── domain/              # Bundle optimizer, scorer, TCO, CO2 impact
│   │   │   ├── sales/                   # Sales bounded context
│   │   │   │   ├── adapter/in/web/      # Fleet contract REST controller
│   │   │   │   ├── adapter/out/         # Persistence + EV Catalogue adapters
│   │   │   │   ├── application/         # Contract lifecycle use cases + services
│   │   │   │   └── domain/              # FleetContract aggregate, Customer, ...
│   │   └── resources/
│   │       └── application.properties   # App configuration
│   └── test/
│       ├── java/io/carlease/
│       │   ├── ev_catalogue/            # EV Catalogue tests (unit, integration, system)
│       │   ├── fleet_consulting/        # Fleet Consulting tests (unit, property-based, system)
│       │   ├── sales/                   # Sales tests (unit, integration, system, BDD)
│       │   └── shared/                  # Shared domain tests
│       └── resources/features/
│           └── fleet_contract.feature   # Cucumber BDD scenarios
├── frontend/
│   ├── src/
│   │   ├── api/                         # Typed API client modules
│   │   ├── components/                  # Reusable React components
│   │   ├── context/                     # Auth context (React Context API)
│   │   ├── pages/                       # Page components (one per route)
│   │   ├── styles/                      # Global CSS
│   │   ├── types.ts                     # Shared TypeScript types
│   │   ├── App.tsx                      # Root component + routing
│   │   └── main.tsx                     # Entry point
│   ├── package.json
│   └── vite.config.ts
├── docker-compose.yml                   # Production container orchestration
├── nginx.conf                           # Nginx reverse proxy config
├── pom.xml                              # Maven build descriptor
└── mvnw / mvnw.cmd                      # Maven wrapper
```

---

## Getting Started

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

## Authentication

The system uses **server-side session-based authentication** with two distinct login flows.

### Employee / WORKER Login

Access to catalogue management, all-contracts view, and contract creation on behalf of customers.

| Field | Value |
|---|---|
| Username | `worker` |
| Password | `12345` |

### Customer Login

No credentials required. Fleet managers identify themselves by entering their personal details.

| Field | Example |
|---|---|
| Full name | `Marcus Meier` |
| Business email | `marcus@logistik-ag.ch` |
| Company name | `Logistik AG` |

---

## API Reference

All REST endpoints are prefixed with `/api/`. The frontend communicates exclusively through these endpoints.

### Authentication — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/auth/login` | Employee login (`{ username, password }`) |
| `POST` | `/api/auth/customer-login` | Customer login (`{ name, email, company }`) |
| `GET` | `/api/auth/me` | Get current session user |
| `POST` | `/api/auth/logout` | Invalidate session |

### EV Catalogue — `/api/catalogue/vehicles`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/catalogue/vehicles` | List all vehicles |
| `POST` | `/api/catalogue/vehicles` | Add a new vehicle |
| `PUT` | `/api/catalogue/vehicles/{vin}` | Update vehicle details |
| `DELETE` | `/api/catalogue/vehicles/{vin}` | Remove a vehicle |
| `GET` | `/api/catalogue/vehicles/events` | SSE stream for real-time status changes |

### Fleet Consulting — `/api/fleet-consulting`

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/fleet-consulting/recommendations` | Get bundle recommendations |

**Query parameters:**

| Parameter | Type | Required | Description |
|---|---|----------|---|
| `daily_route_km` | int | yes      | Daily km per vehicle |
| `number_of_vehicles` | int | yes      | Fleet size |
| `lease_term_months` | int | yes      | 12 / 24 / 36 / 48 / 60 |
| `charging_type` | string | yes      | `COMPANY_CHARGER` / `PUBLIC_STATION` / `MIXED` |
| `electricity_rate_cents` | int | yes      | CHF per kWh × 100 |
| `currency` | string | yes      | `CHF` |
| `budget_monthly_max_cents` | int | no       | Monthly budget cap (total fleet) |
| `brand` | string | no       | Filter by brand |
| `vehicle_type` | string | no       | `SUV` / `SEDAN` / `VAN` / `COMPACT` / `ESTATE` |
| `range_weight` | int | no       | 0–100, must sum to 100 with others |
| `co2_weight` | int | no       | 0–100 |
| `cost_weight` | int | no       | 0–100 |

### Sales — `/api/sales/fleet-contracts`

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/sales/fleet-contracts` | Create a fleet contract |
| `GET` | `/api/sales/fleet-contracts` | List all contracts (WORKER) |
| `GET` | `/api/sales/fleet-contracts/my` | List own contracts (CUSTOMER) |
| `GET` | `/api/sales/fleet-contracts/{number}` | Get contract details |
| `POST` | `/api/sales/fleet-contracts/{number}/sign` | Sign a contract |
| `DELETE` | `/api/sales/fleet-contracts/{number}` | Delete an unsigned contract |
| `POST` | `/api/sales/fleet-contracts/reserve` | Reserve a bundle of vehicles |
| `POST` | `/api/sales/fleet-contracts/release` | Release vehicle reservations |

---

## Testing

The project has comprehensive test coverage across all bounded contexts and test types.

### Running the Tests

```bash
./mvnw test
```

### Test Strategy

The test suite is organized into four levels per bounded context:

| Level | Description | Example |
|---|---|---|
| **Domain unit tests** | Test domain logic in isolation, no Spring context | `VehicleTest`, `FleetContractTest`, `Co2ImpactTest` |
| **Application service tests** | Test use cases with mocked ports | `AddVehicleServiceTest`, `GetRecommendationServiceTest` |
| **Adapter / integration tests** | Test persistence and inter-BC adapters with real DB | `VehiclePersistenceAdapterTest`, `EVCatalogueVehicleAdapterIntegrationTest` |
| **System tests** | Full Spring context, in-memory H2, real HTTP layer | `AddVehicleSystemTest`, `SignFleetContractSystemTest` |

### Specialised Test Types

**BDD / Acceptance Tests (Cucumber)**

Located in `src/test/resources/features/`. Tests are written in Gherkin and cover complete user-facing scenarios:

```gherkin
Scenario: Customer creates and signs a fleet contract
  Given a vehicle "WBA00000000000301" is reserved in the catalogue
  When the customer creates a fleet contract for vehicle "WBA00000000000301"
  Then a valid contract number is persisted in the system
  When the customer signs the contract
  Then the contract is marked as signed
  And the vehicle "WBA00000000000301" is under contract
```

**Property-Based Tests (jqwik)**

Cover mathematical invariants in the domain model:

- `FinancialCalculatorPropertyTest` — PMT formula holds for all valid inputs
- `Co2ImpactPropertyTest` — CO₂ savings are always non-negative for EVs vs. ICE
- `IndividualVehicleScorePropertyTest` — All dimension scores remain in [0, 1]

**Failure Management Tests**

Verify that domain exceptions are thrown correctly for invalid inputs and violated business rules across all bounded contexts.

---

## Deployment

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

## Team

BSc Software Engineering · Spring 2026 · University of St. Gallen  
**Group 04** — Paul Kottmann, Linus Ihringer, Torsten Schmälzle, Valeria Pointet, Kristian Suntev, Nevio Gübeli

---

*Built with Domain-Driven Design · Hexagonal Architecture · Spring Boot · React*
