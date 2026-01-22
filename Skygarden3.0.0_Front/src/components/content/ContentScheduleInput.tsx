import React from "react";

type Props = {
  id: string;
  label: string;
  name: string;
  value: string;
  setState: React.Dispatch<React.SetStateAction<string>>;
  placeholder: string;
  required: boolean;
};

export const ContentScheduleInput: React.FC<Props> = ({ id, label, name, value, setState, placeholder, required }) => {
  return (
    <div className="sky-control-publish-input">
      <label htmlFor={id} className="sky-form-label fw-bold ms-1 mb-1">
        {label}
      </label>
      <input type="datetime-local" className="form-control border-warning sky-input" id={id} name={name} value={value} onChange={(e) => setState(e.target.value.replaceAll("/", "-").replaceAll("T", " "))} placeholder={placeholder} required={required} />
      <div className="invalid-feedback">日時形式で入力してください</div>
    </div>
  );
};
