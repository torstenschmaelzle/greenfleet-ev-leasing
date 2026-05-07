package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Entity
public class VehicleContract {

    @Identity
    private final ContractNumber number;
    private final Customer lessee;
    private final Vehicle vehicle;
    private final Amount price;
    private final Amount installment;
    private Optional<SignDate> signatureDate;

    VehicleContract(ContractNumber number, Customer lessee, Vehicle vehicle, Amount price,
                    Amount installment, Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(lessee);
        requireNonNull(vehicle);
        requireNonNull(price);
        requireNonNull(installment);

        this.number = number;
        this.lessee = lessee;
        this.vehicle = vehicle;
        this.price = price;
        this.installment = installment;
        this.signatureDate = signatureDate != null ? signatureDate : Optional.empty();
    }

    @Identity
    public ContractNumber number() {
        return this.number;
    }

    public Customer lessee() {
        return this.lessee;
    }

    public Vehicle vehicle() {
        return this.vehicle;
    }

    public Amount price() {
        return this.price;
    }

    public Amount installment() {
        return this.installment;
    }

    public void sign(SignDate date) {
        requireNonNull(date);
        if (isSigned()) throw new ContractAlreadySignedException(number.number());
        this.signatureDate = Optional.of(date);
    }

    public boolean isSigned() {
        return this.signatureDate.isPresent();
    }

    public SignDate signatureDate() {
        if (!isSigned()) throw new IllegalStateException("Contract " + number.number() + " is not yet signed");
        return this.signatureDate.get();
    }

    @Override
    public String toString() {
        return "VehicleContract [number=" + number() + ", lessee=" + this.lessee + ", vehicle=" + this.vehicle
                + ", price=" + this.price + ", installment=" + this.installment
                + ", signatureDate=" + this.signatureDate + "]";
    }

    @Override
    public final int hashCode() {
        return this.number.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        VehicleContract other = (VehicleContract) obj;
        if (this.number == null) {
            if (other.number() != null) return false;
        } else if (!this.number.equals(other.number())) return false;
        return true;
    }
}
