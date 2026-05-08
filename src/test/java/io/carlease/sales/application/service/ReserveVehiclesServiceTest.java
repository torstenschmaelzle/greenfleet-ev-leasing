package io.carlease.sales.application.service;

import io.carlease.sales.application.port.out.ReserveVehiclePort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class ReserveVehiclesServiceTest {

    private final ReserveVehiclePort reservePort = mock(ReserveVehiclePort.class);
    private final ReserveVehiclesService service = new ReserveVehiclesService(reservePort);

    @Test
    void reservesEachVinOnce() {
        service.reserve(List.of("WBA00000000000101", "WBA00000000000102"));

        var captor = ArgumentCaptor.forClass(String.class);
        then(reservePort).should(times(2)).reserve(captor.capture());
        assertThat(captor.getAllValues())
            .containsExactlyInAnyOrder("WBA00000000000101", "WBA00000000000102");
    }

    @Test
    void doesNothingForEmptyList() {
        service.reserve(List.of());

        then(reservePort).should(never()).reserve(any());
    }
}
