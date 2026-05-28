package io.carlease.EV_Catalogue.adapter.in.web;

import io.carlease.EV_Catalogue.adapter.in.web.dto.AddVehicleRequest;
import io.carlease.EV_Catalogue.adapter.in.web.dto.UpdateVehicleRequest;
import io.carlease.EV_Catalogue.adapter.in.web.dto.VehicleDTO;
import io.carlease.EV_Catalogue.application.port.in.GetVehicleUseCase;
import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.in.removeVehicle;
import io.carlease.EV_Catalogue.application.port.in.updateVehicle;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    private final ListCatalogueUseCase listCatalogueUseCase;
    private final GetVehicleUseCase getVehicleUseCase;
    private final addVehicle addVehicleUseCase;
    private final updateVehicle updateVehicleUseCase;
    private final removeVehicle removeVehicleUseCase;

    public VehicleRestController(ListCatalogueUseCase listCatalogueUseCase,
                                  GetVehicleUseCase getVehicleUseCase,
                                  addVehicle addVehicleUseCase,
                                  updateVehicle updateVehicleUseCase,
                                  removeVehicle removeVehicleUseCase) {
        this.listCatalogueUseCase = listCatalogueUseCase;
        this.getVehicleUseCase = getVehicleUseCase;
        this.addVehicleUseCase = addVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.removeVehicleUseCase = removeVehicleUseCase;
    }

    @GetMapping
    public List<VehicleDTO> list() {
        return listCatalogueUseCase.listAll().stream()
                .map(VehicleDTO::from)
                .toList();
    }

    @GetMapping("/{vin}")
    public VehicleDTO get(@PathVariable String vin) {
        return VehicleDTO.from(getVehicleUseCase.getByVin(VehicleId.of(vin)));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody AddVehicleRequest req) {
        addVehicleUseCase.add(
                VehicleId.of(req.vin()),
                VehicleSpecification.of(req.brand(), req.model(),
                        Range.ofKm(req.rangeKm()),
                        VehicleType.valueOf(req.vehicleType()),
                        EnergyConsumption.of(BigDecimal.valueOf(req.consumptionKwhPer100km()))),
                BatterySpecification.of(
                        BigDecimal.valueOf(req.capacityKwh()),
                        BigDecimal.valueOf(req.voltageV()),
                        BigDecimal.valueOf(req.maxChargeSpeedKw())),
                Performance.of(req.horsePowerHp(),
                        BigDecimal.valueOf(req.accelerationSec()),
                        req.topSpeedKmh()),
                Amount.ofCents(req.monthlyLeasePriceCents(), Currency.valueOf(req.currency())),
                Percentage.of(BigDecimal.valueOf(req.initialStateOfHealth())),
                Range.ofKm(req.initialMeasuredRangeKm())
        );
    }

    @PutMapping("/{vin}")
    public void update(@PathVariable String vin, @RequestBody UpdateVehicleRequest req) {
        updateVehicleUseCase.update(
                VehicleId.of(vin),
                VehicleSpecification.of(req.brand(), req.model(),
                        Range.ofKm(req.rangeKm()),
                        VehicleType.valueOf(req.vehicleType()),
                        EnergyConsumption.of(BigDecimal.valueOf(req.consumptionKwhPer100km()))),
                BatterySpecification.of(
                        BigDecimal.valueOf(req.capacityKwh()),
                        BigDecimal.valueOf(req.voltageV()),
                        BigDecimal.valueOf(req.maxChargeSpeedKw())),
                Amount.ofCents(req.monthlyLeasePriceCents(), Currency.valueOf(req.currency())),
                Percentage.of(BigDecimal.valueOf(req.newStateOfHealth())),
                Range.ofKm(req.newMeasuredRangeKm())
        );
    }

    @DeleteMapping("/{vin}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable String vin) {
        removeVehicleUseCase.remove(VehicleId.of(vin));
    }
}
