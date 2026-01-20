import React, { useLayoutEffect, useRef, useState } from "react";
import { Modal } from "../components/common/Modal";
import { TextInput } from "../components/common/input/TextInput";
import { SaveButton } from "../components/common/button/SaveButton";
import { UserAdminSwitch } from "../components/user/UserAdminSwitch";
import { UserIDLabal } from "../components/user/UserIDLabal";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

/**
 * ユーザー管理ページコンポーネント
 * ユーザーの作成・編集を行う
 */
export const User = () => {
  const [id, setId] = useState("");
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [admin, setAdmin] = useState(false);
  const cancelButtonRef = useRef<HTMLButtonElement>(null);

  /**
   * 管理者フラグを切り替える
   */
  const handleAdminToggle = () => {
    setAdmin((prev) => !prev);
  };

  /**
   * フォームを送信する
   * バリデーションを実行し、成功時のみ送信する
   */
  const doSubmit = () => {
    const contentForm = document.forms.namedItem("contentform") as HTMLFormElement;
    if (contentForm) {
      if (!contentForm.checkValidity()) {
        cancelButtonRef.current?.click();
        setTimeout(() => {
          //cancelButtonRef.current?.click()との関係で少し待機
          contentForm.reportValidity();
        }, 700);
      } else {
        contentForm.submit();
      }
    } else {
      console.error("Form with name 'contentform' not found");
      return;
    }
  };

  useLayoutEffect(() => {
    const params = new URLSearchParams(window.location.search);
    let fetchId = params.get("id") || "";
    setId(fetchId);
    if (fetchId != "") {
      fetch(`${API_BASE_URL}/getuser?&id=${fetchId}`, {
        method: "GET",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      })
        .then((res) => res.json())
        .then((data) => {
          setName(data.name || "");
          setPassword(data.password || "");
          setEmail(data.email || "");
          setAdmin(data.admin == "1" ? true : false);
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    }
  }, []);

  return (
    <>
      <div className="flex-grow-1 d-flex justify-content-center align-items-center p-4">
        <section className="w-100 sky-User-container sky-content">
          <h2 className="text-center mb-2">ユーザ管理画面</h2>
          <div className="sky-User-box">
            <form id="contentform" name="contentform" action="/webadmin/user_post" method="POST">
              <UserAdminSwitch onChange={handleAdminToggle} admin={admin} />
              <UserIDLabal id={id} />
              <TextInput isLogin={false} id="username" label="Name:" type="text" name="name" value={name} setState={setName} placeholder="Enter your username" required={true} errorMessage="" />
              <TextInput isLogin={false} id="password" label="Password:" type="password" name="password" value={password} setState={setPassword} placeholder="Enter a new password" required={true} errorMessage="" />
              <TextInput isLogin={false} id="email" label="Email Address:" type="email" name="email" value={email} setState={setEmail} placeholder="Enter your email" required={true} errorMessage="" />
              <SaveButton targetModal="exampleModal" />
              <Modal id="exampleModal" label="exampleModalLabel" title="登録しますか？" cansel="キャンセル" submit="登録" submitFun={doSubmit} cancelButtonRef={cancelButtonRef} />
            </form>
          </div>
        </section>
      </div>
    </>
  );
};
