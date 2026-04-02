import api from '@/lib/api';

export interface Enrollment {
  id: number;
  eventId: number;
  userId: number;
  status: string;
  checkinStatus: string;
  checkinTime?: string;
  createTime: string;
}

export const enrollmentApi = {
  enroll: (eventId: number) => api.post<{ code: number; data: Enrollment; msg: string }>('/enrollments', { eventId }),
  checkIn: (eventId: number) => api.post<{ code: number; data: Enrollment; msg: string }>(`/enrollments/checkin/${eventId}`),
  getEventEnrollments: (eventId: number) => api.get<{ code: number; data: Enrollment[]; msg: string }>(`/enrollments/event/${eventId}`),
  getUserEnrollments: () => api.get<{ code: number; data: Enrollment[]; msg: string }>('/enrollments/user'),
};
