import api from '@/lib/api';

export interface EventStat {
  eventId: number;
  eventTitle: string;
  enrollmentCount: number;
  checkinCount: number;
  visitCount: number;
}

export interface DashboardStats {
  totalEvents: number;
  totalEnrollments: number;
  totalCheckins: number;
  totalShortLinkVisits: number;
  recentEventStats: EventStat[];
}

export const statisticsApi = {
  getDashboardStats: () => api.get<{ code: number; data: DashboardStats; msg: string }>('/statistics/dashboard'),
};
