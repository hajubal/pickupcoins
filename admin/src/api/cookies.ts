import apiClient from './client';
import { Cookie, CreateCookieRequest, UpdateCookieRequest } from '@/types';

export const cookiesApi = {
  findAll: async (): Promise<Cookie[]> => {
    const response = await apiClient.get<Cookie[]>('/cookies');
    return response.data;
  },

  findOne: async (id: string): Promise<Cookie> => {
    const response = await apiClient.get<Cookie>(`/cookies/${id}`);
    return response.data;
  },

  create: async (data: CreateCookieRequest): Promise<Cookie> => {
    const response = await apiClient.post<Cookie>('/cookies', data);
    return response.data;
  },

  update: async (id: string, data: UpdateCookieRequest): Promise<Cookie> => {
    const response = await apiClient.put<Cookie>(`/cookies/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/cookies/${id}`);
  },

  toggleValidity: async (id: string): Promise<Cookie> => {
    const response = await apiClient.patch<Cookie>(`/cookies/${id}/toggle-validity`);
    return response.data;
  },
};
