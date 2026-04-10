package io.carlease.EV_Catalogue.application.port.out;

import io.carlease.EV_Catalogue.domain.Vehicle;

import java.util.List;

public interface ListCataloguePort {
    List<Vehicle> listAll();
}
