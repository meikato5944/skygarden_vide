import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

/**
 * プライベートルートコンポーネント
 * 認証が必要なページへのアクセスを制御する
 * 未認証の場合はログインページにリダイレクトする
 */
export const PrivateRoute = () => {
  const authContext = useAuth();
  const { isAuth } = authContext;
  return isAuth ? <Outlet /> : <Navigate to="/login" replace />;
};
