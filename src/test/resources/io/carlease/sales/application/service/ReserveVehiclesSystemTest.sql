INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000201', 'BMW', 'i4', 590, 'SEDAN', 16.0,
        83.9, 400.0, 11.0, 340, 5.7, 225, 65000, 'CHF', 'AVAILABLE',
        null, null, null, null);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000201', 0, '00000000-0000-0000-0000-000000002010', CURRENT_TIMESTAMP, 98.0, 578);

INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status,
                      lock_id, locked_at, expires_at, lock_duration_minutes)
VALUES ('WBA00000000000202', 'BMW', 'i4', 590, 'SEDAN', 16.0,
        83.9, 400.0, 11.0, 340, 5.7, 225, 65000, 'CHF', 'AVAILABLE',
        null, null, null, null);

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000202', 0, '00000000-0000-0000-0000-000000002020', CURRENT_TIMESTAMP, 97.0, 572);
