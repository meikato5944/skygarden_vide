import React, { useEffect, useState } from "react";
import { ListControl } from "../layouts/ListControl";
import { Pagination } from "../components/list/Pagination";
import { UserListGrid } from "../components/list/UserListGrid";
import { fetchListData } from "../utils/commonPrc";
import { initialUserListData, ListData, UserListData } from "../types";

/**
 * ユーザー一覧ページコンポーネント
 * ユーザーの一覧を表示する
 */
export const UserList = () => {
  const [list, setList] = useState<UserListData>(initialUserListData);

  useEffect(() => {
    fetchListData(false).then((data: ListData | UserListData) => {
      setList(data as UserListData);
    });
  }, []);

  return (
    <>
      <section className="sky-list-container sky-section">
        <ListControl screenName={"ユーザ一"} sortOutput={list.sortOutput} registerMessage={list.registerMessage} href={"/user"} />
        <UserListGrid userListResults={list.results} />
        <Pagination pagerOutput={list.pagerOutput} />
      </section>
    </>
  );
};
