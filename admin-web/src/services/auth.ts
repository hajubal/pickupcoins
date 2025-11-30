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
  localStorage.removeItem('access_token');
  localStorage.removeItem('refresh_token');
  localStorage.removeItem('user_name');
  localStorage.removeItem('login_id');
};

/**
 * 인증 상태 확인
 */
export const isAuthenticated = (): boolean => {
  return localStorage.getItem('access_token') !== null;
};

/**
 * 사용자 정보 저장
 */
export const saveUserInfo = (response: LoginResponse) => {
  localStorage.setItem('access_token', response.accessToken);
  localStorage.setItem('refresh_token', response.refreshToken);
  localStorage.setItem('user_name', response.userName);
  localStorage.setItem('login_id', response.loginId);
};

/**
 * 사용자 정보 가져오기
 */
export const getUserInfo = () => {
  return {
    userName: localStorage.getItem('user_name'),
    loginId: localStorage.getItem('login_id'),
  };
};
