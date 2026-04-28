package io.carlease.fleet_consulting.domain;

import org.jmolecules.ddd.annotation.ValueObject;

import java.util.Objects;

@ValueObject
public class SuitabilityScore {

    private final int score;

    SuitabilityScore(int score) {
        this.score = score;
    }


    public int getScore() {
        return score;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SuitabilityScore other = (SuitabilityScore) obj;
        return score == other.score;
    }

    @Override
    public int hashCode() {
        return Objects.hash(score);
    }
}
