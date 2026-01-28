// Auth types
export interface LoginRequest {
  loginId: string;
  password: string;
  rememberMe?: boolean;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userName: string;
  loginId: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

// Dashboard types
export interface DashboardStats {
  savedDayPoint: number;
  savedDayPointRatioDayBefore: number;
  savedWeekPoint: number;
  savedWeekPointRatioWeekBefore: number;
  pointUrlDayCnt: number;
  pointUrlWeekCnt: number;
}

// Cookie types
export interface Cookie {
  id: string;
  userName: string;
  siteName: string;
  cookie: string | null;
  isValid: boolean;
  createdDate: string;
  modifiedDate: string;
}

export interface CreateCookieRequest {
  userName: string;
  siteName: string;
  cookie?: string;
  isValid?: boolean;
}

export interface UpdateCookieRequest {
  userName?: string;
  siteName?: string;
  cookie?: string;
  isValid?: boolean;
}

// PointUrl types
export type PointUrlType = 'NAVER' | 'OFW_NAVER' | 'UNSUPPORT';

export interface PointUrl {
  id: string;
  name: string;
  url: string;
  pointUrlType: PointUrlType | null;
  permanent: boolean;
  createdDate: string;
  modifiedDate: string;
}

export interface CreatePointUrlRequest {
  url: string;
  permanent?: boolean;
}

export interface UpdatePointUrlRequest {
  url?: string;
  permanent?: boolean;
}

// SavedPoint (Point Log) types
export interface SavedPoint {
  id: string;
  cookieId: string;
  amount: number;
  responseBody: string | null;
  createdDate: string;
  modifiedDate: string;
  userName?: string;
  siteName?: string;
}

export interface SavedPointQuery {
  page?: number;
  size?: number;
  startDate?: string;
  endDate?: string;
}

export interface SavedPointListResponse {
  content: SavedPoint[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

// Site types
export interface Site {
  id: string;
  name: string;
  domain: string;
  url: string;
  createdDate: string;
  modifiedDate: string;
}

export interface CreateSiteRequest {
  name: string;
  domain: string;
  url: string;
}

export interface UpdateSiteRequest {
  name?: string;
  domain?: string;
  url?: string;
}

// Auth state
export interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  userName: string | null;
  loginId: string | null;
  isAuthenticated: boolean;
}
