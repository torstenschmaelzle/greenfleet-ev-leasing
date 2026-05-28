package io.carlease.sales.domain;

public class ReservationExpiredException extends ContractDomainException {

    public ReservationExpiredException(String vin) {
        super("The reservation for vehicle " + vin + " has expired. Please start a new fleet recommendation.");
    }
}
