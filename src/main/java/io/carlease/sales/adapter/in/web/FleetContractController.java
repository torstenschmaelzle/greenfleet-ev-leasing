package io.carlease.sales.adapter.in.web;

import io.carlease.sales.application.port.in.CreateFleetContractUseCase;
import io.carlease.sales.application.port.in.CreateFleetContractUseCase.VehicleEntry;
import io.carlease.sales.application.port.in.ReleaseVehiclesUseCase;
import io.carlease.sales.application.port.in.ReserveVehiclesUseCase;
import io.carlease.sales.application.port.in.SignFleetContractUseCase;
import io.carlease.sales.application.port.in.ViewFleetContractUseCase;
import io.carlease.sales.domain.FleetContract;
import io.carlease.sales.domain.ContractNumber;
import io.carlease.sales.domain.SignDate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
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
    public String create(@Valid @RequestBody CreateFleetContractRequest request) {
        ContractNumber number = createFleetContractUseCase.create(
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
        FleetContract contract = viewFleetContractUseCase.with(ContractNumber.of(number));
        return toResponse(contract);
    }

    @PostMapping("/reserve")
    public void reserve(@Valid @RequestBody ReserveRequest request) {
        reserveVehiclesUseCase.reserve(request.vins());
    }

    @DeleteMapping("/reserve")
    public void release(@Valid @RequestBody ReserveRequest request) {
        releaseVehiclesUseCase.release(request.vins());
    }

    @PostMapping("/{number}/sign")
    public void sign(@PathVariable String number) {
        signFleetContractUseCase.sign(ContractNumber.of(number), SignDate.of(LocalDate.now()));
    }

    private FleetContractResponse toResponse(FleetContract contract) {
        List<FleetContractResponse.VehicleResponse> vehicles = contract.vehicleContracts().stream()
                .map(vc -> new FleetContractResponse.VehicleResponse(
                        vc.vehicle().vin(),
                        vc.installment().getAmountInCents(),
                        vc.installment().getCurrency().name()))
                .toList();

        return new FleetContractResponse(
                contract.number().number(),
                contract.customer().name(),
                contract.customer().company(),
                contract.customer().email(),
                contract.bundleType(),
                contract.leaseTerm().getMonths(),
                contract.isSigned(),
                contract.totalMonthlyInstallment().getAmountInCents(),
                contract.totalMonthlyInstallment().getCurrency().name(),
                vehicles
        );
    }

    public record ReserveRequest(
            @NotEmpty(message = "vins must not be empty")
            List<String> vins
    ) {}

    public record CreateFleetContractRequest(
            @NotBlank(message = "customerName must not be blank")
            @Pattern(regexp = "^\\p{L}+(\\s\\p{L}+)*$",
                     message = "customerName must contain only letters and spaces")
            String customerName,

            @NotBlank(message = "customerCompany must not be blank")
            @Pattern(regexp = "^[\\p{L}0-9\\s&.,'\\-]+$",
                     message = "customerCompany must contain only letters, digits, spaces or & . , ' -")
            String customerCompany,

            @NotBlank(message = "customerEmail must not be blank")
            @Email(message = "customerEmail must be a valid email address")
            String customerEmail,

            @Min(value = 1, message = "leaseTermMonths must be at least 1")
            int leaseTermMonths,

            @NotBlank(message = "bundleType must not be blank")
            String bundleType,

            @NotEmpty(message = "vehicles must not be empty")
            @Valid
            List<VehicleEntry> vehicles
    ) {}

    public record FleetContractResponse(
            String number,
            String customerName,
            String customerCompany,
            String customerEmail,
            String bundleType,
            int leaseTermMonths,
            boolean signed,
            long totalMonthlyInstallmentCents,
            String currency,
            List<VehicleResponse> vehicles
    ) {
        public record VehicleResponse(String vin, long monthlyInstallmentCents, String currency) {}
    }
}
