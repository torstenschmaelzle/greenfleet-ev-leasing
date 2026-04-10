package io.carlease.EV_Catalogue.adapter.in.vaadin;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.in.removeVehicle;
import io.carlease.EV_Catalogue.application.port.in.updateVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class EVCatalogueVaadinController {

    private final addVehicle addVehicleUseCase;
    private final ListCatalogueUseCase listCatalogueUseCase;
    private final removeVehicle removeVehicleUseCase;
    private final updateVehicle updateVehicleUseCase;
    private final VehiclePersistancePort vehiclePersistancePort;

    public EVCatalogueVaadinController(addVehicle addVehicleUseCase,
                                       ListCatalogueUseCase listCatalogueUseCase,
                                       removeVehicle removeVehicleUseCase,
                                       updateVehicle updateVehicleUseCase,
                                       VehiclePersistancePort vehiclePersistancePort) {
        this.addVehicleUseCase = addVehicleUseCase;
        this.listCatalogueUseCase = listCatalogueUseCase;
        this.removeVehicleUseCase = removeVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    public List<Vehicle> listAll() {
        return listCatalogueUseCase.listAll();
    }

    public Vehicle getVehicle(String vin) {
        return vehiclePersistancePort.findById(VehicleId.of(vin))
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found: " + vin));
    }

    public void removeVehicle(String vin) {
        removeVehicleUseCase.remove(VehicleId.of(vin));
    }

    public void updateVehicle(String vin,
                              String brand, String model, Integer rangeKm,
                              String vehicleType, Double consumptionKwhPer100km,
                              Double capacityKwh, Double voltageV, Double maxChargeSpeedKw,
                              Long monthlyLeasePriceCents, String currency,
                              Double newStateOfHealth, Integer newMeasuredRangeKm) {
        updateVehicleUseCase.update(
                VehicleId.of(vin),
                VehicleSpecification.of(
                        brand, model,
                        Range.ofKm(rangeKm),
                        VehicleType.valueOf(vehicleType),
                        EnergyConsumption.of(BigDecimal.valueOf(consumptionKwhPer100km))
                ),
                BatterySpecification.of(
                        BigDecimal.valueOf(capacityKwh),
                        BigDecimal.valueOf(voltageV),
                        BigDecimal.valueOf(maxChargeSpeedKw)
                ),
                Amount.ofCents(monthlyLeasePriceCents, Currency.valueOf(currency)),
                Percentage.of(BigDecimal.valueOf(newStateOfHealth)),
                Range.ofKm(newMeasuredRangeKm)
        );
    }

    public void addVehicle(String vin,
                           String brand,
                           String model,
                           Integer rangeKm,
                           String vehicleType,
                           Double consumptionKwhPer100km,
                           Double capacityKwh,
                           Double voltageV,
                           Double maxChargeSpeedKw,
                           Integer horsePowerHp,
                           Double accelerationSec,
                           Integer topSpeedKmh,
                           Long monthlyLeasePriceCents,
                           String currency,
                           Double initialStateOfHealth,
                           Integer initialMeasuredRangeKm) {

        addVehicleUseCase.add(
                VehicleId.of(vin),
                VehicleSpecification.of(
                        brand,
                        model,
                        Range.ofKm(rangeKm),
                        VehicleType.valueOf(vehicleType),
                        EnergyConsumption.of(BigDecimal.valueOf(consumptionKwhPer100km))
                ),
                BatterySpecification.of(
                        BigDecimal.valueOf(capacityKwh),
                        BigDecimal.valueOf(voltageV),
                        BigDecimal.valueOf(maxChargeSpeedKw)
                ),
                Performance.of(
                        horsePowerHp,
                        BigDecimal.valueOf(accelerationSec),
                        topSpeedKmh
                ),
                Amount.ofCents(monthlyLeasePriceCents, Currency.valueOf(currency)),
                Percentage.of(BigDecimal.valueOf(initialStateOfHealth)),
                Range.ofKm(initialMeasuredRangeKm)
        );
    }
}
