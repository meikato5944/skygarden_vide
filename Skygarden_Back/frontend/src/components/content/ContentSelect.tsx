import React from "react";

type Props = {
  isTemplate: boolean;
  id: string;
  label: string;
  name: string;
  value: string;
  setState: React.Dispatch<React.SetStateAction<string>>;
  selectOutput: string;
};

export const ContentSelect: React.FC<Props> = ({ isTemplate, id, label, name, value, setState, selectOutput }) => {
  return (
    <>
      {isTemplate ? (
        <div className="mb-3 sky-input-pulldown">
          <label htmlFor={id} className="sky-form-label fw-bold ms-1">
            {label}
          </label>
          <select className="form-select form-select-lg border-warning" aria-label=".form-select-lg example" id={id} name={name} value={value} onChange={(e) => setState(e.target.value)} dangerouslySetInnerHTML={{ __html: selectOutput }}></select>
        </div>
      ) : (
        <div className="mb-3 sky-input-pulldown">
          <label htmlFor={id} className="sky-form-label fw-bold ms-1">
            {label}
          </label>
          <select className="form-select form-select-lg border-warning" aria-label=".form-select-lg example" style={{ backgroundColor: value }} id={id} name={name} value={value} onChange={(e) => setState(e.target.value)} dangerouslySetInnerHTML={{ __html: selectOutput }}></select>
        </div>
      )}
    </>
  );
};
