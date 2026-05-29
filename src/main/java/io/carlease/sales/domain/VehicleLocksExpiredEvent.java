package io.carlease.sales.domain;

import java.util.List;

public record VehicleLocksExpiredEvent(List<String> vins) {}
