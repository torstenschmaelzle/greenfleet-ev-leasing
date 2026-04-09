package io.carlease.sales.domain;

import org.jmolecules.ddd.annotation.Entity;
import org.jmolecules.ddd.annotation.Identity;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Entity
public class Contract {

    @Identity
    private final ContractNumber number;
	private final Customer lessee;
	private final Car car;
	private final Amount price;
	private record Calculation(LeaseTerm leaseTerm, Interest interest, Amount installment, Amount downPayment, Amount residualValue ) {}
    private Optional<Calculation> calculation;
    private Optional<SignDate> approvalDate;
	private Optional<SignDate> signatureDate;

	public Contract(ContractNumber number, Customer lessee, Car car, Amount price) {
        requireNonNull(number);
		requireNonNull(lessee);
		requireNonNull(car);
		requireNonNull(price);

        this.number = number;
		this.lessee = lessee;
		this.car = car;
		this.price = price;
		this.calculation = Optional.empty();
		this.approvalDate = Optional.empty();
		this.signatureDate = Optional.empty();
	}

	@Identity
	public ContractNumber number() {
        return this.number;
	}

	public Customer lessee() {
        return this.lessee;
	}

	public Car car() {
        return this.car;
	}

	public Amount price() {
		return this.price;
	}

	public boolean isCalculated() {
		return this.calculation.isPresent();
	}

	public void calculateInstallmentFor(LeaseTerm leaseTerm, Interest interest){

        Amount downPayment = Amount.ofCents(0, price.currency());
        Amount residualValue = Amount.ofCents(0, price.currency());

		calculateInstallmentFor(leaseTerm,interest,downPayment,residualValue );
	}

	public void calculateInstallmentFor(LeaseTerm leaseTerm, Interest interest, Amount downPayment, Amount residualValue) {

		requireNonNull(leaseTerm);
		requireNonNull(interest);
		assert !isApproved();

		double pmt = FinancialCalculator.pmt(
			leaseTerm.noOfMonths(),
			interest.perMonth(),
			-1 * price().amount(),
			residualValue.amount(),
			downPayment.amount());

		this.calculation = Optional.of(new Calculation(leaseTerm, interest, Amount.of(pmt, price.currency()), downPayment, residualValue));

		assert isCalculated();
	}

	//state soll auf signed wechseln
	public void sign(SignDate date) {
		requireNonNull(date);
		assert isCalculated();
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


    public LeaseTerm leaseTerm() {
    	assert isCalculated();
	    return this.calculation.get().leaseTerm();
    }

    public Interest interest() {
    	assert isCalculated();
	    return this.calculation.get().interest();
    }

	public Amount installment() {
		assert isCalculated();
		return this.calculation.get().installment();
	}

    public Amount downPayment() {
        assert isCalculated();
        return this.calculation.get().downPayment();
    }

    public Amount residualValue() {
        assert isCalculated();
        return this.calculation.get().residualValue();
    }

	public void approve(SignDate date) {
		requireNonNull(date);
        assert isCalculated();
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

	@Override
	public String toString() {
		return "Contract [number=" + number() + ", lessee=" + this.lessee + ", car=" + this.car
				+ ", price=" + this.price + ", signDate=" + this.approvalDate + ", signatureDate=" + this.signatureDate + "]";
	}

	@Override
	public final int hashCode() {
		return this.number.hashCode();
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		if (this.number == null) {
			if (other.number() != null)
				return false;
		} else if (!this.number.equals(other.number()))
			return false;
		return true;
	}

}
