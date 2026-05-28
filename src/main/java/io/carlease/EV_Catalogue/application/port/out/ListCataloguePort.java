package io.carlease.ev_catalogue.application.port.out;

import io.carlease.ev_catalogue.domain.Vehicle;

import java.util.List;

public interface ListCataloguePort {
    List<Vehicle> listAll();
}
