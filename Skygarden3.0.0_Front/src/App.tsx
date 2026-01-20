import React from "react";
import { Routes, Route } from "react-router-dom";

import { AuthProvider } from "./contexts/AuthContext";
import { Layout } from "./layouts/Layout";
import { PrivateRoute } from "./routes/PrivateRoute";

import { List } from "./pages/List";
import { Login } from "./pages/Login";
import { Content } from "./pages/Content";
import { UserList } from "./pages/UserList";
import { User } from "./pages/User";
import { Setting } from "./pages/Setting";

/**
 * アプリケーションのメインコンポーネント
 * ルーティングと認証プロバイダーを設定する
 */
function App() {
  return (
    <AuthProvider>
      <Layout>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route element={<PrivateRoute />}>
            <Route path="/" element={<List />} />
            <Route path="/content" element={<Content />} />
            <Route path="/user-list" element={<UserList />} />
            <Route path="/user" element={<User />} />
            <Route path="/setting" element={<Setting />} />
          </Route>
        </Routes>
      </Layout>
    </AuthProvider>
  );
}

export default App;
