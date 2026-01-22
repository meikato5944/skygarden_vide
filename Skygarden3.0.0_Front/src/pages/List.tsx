import React, { useEffect, useState } from "react";
import { ListControl } from "../layouts/ListControl";
import { ListGrid } from "../components/list/ListGrid";
import { initialListData, ListData, UserListData } from "../types";
import { Pagination } from "../components/list/Pagination";
import { fetchListData } from "../utils/commonPrc";

/**
 * コンテンツ一覧ページコンポーネント
 * コンテンツ、テンプレート、構成要素などの一覧を表示する
 */
export const List = () => {
  const params = new URLSearchParams(window.location.search);
  const [mode, setMode] = useState(params.get("mode") || "");
  const [list, setList] = useState<ListData>(initialListData);
  useEffect(() => {
    fetchListData(true).then((data: ListData | UserListData) => {
      setList(data as ListData);
    });
  }, [window.location.search]);

  return (
    <>
      <section className="sky-list-container sky-section">
        <ListControl screenName={list.screenName} sortOutput={list.sortOutput} registerMessage={list.registerMessage} href={`/content?mode=${mode}`} />
        <ListGrid ListResults={list.results} mode={mode} />
        <Pagination pagerOutput={list.pagerOutput} />
      </section>
    </>
  );
};
