import type { VehicleRecommendation } from '../types';

export interface RecommendationParams {
  daily_route_km: number;
  number_of_drivers: number;
  charging_type: string;
  electricity_rate_cents: number;
  currency: string;
  budget_monthly_max_cents: number;
  size_preference?: string;
  range_weight: number;
  cost_weight: number;
  co2_weight: number;
}

export async function getRecommendations(params: RecommendationParams): Promise<VehicleRecommendation[]> {
  const query = new URLSearchParams();
  query.set('daily_route_km', String(params.daily_route_km));
  query.set('number_of_drivers', String(params.number_of_drivers));
  query.set('charging_type', params.charging_type);
  query.set('electricity_rate_cents', String(params.electricity_rate_cents));
  query.set('currency', params.currency);
  query.set('budget_monthly_max_cents', String(params.budget_monthly_max_cents));
  if (params.size_preference) query.set('size_preference', params.size_preference);
  query.set('range_weight', String(params.range_weight));
  query.set('cost_weight', String(params.cost_weight));
  query.set('co2_weight', String(params.co2_weight));

  const res = await fetch(`/api/fleet-consulting/recommendations?${query}`, {
    credentials: 'include',
  });
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return res.json();
}
