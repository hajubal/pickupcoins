import axiosInstance from '@/lib/axios';

export interface LoginRequest {
  loginId: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  userName: string;
  loginId: string;
}

const AUTH_KEYS = ['access_token', 'refresh_token', 'user_name', 'login_id'] as const;

/**
 * 로그인 API 호출
 */
export const login = async (loginRequest: LoginRequest): Promise<LoginResponse> => {
  const response = await axiosInstance.post<LoginResponse>('/auth/login', loginRequest);
  return response.data;
};

/**
 * 로그아웃 처리
 */
export const logout = () => {
  // 양쪽 스토리지 모두 클리어
  AUTH_KEYS.forEach(key => {
    localStorage.removeItem(key);
    sessionStorage.removeItem(key);
  });
  localStorage.removeItem('remember_me');
  localStorage.removeItem('saved_login_id');
};

/**
 * 인증 상태 확인
 */
export const isAuthenticated = (): boolean => {
  // 양쪽 스토리지 모두 확인
  return localStorage.getItem('access_token') !== null ||
         sessionStorage.getItem('access_token') !== null;
};

/**
 * 토큰 가져오기 (양쪽 스토리지 확인)
 */
export const getAccessToken = (): string | null => {
  return localStorage.getItem('access_token') || sessionStorage.getItem('access_token');
};

/**
 * 사용자 정보 저장
 */
export const saveUserInfo = (response: LoginResponse, rememberMe: boolean = false) => {
  const storage = rememberMe ? localStorage : sessionStorage;

  // rememberMe 설정 저장 (항상 localStorage에)
  localStorage.setItem('remember_me', String(rememberMe));

  // rememberMe가 true면 로그인 ID 저장
  if (rememberMe) {
    localStorage.setItem('saved_login_id', response.loginId);
  } else {
    localStorage.removeItem('saved_login_id');
  }

  storage.setItem('access_token', response.accessToken);
  storage.setItem('refresh_token', response.refreshToken);
  storage.setItem('user_name', response.userName);
  storage.setItem('login_id', response.loginId);
};

/**
 * 사용자 정보 가져오기
 */
export const getUserInfo = () => {
  return {
    userName: localStorage.getItem('user_name') || sessionStorage.getItem('user_name'),
    loginId: localStorage.getItem('login_id') || sessionStorage.getItem('login_id'),
  };
};

/**
 * Remember Me 설정 가져오기
 */
export const getRememberMe = (): boolean => {
  return localStorage.getItem('remember_me') === 'true';
};

/**
 * 저장된 로그인 ID 가져오기
 */
export const getSavedLoginId = (): string | null => {
  return localStorage.getItem('saved_login_id');
};
