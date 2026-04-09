package io.carlease.EV_Catalogue.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class VehicleId {

    private final String vin;

    private VehicleId(String vin) {
        this.vin = vin;
    }

    public static VehicleId of(String vin) {
        if (vin == null || vin.isBlank()) {
            throw new InvalidVehicleAttributeException("vin", "must not be null or blank");
        }
        if (vin.length() != 17) {
            throw new InvalidVehicleAttributeException("vin", "must be exactly 17 characters");
        }
        if (!vin.matches("[a-zA-Z0-9]+")) {
            throw new InvalidVehicleAttributeException("vin", "must contain only alphanumeric characters");
        }
        return new VehicleId(vin);
    }

    public String getVin() {
        return vin;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VehicleId other = (VehicleId) obj;
        return vin.equals(other.vin);
    }
    @Override
    public int hashCode() {
        return Objects.hash(vin);
    }

}
