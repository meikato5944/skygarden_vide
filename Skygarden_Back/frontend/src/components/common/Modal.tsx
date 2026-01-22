import React, { RefObject } from "react";

type Modal = {
  id: string;
  label: string;
  title: string;
  cansel: string;
  submit: string;
  submitFun: () => void;
  cancelButtonRef: RefObject<HTMLButtonElement | null>;
};

export const Modal: React.FC<Modal> = ({ id, label, title, cansel, submit, submitFun, cancelButtonRef }) => {
  return (
    <div className="modal fade" id={id} tabIndex={-1} aria-labelledby={label} aria-hidden="true">
      <div className="modal-dialog modal-dialog-centered">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title" id={label}>
              {title}
            </h5>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn me-2" data-bs-dismiss="modal" ref={cancelButtonRef}>
              {cansel}
            </button>
            <button type="button" className="btn btn-warning px-4" onClick={submitFun}>
              {submit}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
