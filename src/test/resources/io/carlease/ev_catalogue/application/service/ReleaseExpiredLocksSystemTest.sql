-- Vehicle in RESERVED status with an expired lock (30 minutes ago)
INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000996', 'BMW', 'iX3', 460, 'SUV', 17.0,
        80.0, 400.0, 11.0, 286, 6.8, 180, 75000, 'CHF', 'RESERVED',
        '00000000-0000-0000-0000-000000000996',
        TIMESTAMPADD(MINUTE, -45, CURRENT_TIMESTAMP),
        TIMESTAMPADD(MINUTE, -30, CURRENT_TIMESTAMP),
        15);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000996', 0, '00000000-0000-0000-0000-000000009960', CURRENT_TIMESTAMP, 95.0, 440);

INSERT INTO contracts (number, lessee, lessee_company, lessee_email, car, price_amount, price_currency, monthly_installment_cents)
VALUES ('vc-chain-001', 'Chain Test', 'Chain Corp AG', 'chain@test.com', 'WBA00000000000996', 7500000, 'CHF', 75000);

INSERT INTO fleet_contracts (number, customer_name, customer_company, customer_email, lease_term_months, bundle_type, signature_date)
VALUES ('fc-chain-001', 'Chain Test', 'Chain Corp AG', 'chain@test.com', 36, 'BALANCED', null);

INSERT INTO fleet_contract_vehicle_numbers (fleet_contract_number, vehicle_contract_number)
VALUES ('fc-chain-001', 'vc-chain-001');
