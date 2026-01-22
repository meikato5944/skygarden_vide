import React, { useState, useEffect } from "react";
import logo from "../common/image/logo.png";
import { useSearchParams } from "react-router-dom";
import { TextInput } from "../components/common/input/TextInput";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

/**
 * ログインページコンポーネント
 * ユーザーのログイン処理を行う
 */
export const Login = () => {
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [searchParams] = useSearchParams();
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const loginError = searchParams.get("loginError");
    if (loginError) {
      setErrorMessage(loginError);
    }
  }, [searchParams]);

  return (
    <main>
      <form action={`${API_BASE_URL}/login_post`} method="post">
        <div className="d-flex flex-column justify-content-center align-items-center sky-Login-container">
          <h2 className="text-center mb-4">
            <img src={logo} alt="logo.png" className="img-fluid sky-Login-logo" /> <span className="fs-5">skygarden</span>
          </h2>
          <div className="card w-100 sky-Login-form">
            <div className="card-body text-center border border-2 border-warning rounded">
              <h2 className="my-2 mb-3">Login</h2>
              {errorMessage && <div style={{ color: "red" }}>{errorMessage}</div>}
              <TextInput isLogin={true} id="id" label="ID:" type="id" name="name" value={name} setState={setName} placeholder="ID" required={true} errorMessage="" />
              <TextInput isLogin={true} id="password" label="Password:" type="password" name="password" value={password} setState={setPassword} placeholder="Password" required={true} errorMessage="" />
              <button type="submit" className="btn btn-warning w-100 mt-2 sky-bg-2">
                Go
              </button>
            </div>
          </div>
        </div>
      </form>
    </main>
  );
};
