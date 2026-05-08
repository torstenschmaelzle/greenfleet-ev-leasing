package io.carlease.sales.application.service;

import io.carlease.sales.application.port.out.ReleaseVehiclePort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class ReleaseVehiclesServiceTest {

    private final ReleaseVehiclePort releasePort = mock(ReleaseVehiclePort.class);
    private final ReleaseVehiclesService service = new ReleaseVehiclesService(releasePort);

    @Test
    void releasesEachVinOnce() {
        service.release(List.of("WBA00000000000101", "WBA00000000000102"));

        var captor = ArgumentCaptor.forClass(String.class);
        then(releasePort).should(times(2)).release(captor.capture());
        assertThat(captor.getAllValues())
            .containsExactlyInAnyOrder("WBA00000000000101", "WBA00000000000102");
    }

    @Test
    void doesNothingForEmptyList() {
        service.release(List.of());

        then(releasePort).should(never()).release(any());
    }
}
