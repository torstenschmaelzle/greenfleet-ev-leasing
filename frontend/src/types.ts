export type VehicleType = 'SUV' | 'KOMBI' | 'LIMOUSINE' | 'VAN';
export type VehicleStatus = 'AVAILABLE' | 'RESERVED' | 'UNDER_CONTRACT' | 'REMOVED';
export type Currency = 'CHF' | 'USD' | 'EUR' | 'GBP';
export type UserRole = 'WORKER' | 'CUSTOMER';

export interface Vehicle {
  vin: string;
  brand: string;
  model: string;
  vehicleType: VehicleType;
  rangeKm: number;
  consumptionKwhPer100km: number;
  capacityKwh: number;
  voltageV: number;
  maxChargeSpeedKw: number;
  horsePowerHp: number;
  accelerationSec: number;
  topSpeedKmh: number;
  monthlyLeasePriceCents: number;
  currency: Currency;
  status: VehicleStatus;
  stateOfHealth: number;
  measuredRangeKm: number;
}

export interface AuthUser {
  username: string;
  role: UserRole;
}

export interface AddVehicleRequest {
  vin: string;
  brand: string;
  model: string;
  rangeKm: number;
  vehicleType: string;
  consumptionKwhPer100km: number;
  capacityKwh: number;
  voltageV: number;
  maxChargeSpeedKw: number;
  horsePowerHp: number;
  accelerationSec: number;
  topSpeedKmh: number;
  monthlyLeasePriceCents: number;
  currency: string;
  initialStateOfHealth: number;
  initialMeasuredRangeKm: number;
}

export interface UpdateVehicleRequest {
  brand: string;
  model: string;
  rangeKm: number;
  vehicleType: string;
  consumptionKwhPer100km: number;
  capacityKwh: number;
  voltageV: number;
  maxChargeSpeedKw: number;
  monthlyLeasePriceCents: number;
  currency: string;
  newStateOfHealth: number;
  newMeasuredRangeKm: number;
}
