import React, { useState, useRef, useEffect } from "react";
import { ContentTextarea } from "../components/content/ContentTextarea";
import { ContentSelect } from "../components/content/ContentSelect";
import { ContentDeleteButton } from "../components/content/ContentDeleteButton";
import { ContentPreviewButton } from "../components/content/ContentPreviewButton";
import { ContentPublish } from "../components/content/ContentPublish";
import { ContentScheduleInput } from "../components/content/ContentScheduleInput";
import { ContentElementInput } from "../components/content/ContentElementInput";
import { EleResultData } from "../types";
import { ElementSelectModal } from "../components/content/ElementSelectModal";
import { Modal } from "../components/common/Modal";
import { SaveButton } from "../components/common/button/SaveButton";
import { TextInput } from "../components/common/input/TextInput";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export interface ContentData {
  screenName: string;
  schedule_published: string;
  schedule_unpublished: string;
  title: string;
  head: string;
  template: string;
  content: string;
  url: string;
  elementcolor: string;
  templateOutput: string;
  colorOutput: string;
  eleResults: Array<{
    eleResult: EleResultData;
  }>;
  publishflgKeep: string;
}

export interface ElementListData {
  results: Array<{
    id: string;
    created: string;
    updated: string;
    created_by: string;
    updated_by: string;
    schedule_published: string;
    schedule_unpublished: string;
    title: string;
    content: string;
    head: string;
    url: string;
    type: string;
    elementcolor: string;
    template: string;
  }>;
}

/**
 * 日時をフォーマットする
 * "yyyy-MM-dd HH:mm"形式に変換する
 * 
 * @param date 日時オブジェクト
 * @returns フォーマットされた日時文字列
 */
const formatDateTime = (date: Date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

/**
 * コンテンツ編集ページコンポーネント
 * コンテンツ、テンプレート、構成要素などの作成・編集を行う
 */
export const Content = () => {
  let screenName = "";
  const [schedule_published, setSchedule_published] = useState(formatDateTime(new Date()));
  const [schedule_unpublished, setSchedule_unpublished] = useState("");
  const [template, setTemplate] = useState("");
  const [head, setHead] = useState("");
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [url, setUrl] = useState("");
  const [id, setId] = useState("");
  const [mode, setMode] = useState("");
  const [elementcolor, setElementcolor] = useState("");
  const [templateOutput, setTemplateOutput] = useState("");
  const [colorOutput, setColorOutput] = useState("");
  const [eleResults, setEleResults] = useState<EleResultData[]>([
    {
      id: "",
      title: "コンテンツ部分",
      code: "",
    },
  ]);
  const [result, setResult] = useState<ContentData>({
    screenName: "",
    schedule_published: "",
    schedule_unpublished: "",
    template: "",
    title: "",
    head: "",
    content: "",
    url: "",
    elementcolor: "",
    templateOutput: "",
    colorOutput: "",
    eleResults: [],
    publishflgKeep: "",
  });
  const elementItems = useRef<EleResultData[]>([]);
  const cancelButtonRef = useRef<HTMLButtonElement>(null);
  const deleteButtonRef = useRef<HTMLButtonElement>(null);
  const [isPublished, setIsPublished] = useState(true);
  let elementAddIndex = useRef(0);
  const [list, setList] = useState<ElementListData>({
    results: [
      {
        id: "",
        created: "",
        updated: "",
        created_by: "",
        updated_by: "",
        schedule_published: "",
        schedule_unpublished: "",
        title: "",
        content: "",
        head: "",
        url: "",
        type: "",
        elementcolor: "",
        template: "",
      },
    ],
  });
  const [isFetched, setIsFetched] = useState(false);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    setMode(params.get("mode") || "");
    let fetchId = params.get("id") || "";
    let fetchmode = params.get("mode") || "";
    if (fetchId != "") {
      fetch(`${API_BASE_URL}/getcontent?mode=${fetchmode}&id=${fetchId}`, {
        method: "GET",
        credentials: "include",
        headers: {
          Accept: "application/json",
        },
      })
        .then((res) => res.json())
        .then((data) => {
          setResult(data);
          screenName = data.screenName || "";
          data.schedule_published == "" ? setSchedule_published(formatDateTime(new Date())) : setSchedule_published(data.schedule_published || "");
          setSchedule_unpublished(data.schedule_unpublished || "");
          setTemplate(data.template || "");
          setTitle(data.title || "");
          setHead(data.head || "");
          setContent(data.content || "");
          setUrl(data.url || "");
          setId(fetchId || "");
          setElementcolor(data.elementcolor || "");
          setTemplateOutput(data.templateOutput || "");
          setColorOutput(data.colorOutput || "");
          setEleResults(data.eleResults || [{ id: "", title: "", code: "" }]);
          if (fetchmode == "template" && !isFetched) {
            setIsFetched(true);
          }
          setIsPublished(data.publishflgKeep == 1 ? true : false);
        })
        .catch((error) => {
          console.error("Error fetching data:", error);
        });
    } else if (fetchmode == "template" && !isFetched) {
      setIsFetched(true);
    } else {
      if (fetchmode == "element") {
        fetch(`${API_BASE_URL}/getelement`, {
          credentials: "include",
          method: "GET",
        })
          .then((res) => res.text())
          .then((data) => {
            setColorOutput(data || "");
          })
          .catch((error) => {
            console.error("Error fetching data:", error);
          });
      } else {
        fetch(`${API_BASE_URL}/gettemplate`, {
          credentials: "include",
          method: "GET",
        })
          .then((res) => res.text())
          .then((data) => {
            setTemplateOutput(data || "");
          })
          .catch((error) => {
            console.error("Error fetching data:", error);
          });
      }
    }
  }, []);

  useEffect(() => {
    createElementItems();
  }, [isFetched]);

  /**
   * 公開フラグのチェックボックスを切り替える
   */
  const publishedCheckbox = () => {
    setIsPublished((prev) => !prev);
  };

  /**
   * プレビューを表示する
   * フォームのアクションを一時的に変更してプレビュー用のエンドポイントに送信する
   */
  const preview = () => {
    const contentform = document.getElementById("contentform") as HTMLFormElement;
    if (contentform) {
      contentform.action = API_BASE_URL + "/preview";
      contentform.target = "_blank";
      contentform.submit();
      contentform.action = API_BASE_URL + "/update_post";
      contentform.target = "";
    }
  };

  /**
   * フォームを送信する
   * バリデーションを実行し、成功時のみ送信する
   */
  const doSubmit = async () => {
    if (await validate()) {
      const contentForm = document.forms.namedItem("contentform") as HTMLFormElement;
      if (contentForm) {
        contentForm.submit();
      } else {
        console.error("Form with name 'contentform' not found");
        return;
      }
    } else {
      cancelButtonRef.current?.click();
    }
  };

  /**
   * フォームのバリデーションを実行する
   * 日時形式、URL形式、URL重複をチェックする
   * 
   * @returns バリデーション成功時true、失敗時false
   */
  const validate = async () => {
    const forbiddenChars = /[\s<>#%{}`\\^\[\]'""]/;
    let validateFlg = true;
    const errorElements = document.getElementsByClassName("errorMessage");

    while (errorElements.length > 0) {
      errorElements[0].remove();
    }
    if (schedule_published !== "" && !isValidDateTimeFormat(schedule_published)) {
      $("#schedule_published").addClass("is-invalid");
      validateFlg = false;
    }
    if (schedule_unpublished !== "" && !isValidDateTimeFormat(schedule_unpublished)) {
      $("#schedule_unpublished").addClass("is-invalid");
      validateFlg = false;
    }
    if (url.startsWith("/") || url.endsWith("/")) {
      $("#url").addClass("is-invalid");
      validateFlg = false;
    } else if (forbiddenChars.test(url)) {
      $("#url").addClass("is-invalid");
      validateFlg = false;
    } else if (url != "") {
      const urlExists = await isValidUrlMatch(url, id); // 非同期でチェックを待つ
      if (urlExists) {
        const newElement = document.createElement("div");
        newElement.textContent = "そのurlはすでに使われています";
        newElement.classList.add("ms-2");
        newElement.classList.add("errorMessage");
        newElement.style.color = "red";
        $(newElement).insertAfter("#url");
        validateFlg = false;
      }
    }
    return validateFlg;
  };

  /**
   * 日時文字列の形式を検証する
   * "yyyy-MM-dd HH:mm"形式かチェックする
   * 
   * @param dateTimeString 検証する日時文字列
   * @returns 有効な形式の場合true、無効な場合false
   */
  const isValidDateTimeFormat = (dateTimeString: string) => {
    const regex = /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/;
    if (!regex.test(dateTimeString)) return false;

    const [datePart, timePart] = dateTimeString.split(" ");
    const [year, month, day] = datePart.split("-").map(Number);
    const [hour, minute] = timePart.split(":").map(Number);
    const date = new Date(year, month - 1, day, hour, minute);
    return date.getFullYear() === year && date.getMonth() === month - 1 && date.getDate() === day && date.getHours() === hour && date.getMinutes() === minute;
  };

  /**
   * URLの重複をチェックする
   * サーバーに問い合わせて、指定されたURLが既に使用されているか確認する
   * 
   * @param url チェックするURL
   * @param myId 現在のコンテンツID（自分のIDは除外する）
   * @returns URLが重複している場合true、重複していない場合false
   */
  const isValidUrlMatch = async (url: string, myId: string) => {
    try {
      const res = await fetch(`${API_BASE_URL}/urlmatches?url=${url}&myId=${myId}`, {
        method: "GET",
        credentials: "include",
      });
      const data = await res.json();
      return data || false; // サーバーからのレスポンスを返す
    } catch (error) {
      console.error("Error fetching data:", error);
      return false;
    }
  };

  /**
   * コンテンツを削除する
   * 削除フォームを送信する
   */
  const doDelete = () => {
    const deleteform = document.forms.namedItem("deleteform") as HTMLFormElement;
    if (deleteform) {
      deleteform.submit();
    } else {
      console.error("Form with name 'contentform' not found");
      return;
    }
  };

  // Template--------------------------------------------------------------------------------------------------------------
  /**
   * 構成要素の一覧から配列を作成する
   * DOMから構成要素の情報を取得して配列に格納する
   */
  function createElementItems() {
    const elements = Array.from(document.querySelectorAll('[data-name="element-content"]'));
    elementItems.current = []; //初期化
    for (let i = 0; i < elements.length; i++) {
      let element = elements[i];
      let elementItem: EleResultData = { id: "", title: "", code: "" };
      let elementId = element.getAttribute("data-element-id");
      let idStr = "";
      elementId == "content" || elementId == "" ? (idStr = "content") : (idStr = elementId || "");
      elementItem["id"] = idStr;
      elementItem["title"] = element.getAttribute("data-element-title") || "";
      elementItem["code"] = element.getAttribute("data-element-code") || "";
      elementItems.current.push(elementItem);
      setEleResults([...elementItems.current]);
    }
  }

  /**
   * 構成要素の配列を一覧に反映する
   * 配列の内容をDOMに反映して表示を更新する
   */
  function reElements() {
    const elements = Array.from(document.querySelectorAll('[data-name="element-content"]'));
    for (let i = 0; i < elementItems.current.length; i++) {
      let element = elements[i] as HTMLFormElement;
      if (elementItems.current[i]["id"] === "" || elementItems.current[i]["id"] === "content") {
        let title = elementItems.current[i]["title"];
        let code = elementItems.current[i]["code"];
        element.id = "element-" + i;
        element.setAttribute("data-element-id", "content");
        element.setAttribute("data-element-title", title);
        element.setAttribute("data-element-code", code);
        element.style.backgroundColor = "#fff888";
        element.children[1].innerHTML = "コンテンツ部分";
      } else {
        let elementId = elementItems.current[i]["id"];
        let title = elementItems.current[i]["title"];
        let code = elementItems.current[i]["code"];
        element.id = "element-" + i;
        element.setAttribute("data-element-id", elementId);
        element.setAttribute("data-element-title", title);
        element.setAttribute("data-element-code", code);
        element.style.backgroundColor = code;
        element.children[1].innerHTML = title;
      }
    }
  }

  /**
   * 構成要素を上に移動する
   * 
   * @param event マウスイベント
   */
  const upButton = (event: React.MouseEvent<HTMLDivElement>) => {
    const indexStr = event.currentTarget.parentElement?.parentElement?.id;
    if (indexStr) {
      const index = parseInt(indexStr.replace("element-", ""), 10);
      if (-1 < index && 0 < elementItems.current.length) {
        $("#element-" + index).insertBefore("#element-" + (index - 1));
        createElementItems();
        reElements();
      }
    }
  };

  /**
   * 構成要素を下に移動する
   * 
   * @param event マウスイベント
   */
  const downButton = (event: React.MouseEvent<HTMLDivElement>) => {
    const indexStr = event.currentTarget.parentElement?.parentElement?.id;
    if (indexStr) {
      const index = parseInt(indexStr.replace("element-", ""), 10);
      if (index <= elementItems.current.length) {
        $("#element-" + index).insertAfter("#element-" + (index + 1));
        createElementItems();
        reElements();
      }
    }
  };

  /**
   * 構成要素を削除する
   * コンテンツ部分は削除できない
   * 
   * @param event マウスイベント
   */
  const eleDelete = (event: React.MouseEvent<HTMLDivElement | HTMLButtonElement>) => {
    const indexStr = event.currentTarget.parentElement?.parentElement?.id;
    const indexId = event.currentTarget.parentElement?.parentElement?.getAttribute("data-element-id");
    if (indexStr && indexId && indexId != "content" && indexId != "") {
      const indexToRemove = parseInt(indexStr.replace("element-", ""), 10);
      setEleResults((prev) => prev.filter((_, index) => index !== indexToRemove));
      elementItems.current.splice(indexToRemove, 1);
    }
  };

  /**
   * 構成要素の追加をキャンセルする
   * 最後に追加した構成要素を削除する
   */
  const eleCancelDelete = () => {
    setEleResults((prev) => prev.slice(0, -1)); // 最後の要素を削除
    elementItems.current.splice(-1, 1);
  };

  /**
   * 構成要素を追加する
   * 新しい構成要素をリストに追加する
   */
  const elementAdd = () => {
    const index = elementItems.current.length;
    const newElement: EleResultData = {
      id: "",
      title: "タイトル",
      code: "",
    };
    elementItems.current.push(newElement);
    elementAddIndex.current = index;
    setEleResults([...eleResults, newElement]);
  };

  // elementSelectModal----------------------------------------------------------------------------------------------
  useEffect(() => {
    fetch(`${API_BASE_URL}/getElementItem`, {
      method: "GET",
      credentials: "include",
      headers: {
        Accept: "application/json",
      },
    })
      .then((res) => res.json())
      .then((data) => {
        setList(data);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  }, []);

  /**
   * 構成要素を選択する
   * モーダル内で構成要素を選択した際に呼ばれる
   * 
   * @param index 選択した構成要素のインデックス
   */
  const elementSelect = (index: number) => {
    $(".entry").css("box-shadow", "none");
    $(".element-" + index).css("box-shadow", "0 0 0 .25rem rgba(13, 110, 253, .25)");
    const selectedIndex = document.getElementById("selected-index") as HTMLInputElement;
    if (selectedIndex) {
      selectedIndex.value = index.toString();
    }
  };

  /**
   * 選択した構成要素を確定する
   * モーダルで選択した構成要素を実際のリストに反映する
   */
  const elementSelecedComp = () => {
    const selectedIndex = document.getElementById("selected-index") as HTMLInputElement;
    if (selectedIndex) {
      const selectedId = document.getElementById(`element-${selectedIndex.value}-id`) as HTMLInputElement;
      const selectedTitle = document.getElementById(`element-${selectedIndex.value}-title`) as HTMLInputElement;
      const selectedCode = document.getElementById(`element-${selectedIndex.value}-code`) as HTMLInputElement;
      if (selectedId && selectedTitle && selectedCode) {
        elementItems.current[elementAddIndex.current]["id"] = selectedId.value;
        elementItems.current[elementAddIndex.current]["title"] = selectedTitle.value;
        elementItems.current[elementAddIndex.current]["code"] = selectedCode.value;
        reElements();
      }
    }
  };

  /**
   * テンプレートのフォームを送信する
   * 構成要素の配列を文字列形式に変換して送信する
   */
  function doSubmitTemp() {
    let eleContent = "";
    for (var i = 0; i < elementItems.current.length; i++) {
      if (elementItems.current[i]["id"] == "content") {
        eleContent += "###content###,";
      } else {
        eleContent += "###element(" + elementItems.current[i]["id"] + ")###,";
      }
    }
    eleContent = eleContent.slice(0, -1);
    const contentForm = document.forms.namedItem("contentform") as HTMLFormElement;
    const contentInput = document.getElementById("content") as HTMLInputElement;
    if (contentForm && contentInput) {
      contentInput.value = eleContent;
      contentForm.submit();
    } else {
      console.error("contentForm or content not found");
    }
  }

  return (
    <>
      <div className="flex-grow-1 d-flex justify-content-center align-items-center p-4 sky-content">
        <div className="w-100">
          <h2 className="text-center mb-3">{screenName}入稿画面</h2>
          <div className="card border-warning rounded sky-bg-1">
            <div className="card-body">
              <form id="contentform" name="contentform" action={`${API_BASE_URL}/update_post`} method="POST">
                {/* publish&preview  */}
                {mode == "" ? (
                  <>
                    <div className="sky-control mb-2">
                      <div className="sky-control-publish">
                        {/* published */}
                        <ContentScheduleInput id="schedule_published" label="start" name="schedule_published" value={schedule_published} setState={setSchedule_published} placeholder="Enter start" required={true} />
                        {/* unpublished */}
                        <ContentScheduleInput id="schedule_unpublished" label="end" name="schedule_unpublished" value={schedule_unpublished} setState={setSchedule_unpublished} placeholder="Enter end" required={false} />
                      </div>
                      <div className="d-flex justify-content-center">
                        {/* publish */}
                        <ContentPublish onChange={publishedCheckbox} isPublished={isPublished} />
                        {/* preview */}
                        <ContentPreviewButton onClick={preview} />
                        {/* delete */}
                        {id != "" && <ContentDeleteButton mode={mode} />}
                      </div>
                    </div>
                  </>
                ) : (
                  <>
                    {/* delete */}
                    {id != "" && (
                      <>
                        <ContentDeleteButton mode={mode} />
                        <input type="hidden" id="published" name="published" value="1" />
                      </>
                    )}
                  </>
                )}
                {/* template select */}
                {mode == "" && <ContentSelect isTemplate={true} id="template" label="template" name="template" value={template} setState={setTemplate} selectOutput={templateOutput} />}
                {/* element select */}
                {mode == "element" && <ContentSelect isTemplate={false} id="elementcolor" label="type" name="elementcolor" value={elementcolor} setState={setElementcolor} selectOutput={colorOutput} />}
                {/* title */}
                <TextInput isLogin={false} id="title" label="title" type="text" name="title" value={title} setState={setTitle} placeholder="Enter title" required={true} errorMessage="" />
                {/* head */}
                {mode == "" || mode == "template" ? <ContentTextarea id="head" isContent={false} label="head" name="head" value={head} setState={setHead} placeholder="Enter head" rows={10} /> : ""}
                {/* content */}
                {mode == "template" ? <ContentElementInput eleResults={eleResults} upButton={upButton} downButton={downButton} eleDelete={eleDelete} elementAdd={elementAdd} id={id} content={content} /> : <ContentTextarea id="content" isContent={true} label="content" name="content" value={content} setState={setContent} placeholder="Enter content" rows={10} />}
                {/* url */}
                {mode != "template" && <TextInput isLogin={false} id="url" label="url" type="text" name="url" value={url} setState={setUrl} placeholder="Enter url" required={true} errorMessage="urlの入力に誤りがあります" />}
                <SaveButton targetModal="exampleModal" />
                <input type="hidden" name="id" value={id} />
                <input type="hidden" name="type" value={mode} />
              </form>
              {/* element Select modal */}
              {mode == "template" && <ElementSelectModal results={list.results} elementSelect={elementSelect} eleCancelDelete={eleCancelDelete} elementSelecedComp={elementSelecedComp} />}
              {/* submit */}
              <Modal id="exampleModal" label="exampleModalLabel" title="登録しますか？" cansel="キャンセル" submit="登録" submitFun={mode == "template" ? doSubmitTemp : doSubmit} cancelButtonRef={cancelButtonRef} />
              {/* delete*/}
              <Modal id="deleteModal" label="deleteModalLabel" title="削除しますか？元には戻せません" cansel="キャンセル" submit="削除" submitFun={doDelete} cancelButtonRef={deleteButtonRef} />
              <form id="deleteform" name="deleteform" action={`${API_BASE_URL}/delete_post`} method="POST">
                <input type="hidden" name="id" id="deleteId" value={id} />
                <input type="hidden" name="mode" value={mode} />
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
