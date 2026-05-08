import { api } from './client';
import type { AuthUser } from '../types';

export const authApi = {
  login: (username: string, password: string) =>
    api.post<AuthUser>('/auth/login', { username, password }),
  customerLogin: (name: string, email: string, company: string) =>
    api.post<AuthUser>('/auth/customer-login', { name, email, company }),
  me: () => api.get<AuthUser>('/auth/me'),
  logout: () => api.post<void>('/auth/logout', {}),
};
