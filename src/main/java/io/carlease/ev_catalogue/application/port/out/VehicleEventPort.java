package io.carlease.ev_catalogue.application.port.out;

import java.util.List;

public interface VehicleEventPort {
    void publishLocksExpired(List<String> vins);
}
