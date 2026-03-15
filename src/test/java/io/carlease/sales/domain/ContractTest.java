package io.carlease.sales.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import java.util.Optional;
class ContractTest {

	@Test
	void givenAFilledOutContract_whenCalculate_thenInstallmentIsX() {
		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when
		contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));

		// then
		assertThat(contract.isCalculated()).isTrue();
		assertThat(contract.leaseTerm()).isEqualTo(LeaseTerm.ofMonths(48));
		assertThat(contract.interest()).isEqualTo(Interest.of(3.7));
		assertThat(contract.installment()).isEqualTo(Amount.of(897.80, Currency.EUR));
	}

	@Test
	void givenAFilledOutContractWith0Interest_whenCalculate_thenInstallmentIsX() {
		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when
		contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(0));

		// then
		assertThat(contract.isCalculated()).isTrue();
		assertThat(contract.installment()).isEqualTo(Amount.of(833.33, Currency.EUR));
	}

	@Test
	void givenACalculatedContract_whenApprove_thenContractIsApproved() {
		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(20_000, Currency.EUR));
        contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));

		// when
		contract.approve(SignDate.of(2018, 12, 24));

		// then
		assertThat(contract.isApproved()).isTrue();
		assertThat(contract.approvalDate()).isEqualTo(SignDate.of(2018, 12, 24));
	}

	@Test
	void givenTwoContractsWithSameIdButDifferentFields_whenEquals_thenShouldReturnTrue() {
		// given
		var contract1 = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(40_000, Currency.EUR));
        var contract2 = new Contract(ContractNumber.of("4711"),
				Customer.of("Bob Myers"),
				Car.of("Volkswagen ID.3"),
				Amount.of(30_000, Currency.EUR));

		// when
		var equal = contract1.equals(contract2);

		// then
		assertThat(equal).isTrue();
	}

	@Test
	void givenTwoContractsWithDifferentIdButSameFields_whenEquals_thenShouldReturnFalse() {
		// given
		var contract1 = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(40_000, Currency.EUR));
        var contract2 = new Contract(ContractNumber.of("4712"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(40_000, Currency.EUR));

		// when
		var equal = contract1.equals(contract2);

		// then
		assertThat(equal).isFalse();
	}

	@Test
	void givenTwoContractsWithSameId_whenHashcode_thenShouldBeEqual() {
		// given
		var contract1 = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(40_000, Currency.EUR));
        var contract2 = new Contract(ContractNumber.of("4711"),
				Customer.of("Bob Myers"),
				Car.of("Volkswagen ID.3"),
				Amount.of(30_000, Currency.EUR));

		// when
		var hashcode1 = contract1.hashCode();
		var hashcode2 = contract2.hashCode();

		// then
		assertThat(hashcode1).isEqualTo(hashcode2);
	}
	// Given BDD Scenario
	@Test
	void givenAContractWithDownPaymentAndResidualValue_whenCalculate_thenInstallmentMatchesScenario() {

		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));
		// when
		contract.calculateInstallmentFor(
				LeaseTerm.ofMonths(48),
				Interest.of(3.7),
				5_000,
				10_000
		);
		// then
		assertThat(contract.isCalculated()).isTrue();
		assertThat(contract.installment()).isEqualTo(Amount.of(42.89, Currency.EUR));
	}
	@Test
	void whenCalculatedWithOldMethod_thenSameAsNewMethodWithZeroDownPaymentAndResidualValue() {
		// given
		var c1 = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		var c2 = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when
		c1.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));
		c2.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 0, 0);

		// then
		assertThat(c1.isCalculated()).isTrue();
		assertThat(c2.isCalculated()).isTrue();
		assertThat(c1.installment()).isEqualTo(c2.installment());
		assertThat(c1.leaseTerm()).isEqualTo(c2.leaseTerm());
		assertThat(c1.interest()).isEqualTo(c2.interest());
	}

	@Test
	void givenNegativeDownPayment_whenCalculate_thenThrowsIllegalArgumentException() {
		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when / then
		assertThatThrownBy(() ->
				contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), -1, 0)
		).isInstanceOf(IllegalArgumentException.class);
	}


	@Test
	void givenNegativeResidualValue_whenCalculate_thenThrowsIllegalArgumentException() {
		// given
		var contract = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when / then
		assertThatThrownBy(() ->
				contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 0, -1)
		).isInstanceOf(IllegalArgumentException.class);
	}
	@Test
	void whenDownPaymentIncreases_thenInstallmentDecreases() {
		// given
		var noDp = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		var withDp = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when
		noDp.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 0, 0);
		withDp.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 5_000, 0);

		// then
		assertThat(withDp.installment().amount()).isLessThan(noDp.installment().amount());
	}


	@Test
	void whenResidualValueIncreases_thenInstallmentDecreases() {
		// given
		var noRv = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		var withRv = new Contract(ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Volkswagen ID.3"),
				Amount.of(40_000, Currency.EUR));

		// when
		noRv.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 0, 0);
		withRv.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7), 0, 10_000);

		// then
		assertThat(withRv.installment().amount()).isLessThan(noRv.installment().amount());
	}

	@Test
	void givenAnApprovedContract_whenSign_thenContractIsSigned() {
		// given
		var contract = new Contract(
				ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(20_000, Currency.EUR));

		contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));
		contract.approve(SignDate.of(2018, 12, 24));

		// when
		contract.sign(SignDate.of(2018, 12, 25));

		// then
		assertThat(contract.isSigned()).isTrue();
		assertThat(contract.signatureDate()).isEqualTo(SignDate.of(2018, 12, 25));
	}

	@Test
	void givenAnApprovedContract_whenSign_thenApprovalDateIsStillPresent() {
		// given
		var contract = new Contract(
				ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(20_000, Currency.EUR));

		contract.calculateInstallmentFor(LeaseTerm.ofMonths(48), Interest.of(3.7));
		contract.approve(SignDate.of(2018, 12, 24));

		// when
		contract.sign(SignDate.of(2018, 12, 25));

		// then
		assertThat(contract.isApproved()).isTrue();
		assertThat(contract.approvalDate()).isEqualTo(SignDate.of(2018, 12, 24));
		assertThat(contract.isSigned()).isTrue();
	}

	@Test
	void givenASignedContractData_whenRestoreContract_thenContractIsSigned() {
		// when
		var contract = ContractFactory.restoreContract(
				ContractNumber.of("4711"),
				Customer.of("John Buyer"),
				Car.of("Mercedes Benz C-Class"),
				Amount.of(20_000, Currency.EUR),
				Optional.of(LeaseTerm.ofMonths(48)),
				Optional.of(Interest.of(3.7)),
				Optional.of(SignDate.of(2018, 12, 24)),
				Optional.of(SignDate.of(2018, 12, 25)),
				0.0,
				0.0
		);

		// then
		assertThat(contract.isCalculated()).isTrue();
		assertThat(contract.isApproved()).isTrue();
		assertThat(contract.isSigned()).isTrue();
		assertThat(contract.signatureDate()).isEqualTo(SignDate.of(2018, 12, 25));
	}


}
