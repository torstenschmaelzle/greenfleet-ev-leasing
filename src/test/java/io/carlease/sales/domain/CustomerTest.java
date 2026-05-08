package io.carlease.sales.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    void givenAStringWithOnlyLetters_whenIsValidName_thenTrue() {
        assertThat(Customer.isValidName("John")).isTrue();
    }

    @Test
    void givenAStringWithNonStandardLatinCharacter_whenIsValidName_thenTrue() {
        assertThat(Customer.isValidName("Björn")).isTrue();
    }

    @Test
    void givenAStringWithOnlyLettersAndSpace_whenIsValidName_thenTrue() {
        assertThat(Customer.isValidName("John Buyer")).isTrue();
    }

    @Test
    void givenAStringWithNonStandardLatinCharacterAndSpace_whenIsValidName_thenTrue() {
        assertThat(Customer.isValidName("John le Carré")).isTrue();
    }

    @Test
    void givenAStringWithNumbers_whenIsValidName_thenFalse() {
        assertThat(Customer.isValidName("John42")).isFalse();
    }

    // --- Customer.of(name, company, email) ---

    @Test
    void givenValidNameCompanyEmail_whenOf_thenCustomerIsCreated() {
        var customer = Customer.of("Anna Müller", "ACME AG", "anna@example.com");
        assertThat(customer.name()).isEqualTo("Anna Müller");
        assertThat(customer.company()).isEqualTo("ACME AG");
        assertThat(customer.email()).isEqualTo("anna@example.com");
    }

    @Test
    void givenInvalidNameWithDigits_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John42", "ACME AG", "john@example.com"))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

    @Test
    void givenBlankCompany_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John Buyer", "  ", "john@example.com"))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

    @Test
    void givenCompanyWithInvalidChars_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John Buyer", "ACME@GmbH!", "john@example.com"))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

    @Test
    void givenCompanyWithAllowedSpecialChars_whenOf_thenCustomerIsCreated() {
        var customer = Customer.of("John Buyer", "Smith & Sons, Ltd.", "john@example.com");
        assertThat(customer.company()).isEqualTo("Smith & Sons, Ltd.");
    }

    @Test
    void givenEmailWithoutAtSign_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John Buyer", "ACME AG", "johnexample.com"))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

    @Test
    void givenEmailWithoutDot_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John Buyer", "ACME AG", "john@examplecom"))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

    @Test
    void givenBlankEmail_whenOf_thenThrowsInvalidCustomerAttributeException() {
        assertThatThrownBy(() -> Customer.of("John Buyer", "ACME AG", ""))
                .isInstanceOf(InvalidCustomerAttributeException.class);
    }

}
