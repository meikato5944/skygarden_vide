import React, { useEffect, useState } from "react";
import logo from "../../common/image/logo.png";
import { getSessionData } from "../../utils/commonPrc";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

/**
 * ヘッダーコンポーネント
 * ナビゲーションメニューとロゴを表示する
 * PC版とSP版の両方に対応している
 */
export const Header = () => {
  const [username, setUserName] = useState("");
  const [admin, setAdmin] = useState("");
  useEffect(() => {
    getSessionData("name").then((data: string) => {
      setUserName(data);
    });
    getSessionData("admin").then((data: string) => {
      setAdmin(data);
    });
  }, []);

  return (
    <>
      {/* PC */}
      <nav className="vh-100 sky-Sidemenu">
        <div className="p-3">
          <div className="mt-2">
            <a className="navbar-brand" href="/?mode=">
              <img src={logo} alt="logo.png" className="sky-logo" />
              <span>Skygarden</span>
            </a>
          </div>
          <ul className="navbar-nav ms-auto mt-4">
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=">
                Content
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=template">
                　Template
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=element">
                　Element
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=stylesheet">
                　CSS
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=script">
                　JS
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=image">
                　Image
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=file">
                　File
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href="/?mode=movie">
                　Movie
              </a>
            </li>
            {admin == "1" && (
              <li className="nav-item ms-3">
                <a className="nav-link" href="/user-list">
                  Users
                </a>
              </li>
            )}
            <li className="nav-item ms-3">
              <a className="nav-link" href="/setting">
                Settings
              </a>
            </li>
            <li className="nav-item ms-3">
              <a className="nav-link" href={`${API_BASE_URL}/logout`}>
                Logout
              </a>
            </li>
            <li className="nav-item my-4 ms-3">user: {username}</li>
          </ul>
        </div>
      </nav>

      {/* SP */}
      <nav className="navbar bg-warning p-2 sky-header sky-bg-4">
        <button className="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasMenu" aria-controls="offcanvasMenu">
          <span className="navbar-toggler-icon"></span>
        </button>
        <a className="navbar-brand sky-fc-1" href="/?mode=">
          <img src={logo} alt="logo.png" className="sky-logo" />
          Skygarden
        </a>
      </nav>
      <div className="offcanvas offcanvas-start sky-bg-1" tabIndex={-1} id="offcanvasMenu" aria-labelledby="offcanvasMenuLabel">
        <div className="offcanvas-header m-2">
          <a className="navbar-brand" href="#">
            <img src={logo} alt="logo.png" className="sky-logo" />
            Skygarden
          </a>
          <button type="button" className="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
        </div>
        <div className="offcanvas-body ms-2">
          <ul className="nav flex-column">
            <li className="nav-item">
              <a className="nav-link" href="/?mode=">
                Content
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=template">
                　Template
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=element">
                　Element
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=stylesheet">
                　CSS
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=script">
                　JS
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=image">
                　Image
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=file">
                　File
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="/?mode=movie">
                　Movie
              </a>
            </li>
            {admin == "1" && (
              <li className="nav-item">
                <a className="nav-link" href="/user-list">
                  Users
                </a>
              </li>
            )}
            <li className="nav-item">
              <a className="nav-link" href="/setting">
                Settings
              </a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href={`${API_BASE_URL}/logout`}>
                Logout
              </a>
            </li>
            <li className="nav-item my-4 ms-3">user: {username}</li>
          </ul>
        </div>
      </div>
    </>
  );
};
