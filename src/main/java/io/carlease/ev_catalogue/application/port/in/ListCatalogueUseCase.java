package io.carlease.ev_catalogue.application.port.in;

import io.carlease.ev_catalogue.domain.Vehicle;

import java.util.List;

public interface ListCatalogueUseCase {
    List<Vehicle> listAll();
}
