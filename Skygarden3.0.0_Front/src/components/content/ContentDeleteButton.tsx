import React from "react";
import deleteImg from "../../common/image/trash.svg";

export const ContentDeleteButton: React.FC<{ mode: string }> = ({ mode }) => {
  return (
    <div className="sky-Content-delete">
      <a className={`btn btn-warning sky-Content-delete-item sky-bg-2 ${mode != "" && "ms-auto"}`} data-bs-toggle="modal" data-bs-target="#deleteModal">
        <img src={deleteImg} alt="delete" />
      </a>
    </div>
  );
};
