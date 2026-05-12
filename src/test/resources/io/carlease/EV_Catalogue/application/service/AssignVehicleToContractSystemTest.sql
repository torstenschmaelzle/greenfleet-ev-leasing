INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000005', 'BMW', 'iX3', 460, 'SUV', 17.0,
        80.0, 400.0, 11.0, 286, 6.8, 180, 75000, 'CHF', 'RESERVED',
        '00000000-0000-0000-0000-000000000050', CURRENT_TIMESTAMP, DATEADD(MINUTE, 120, CURRENT_TIMESTAMP), 120);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000005', 0, '00000000-0000-0000-0000-000000000051', CURRENT_TIMESTAMP, 95.0, 440);
