import apiClient from './client';
import { PointUrl, CreatePointUrlRequest, UpdatePointUrlRequest } from '@/types';

export const pointUrlsApi = {
  findAll: async (): Promise<PointUrl[]> => {
    const response = await apiClient.get<PointUrl[]>('/point-urls');
    return response.data;
  },

  findOne: async (id: string): Promise<PointUrl> => {
    const response = await apiClient.get<PointUrl>(`/point-urls/${id}`);
    return response.data;
  },

  create: async (data: CreatePointUrlRequest): Promise<PointUrl> => {
    const response = await apiClient.post<PointUrl>('/point-urls', data);
    return response.data;
  },

  update: async (id: string, data: UpdatePointUrlRequest): Promise<PointUrl> => {
    const response = await apiClient.put<PointUrl>(`/point-urls/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/point-urls/${id}`);
  },

  togglePermanent: async (id: string): Promise<PointUrl> => {
    const response = await apiClient.patch<PointUrl>(`/point-urls/${id}/toggle-permanent`);
    return response.data;
  },
};
