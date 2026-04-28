import { api } from './client';
import type { AddVehicleRequest, UpdateVehicleRequest, Vehicle } from '../types';

export const vehiclesApi = {
  list: () => api.get<Vehicle[]>('/vehicles'),
  get: (vin: string) => api.get<Vehicle>(`/vehicles/${vin}`),
  add: (req: AddVehicleRequest) => api.post<void>('/vehicles', req),
  update: (vin: string, req: UpdateVehicleRequest) => api.put<void>(`/vehicles/${vin}`, req),
  remove: (vin: string) => api.delete<void>(`/vehicles/${vin}`),
};
