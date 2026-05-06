package io.carlease.sales.adapter.out.persistence;

import io.carlease.sales.domain.*;

import java.util.List;
import java.util.Optional;

public class FleetContractMapper {

    public static FleetContractJpaEntity mapToJpaEntity(FleetContract fleetContract) {
        List<String> vehicleContractNumbers = fleetContract.vehicleContracts().stream()
                .map(vc -> vc.number().number())
                .toList();

        return new FleetContractJpaEntity(
                fleetContract.number().number(),
                fleetContract.customer().name(),
                fleetContract.customer().company(),
                fleetContract.customer().email(),
                fleetContract.leaseTerm().noOfMonths(),
                fleetContract.bundleType(),
                fleetContract.isApproved() ? fleetContract.approvalDate().date() : null,
                fleetContract.isSigned()   ? fleetContract.signatureDate().date() : null,
                vehicleContractNumbers
        );
    }

    public static FleetContract mapToDomain(FleetContractJpaEntity entity,
                                            List<VehicleContract> vehicleContracts) {
        return ContractFactory.restoreFleetContract(
                FleetContractNumber.of(entity.getNumber()),
                Customer.of(entity.getCustomerName(), entity.getCustomerCompany(), entity.getCustomerEmail()),
                LeaseTerm.ofMonths(entity.getLeaseTermMonths()),
                entity.getBundleType(),
                vehicleContracts,
                entity.getApprovalDate() != null
                        ? Optional.of(SignDate.of(entity.getApprovalDate()))
                        : Optional.empty(),
                entity.getSignatureDate() != null
                        ? Optional.of(SignDate.of(entity.getSignatureDate()))
                        : Optional.empty()
        );
    }
}
