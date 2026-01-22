import React from "react";

export const UserIDLabal: React.FC<{ id: string }> = ({ id }) => {
  return (
    <div className="mb-2 d-flex">
      <label htmlFor="username" className="sky-form-label me-2">
        ID:
      </label>
      <p>{id != "" ? id : "新規"}</p>
      <input type="hidden" name="id" value={id} />
    </div>
  );
};
