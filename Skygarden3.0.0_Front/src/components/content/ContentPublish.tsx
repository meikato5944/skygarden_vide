import React, { ChangeEventHandler } from "react";

type Props = {
  onChange: ChangeEventHandler<HTMLInputElement>;
  isPublished: boolean;
};

export const ContentPublish: React.FC<Props> = ({ onChange, isPublished }) => {
  return (
    <div className="form-check form-switch sky-Content-publish">
      <input className="form-check-input sky-input-switch" type="checkbox" role="switch" id="published" name="published" value="1" onChange={onChange} checked={isPublished} />
      <label className="form-check-label ms-1 pt-1" htmlFor="published">
        publish
      </label>
    </div>
  );
};
