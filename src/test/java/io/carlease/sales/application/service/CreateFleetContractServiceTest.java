package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase.VehicleEntry;
import io.carlease.sales.application.port.out.FleetContractPersistencePort;
import io.carlease.sales.domain.FleetContract;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class CreateFleetContractServiceTest {

    private final FleetContractPersistencePort persistencePort = mock(FleetContractPersistencePort.class);
    private final CreateFleetContractService service = new CreateFleetContractService(persistencePort);

    private List<VehicleEntry> twoVehicles() {
        return List.of(
            new VehicleEntry("WBA00000000000101", 60000L, "CHF"),
            new VehicleEntry("WBA00000000000102", 45000L, "CHF")
        );
    }

    @Nested
    class whenInputIsValid {

        @Test
        void savesFleetContractAndReturnsNonBlankContractNumber() {
            var result = service.create(
                "John Buyer", "ACME AG", "john@example.com", 36, "ECO_FOCUSED", twoVehicles()
            );

            assertThat(result.number()).isNotBlank();
            then(persistencePort).should().save(any(FleetContract.class));
        }

        @Test
        void savedFleetContractContainsOneVehicleContractPerEntry() {
            service.create(
                "John Buyer", "ACME AG", "john@example.com", 36, "ECO_FOCUSED", twoVehicles()
            );

            var captor = forClass(FleetContract.class);
            then(persistencePort).should().save(captor.capture());
            assertThat(captor.getValue().vehicleContracts()).hasSize(2);
        }
    }
}
