import React from "react";

type Props = {
  id: string;
  isContent: boolean;
  label: string;
  name: string;
  value: string;
  setState: React.Dispatch<React.SetStateAction<string>>;
  placeholder: string;
  rows: number;
};

export const ContentTextarea: React.FC<Props> = ({ id, isContent, label, name, value, setState, placeholder, rows }) => {
  return (
    <div className="mb-3">
      <label htmlFor={id} className="sky-form-label fw-bold ms-1">
        {label}
      </label>
      <textarea id={id} className={`form-control border-warning sky-input ${isContent ? "sky-input-content" : "sky-input-head"}`} name={name} value={value} onChange={(e) => setState(e.target.value)} placeholder={placeholder} rows={rows}></textarea>
    </div>
  );
};
