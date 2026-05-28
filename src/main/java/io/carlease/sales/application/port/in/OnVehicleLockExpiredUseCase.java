package io.carlease.sales.application.port.in;

import java.util.List;

public interface OnVehicleLockExpiredUseCase {
    void handle(List<String> vins);
}
