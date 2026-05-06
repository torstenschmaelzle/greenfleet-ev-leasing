package io.carlease.sales.application.port.in;

import java.util.List;

public interface ReleaseVehiclesUseCase {
    void release(List<String> vins);
}
