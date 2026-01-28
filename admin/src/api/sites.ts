import apiClient from './client';
import { Site, CreateSiteRequest, UpdateSiteRequest } from '@/types';

export const sitesApi = {
  findAll: async (): Promise<Site[]> => {
    const response = await apiClient.get<Site[]>('/sites');
    return response.data;
  },

  findOne: async (id: string): Promise<Site> => {
    const response = await apiClient.get<Site>(`/sites/${id}`);
    return response.data;
  },

  create: async (data: CreateSiteRequest): Promise<Site> => {
    const response = await apiClient.post<Site>('/sites', data);
    return response.data;
  },

  update: async (id: string, data: UpdateSiteRequest): Promise<Site> => {
    const response = await apiClient.put<Site>(`/sites/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/sites/${id}`);
  },
};
