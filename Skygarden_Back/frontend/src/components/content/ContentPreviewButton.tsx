import React, { MouseEventHandler } from "react";
import previewImg from "../../common/image/preview.svg";

type Props = {
  onClick: MouseEventHandler<HTMLAnchorElement>;
};

export const ContentPreviewButton: React.FC<Props> = ({ onClick }) => {
  return (
    <div className="sky-Content-preview">
      <a className="btn btn-warning sky-Content-preview-item sky-bg-2" id="preview-button" onClick={onClick}>
        <img src={previewImg} alt="preview" />
      </a>
    </div>
  );
};
