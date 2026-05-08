INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000301', 'BMW', 'iX', 630, 'SUV', 21.0,
        111.5, 400.0, 11.0, 326, 6.1, 200, 95000, 'CHF', 'RESERVED',
        '00000000-0000-0000-0000-000000003010',
        CURRENT_TIMESTAMP,
        TIMESTAMPADD(HOUR, 8, CURRENT_TIMESTAMP),
        480);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000301', 0, '00000000-0000-0000-0000-000000003011', CURRENT_TIMESTAMP, 96.0, 605);

INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000302', 'BMW', 'iX', 630, 'SUV', 21.0,
        111.5, 400.0, 11.0, 326, 6.1, 200, 95000, 'CHF', 'RESERVED',
        '00000000-0000-0000-0000-000000003020',
        CURRENT_TIMESTAMP,
        TIMESTAMPADD(HOUR, 8, CURRENT_TIMESTAMP),
        480);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000302', 0, '00000000-0000-0000-0000-000000003021', CURRENT_TIMESTAMP, 95.0, 599);
