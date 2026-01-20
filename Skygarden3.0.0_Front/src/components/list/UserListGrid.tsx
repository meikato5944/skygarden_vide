import React from "react";
import { UserListResult } from "../../types";
import { ListCard } from "./ListCard";

type UserListGrid = {
  userListResults: UserListResult[];
};

/**
 * ユーザー一覧グリッドコンポーネント
 * ユーザー一覧をカード形式で表示する
 * 
 * @param userListResults ユーザー一覧のデータ
 */
export const UserListGrid: React.FC<UserListGrid> = ({ userListResults }) => {
  return (
    <>
      {userListResults.map((userListResult: any, index: number) => (
        <ListCard key={index} isContent={false} mode={""} id={userListResult.id} title={""} url={""} updated={""} username={userListResult.name} />
      ))}
    </>
  );
};
