package io.carlease.EV_Catalogue.application.port.in;

import io.carlease.EV_Catalogue.domain.Vehicle;

import java.util.List;

public interface ListCatalogueUseCase {
    List<Vehicle> listAll();
}
