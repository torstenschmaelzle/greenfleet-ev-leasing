import { api } from './client';
import type { AuthUser } from '../types';

export const authApi = {
  login: (username: string, password: string) =>
    api.post<AuthUser>('/auth/login', { username, password }),
  me: () => api.get<AuthUser>('/auth/me'),
  logout: () => api.post<void>('/auth/logout', {}),
};
