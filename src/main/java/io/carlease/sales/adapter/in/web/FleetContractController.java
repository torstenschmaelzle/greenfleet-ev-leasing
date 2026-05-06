package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.in.CreateFleetContractUseCase.VehicleEntry;
import io.carlease.sales.application.port.in.ReleaseVehiclesUseCase;
import io.carlease.sales.application.port.in.ReserveVehiclesUseCase;
import io.carlease.sales.application.port.in.SignFleetContractUseCase;
import io.carlease.sales.application.port.in.ViewFleetContractUseCase;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.FleetContractNumber;
import io.carlease.sales.domain.SignDate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sales/fleet-contracts")
public class FleetContractController {

    private final CreateFleetContractUseCase createFleetContractUseCase;
    private final ViewFleetContractUseCase viewFleetContractUseCase;
    private final SignFleetContractUseCase signFleetContractUseCase;
    private final ReserveVehiclesUseCase reserveVehiclesUseCase;
    private final ReleaseVehiclesUseCase releaseVehiclesUseCase;

    public FleetContractController(CreateFleetContractUseCase createFleetContractUseCase,
                                   ViewFleetContractUseCase viewFleetContractUseCase,
                                   SignFleetContractUseCase signFleetContractUseCase,
                                   ReserveVehiclesUseCase reserveVehiclesUseCase,
                                   ReleaseVehiclesUseCase releaseVehiclesUseCase) {
        this.createFleetContractUseCase = createFleetContractUseCase;
        this.viewFleetContractUseCase = viewFleetContractUseCase;
        this.signFleetContractUseCase = signFleetContractUseCase;
        this.reserveVehiclesUseCase = reserveVehiclesUseCase;
        this.releaseVehiclesUseCase = releaseVehiclesUseCase;
    }

    @PostMapping
    public String create(@RequestBody CreateFleetContractRequest request) {
        FleetContractNumber number = createFleetContractUseCase.create(
                request.customerName(),
                request.customerCompany(),
                request.customerEmail(),
                request.leaseTermMonths(),
                request.bundleType(),
                request.vehicles()
        );
        return number.number();
    }

    @GetMapping("/{number}")
    public FleetContractResponse get(@PathVariable String number) {
        FleetContract contract = viewFleetContractUseCase.with(FleetContractNumber.of(number));
        return toResponse(contract);
    }

    @PostMapping("/reserve")
    public void reserve(@RequestBody ReserveRequest request) {
        reserveVehiclesUseCase.reserve(request.vins());
    }

    @DeleteMapping("/reserve")
    public void release(@RequestBody ReserveRequest request) {
        releaseVehiclesUseCase.release(request.vins());
    }

    @PostMapping("/{number}/sign")
    public void sign(@PathVariable String number) {
        signFleetContractUseCase.sign(FleetContractNumber.of(number), SignDate.of(LocalDate.now()));
    }

    private FleetContractResponse toResponse(FleetContract contract) {
        List<FleetContractResponse.VehicleResponse> vehicles = contract.vehicleContracts().stream()
                .map(vc -> new FleetContractResponse.VehicleResponse(
                        vc.car().car(),
                        vc.installment().amountInCents(),
                        vc.installment().currency().name()))
                .toList();

        return new FleetContractResponse(
                contract.number().number(),
                contract.customer().name(),
                contract.customer().company(),
                contract.customer().email(),
                contract.bundleType(),
                contract.leaseTerm().noOfMonths(),
                contract.isApproved(),
                contract.isSigned(),
                contract.totalMonthlyInstallment().amountInCents(),
                contract.totalMonthlyInstallment().currency().name(),
                vehicles
        );
    }

    public record ReserveRequest(List<String> vins) {}

    public record CreateFleetContractRequest(
            String customerName,
            String customerCompany,
            String customerEmail,
            int leaseTermMonths,
            String bundleType,
            List<VehicleEntry> vehicles
    ) {}

    public record FleetContractResponse(
            String number,
            String customerName,
            String customerCompany,
            String customerEmail,
            String bundleType,
            int leaseTermMonths,
            boolean approved,
            boolean signed,
            long totalMonthlyInstallmentCents,
            String currency,
            List<VehicleResponse> vehicles
    ) {
        public record VehicleResponse(String vin, long monthlyInstallmentCents, String currency) {}
    }
}
