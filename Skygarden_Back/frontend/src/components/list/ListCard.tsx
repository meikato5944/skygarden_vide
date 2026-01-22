import React from "react";

type ListCard = {
  key: number;
  isContent: boolean;
  mode: string;
  id: string;
  title: string;
  url: string;
  updated: string;
  username: string;
};

export const ListCard: React.FC<ListCard> = ({ key, isContent, mode, id, title, url, updated, username }) => {
  return (
    <>
      {isContent ? (
        <a href={`/content?mode=${mode}&id=${id}`} key={key}>
          <div className="sky-list-card">
            <p className="mb-0">ID: {id}</p>
            <h3 className="mb-1">{title}</h3>
            <div className="mb-0 d-flex justify-content-between">
              <div>URL: {url}</div>
              <div>updated: {updated}</div>
            </div>
          </div>
        </a>
      ) : (
        <a href={`/user?id=${id}`} key={key}>
          <div className="sky-list-card">
            <p className="mb-0">ID: {id}</p>
            <h4 className="mb-1">{username}</h4>
          </div>
        </a>
      )}
    </>
  );
};
