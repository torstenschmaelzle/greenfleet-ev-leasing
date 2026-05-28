Feature: Fleet Contract Lifecycle
  As a customer
  I want to create and sign a fleet contract
  So that the leased vehicles are locked under my contract

  Scenario: Customer creates and signs a fleet contract
    Given a vehicle "WBA00000000000301" is reserved in the catalogue
    When the customer creates a fleet contract for vehicle "WBA00000000000301"
    Then a valid contract number is persisted in the system
    And the vehicle "WBA00000000000301" is still reserved
    When the customer signs the contract
    Then the contract is marked as signed
    And the vehicle "WBA00000000000301" is under contract

  Scenario: Signing fails when the vehicle is not reserved
    Given a vehicle "WBA00000000000302" is available in the catalogue
    When the customer creates a fleet contract for vehicle "WBA00000000000302"
    And the customer tries to sign the contract
    Then the signing is rejected with an error
    And the contract is not yet signed
    And the vehicle "WBA00000000000302" is still available
