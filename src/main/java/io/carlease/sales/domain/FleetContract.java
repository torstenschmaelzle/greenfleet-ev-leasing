package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;
import org.jmolecules.ddd.annotation.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@AggregateRoot
public class FleetContract {

    @Identity
    private final FleetContractNumber number;
    private final Customer customer;
    private final LeaseTerm leaseTerm;
    private final String bundleType;
    private final List<VehicleContract> vehicleContracts;
    private Optional<SignDate> approvalDate;
    private Optional<SignDate> signatureDate;

    public FleetContract(FleetContractNumber number, Customer customer, LeaseTerm leaseTerm,
                         String bundleType, List<VehicleContract> vehicleContracts) {
        requireNonNull(number);
        requireNonNull(customer);
        requireNonNull(leaseTerm);
        requireNonNull(bundleType);
        requireNonNull(vehicleContracts);
        assert !vehicleContracts.isEmpty();

        this.number = number;
        this.customer = customer;
        this.leaseTerm = leaseTerm;
        this.bundleType = bundleType;
        this.vehicleContracts = new ArrayList<>(vehicleContracts);
        this.approvalDate = Optional.empty();
        this.signatureDate = Optional.empty();
    }

    public FleetContractNumber number() {
        return number;
    }

    public Customer customer() {
        return customer;
    }

    public LeaseTerm leaseTerm() {
        return leaseTerm;
    }

    public String bundleType() {
        return bundleType;
    }

    public List<VehicleContract> vehicleContracts() {
        return List.copyOf(vehicleContracts);
    }

    public Amount totalMonthlyInstallment() {
        assert vehicleContracts.stream().allMatch(VehicleContract::isCalculated);
        Amount total = Amount.ofCents(0, vehicleContracts.get(0).installment().currency());
        for (VehicleContract vc : vehicleContracts) {
            total = total.add(vc.installment());
        }
        return total;
    }

    public void approve(SignDate date) {
        requireNonNull(date);
        assert !isApproved();
        this.approvalDate = Optional.of(date);
        assert isApproved();
    }

    public boolean isApproved() {
        return this.approvalDate.isPresent();
    }

    public SignDate approvalDate() {
        assert isApproved();
        return this.approvalDate.get();
    }

    public void sign(SignDate date) {
        requireNonNull(date);
        assert isApproved();
        assert !isSigned();
        this.signatureDate = Optional.of(date);
        assert isSigned();
    }

    public boolean isSigned() {
        return this.signatureDate.isPresent();
    }

    public SignDate signatureDate() {
        assert isSigned();
        return this.signatureDate.get();
    }

    @Override
    public String toString() {
        return "FleetContract [number=" + number + ", customer=" + customer
                + ", leaseTerm=" + leaseTerm + ", bundleType=" + bundleType
                + ", vehicles=" + vehicleContracts.size()
                + ", approvalDate=" + approvalDate + ", signatureDate=" + signatureDate + "]";
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
