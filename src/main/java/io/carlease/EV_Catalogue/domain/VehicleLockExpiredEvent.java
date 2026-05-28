package io.carlease.ev_catalogue.domain;

import java.util.List;

public record VehicleLockExpiredEvent(List<String> vins) {}
