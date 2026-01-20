import React from "react";
import newCreate from "../../../common/image/plus-lg.svg";

export const NewCreateButton: React.FC<{ href: string }> = ({ href }) => {
  return (
    <div className="mb-3 sky-button-newCreate">
      <a href={href}>
        <button className="btn btn-warning w-100 sky-bg-4">
          <img className="sky-list-newCreate-img" src={newCreate} alt="newCreate" />
          <span>newCreate</span>
        </button>
      </a>
    </div>
  );
};
