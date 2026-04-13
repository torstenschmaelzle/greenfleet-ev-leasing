package io.carlease.EV_Catalogue.adapter.in.web;

import io.carlease.EV_Catalogue.application.port.in.ListCatalogueUseCase;
import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.in.removeVehicle;
import io.carlease.EV_Catalogue.application.port.in.updateVehicle;
import io.carlease.EV_Catalogue.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class EVCatalogueController {

    private final addVehicle addVehicleUseCase;
    private final updateVehicle updateVehicleUseCase;
    private final removeVehicle removeVehicleUseCase;
    private final ListCatalogueUseCase listCatalogueUseCase;

    public EVCatalogueController(addVehicle addVehicleUseCase,
                                 updateVehicle updateVehicleUseCase,
                                 removeVehicle removeVehicleUseCase,
                                 ListCatalogueUseCase listCatalogueUseCase) {
        this.addVehicleUseCase = addVehicleUseCase;
        this.updateVehicleUseCase = updateVehicleUseCase;
        this.removeVehicleUseCase = removeVehicleUseCase;
        this.listCatalogueUseCase = listCatalogueUseCase;
    }

    @GetMapping("/ev-catalogue/list")
    @ResponseBody
    public List<Vehicle> listCatalogue() {
        return listCatalogueUseCase.listAll();
    }

    @PostMapping("/ev-catalogue/add_vehicle")
    public String addVehicle(
            @RequestParam(name = "vin") String vin,
            @RequestParam(name = "brand") String brand,
            @RequestParam(name = "model") String model,
            @RequestParam(name = "range_km") int rangeKm,
            @RequestParam(name = "vehicle_type") String vehicleType,
            @RequestParam(name = "consumption_kwh_per_100km") double consumptionKwhPer100km,
            @RequestParam(name = "battery_capacity_kwh") double batteryCapacityKwh,
            @RequestParam(name = "battery_voltage_v") double batteryVoltageV,
            @RequestParam(name = "battery_max_charge_speed_kw") double batteryMaxChargeSpeedKw,
            @RequestParam(name = "horse_power_hp") int horsePowerHp,
            @RequestParam(name = "acceleration_sec") double accelerationSec,
            @RequestParam(name = "top_speed_kmh") int topSpeedKmh,
            @RequestParam(name = "monthly_lease_price_cents") long monthlyLeasePriceCents,
            @RequestParam(name = "currency") String currency,
            @RequestParam(name = "initial_state_of_health") double initialStateOfHealth,
            @RequestParam(name = "initial_measured_range_km") int initialMeasuredRangeKm) {

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
                        BigDecimal.valueOf(batteryCapacityKwh),
                        BigDecimal.valueOf(batteryVoltageV),
                        BigDecimal.valueOf(batteryMaxChargeSpeedKw)
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
        return "redirect:/ev-catalogue/list";
    }

    @PostMapping("/ev-catalogue/update_vehicle")
    public String updateVehicle(
            @RequestParam(name = "vin") String vin,
            @RequestParam(name = "brand") String brand,
            @RequestParam(name = "model") String model,
            @RequestParam(name = "range_km") int rangeKm,
            @RequestParam(name = "vehicle_type") String vehicleType,
            @RequestParam(name = "consumption_kwh_per_100km") double consumptionKwhPer100km,
            @RequestParam(name = "battery_capacity_kwh") double batteryCapacityKwh,
            @RequestParam(name = "battery_voltage_v") double batteryVoltageV,
            @RequestParam(name = "battery_max_charge_speed_kw") double batteryMaxChargeSpeedKw,
            @RequestParam(name = "monthly_lease_price_cents") long monthlyLeasePriceCents,
            @RequestParam(name = "currency") String currency,
            @RequestParam(name = "new_state_of_health") double newStateOfHealth,
            @RequestParam(name = "new_measured_range_km") int newMeasuredRangeKm) {

        updateVehicleUseCase.update(
                VehicleId.of(vin),
                VehicleSpecification.of(
                        brand,
                        model,
                        Range.ofKm(rangeKm),
                        VehicleType.valueOf(vehicleType),
                        EnergyConsumption.of(BigDecimal.valueOf(consumptionKwhPer100km))
                ),
                BatterySpecification.of(
                        BigDecimal.valueOf(batteryCapacityKwh),
                        BigDecimal.valueOf(batteryVoltageV),
                        BigDecimal.valueOf(batteryMaxChargeSpeedKw)
                ),
                Amount.ofCents(monthlyLeasePriceCents, Currency.valueOf(currency)),
                Percentage.of(BigDecimal.valueOf(newStateOfHealth)),
                Range.ofKm(newMeasuredRangeKm)
        );
        return "redirect:/ev-catalogue/list";
    }

    @PostMapping("/ev-catalogue/remove_vehicle")
    public String removeVehicle(
            @RequestParam(name = "vin") String vin) {

        removeVehicleUseCase.remove(VehicleId.of(vin));
        return "redirect:/ev-catalogue/list";
    }
}
