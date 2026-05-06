package io.carlease.sales.application.service;

import io.carlease.sales.application.port.in.ReserveVehiclesUseCase;
import io.carlease.sales.application.port.out.ReserveVehiclePort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReserveVehiclesService implements ReserveVehiclesUseCase {

    private final ReserveVehiclePort reserveVehiclePort;

    public ReserveVehiclesService(ReserveVehiclePort reserveVehiclePort) {
        this.reserveVehiclePort = reserveVehiclePort;
    }

    @Override
    public void reserve(List<String> vins) {
        vins.forEach(reserveVehiclePort::reserve);
    }
}
