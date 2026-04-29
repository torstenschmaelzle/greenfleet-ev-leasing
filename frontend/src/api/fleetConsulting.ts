export interface VehicleScore {
  rangeScore: number;
  co2Score: number;
  costScore: number;
  finalScore: number;
}

export interface BundleVehicle {
  vin: string;
  brand: string;
  model: string;
  vehicleType: string;
  rangeKm: number;
  monthlyInstallmentCents: number;
  currency: string;
  co2SavingsKgPerYear: number;
  score: VehicleScore;
}

export interface Tco {
  monthlyInstallmentPerVehicleCents: number;
  monthlyLeaseCostCents: number;
  monthlyChargingCostCents: number;
  monthlyTotalCents: number;
  annualTotalCents: number;
  currency: string;
}

export interface FleetBundle {
  bundleType: string;
  vehicles: BundleVehicle[];
  tco: Tco;
  totalCo2SavingsKgPerYear: number;
  bundleScore: number;
}

export interface FleetBundleRecommendation {
  requestId: string;
  numberOfVehicles: number;
  leaseTermMonths: number;
  bundles: FleetBundle[];
}

export interface RecommendationParams {
  daily_route_km: number;
  number_of_vehicles: number;
  lease_term_months: number;
  charging_type: string;
  electricity_rate_cents: number;
  currency: string;
  budget_monthly_max_cents?: number;
  brand?: string;
  vehicle_type?: string;
  range_weight?: number;
  co2_weight?: number;
  cost_weight?: number;
}

export async function getRecommendations(params: RecommendationParams): Promise<FleetBundleRecommendation> {
  const query = new URLSearchParams();
  query.set('daily_route_km', String(params.daily_route_km));
  query.set('number_of_vehicles', String(params.number_of_vehicles));
  query.set('lease_term_months', String(params.lease_term_months));
  query.set('charging_type', params.charging_type);
  query.set('electricity_rate_cents', String(params.electricity_rate_cents));
  query.set('currency', params.currency);
  if (params.budget_monthly_max_cents != null) query.set('budget_monthly_max_cents', String(params.budget_monthly_max_cents));
  if (params.brand) query.set('brand', params.brand);
  if (params.vehicle_type) query.set('vehicle_type', params.vehicle_type);
  if (params.range_weight != null) query.set('range_weight', String(params.range_weight));
  if (params.co2_weight != null) query.set('co2_weight', String(params.co2_weight));
  if (params.cost_weight != null) query.set('cost_weight', String(params.cost_weight));

  const res = await fetch(`/api/fleet-consulting/recommendations?${query}`, { credentials: 'include' });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return res.json();
}
