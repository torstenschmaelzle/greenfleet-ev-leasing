package io.carlease.sales.domain;

import io.carlease.shared.domain.Amount;
import io.carlease.shared.domain.BundleType;
import io.carlease.shared.domain.LeaseTerm;
import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@AggregateRoot
public class FleetContract {

    @Identity
    private final ContractNumber number;
    private final Customer customer;
    private final LeaseTerm leaseTerm;
    private final BundleType bundleType;
    private final List<VehicleContract> vehicleContracts;
    private Optional<SignDate> signatureDate;

    FleetContract(ContractNumber number, Customer customer, LeaseTerm leaseTerm,
                  BundleType bundleType, List<VehicleContract> vehicleContracts,
                  Optional<SignDate> signatureDate) {
        requireNonNull(number);
        requireNonNull(customer);
        requireNonNull(leaseTerm);
        requireNonNull(bundleType);
        requireNonNull(vehicleContracts);
        if (vehicleContracts.isEmpty()) throw new InvalidFleetAttributeException("vehicleContracts", "fleet must contain at least one vehicle");

        this.number = number;
        this.customer = customer;
        this.leaseTerm = leaseTerm;
        this.bundleType = bundleType;
        this.vehicleContracts = new ArrayList<>(vehicleContracts);
        this.signatureDate = signatureDate != null ? signatureDate : Optional.empty();
    }

    public ContractNumber number() {
        return number;
    }

    public Customer customer() {
        return customer;
    }

    public LeaseTerm leaseTerm() {
        return leaseTerm;
    }

    public BundleType bundleType() {
        return bundleType;
    }

    public List<VehicleContract> vehicleContracts() {
        return List.copyOf(vehicleContracts);
    }

    public Amount totalMonthlyInstallment() {
        Amount total = Amount.ofCents(0, vehicleContracts.get(0).installment().getCurrency());
        for (VehicleContract vc : vehicleContracts) {
            total = total.add(vc.installment());
        }
        return total;
    }

    public void sign(SignDate date) {
        requireNonNull(date);
        if (isSigned()) throw new ContractAlreadySignedException(number.number());
        this.signatureDate = Optional.of(date);
        vehicleContracts.forEach(vc -> vc.sign(date));
    }

    public boolean isSigned() {
        return this.signatureDate.isPresent();
    }

    public SignDate signatureDate() {
        if (!isSigned()) throw new IllegalStateException("Fleet contract " + number.number() + " is not yet signed");
        return this.signatureDate.get();
    }

    @Override
    public String toString() {
        return "FleetContract [number=" + number + ", customer=" + customer
                + ", leaseTerm=" + leaseTerm + ", bundleType=" + bundleType
                + ", vehicles=" + vehicleContracts.size()
                + ", signatureDate=" + signatureDate + "]";
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
        FleetContract other = (FleetContract) obj;
        return this.number.equals(other.number);
    }
}
