# Testing

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

## Exploratory and usability testing

- **Own exploratory testing** of the Fleet Consulting and EV Catalogue UIs.
- **Cross-team testing:** another team tested GreenFleet and found, among others, a CO₂ double-division bug and an emission constant that was 24 times too low. Both were fixed, and the finding changed how the team reviewed code.
- **Usability test** with a participant matching the fleet manager persona, across all main tasks. Reserving, signing and retrieving contracts worked smoothly; the main feedback was that similar-looking bundles needed a short explanation of their ranking, that CO₂ figures needed context, and that the catalogue lacked a range filter.

---

[← Back to the README](../README.md) · [Documentation index](README.md)
