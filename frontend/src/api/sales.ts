export interface VehicleEntry {
  vin: string;
  monthlyInstallmentCents: number;
  currency: string;
}

export interface CreateFleetContractRequest {
  customerName: string;
  customerCompany: string;
  customerEmail: string;
  leaseTermMonths: number;
  bundleType: string;
  vehicles: VehicleEntry[];
}

export interface VehicleResponse {
  vin: string;
  monthlyInstallmentCents: number;
  currency: string;
}

export interface FleetContractResponse {
  number: string;
  customerName: string;
  customerCompany: string;
  customerEmail: string;
  bundleType: string;
  leaseTermMonths: number;
  approved: boolean;
  signed: boolean;
  totalMonthlyInstallmentCents: number;
  currency: string;
  vehicles: VehicleResponse[];
}

async function request(path: string, options?: RequestInit): Promise<Response> {
  const res = await fetch(path, { credentials: 'include', ...options });
  if (!res.ok) {
    const text = await res.text();
    const parsed = text ? JSON.parse(text) : {};
    throw new Error(parsed?.error ?? `${res.status} ${res.statusText}`);
  }
  return res;
}

export async function listFleetContracts(): Promise<FleetContractResponse[]> {
  const res = await request('/api/sales/fleet-contracts');
  return res.json();
}

export async function listMyContracts(): Promise<FleetContractResponse[]> {
  const res = await request('/api/sales/fleet-contracts/mine');
  return res.json();
}

export async function createFleetContract(req: CreateFleetContractRequest): Promise<string> {
  const res = await request('/api/sales/fleet-contracts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(req),
  });
  return res.text();
}

export async function getFleetContract(number: string): Promise<FleetContractResponse> {
  const res = await request(`/api/sales/fleet-contracts/${encodeURIComponent(number)}`);
  return res.json();
}

export async function signFleetContract(number: string): Promise<void> {
  await request(`/api/sales/fleet-contracts/${encodeURIComponent(number)}/sign`, { method: 'POST' });
}

export async function reserveFleetBundle(vins: string[]): Promise<void> {
  await request('/api/sales/fleet-contracts/reserve', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ vins }),
  });
}

export async function releaseFleetBundle(vins: string[]): Promise<void> {
  await request('/api/sales/fleet-contracts/reserve', {
    method: 'DELETE',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ vins }),
  });
}

export async function deleteFleetContract(number: string): Promise<void> {
  await request(`/api/sales/fleet-contracts/${encodeURIComponent(number)}`, { method: 'DELETE' });
}
