import api from '@/lib/api';

export interface ShortLink {
  id: number;
  shortCode: string;
  originalUrl: string;
  eventId?: number;
  expireTime?: string;
}

export interface ShortLinkReq {
  originalUrl: string;
  eventId?: number;
  expireTime?: string;
}

export const shortLinkApi = {
  createShortLink: (data: ShortLinkReq) => api.post<{ code: number; data: ShortLink; msg: string }>('/short-link/create', data),
};
