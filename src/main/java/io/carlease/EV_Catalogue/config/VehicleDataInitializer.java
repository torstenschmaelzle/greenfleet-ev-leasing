package io.carlease.EV_Catalogue.config;

import io.carlease.EV_Catalogue.application.port.in.addVehicle;
import io.carlease.EV_Catalogue.application.port.out.VehiclePersistancePort;
import io.carlease.EV_Catalogue.domain.*;
import io.carlease.shared.domain.*;
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
        // Tesla Model 3 Long Range — maxCharge: 170 kW, new price: 56990 CHF
        addIfNotExists("TSLAMDL3LR0000001", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("82"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 56990L, Currency.CHF, new BigDecimal("97"), 450);
        // Tesla Model Y AWD — maxCharge: 250 kW, new price: 54990 CHF
        addIfNotExists("TSLAMDLYAWD000001", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("75"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 54990L, Currency.CHF, new BigDecimal("96"), 520);
        // Tesla Model S Plaid — maxCharge: 250 kW, new price: 109990 CHF
        addIfNotExists("TSLAMDLSPLAID0001", "Tesla", "Model S Plaid", 628, VehicleType.SEDAN, new BigDecimal("17.3"), new BigDecimal("100"), new BigDecimal("400"), new BigDecimal("250"), 1020, new BigDecimal("2.1"), 322, 109990L, Currency.CHF, new BigDecimal("99"), 620);
        // Tesla Model X Long Range — maxCharge: 250 kW, new price: 104990 CHF
        addIfNotExists("TSLAMDLXLNG000001", "Tesla", "Model X Long Range", 560, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("100"), new BigDecimal("400"), new BigDecimal("250"), 670, new BigDecimal("3.9"), 250, 104990L, Currency.CHF, new BigDecimal("98"), 555);
        // BMW i4 eDrive40 — maxCharge: 200 kW, new price: 67900 CHF
        addIfNotExists("BMWI4EDRIVE400001", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("84"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 67900L, Currency.CHF, new BigDecimal("95"), 580);
        // BMW iX3 xDrive — maxCharge: 150 kW, new price: 69900 CHF
        addIfNotExists("BMWIX3XDRIVE00001", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 69900L, Currency.CHF, new BigDecimal("96"), 455);
        // BMW iX xDrive50 — maxCharge: 200 kW, new price: 109900 CHF
        addIfNotExists("BMWIXXDRIVE500001", "BMW", "iX xDrive50", 630, VehicleType.SUV, new BigDecimal("19.5"), new BigDecimal("112"), new BigDecimal("400"), new BigDecimal("200"), 523, new BigDecimal("3.8"), 250, 109900L, Currency.CHF, new BigDecimal("98"), 625);
        // Volkswagen ID.4 Pro 4Motion — maxCharge: 135 kW, new price: 54990 CHF
        addIfNotExists("VWID4PRO4MOTION01", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("77"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 54990L, Currency.CHF, new BigDecimal("94"), 510);
        // Volkswagen ID.3 Pro — maxCharge: 100 kW, new price: 39990 CHF
        addIfNotExists("VWID3PRO58KWH0001", "Volkswagen", "ID.3 Pro", 426, VehicleType.COMPACT, new BigDecimal("15.4"), new BigDecimal("58"), new BigDecimal("350"), new BigDecimal("100"), 150, new BigDecimal("7.3"), 160, 39990L, Currency.CHF, new BigDecimal("95"), 420);
        // Audi e-tron 55 quattro — maxCharge: 150 kW, new price: 84900 CHF
        addIfNotExists("AUDIETRON55QUA001", "Audi", "e-tron 55 quattro", 441, VehicleType.SUV, new BigDecimal("23.5"), new BigDecimal("95"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("5.7"), 200, 84900L, Currency.CHF, new BigDecimal("93"), 435);
        // Audi Q4 e-tron 40 — maxCharge: 125 kW, new price: 54900 CHF
        addIfNotExists("AUDIQ4ETRON400001", "Audi", "Q4 e-tron 40", 520, VehicleType.SUV, new BigDecimal("16.5"), new BigDecimal("82"), new BigDecimal("350"), new BigDecimal("125"), 204, new BigDecimal("8.5"), 160, 54900L, Currency.CHF, new BigDecimal("96"), 515);
        // Mercedes EQC 400 4MATIC — maxCharge: 110 kW, new price: 84900 CHF
        addIfNotExists("MERCEDESEQC400001", "Mercedes", "EQC 400 4MATIC", 400, VehicleType.SUV, new BigDecimal("21.0"), new BigDecimal("85"), new BigDecimal("400"), new BigDecimal("110"), 408, new BigDecimal("5.1"), 180, 84900L, Currency.CHF, new BigDecimal("92"), 395);
        // Mercedes EQS 450+ — maxCharge: 200 kW, new price: 114900 CHF
        addIfNotExists("MERCEDESEQS450001", "Mercedes", "EQS 450+", 770, VehicleType.SEDAN, new BigDecimal("19.0"), new BigDecimal("108"), new BigDecimal("400"), new BigDecimal("200"), 333, new BigDecimal("6.2"), 210, 114900L, Currency.CHF, new BigDecimal("99"), 765);
        // Porsche Taycan 4S — maxCharge: 270 kW, new price: 129900 CHF
        addIfNotExists("PORSCHETAYCAN4S01", "Porsche", "Taycan 4S", 463, VehicleType.SEDAN, new BigDecimal("23.0"), new BigDecimal("93"), new BigDecimal("800"), new BigDecimal("270"), 571, new BigDecimal("4.0"), 250, 129900L, Currency.CHF, new BigDecimal("97"), 458);
        // Hyundai IONIQ 5 RWD — maxCharge: 230 kW, new price: 49990 CHF
        addIfNotExists("HYUNDAIONIQ5RWD01", "Hyundai", "IONIQ 5 RWD", 481, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("73"), new BigDecimal("800"), new BigDecimal("230"), 170, new BigDecimal("8.5"), 185, 49990L, Currency.CHF, new BigDecimal("96"), 475);
        // Hyundai IONIQ 6 RWD — maxCharge: 230 kW, new price: 47990 CHF
        addIfNotExists("HYUNDAIONIQ6RWD01", "Hyundai", "IONIQ 6 RWD", 614, VehicleType.SEDAN, new BigDecimal("14.3"), new BigDecimal("77"), new BigDecimal("800"), new BigDecimal("230"), 228, new BigDecimal("7.4"), 185, 47990L, Currency.CHF, new BigDecimal("98"), 608);
        // Kia EV6 GT-Line AWD — maxCharge: 230 kW, new price: 56990 CHF
        addIfNotExists("KIAEV6GTLAWD00001", "Kia", "EV6 GT-Line AWD", 506, VehicleType.ESTATE, new BigDecimal("17.5"), new BigDecimal("77"), new BigDecimal("800"), new BigDecimal("230"), 325, new BigDecimal("5.1"), 185, 56990L, Currency.CHF, new BigDecimal("95"), 500);
        // Volvo XC40 Recharge AWD — maxCharge: 150 kW, new price: 63900 CHF
        addIfNotExists("VOLVOXC40RCHG0001", "Volvo", "XC40 Recharge AWD", 418, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("69"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("4.9"), 180, 63900L, Currency.CHF, new BigDecimal("94"), 412);
        // Polestar 2 Long Range AWD — maxCharge: 155 kW, new price: 57900 CHF
        addIfNotExists("POLESTAR2LNGRNG01", "Polestar", "2 Long Range AWD", 476, VehicleType.ESTATE, new BigDecimal("18.0"), new BigDecimal("78"), new BigDecimal("400"), new BigDecimal("155"), 408, new BigDecimal("4.5"), 205, 57900L, Currency.CHF, new BigDecimal("96"), 470);
        // Renault Zoe R135 Z.E.50 — maxCharge: 50 kW, new price: 32990 CHF
        addIfNotExists("RENAULTZOE50KWH01", "Renault", "Zoe R135 Z.E.50", 395, VehicleType.COMPACT, new BigDecimal("15.0"), new BigDecimal("52"), new BigDecimal("400"), new BigDecimal("50"), 135, new BigDecimal("9.5"), 135, 32990L, Currency.CHF, new BigDecimal("93"), 388);

        addIfNotExists("TSLAMDL3LR0000002", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("79"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 52990L, Currency.CHF, new BigDecimal("91"), 418);
        addIfNotExists("TSLAMDLYAWD000002", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("72"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 50990L, Currency.CHF, new BigDecimal("88"), 469);
        addIfNotExists("BMWI4EDRIVE400002", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("81"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 62900L, Currency.CHF, new BigDecimal("89"), 525);
        addIfNotExists("BMWIX3XDRIVE00002", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("77"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 62900L, Currency.CHF, new BigDecimal("87"), 400);
        addIfNotExists("VWID4PRO4MOTION02", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("74"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 51990L, Currency.CHF, new BigDecimal("90"), 468);
        addIfNotExists("HYUNDAIONIQ5RWD02", "Hyundai", "IONIQ 5 RWD", 481, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("71"), new BigDecimal("800"), new BigDecimal("230"), 170, new BigDecimal("8.5"), 185, 45990L, Currency.CHF, new BigDecimal("92"), 443);

        // Tesla Model 3 Long Range - weitere Einheiten
        addIfNotExists("TSLAMDL3LR0000003", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("80"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 51990L, Currency.CHF, new BigDecimal("89"), 410);
        addIfNotExists("TSLAMDL3LR0000004", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("78"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 49990L, Currency.CHF, new BigDecimal("86"), 395);
        addIfNotExists("TSLAMDL3LR0000005", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("81"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 54990L, Currency.CHF, new BigDecimal("93"), 428);
        addIfNotExists("TSLAMDL3LR0000006", "Tesla", "Model 3 Long Range", 460, VehicleType.SEDAN, new BigDecimal("14.0"), new BigDecimal("77"), new BigDecimal("350"), new BigDecimal("170"), 283, new BigDecimal("5.1"), 225, 48990L, Currency.CHF, new BigDecimal("85"), 391);

        // Tesla Model Y AWD - weitere Einheiten
        addIfNotExists("TSLAMDLYAWD000003", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("73"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 51990L, Currency.CHF, new BigDecimal("90"), 480);
        addIfNotExists("TSLAMDLYAWD000004", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("71"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 49990L, Currency.CHF, new BigDecimal("87"), 464);
        addIfNotExists("TSLAMDLYAWD000005", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("74"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 52990L, Currency.CHF, new BigDecimal("93"), 496);
        addIfNotExists("TSLAMDLYAWD000006", "Tesla", "Model Y AWD", 533, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("70"), new BigDecimal("350"), new BigDecimal("250"), 299, new BigDecimal("5.0"), 217, 47990L, Currency.CHF, new BigDecimal("85"), 453);

        // Tesla Model S Plaid - weitere Einheiten
        addIfNotExists("TSLAMDLSPLAID0002", "Tesla", "Model S Plaid", 628, VehicleType.SEDAN, new BigDecimal("17.3"), new BigDecimal("97"), new BigDecimal("400"), new BigDecimal("250"), 1020, new BigDecimal("2.1"), 322, 103990L, Currency.CHF, new BigDecimal("94"), 592);
        addIfNotExists("TSLAMDLSPLAID0003", "Tesla", "Model S Plaid", 628, VehicleType.SEDAN, new BigDecimal("17.3"), new BigDecimal("95"), new BigDecimal("400"), new BigDecimal("250"), 1020, new BigDecimal("2.1"), 322, 99990L, Currency.CHF, new BigDecimal("91"), 571);

        // Tesla Model X Long Range - weitere Einheiten
        addIfNotExists("TSLAMDLXLNG000002", "Tesla", "Model X Long Range", 560, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("97"), new BigDecimal("400"), new BigDecimal("250"), 670, new BigDecimal("3.9"), 250, 97990L, Currency.CHF, new BigDecimal("93"), 521);
        addIfNotExists("TSLAMDLXLNG000003", "Tesla", "Model X Long Range", 560, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("95"), new BigDecimal("400"), new BigDecimal("250"), 670, new BigDecimal("3.9"), 250, 94990L, Currency.CHF, new BigDecimal("90"), 504);

        // BMW i4 eDrive40 - weitere Einheiten
        addIfNotExists("BMWI4EDRIVE400003", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("82"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 64900L, Currency.CHF, new BigDecimal("92"), 543);
        addIfNotExists("BMWI4EDRIVE400004", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("80"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 61900L, Currency.CHF, new BigDecimal("88"), 519);
        addIfNotExists("BMWI4EDRIVE400005", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("83"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 65900L, Currency.CHF, new BigDecimal("94"), 555);
        addIfNotExists("BMWI4EDRIVE400006", "BMW", "i4 eDrive40", 590, VehicleType.SEDAN, new BigDecimal("16.5"), new BigDecimal("79"), new BigDecimal("400"), new BigDecimal("200"), 340, new BigDecimal("5.7"), 190, 59900L, Currency.CHF, new BigDecimal("86"), 507);

        // BMW iX3 xDrive - weitere Einheiten
        addIfNotExists("BMWIX3XDRIVE00003", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("78"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 64900L, Currency.CHF, new BigDecimal("91"), 419);
        addIfNotExists("BMWIX3XDRIVE00004", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("76"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 61900L, Currency.CHF, new BigDecimal("88"), 405);
        addIfNotExists("BMWIX3XDRIVE00005", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("79"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 66900L, Currency.CHF, new BigDecimal("93"), 428);
        addIfNotExists("BMWIX3XDRIVE00006", "BMW", "iX3 xDrive", 460, VehicleType.SUV, new BigDecimal("17.8"), new BigDecimal("75"), new BigDecimal("400"), new BigDecimal("150"), 286, new BigDecimal("6.8"), 180, 59900L, Currency.CHF, new BigDecimal("86"), 396);

        // BMW iX xDrive50 - weitere Einheiten
        addIfNotExists("BMWIXXDRIVE500002", "BMW", "iX xDrive50", 630, VehicleType.SUV, new BigDecimal("19.5"), new BigDecimal("109"), new BigDecimal("400"), new BigDecimal("200"), 523, new BigDecimal("3.8"), 250, 102900L, Currency.CHF, new BigDecimal("93"), 586);
        addIfNotExists("BMWIXXDRIVE500003", "BMW", "iX xDrive50", 630, VehicleType.SUV, new BigDecimal("19.5"), new BigDecimal("107"), new BigDecimal("400"), new BigDecimal("200"), 523, new BigDecimal("3.8"), 250, 98900L, Currency.CHF, new BigDecimal("90"), 567);

        // Volkswagen ID.4 Pro 4Motion - weitere Einheiten
        addIfNotExists("VWID4PRO4MOTION03", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("75"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 51990L, Currency.CHF, new BigDecimal("90"), 468);
        addIfNotExists("VWID4PRO4MOTION04", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("73"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 49990L, Currency.CHF, new BigDecimal("87"), 452);
        addIfNotExists("VWID4PRO4MOTION05", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("76"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 52990L, Currency.CHF, new BigDecimal("92"), 478);
        addIfNotExists("VWID4PRO4MOTION06", "Volkswagen", "ID.4 Pro 4Motion", 520, VehicleType.SUV, new BigDecimal("18.5"), new BigDecimal("72"), new BigDecimal("350"), new BigDecimal("135"), 299, new BigDecimal("6.2"), 180, 47990L, Currency.CHF, new BigDecimal("85"), 442);

        // Volkswagen ID.3 Pro - weitere Einheiten
        addIfNotExists("VWID3PRO58KWH0002", "Volkswagen", "ID.3 Pro", 426, VehicleType.COMPACT, new BigDecimal("15.4"), new BigDecimal("56"), new BigDecimal("350"), new BigDecimal("100"), 150, new BigDecimal("7.3"), 160, 36990L, Currency.CHF, new BigDecimal("91"), 387);
        addIfNotExists("VWID3PRO58KWH0003", "Volkswagen", "ID.3 Pro", 426, VehicleType.COMPACT, new BigDecimal("15.4"), new BigDecimal("55"), new BigDecimal("350"), new BigDecimal("100"), 150, new BigDecimal("7.3"), 160, 34990L, Currency.CHF, new BigDecimal("88"), 375);

        // Audi e-tron 55 quattro - weitere Einheiten
        addIfNotExists("AUDIETRON55QUA002", "Audi", "e-tron 55 quattro", 441, VehicleType.SUV, new BigDecimal("23.5"), new BigDecimal("92"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("5.7"), 200, 78900L, Currency.CHF, new BigDecimal("89"), 393);
        addIfNotExists("AUDIETRON55QUA003", "Audi", "e-tron 55 quattro", 441, VehicleType.SUV, new BigDecimal("23.5"), new BigDecimal("90"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("5.7"), 200, 74900L, Currency.CHF, new BigDecimal("86"), 379);

        // Audi Q4 e-tron 40 - weitere Einheiten
        addIfNotExists("AUDIQ4ETRON400002", "Audi", "Q4 e-tron 40", 520, VehicleType.SUV, new BigDecimal("16.5"), new BigDecimal("80"), new BigDecimal("350"), new BigDecimal("125"), 204, new BigDecimal("8.5"), 160, 50900L, Currency.CHF, new BigDecimal("91"), 473);
        addIfNotExists("AUDIQ4ETRON400003", "Audi", "Q4 e-tron 40", 520, VehicleType.SUV, new BigDecimal("16.5"), new BigDecimal("78"), new BigDecimal("350"), new BigDecimal("125"), 204, new BigDecimal("8.5"), 160, 48900L, Currency.CHF, new BigDecimal("88"), 458);

        // Mercedes EQC 400 4MATIC - weitere Einheiten
        addIfNotExists("MERCEDESEQC400002", "Mercedes", "EQC 400 4MATIC", 400, VehicleType.SUV, new BigDecimal("21.0"), new BigDecimal("83"), new BigDecimal("400"), new BigDecimal("110"), 408, new BigDecimal("5.1"), 180, 78900L, Currency.CHF, new BigDecimal("88"), 352);
        addIfNotExists("MERCEDESEQC400003", "Mercedes", "EQC 400 4MATIC", 400, VehicleType.SUV, new BigDecimal("21.0"), new BigDecimal("81"), new BigDecimal("400"), new BigDecimal("110"), 408, new BigDecimal("5.1"), 180, 74900L, Currency.CHF, new BigDecimal("85"), 340);

        // Mercedes EQS 450+ - weitere Einheiten
        addIfNotExists("MERCEDESEQS450002", "Mercedes", "EQS 450+", 770, VehicleType.SEDAN, new BigDecimal("19.0"), new BigDecimal("105"), new BigDecimal("400"), new BigDecimal("200"), 333, new BigDecimal("6.2"), 210, 107900L, Currency.CHF, new BigDecimal("94"), 724);
        addIfNotExists("MERCEDESEQS450003", "Mercedes", "EQS 450+", 770, VehicleType.SEDAN, new BigDecimal("19.0"), new BigDecimal("103"), new BigDecimal("400"), new BigDecimal("200"), 333, new BigDecimal("6.2"), 210, 104900L, Currency.CHF, new BigDecimal("91"), 700);

        // Porsche Taycan 4S - weitere Einheiten
        addIfNotExists("PORSCHETAYCAN4S02", "Porsche", "Taycan 4S", 463, VehicleType.SEDAN, new BigDecimal("23.0"), new BigDecimal("91"), new BigDecimal("800"), new BigDecimal("270"), 571, new BigDecimal("4.0"), 250, 119900L, Currency.CHF, new BigDecimal("92"), 426);
        addIfNotExists("PORSCHETAYCAN4S03", "Porsche", "Taycan 4S", 463, VehicleType.SEDAN, new BigDecimal("23.0"), new BigDecimal("89"), new BigDecimal("800"), new BigDecimal("270"), 571, new BigDecimal("4.0"), 250, 112900L, Currency.CHF, new BigDecimal("88"), 407);

        // Hyundai IONIQ 5 RWD - weitere Einheiten
        addIfNotExists("HYUNDAIONIQ5RWD03", "Hyundai", "IONIQ 5 RWD", 481, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("71"), new BigDecimal("800"), new BigDecimal("230"), 170, new BigDecimal("8.5"), 185, 45990L, Currency.CHF, new BigDecimal("91"), 437);
        addIfNotExists("HYUNDAIONIQ5RWD04", "Hyundai", "IONIQ 5 RWD", 481, VehicleType.SUV, new BigDecimal("16.0"), new BigDecimal("69"), new BigDecimal("800"), new BigDecimal("230"), 170, new BigDecimal("8.5"), 185, 43990L, Currency.CHF, new BigDecimal("87"), 418);

        // Hyundai IONIQ 6 RWD - weitere Einheiten
        addIfNotExists("HYUNDAIONIQ6RWD02", "Hyundai", "IONIQ 6 RWD", 614, VehicleType.SEDAN, new BigDecimal("14.3"), new BigDecimal("75"), new BigDecimal("800"), new BigDecimal("230"), 228, new BigDecimal("7.4"), 185, 44990L, Currency.CHF, new BigDecimal("93"), 571);
        addIfNotExists("HYUNDAIONIQ6RWD03", "Hyundai", "IONIQ 6 RWD", 614, VehicleType.SEDAN, new BigDecimal("14.3"), new BigDecimal("73"), new BigDecimal("800"), new BigDecimal("230"), 228, new BigDecimal("7.4"), 185, 42990L, Currency.CHF, new BigDecimal("89"), 547);

        // Kia EV6 GT-Line AWD - weitere Einheiten
        addIfNotExists("KIAEV6GTLAWD00002", "Kia", "EV6 GT-Line AWD", 506, VehicleType.ESTATE, new BigDecimal("17.5"), new BigDecimal("75"), new BigDecimal("800"), new BigDecimal("230"), 325, new BigDecimal("5.1"), 185, 52990L, Currency.CHF, new BigDecimal("91"), 460);
        addIfNotExists("KIAEV6GTLAWD00003", "Kia", "EV6 GT-Line AWD", 506, VehicleType.ESTATE, new BigDecimal("17.5"), new BigDecimal("73"), new BigDecimal("800"), new BigDecimal("230"), 325, new BigDecimal("5.1"), 185, 49990L, Currency.CHF, new BigDecimal("87"), 440);

        // Volvo XC40 Recharge AWD - weitere Einheiten
        addIfNotExists("VOLVOXC40RCHG0002", "Volvo", "XC40 Recharge AWD", 418, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("67"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("4.9"), 180, 58900L, Currency.CHF, new BigDecimal("90"), 376);
        addIfNotExists("VOLVOXC40RCHG0003", "Volvo", "XC40 Recharge AWD", 418, VehicleType.SUV, new BigDecimal("20.0"), new BigDecimal("65"), new BigDecimal("400"), new BigDecimal("150"), 408, new BigDecimal("4.9"), 180, 55900L, Currency.CHF, new BigDecimal("87"), 364);

        // Polestar 2 Long Range AWD - weitere Einheiten
        addIfNotExists("POLESTAR2LNGRNG02", "Polestar", "2 Long Range AWD", 476, VehicleType.ESTATE, new BigDecimal("18.0"), new BigDecimal("76"), new BigDecimal("400"), new BigDecimal("155"), 408, new BigDecimal("4.5"), 205, 53900L, Currency.CHF, new BigDecimal("91"), 433);
        addIfNotExists("POLESTAR2LNGRNG03", "Polestar", "2 Long Range AWD", 476, VehicleType.ESTATE, new BigDecimal("18.0"), new BigDecimal("74"), new BigDecimal("400"), new BigDecimal("155"), 408, new BigDecimal("4.5"), 205, 50900L, Currency.CHF, new BigDecimal("87"), 414);

        // Renault Zoe R135 Z.E.50 - weitere Einheiten
        addIfNotExists("RENAULTZOE50KWH02", "Renault", "Zoe R135 Z.E.50", 395, VehicleType.COMPACT, new BigDecimal("15.0"), new BigDecimal("50"), new BigDecimal("400"), new BigDecimal("50"), 135, new BigDecimal("9.5"), 135, 29990L, Currency.CHF, new BigDecimal("89"), 352);
        addIfNotExists("RENAULTZOE50KWH03", "Renault", "Zoe R135 Z.E.50", 395, VehicleType.COMPACT, new BigDecimal("15.0"), new BigDecimal("49"), new BigDecimal("400"), new BigDecimal("50"), 135, new BigDecimal("9.5"), 135, 28990L, Currency.CHF, new BigDecimal("86"), 340);

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
