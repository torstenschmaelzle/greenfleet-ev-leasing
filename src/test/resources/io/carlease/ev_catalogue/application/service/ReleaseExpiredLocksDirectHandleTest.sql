DELETE FROM fleet_contract_vehicle_numbers WHERE fleet_contract_number = 'fc-direct-001';
DELETE FROM fleet_contracts WHERE number = 'fc-direct-001';
DELETE FROM contracts WHERE number = 'vc-direct-001';
DELETE FROM battery_health_records WHERE vin = 'WBA00000000000997';
DELETE FROM vehicles WHERE vin = 'WBA00000000000997';

-- Seed for handle() direct test (VIN 997 to avoid conflict)
INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status)
VALUES ('WBA00000000000997', 'BMW', 'iX3', 460, 'SUV', 17.0,
        80.0, 400.0, 11.0, 286, 6.8, 180, 75000, 'CHF', 'AVAILABLE');

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000997', 0, '00000000-0000-0000-0000-000000009970', CURRENT_TIMESTAMP, 95.0, 440);

INSERT INTO contracts (number, lessee, lessee_company, lessee_email, car, price_amount, price_currency, monthly_installment_cents)
VALUES ('vc-direct-001', 'Test Lessee', 'Test Corp AG', 'test@testcorp.com', 'WBA00000000000997', 7500000, 'CHF', 75000);

INSERT INTO fleet_contracts (number, customer_name, customer_company, customer_email, lease_term_months, bundle_type, signature_date)
VALUES ('fc-direct-001', 'Test Lessee', 'Test Corp AG', 'test@testcorp.com', 36, 'BALANCED', null);

INSERT INTO fleet_contract_vehicle_numbers (fleet_contract_number, vehicle_contract_number)
VALUES ('fc-direct-001', 'vc-direct-001');
