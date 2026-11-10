# Tech stack

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

[← Back to the README](../README.md) · [Documentation index](README.md)
