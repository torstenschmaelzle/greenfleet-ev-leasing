package io.carlease.EV_Catalogue.application.service;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.application.port.out.ListCataloguePort;
import io.carlease.EV_Catalogue.domain.Vehicle;
import org.jmolecules.architecture.layered.ApplicationLayer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Component
@ApplicationLayer
@Service
public class ListCatalogueService implements ListCatalogueUseCase {

    private final ListCataloguePort listCataloguePort;

    public ListCatalogueService(ListCataloguePort listCataloguePort) {
        this.listCataloguePort = listCataloguePort;
    }

    @Override
    public List<Vehicle> listAll() {
        return listCataloguePort.listAll();
    }
}
