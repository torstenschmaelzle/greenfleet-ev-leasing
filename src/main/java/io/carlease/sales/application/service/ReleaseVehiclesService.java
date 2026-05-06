package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.ReleaseVehiclesUseCase;
import io.carlease.sales.application.port.out.ReleaseVehiclePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReleaseVehiclesService implements ReleaseVehiclesUseCase {

    private final ReleaseVehiclePort releaseVehiclePort;

    public ReleaseVehiclesService(ReleaseVehiclePort releaseVehiclePort) {
        this.releaseVehiclePort = releaseVehiclePort;
    }

    @Override
    public void release(List<String> vins) {
        vins.forEach(releaseVehiclePort::release);
    }
}
