package io.carlease.ev_catalogue.adapter.in.web;

import io.carlease.ev_catalogue.adapter.in.web.dto.AddVehicleRequest;
import io.carlease.ev_catalogue.adapter.in.web.dto.UpdateVehicleRequest;
import io.carlease.ev_catalogue.adapter.in.web.dto.VehicleDTO;
import io.carlease.ev_catalogue.application.port.in.GetVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.ev_catalogue.application.port.in.AddVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.RemoveVehicleUseCase;
import io.carlease.ev_catalogue.application.port.in.UpdateVehicleUseCase;
import io.carlease.ev_catalogue.domain.*;
import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.Currency;
import io.carlease.shared.domain.VehicleType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/vehicles")
public class VehicleRestController {

    private final ListCatalogueUseCase listCatalogueUseCase;
    private final GetVehicleUseCase getVehicleUseCase;
    private final AddVehicleUseCase addVehicleUseCase;
    private final UpdateVehicleUseCase updateVehicleUseCase;
    private final RemoveVehicleUseCase removeVehicleUseCase;

    public VehicleRestController(ListCatalogueUseCase listCatalogueUseCase,
                                  GetVehicleUseCase getVehicleUseCase,
                                  AddVehicleUseCase addVehicleUseCase,
                                  UpdateVehicleUseCase updateVehicleUseCase,
                                  RemoveVehicleUseCase removeVehicleUseCase) {
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
    public void add(@Valid @RequestBody AddVehicleRequest req) {
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
                Amount.ofCents(req.monthlyLeasePriceCents(), Currency.from(req.currency())),
                Percentage.of(BigDecimal.valueOf(req.initialStateOfHealth())),
                Range.ofKm(req.initialMeasuredRangeKm())
        );
    }

    @PutMapping("/{vin}")
    public void update(@PathVariable String vin, @Valid @RequestBody UpdateVehicleRequest req) {
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
                Amount.ofCents(req.monthlyLeasePriceCents(), Currency.from(req.currency())),
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
