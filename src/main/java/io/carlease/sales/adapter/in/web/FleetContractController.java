package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.in.CreateFleetContractUseCase.VehicleEntry;
import io.carlease.sales.domain.FleetContractNumber;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/fleet-contracts")
public class FleetContractController {

    private final CreateFleetContractUseCase createFleetContractUseCase;

    public FleetContractController(CreateFleetContractUseCase createFleetContractUseCase) {
        this.createFleetContractUseCase = createFleetContractUseCase;
    }

    @PostMapping
    public String create(@RequestBody CreateFleetContractRequest request) {
        FleetContractNumber number = createFleetContractUseCase.create(
                request.fleetContractNumber(),
                request.customer(),
                request.leaseTermMonths(),
                request.bundleType(),
                request.vehicles()
        );
        return number.number();
    }

    public record CreateFleetContractRequest(
            String fleetContractNumber,
            String customer,
            int leaseTermMonths,
            String bundleType,
            List<VehicleEntry> vehicles
    ) {}
}
