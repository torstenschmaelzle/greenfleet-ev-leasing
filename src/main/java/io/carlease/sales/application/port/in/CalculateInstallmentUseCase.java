package io.carlease.sales.application.port.in;

public interface CalculateInstallmentUseCase {

    void calculateInstallment(
            String contractNumber,
            int leaseTerm,
            double interest,
            double downPayment,
            double residualValue
    );
}