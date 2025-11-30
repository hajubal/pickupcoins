import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import * as authService from '@/services/auth';

export const useAuth = () => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = async (loginId: string, password: string) => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await authService.login({ loginId, password });
      authService.saveUserInfo(response);
      navigate('/');
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message || '로그인에 실패했습니다');
      } else {
        setError('로그인에 실패했습니다');
      }
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    authService.logout();
    navigate('/login');
  };

  return {
    login,
    logout,
    isLoading,
    error,
    isAuthenticated: authService.isAuthenticated(),
    userInfo: authService.getUserInfo(),
  };
};
