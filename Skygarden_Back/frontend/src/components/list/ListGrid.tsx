import React from "react";
import { ListResult } from "../../types";
import { ListCard } from "./ListCard";

type ListGrid = {
  ListResults: ListResult[];
  mode: string;
};

/**
 * コンテンツ一覧グリッドコンポーネント
 * コンテンツ一覧をカード形式で表示する
 * 
 * @param ListResults コンテンツ一覧のデータ
 * @param mode モード（コンテンツタイプ）
 */
export const ListGrid: React.FC<ListGrid> = ({ ListResults, mode }) => {
  return (
    <>
      {ListResults.map((ListResult: any, index: number) => (
        <ListCard key={index} isContent={true} mode={mode} id={ListResult.id} title={ListResult.title} url={ListResult.url} updated={ListResult.updated} username={""} />
      ))}
    </>
  );
};
