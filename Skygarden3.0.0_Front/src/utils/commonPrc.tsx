import { initialListData, initialUserListData, ListData, UserListData } from "../types";

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

/**
 * ソート順を変更する
 * 選択されたソート順に基づいてURLを更新し、ページをリロードする
 */
export const sortList = () => {
  const sort = document.getElementById("sort") as HTMLSelectElement;
  if (sort) {
    const sortValue = sort.value;
    const params = new URLSearchParams(window.location.search);
    const modeValue = params.get("mode") || "";
    const pageValue = params.get("page") || "";
    const url = window.location.pathname;
    window.location.href = url + "?mode=" + modeValue + "&sort=" + sortValue + "&page=" + pageValue;
  }
};

/**
 * 一覧データを取得する
 * コンテンツ一覧またはユーザー一覧を取得する
 * 
 * @param isContent コンテンツ一覧の場合true、ユーザー一覧の場合false
 * @returns 一覧データ（ListDataまたはUserListData）
 */
export const fetchListData = (isContent: boolean): Promise<ListData | UserListData> => {
  const params = new URLSearchParams(window.location.search);
  const mode = params.get("mode") || "";
  const sort = params.get("sort") || "";
  const page = params.get("page") || "";
  let url = "";
  if (isContent) {
    url = `${API_BASE_URL}/getlist?mode=${mode}&sort=${sort}&page=${page}`;
  } else {
    url = `${API_BASE_URL}/getlist-user?sort=${sort}&page=${page}`;
  }
  return fetch(url, {
    method: "GET",
    credentials: "include",
    headers: {
      Accept: "application/json",
    },
  })
    .then((res) => res.json())
    .then((data) => {
      return data;
    })
    .catch((error) => {
      console.error("Error fetching data:", error);
      if (isContent) {
        return initialListData;
      } else {
        return initialUserListData;
      }
    });
};

/**
 * セッションデータを取得する
 * サーバーからセッション属性の値を取得する
 * 
 * @param attribute 取得するセッション属性名
 * @returns セッション属性の値
 */
export const getSessionData = (attribute: string): Promise<string> => {
  return fetch(`${API_BASE_URL}/get-session?attribute=${attribute}`, {
    method: "GET",
    credentials: "include",
  })
    .then((res) => res.text())
    .then((data) => {
      return data;
    })
    .catch((error) => {
      console.error("Error fetching data:", error);
      return "";
    });
};
