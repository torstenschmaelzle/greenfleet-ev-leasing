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

export interface Amount {
  amountInCents: number;
  currency: Currency;
}

export interface Kilometre {
  value: number;
}

export interface RangeFit {
  requiredKm: Kilometre;
  actualKm: Kilometre;
  sufficient: boolean;
}

export interface TotalCostOfOwnership {
  monthlyLeaseCost: Amount;
  monthlyChargingCost: Amount;
  monthlyTotal: Amount;
  annualTotal: Amount;
}

export interface Co2Impact {
  evGramsPerKm: number;
  annualSavingsKg: number;
}

export interface SuitabilityScore {
  score: number;
}

export interface VehicleRecommendation {
  brand: string;
  model: string;
  vehicleType: VehicleType;
  rangeKm: Kilometre;
  monthlyLeasePrice: Amount;
  kwhPer100km: number;
  rangeFit: RangeFit;
  tco: TotalCostOfOwnership;
  co2Impact: Co2Impact;
  suitabilityScore: SuitabilityScore;
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
