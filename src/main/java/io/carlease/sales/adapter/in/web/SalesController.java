package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.FilloutContractUseCase;
import io.carlease.sales.application.port.in.ApproveContractUseCase;
import io.carlease.sales.application.port.in.ViewContractUseCase;
import io.carlease.sales.application.port.in.CalculateInstallmentUseCase;
import io.carlease.sales.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import io.carlease.sales.application.port.in.SignContractUseCase;

@Controller
public class SalesController {

	private final FilloutContractUseCase filloutContractUseCase;
	private final ViewContractUseCase viewContractUseCase;
	private final ApproveContractUseCase approveContractUseCase;
	private final SignContractUseCase signContractUseCase;
	private final CalculateInstallmentUseCase calculateInstallmentUseCase;

	public SalesController(
			FilloutContractUseCase filloutContractUseCase,
			ViewContractUseCase viewContractUseCase,
			ApproveContractUseCase approveContractUseCase,
			SignContractUseCase signContractUseCase) {
		this.filloutContractUseCase = filloutContractUseCase;
		this.viewContractUseCase = viewContractUseCase;
		this.approveContractUseCase = approveContractUseCase;
		this.signContractUseCase = signContractUseCase;
			CalculateInstallmentUseCase calculateInstallmentUseCase) {
		this.filloutContractUseCase = filloutContractUseCase;
		this.viewContractUseCase = viewContractUseCase;
		this.approveContractUseCase = approveContractUseCase;
		this.calculateInstallmentUseCase = calculateInstallmentUseCase;
	}

	@GetMapping("/sales/view_contract")
	public Contract viewContract(
			@RequestParam(name = "number", required = false) String number) {
		Contract contract = null;
		if (number != null) {
			contract = this.viewContractUseCase.with(ContractNumber.of(number));
		}
		return contract;
	}

	@PostMapping("/sales/fillout_contract")
	public String filloutContract(
			@RequestParam(name = "number") String number,
			@RequestParam(name = "lessee") String lesseeString,
			@RequestParam(name = "car") String carString,
			@RequestParam(name = "price_amount") int priceAmount,
			@RequestParam(name = "price_currency") String priceCurrency) {
		this.filloutContractUseCase.with(
				ContractNumber.of(number),
				Customer.of(lesseeString),
				Car.of(carString),
				Amount.of(priceAmount, Currency.valueOf(priceCurrency)));
		return "redirect:/sales/view_contract?number=" + number;
	}

	@PostMapping("/sales/approve_contract")
	public String approveContract(
			@RequestParam(name = "number") String number) {
		this.approveContractUseCase.with(
				ContractNumber.of(number),
				SignDate.of(LocalDate.now().getYear(),
						LocalDate.now().getMonth().getValue(),
						LocalDate.now().getDayOfMonth()));
		return "redirect:/sales/view_contract?number=" + number;
	}

	@PostMapping("/sales/sign_contract")
	public String signContract(
			@RequestParam(name = "number") String number)
	{
		this.signContractUseCase.with(
				ContractNumber.of(number),
				SignDate.of(LocalDate.now().getYear(),
						LocalDate.now().getMonth().getValue(),
						LocalDate.now().getDayOfMonth()));
		return "redirect:/sales/view_contract?number=" + number;
	}

}
	@PostMapping("/sales/calculate_installment")
	public String calculateInstallment(
			@RequestParam(name = "number") String number,
			@RequestParam(name = "lease_term") int leaseTerm,
			@RequestParam(name = "interest") double interest,
			@RequestParam(name = "down_payment") double downPayment,
			@RequestParam(name = "residual_value") double residualValue
	) {
		this.calculateInstallmentUseCase.calculateInstallment(
				number,
				leaseTerm,
				interest,
				downPayment,
				residualValue
		);
		return "redirect:/sales/view_contract?number=" + number;
	}
}
