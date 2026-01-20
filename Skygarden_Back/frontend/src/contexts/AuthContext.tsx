import React, { createContext, useContext, useEffect, useState, ReactNode } from "react";

interface AuthContextType {
  isAuth: boolean;
}

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;
const AuthContext = createContext<AuthContextType>({ isAuth: true });

/**
 * 認証プロバイダーコンポーネント
 * アプリケーション全体で認証状態を管理する
 * 
 * @param children 子コンポーネント
 */
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [isAuth, setIsAuth] = useState<boolean>(true);

  useEffect(() => {
    fetch(`${API_BASE_URL}/auth`, {
      credentials: "include",
    })
      .then((res) => res.json())
      .then((data) => setIsAuth(data))
      .catch(() => setIsAuth(false));
  }, []);

  return <AuthContext.Provider value={{ isAuth }}>{children}</AuthContext.Provider>;
};

/**
 * 認証コンテキストを使用するカスタムフック
 * 認証状態を取得する
 * 
 * @returns 認証コンテキスト
 * @throws Error AuthProviderの外で使用された場合
 */
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error("useAuth must be used within an AuthProvider");
  return context;
};
