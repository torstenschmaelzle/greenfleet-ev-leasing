package io.carlease.EV_Catalogue.adapter.out.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends CrudRepository<VehicleJpaEntity, String> {
    List<VehicleJpaEntity> findByStatus(String status);
}
