package io.carlease.sales.adapter.out.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FleetContractRepository extends CrudRepository<FleetContractJpaEntity, String> {
    List<FleetContractJpaEntity> findByCustomerEmail(String customerEmail);
}
