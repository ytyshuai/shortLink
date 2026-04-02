import api from '@/lib/api';

export interface Event {
  id: number;
  title: string;
  description: string;
  status: string;
  startTime: string;
  endTime: string;
  createTime: string;
  updateTime: string;
  creatorId: number;
  capacity: number;
  location?: string;
}

export const eventApi = {
  getEvents: () => api.get<{ code: number; data: Event[]; msg: string }>('/event/list'),
  getEventById: (id: number) => api.get<{ code: number; data: Event; msg: string }>(`/event/${id}`),
  createEvent: (data: Partial<Event>) => api.post<{ code: number; data: Event; msg: string }>('/event/create', data),
  updateEvent: (id: number, data: Partial<Event>) => api.put<{ code: number; data: Event; msg: string }>(`/event/${id}`, data),
  deleteEvent: (id: number) => api.delete<{ code: number; msg: string }>(`/event/${id}`),
};
