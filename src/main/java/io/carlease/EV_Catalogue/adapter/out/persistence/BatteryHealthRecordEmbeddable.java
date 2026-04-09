package io.carlease.EV_Catalogue.adapter.out.persistence;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatteryHealthRecordEmbeddable {

    private String recordId;
    private Instant measuredAt;
    private BigDecimal stateOfHealth;
    private Integer measuredRangeKm;
}
