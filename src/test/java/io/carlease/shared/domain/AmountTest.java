package io.carlease.shared.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountTest {

    @Test
    void givenSameCentsAndCurrency_whenEquals_thenTrue() {
        assertThat(Amount.ofCents(10045, Currency.EUR))
                .isEqualTo(Amount.ofCents(10045, Currency.EUR));
    }

    @Test
    void givenDifferentCents_whenEquals_thenFalse() {
        assertThat(Amount.ofCents(10045, Currency.EUR))
                .isNotEqualTo(Amount.ofCents(10046, Currency.EUR));
    }

    @Test
    void givenTwoAmounts_whenAdd_thenSumIsCorrect() {
        Amount result = Amount.ofCents(10000, Currency.EUR).add(Amount.ofCents(20000, Currency.EUR));
        assertThat(result).isEqualTo(Amount.ofCents(30000, Currency.EUR));
    }

    @Test
    void givenTwoAmounts_whenSubtract_thenDifferenceIsCorrect() {
        Amount result = Amount.ofCents(30000, Currency.EUR).subtract(Amount.ofCents(20000, Currency.EUR));
        assertThat(result).isEqualTo(Amount.ofCents(10000, Currency.EUR));
    }

    @Test
    void givenAmountInEUR_whenConvertToUSD_thenConvertedCorrectly() {
        Amount result = Amount.ofCents(10000, Currency.EUR).convertTo(Currency.USD, 1.03);
        assertThat(result).isEqualTo(Amount.ofCents(10300, Currency.USD));
    }

    @Test
    void givenNullCurrency_whenOfCents_thenThrows() {
        assertThatThrownBy(() -> Amount.ofCents(100, null))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void givenNegativeAmount_whenOfCents_thenThrows() {
        assertThatThrownBy(() -> Amount.ofCents(-1, Currency.CHF))
                .isInstanceOf(InvalidAmountException.class);
    }
}
