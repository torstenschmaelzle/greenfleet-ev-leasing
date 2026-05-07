package io.carlease.sales.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "fleet_contracts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FleetContractJpaEntity {

    @Id
    private String number;

    private String customerName;

    private String customerCompany;

    private String customerEmail;

    private int leaseTermMonths;

    private String bundleType;

    private LocalDate signatureDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "fleet_contract_vehicle_numbers",
            joinColumns = @JoinColumn(name = "fleet_contract_number"))
    @Column(name = "vehicle_contract_number")
    private List<String> vehicleContractNumbers;
}
