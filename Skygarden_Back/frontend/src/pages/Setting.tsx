import React, { useEffect, useRef, useState } from "react";
import deleteImg from "../common/image/trash.svg";
import newCreate from "../common/image/plus-lg.svg";
import { Modal } from "../components/common/Modal";
import { SaveButton } from "../components/common/button/SaveButton";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

type ColorElements = {
  name: string;
  code: string;
};

/**
 * 設定ページコンポーネント
 * 構成要素の色設定などのアプリケーション設定を行う
 */
export const Setting = () => {
  const cancelButtonRef = useRef<HTMLButtonElement>(null);
  const [colorElements, setColorElements] = useState<ColorElements[]>([
    {
      name: "",
      code: "",
    },
  ]);
  useEffect(() => {
    fetch(`${API_BASE_URL}/get-setting`, {
      method: "GET",
      credentials: "include",
      headers: {
        Accept: "application/json",
      },
    })
      .then((res) => res.json())
      .then((data) => {
        setColorElements(data.colorElements);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  /**
   * 色要素を追加する
   * 入力された要素名と色コードから新しい色要素を作成する
   */
  const addElement = () => {
    const inputname = document.getElementById("elementcolor-name") as HTMLInputElement;
    const inputcode = document.getElementById("elementcolor-code") as HTMLInputElement;
    const templateElement = document.getElementById("template-element") as HTMLElement;
    const elementsContainer = document.getElementById("elements") as HTMLElement;

    if (inputname && inputcode && templateElement && elementsContainer && inputname.value !== "" && inputcode.value !== "") {
      const newelement = templateElement.cloneNode(true) as HTMLElement;
      const name = inputname.value;
      const colorcode = inputcode.value;
      const elements = document.querySelectorAll('[data-name="colorelements"]');
      const index = elements.length + 1;
      const nameInput = newelement.children[1] as HTMLInputElement;
      const codeInput = newelement.children[2] as HTMLInputElement;
      newelement.children[0].children[0].innerHTML = name;
      (newelement.children[0].children[0] as HTMLElement).style.backgroundColor = colorcode;
      nameInput.id = "element-name-" + index;
      nameInput.setAttribute("name", "element-name-" + index);
      nameInput.value = name;
      codeInput.id = "element-code-" + index;
      codeInput.setAttribute("name", "element-code-" + index);
      codeInput.value = colorcode;
      newelement.id = "element-" + index;
      newelement.setAttribute("data-name", "colorelements");
      newelement.style.display = "";
      const deleteButton = newelement.children[0].children[1] as HTMLButtonElement;
      deleteButton.onclick = () => deleteElement(index);
      newelement.classList.remove("setting-template");
      elementsContainer.appendChild(newelement);
      inputname.value = "";
      inputcode.value = "";
    }
  };

  /**
   * 色要素を削除する
   * 
   * @param index 削除する要素のインデックス
   */
  const deleteElement = (index: number) => {
    const element = document.getElementById("element-" + index);
    if (element) {
      element.remove();
      const elements = document.querySelectorAll('[data-name="colorelements"]');
      for (let i = 0; i < elements.length; i++) {
        const currentElement = elements[i] as HTMLElement;
        currentElement.id = "element-" + (i + 1);
        if (currentElement.children[1]) currentElement.children[1].id = "element-name-" + (i + 1);
        if (currentElement.children[2]) currentElement.children[2].id = "element-code-" + (i + 1);
      }
    }
  };

  /**
   * 設定を保存する
   * 色要素のリストを文字列形式に変換して送信する
   */
  const save_submit = () => {
    const elements = document.querySelectorAll('[data-name="colorelements"]');
    let elementsValue = "";
    for (let i = 1; i < elements.length + 1; i++) {
      const nameElement = document.getElementById(`element-name-${i}`) as HTMLInputElement;
      const codeElement = document.getElementById(`element-code-${i}`) as HTMLInputElement;
      if (nameElement && codeElement) {
        elementsValue += `${nameElement.value}=${codeElement.value}*`;
      }
    }
    const colorValueElement = document.getElementById("elements-color-value") as HTMLInputElement;
    const settingsForm = document.getElementById("settingsform") as HTMLFormElement;
    if (colorValueElement && settingsForm) {
      colorValueElement.value = elementsValue;
      settingsForm.submit();
    }
  };

  return (
    <>
      <section style={{ display: "flex", paddingBottom: "200px" }}>
        <div className="flex-grow-1 d-flex justify-content-center align-items-center p-4">
          <div className="w-100 sky-Setting-content">
            <h2 className="text-center">Setting</h2>
            <div className="card border-warning rounded sky-Setting-card">
              <div className="card-body">
                <form id="settingsform" name="settingsform" action={`${API_BASE_URL}/setting_post`} method="post">
                  <h5 className="fw-bold ms-2">elementColor</h5>
                  <div className="d-sm-flex">
                    <input className="form-control sky-input" id="elementcolor-name" type="text" placeholder="要素名 例:header" />
                    <input className="form-control sky-input" id="elementcolor-code" type="text" placeholder="Code 例:#000000" />
                    <button className="btn btn-warning fw-bold sky-bg-2" id="add-element-button" type="button" onClick={addElement} style={{ minWidth: "100px" }}>
                      <img className="sky-list-newCreate-img" src={newCreate} alt="newCreate" />
                    </button>
                    <input id="elements-color-value" name="elements-color-value" type="hidden" />
                  </div>
                  <div id="elements">
                    {colorElements.map((colorElement: any, index: number) => (
                      <div key={index} id={`element-${index + 1}`} data-name="colorelements">
                        <div className="d-sm-flex">
                          <div className="d-flex justify-content-center align-items-center w-100" style={{ backgroundColor: colorElement.code }}>
                            {colorElement.name}
                          </div>
                          <button className="btn btn-warning fw-bold sky-bg-2" type="button" onClick={() => deleteElement(index + 1)} style={{ minWidth: "100px" }}>
                            <img src={deleteImg} alt="delete" />
                          </button>
                        </div>
                        <input type="hidden" id={`element-name-${index + 1}`} name={`element-name-${index + 1}`} value={colorElement.name} />
                        <input type="hidden" id={`element-code-${index + 1}`} name={`element-code-${index + 1}`} value={colorElement.code} />
                      </div>
                    ))}
                  </div>

                  <div id="template-element" className="flex setting-delete" style={{ display: "none" }}>
                    <div className="d-sm-flex">
                      <div className="d-flex justify-content-center align-items-center w-100" style={{ backgroundColor: "" }}></div>
                      <button className="btn btn-warning fw-bold sky-bg-2" type="button" style={{ minWidth: "100px" }}>
                        <img src={deleteImg} alt="delete" />
                      </button>
                    </div>
                    <input type="hidden" id="" name="" value="" />
                    <input type="hidden" id="" name="" value="" />
                  </div>
                  <SaveButton targetModal="exampleModal" />
                </form>
                <Modal id="exampleModal" label="exampleModalLabel" title="登録しますか？" cansel="キャンセル" submit="登録" submitFun={save_submit} cancelButtonRef={cancelButtonRef} />
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  );
};
