INSERT INTO contracts (number, lessee, lessee_company, lessee_email, car, price_amount, price_currency, monthly_installment_cents)
VALUES ('vc-view-001', 'John Buyer', 'ACME AG', 'john@example.com', 'WBA00000000000111', 6000000, 'CHF', 60000);

INSERT INTO contracts (number, lessee, lessee_company, lessee_email, car, price_amount, price_currency, monthly_installment_cents)
VALUES ('vc-view-002', 'John Buyer', 'ACME AG', 'john@example.com', 'WBA00000000000112', 5000000, 'CHF', 45000);

INSERT INTO fleet_contracts (number, customer_name, customer_company, customer_email, lease_term_months, bundle_type, signature_date)
VALUES ('fc-view-test-001', 'John Buyer', 'ACME AG', 'john@example.com', 36, 'BALANCED', null);

INSERT INTO fleet_contract_vehicle_numbers (fleet_contract_number, vehicle_contract_number)
VALUES ('fc-view-test-001', 'vc-view-001');

INSERT INTO fleet_contract_vehicle_numbers (fleet_contract_number, vehicle_contract_number)
VALUES ('fc-view-test-001', 'vc-view-002');
