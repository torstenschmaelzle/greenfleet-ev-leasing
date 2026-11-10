# API and authentication

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

## API reference

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

[← Back to the README](../README.md) · [Documentation index](README.md)
