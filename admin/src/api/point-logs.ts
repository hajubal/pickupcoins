import apiClient from './client';
import { SavedPoint, SavedPointQuery, SavedPointListResponse } from '@/types';

export const pointLogsApi = {
  findAll: async (query?: SavedPointQuery): Promise<SavedPointListResponse> => {
    const params = new URLSearchParams();
    if (query?.page !== undefined) params.append('page', query.page.toString());
    if (query?.size !== undefined) params.append('size', query.size.toString());
    if (query?.startDate) params.append('startDate', query.startDate);
    if (query?.endDate) params.append('endDate', query.endDate);

    const response = await apiClient.get<SavedPointListResponse>('/point-logs', { params });
    return response.data;
  },

  findOne: async (id: string): Promise<SavedPoint> => {
    const response = await apiClient.get<SavedPoint>(`/point-logs/${id}`);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/point-logs/${id}`);
  },
};
