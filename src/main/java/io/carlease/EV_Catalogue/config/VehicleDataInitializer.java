package io.carlease.EV_Catalogue.config;

import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Profile("dev")
public class VehicleDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(VehicleDataInitializer.class);

    private final addVehicle addVehicleUseCase;
    private final VehiclePersistancePort vehiclePersistancePort;

    public VehicleDataInitializer(addVehicle addVehicleUseCase,
                                  VehiclePersistancePort vehiclePersistancePort) {
        this.addVehicleUseCase = addVehicleUseCase;
        this.vehiclePersistancePort = vehiclePersistancePort;
    }

    @Override
    public void run(String... args) {
        addIfNotExists("TSLAMDL3LR0000001", "Tesla",      "Model 3 Long Range",   460, VehicleType.SEDAN,   new BigDecimal("14.0"), new BigDecimal("82"),  new BigDecimal("350"), new BigDecimal("170"), 283,  new BigDecimal("5.1"), 225, 89900L,  Currency.CHF, new BigDecimal("97"), 450);
        addIfNotExists("TSLAMDLYAWD000001", "Tesla",      "Model Y AWD",          533, VehicleType.SUV,     new BigDecimal("16.0"), new BigDecimal("75"),  new BigDecimal("350"), new BigDecimal("250"), 299,  new BigDecimal("5.0"), 217, 79900L,  Currency.CHF, new BigDecimal("96"), 520);
        addIfNotExists("TSLAMDLSPLAID0001", "Tesla",      "Model S Plaid",        628, VehicleType.SEDAN,   new BigDecimal("17.3"), new BigDecimal("100"), new BigDecimal("400"), new BigDecimal("250"), 1020, new BigDecimal("2.1"), 322, 149900L, Currency.CHF, new BigDecimal("99"), 620);
        addIfNotExists("TSLAMDLXLNG000001", "Tesla",      "Model X Long Range",   560, VehicleType.SUV,     new BigDecimal("20.0"), new BigDecimal("100"), new BigDecimal("400"), new BigDecimal("250"), 670,  new BigDecimal("3.9"), 250, 129900L, Currency.CHF, new BigDecimal("98"), 555);
        addIfNotExists("BMWI4EDRIVE400001", "BMW",        "i4 eDrive40",          590, VehicleType.SEDAN,   new BigDecimal("16.5"), new BigDecimal("84"),  new BigDecimal("400"), new BigDecimal("200"), 340,  new BigDecimal("5.7"), 190, 75900L,  Currency.CHF, new BigDecimal("95"), 580);
        addIfNotExists("BMWIX3XDRIVE00001", "BMW",        "iX3 xDrive",           460, VehicleType.SUV,     new BigDecimal("17.8"), new BigDecimal("80"),  new BigDecimal("400"), new BigDecimal("150"), 286,  new BigDecimal("6.8"), 180, 72900L,  Currency.CHF, new BigDecimal("96"), 455);
        addIfNotExists("BMWIXXDRIVE500001", "BMW",        "iX xDrive50",          630, VehicleType.SUV,     new BigDecimal("19.5"), new BigDecimal("112"), new BigDecimal("400"), new BigDecimal("200"), 523,  new BigDecimal("3.8"), 250, 119900L, Currency.CHF, new BigDecimal("98"), 625);
        addIfNotExists("VWID4PRO4MOTION01", "Volkswagen", "ID.4 Pro 4Motion",     520, VehicleType.SUV,     new BigDecimal("18.5"), new BigDecimal("77"),  new BigDecimal("350"), new BigDecimal("135"), 299,  new BigDecimal("6.2"), 180, 59900L,  Currency.CHF, new BigDecimal("94"), 510);
        addIfNotExists("VWID3PRO58KWH0001", "Volkswagen", "ID.3 Pro",             426, VehicleType.COMPACT, new BigDecimal("15.4"), new BigDecimal("58"),  new BigDecimal("350"), new BigDecimal("100"), 150,  new BigDecimal("7.3"), 160, 44900L,  Currency.CHF, new BigDecimal("95"), 420);
        addIfNotExists("AUDIETRON55QUA001", "Audi",       "e-tron 55 quattro",    441, VehicleType.SUV,     new BigDecimal("23.5"), new BigDecimal("95"),  new BigDecimal("400"), new BigDecimal("150"), 408,  new BigDecimal("5.7"), 200, 99900L,  Currency.CHF, new BigDecimal("93"), 435);
        addIfNotExists("AUDIQ4ETRON400001", "Audi",       "Q4 e-tron 40",         520, VehicleType.SUV,     new BigDecimal("16.5"), new BigDecimal("82"),  new BigDecimal("350"), new BigDecimal("125"), 204,  new BigDecimal("8.5"), 160, 64900L,  Currency.CHF, new BigDecimal("96"), 515);
        addIfNotExists("MERCEDESEQC400001", "Mercedes",   "EQC 400 4MATIC",       400, VehicleType.SUV,     new BigDecimal("21.0"), new BigDecimal("85"),  new BigDecimal("400"), new BigDecimal("110"), 408,  new BigDecimal("5.1"), 180, 89900L,  Currency.CHF, new BigDecimal("92"), 395);
        addIfNotExists("MERCEDESEQS450001", "Mercedes",   "EQS 450+",             770, VehicleType.SEDAN,   new BigDecimal("19.0"), new BigDecimal("108"), new BigDecimal("400"), new BigDecimal("200"), 333,  new BigDecimal("6.2"), 210, 139900L, Currency.CHF, new BigDecimal("99"), 765);
        addIfNotExists("PORSCHETAYCAN4S01", "Porsche",    "Taycan 4S",            463, VehicleType.SEDAN,   new BigDecimal("23.0"), new BigDecimal("93"),  new BigDecimal("800"), new BigDecimal("270"), 571,  new BigDecimal("4.0"), 250, 149900L, Currency.CHF, new BigDecimal("97"), 458);
        addIfNotExists("HYUNDAIONIQ5RWD01", "Hyundai",    "IONIQ 5 RWD",          481, VehicleType.SUV,     new BigDecimal("16.0"), new BigDecimal("73"),  new BigDecimal("800"), new BigDecimal("230"), 170,  new BigDecimal("8.5"), 185, 54900L,  Currency.CHF, new BigDecimal("96"), 475);
        addIfNotExists("HYUNDAIONIQ6RWD01", "Hyundai",    "IONIQ 6 RWD",          614, VehicleType.SEDAN,   new BigDecimal("14.3"), new BigDecimal("77"),  new BigDecimal("800"), new BigDecimal("230"), 228,  new BigDecimal("7.4"), 185, 52900L,  Currency.CHF, new BigDecimal("98"), 608);
        addIfNotExists("KIAEV6GTLAWD00001", "Kia",        "EV6 GT-Line AWD",      506, VehicleType.ESTATE,  new BigDecimal("17.5"), new BigDecimal("77"),  new BigDecimal("800"), new BigDecimal("230"), 325,  new BigDecimal("5.1"), 185, 59900L,  Currency.CHF, new BigDecimal("95"), 500);
        addIfNotExists("VOLVOXC40RCHG0001", "Volvo",      "XC40 Recharge AWD",    418, VehicleType.SUV,     new BigDecimal("20.0"), new BigDecimal("69"),  new BigDecimal("400"), new BigDecimal("150"), 408,  new BigDecimal("4.9"), 180, 69900L,  Currency.CHF, new BigDecimal("94"), 412);
        addIfNotExists("POLESTAR2LNGRNG01", "Polestar",   "2 Long Range AWD",     476, VehicleType.ESTATE,  new BigDecimal("18.0"), new BigDecimal("78"),  new BigDecimal("400"), new BigDecimal("155"), 408,  new BigDecimal("4.5"), 205, 72900L,  Currency.CHF, new BigDecimal("96"), 470);
        addIfNotExists("RENAULTZOE50KWH01", "Renault",    "Zoe R135 Z.E.50",      395, VehicleType.COMPACT, new BigDecimal("15.0"), new BigDecimal("52"),  new BigDecimal("400"), new BigDecimal("50"),  135,  new BigDecimal("9.5"), 135, 34900L,  Currency.CHF, new BigDecimal("93"), 388);
    }

    private void addIfNotExists(String vin, String brand, String model,
                                int rangeKm, VehicleType vehicleType,
                                BigDecimal consumptionKwh,
                                BigDecimal batteryCapacityKwh, BigDecimal batteryVoltageV,
                                BigDecimal maxChargeSpeedKw,
                                int horsePowerHp, BigDecimal accelerationSec, int topSpeedKmh,
                                long monthlyLeasePriceCents, Currency currency,
                                BigDecimal initialSoh, int initialRangeKm) {

        VehicleId vehicleId = VehicleId.of(vin);
        if (vehiclePersistancePort.existsById(vehicleId)) {
            logger.debug("Vehicle already exists, skipping: {}", vin);
            return;
        }

        addVehicleUseCase.add(
                vehicleId,
                VehicleSpecification.of(brand, model, Range.ofKm(rangeKm), vehicleType,
                        EnergyConsumption.of(consumptionKwh)),
                BatterySpecification.of(batteryCapacityKwh, batteryVoltageV, maxChargeSpeedKw),
                Performance.of(horsePowerHp, accelerationSec, topSpeedKmh),
                Amount.ofCents(monthlyLeasePriceCents, currency),
                Percentage.of(initialSoh),
                Range.ofKm(initialRangeKm)
        );
        logger.info("Seeded vehicle: {} {}", brand, model);
    }
}
