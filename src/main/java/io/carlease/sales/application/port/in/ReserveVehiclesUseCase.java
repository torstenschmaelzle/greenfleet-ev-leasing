package io.carlease.sales.application.port.in;

import java.util.List;

public interface ReserveVehiclesUseCase {
    void reserve(List<String> vins);
}
