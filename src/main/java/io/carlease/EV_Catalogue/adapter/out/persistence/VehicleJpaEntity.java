package io.carlease.EV_Catalogue.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "vehicles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleJpaEntity {

    @Id
    private String vin;

    // VehicleSpecification
    private String brand;
    private String model;
    private Integer rangeKm;
    private String vehicleType;
    private BigDecimal consumptionKwhPer100km;

    // BatterySpecification
    private BigDecimal batteryCapacityKwh;
    private BigDecimal batteryVoltageV;
    private BigDecimal batteryMaxChargeSpeedKw;

    // Performance
    private Integer horsePowerHp;
    private BigDecimal accelerationSec;
    private Integer topSpeedKmh;

    // Amount (monthly lease price)
    private Long monthlyLeasePriceCents;
    private String monthlyLeasePriceCurrency;

    // VehicleStatus
    private String status;

    // ReservationLock (nullable)
    private String lockId;
    private Instant lockedAt;
    private Instant expiresAt;
    private Integer lockDurationHours;

    // BatteryHealthRecords
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "battery_health_records", joinColumns = @JoinColumn(name = "vin"))
    private List<BatteryHealthRecordEmbeddable> healthRecords;
}
