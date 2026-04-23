import type { ApiResponse, UrlResponse, UrlRequest, AnalyticsDto, DashboardStats, PageResponse } from '@/types';

// Use environment variable or fallback to localhost for local development
// When deployed, user should set VITE_API_URL to their backend URL
const API_BASE = import.meta.env.VITE_API_URL || localStorage.getItem('api_url') || '/api';

// Helper to build full URL
function buildUrl(path: string): string {
  if (API_BASE.startsWith('http')) {
    return `${API_BASE}${path}`;
  }
  return `${window.location.origin}${API_BASE}${path}`;
}

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const error = await response.json().catch(() => ({ message: 'Network error' }));
    throw new Error(error.message || `HTTP ${response.status}`);
  }
  return response.json();
}

export const api = {
  // Check if backend is available
  async healthCheck(): Promise<boolean> {
    try {
      const response = await fetch(buildUrl('/urls/recent'), { method: 'HEAD' });
      return response.ok;
    } catch {
      return false;
    }
  },

  // URL CRUD
  async createShortUrl(request: UrlRequest): Promise<ApiResponse<UrlResponse>> {
    const response = await fetch(buildUrl('/urls'), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    return handleResponse(response);
  },

  async getAllUrls(page = 0, size = 10, sortBy = 'createdAt', direction = 'desc'): Promise<ApiResponse<PageResponse<UrlResponse>>> {
    const response = await fetch(buildUrl(`/urls?page=${page}&size=${size}&sortBy=${sortBy}&direction=${direction}`));
    return handleResponse(response);
  },

  async getRecentUrls(): Promise<ApiResponse<UrlResponse[]>> {
    const response = await fetch(buildUrl('/urls/recent'));
    return handleResponse(response);
  },

  async getUrlByShortCode(shortCode: string): Promise<ApiResponse<UrlResponse>> {
    const response = await fetch(buildUrl(`/urls/${shortCode}`));
    return handleResponse(response);
  },

  async searchUrls(keyword: string, page = 0, size = 10): Promise<ApiResponse<PageResponse<UrlResponse>>> {
    const response = await fetch(buildUrl(`/urls/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`));
    return handleResponse(response);
  },

  async updateUrl(id: number, request: UrlRequest): Promise<ApiResponse<UrlResponse>> {
    const response = await fetch(buildUrl(`/urls/${id}`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(request),
    });
    return handleResponse(response);
  },

  async deleteUrl(id: number): Promise<ApiResponse<void>> {
    const response = await fetch(buildUrl(`/urls/${id}`), { method: 'DELETE' });
    return handleResponse(response);
  },

  async permanentDeleteUrl(id: number): Promise<ApiResponse<void>> {
    const response = await fetch(buildUrl(`/urls/${id}/permanent`), { method: 'DELETE' });
    return handleResponse(response);
  },

  // Analytics
  async getAnalytics(id: number): Promise<ApiResponse<AnalyticsDto>> {
    const response = await fetch(buildUrl(`/urls/${id}/analytics`));
    return handleResponse(response);
  },

  // Dashboard
  async getDashboardStats(): Promise<ApiResponse<DashboardStats>> {
    const response = await fetch(buildUrl('/dashboard/stats'));
    return handleResponse(response);
  },

  // Check availability
  async checkShortCode(shortCode: string): Promise<ApiResponse<{ available: boolean; shortCode: string }>> {
    const response = await fetch(buildUrl(`/urls/check/${shortCode}`));
    return handleResponse(response);
  },
};
