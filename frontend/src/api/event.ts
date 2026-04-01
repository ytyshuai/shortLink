import api from '@/lib/api';

export interface Event {
  id: number;
  title: string;
  description: string;
  location: string;
  startTime: string;
  endTime: string;
  capacity: number;
  status: string;
  creatorId: number;
  createTime: string;
}

export interface EventReq {
  title: string;
  description?: string;
  location: string;
  startTime: string;
  endTime: string;
  capacity?: number;
  status?: string;
}

export const eventApi = {
  getEvents: () => api.get<{ code: number; data: Event[]; msg: string }>('/events'),
  getEventById: (id: number) => api.get<{ code: number; data: Event; msg: string }>(`/events/${id}`),
  createEvent: (data: EventReq) => api.post<{ code: number; data: Event; msg: string }>('/events', data),
  updateEvent: (id: number, data: EventReq) => api.put<{ code: number; data: Event; msg: string }>(`/events/${id}`, data),
  deleteEvent: (id: number) => api.delete<{ code: number; data: null; msg: string }>(`/events/${id}`),
};
