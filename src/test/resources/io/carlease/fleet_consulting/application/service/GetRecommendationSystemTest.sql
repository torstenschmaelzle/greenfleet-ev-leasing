INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status)
VALUES ('WBA00000000000201', 'BMW', 'i3', 460, 'SEDAN', 17.0,
        42.0, 360.0, 11.0, 170, 7.0, 150, 60000, 'CHF', 'AVAILABLE');

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000201', 0, '00000000-0000-0000-0000-000000000201', CURRENT_TIMESTAMP, 95.0, 440);

INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status)
VALUES ('WBA00000000000202', 'Tesla', 'Model 3', 500, 'SEDAN', 15.0,
        75.0, 400.0, 11.0, 283, 5.6, 225, 150000, 'CHF', 'AVAILABLE');

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000202', 0, '00000000-0000-0000-0000-000000000202', CURRENT_TIMESTAMP, 97.0, 480);

INSERT INTO vehicles (vin, brand, model, range_km, vehicle_type, consumption_kwh_per100km,
                      battery_capacity_kwh, battery_voltagev, battery_max_charge_speed_kw,
                      horse_power_hp, acceleration_sec, top_speed_kmh,
                      monthly_lease_price_cents, monthly_lease_price_currency, status)
VALUES ('WBA00000000000203', 'VW', 'ID.4', 450, 'SUV', 18.5,
        77.0, 400.0, 11.0, 204, 8.5, 160, 90000, 'CHF', 'REMOVED');

INSERT INTO battery_health_records (vin, measurement_order, record_id, measured_at, state_of_health, measured_range_km)
VALUES ('WBA00000000000203', 0, '00000000-0000-0000-0000-000000000203', CURRENT_TIMESTAMP, 92.0, 410);
