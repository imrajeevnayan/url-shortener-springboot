export interface UrlResponse {
  id: number;
  shortCode: string;
  originalUrl: string;
  shortUrl: string;
  customAlias: string | null;
  title: string | null;
  description: string | null;
  clickCount: number;
  createdAt: string;
  expiresAt: string | null;
  lastAccessedAt: string | null;
  active: boolean;
  hasPassword: boolean;
  qrCodeBase64: string | null;
}

export interface UrlRequest {
  originalUrl: string;
  customAlias?: string;
  title?: string;
  description?: string;
  expiryDays?: number;
  password?: string;
}

export interface AnalyticsDto {
  urlId: number;
  shortCode: string;
  originalUrl: string;
  totalClicks: number;
  uniqueVisitors: number;
  clicksOverTime: DailyClick[];
  browsers: StatItem[];
  operatingSystems: StatItem[];
  devices: StatItem[];
  referrers: StatItem[];
  countries: StatItem[];
}

export interface DailyClick {
  date: string;
  count: number;
}

export interface StatItem {
  name: string;
  count: number;
  percentage: number;
}

export interface DashboardStats {
  totalUrls: number;
  activeUrls: number;
  totalClicks: number;
  recentUrls: UrlResponse[];
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
