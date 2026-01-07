import { useState } from 'react';

interface AuthState {
  isAuthenticated: boolean;
  user: any | null;
}

export const useAuth = () => {
  const [authState, setAuthState] = useState<AuthState>({
    isAuthenticated: false,
    user: null,
  });

  const login = (user: any) => {
    setAuthState({ isAuthenticated: true, user });
    localStorage.setItem('token', user.token);
  };

  const logout = () => {
    setAuthState({ isAuthenticated: false, user: null });
    localStorage.removeItem('token');
  };

  return {
    ...authState,
    login,
    logout,
  };
};