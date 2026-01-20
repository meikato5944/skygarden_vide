import React from "react";

export const SaveButton: React.FC<{ targetModal: string }> = ({ targetModal }) => {
  return (
    <div className="text-center">
      <button type="button" className="btn btn-warning w-100 mb-2 mt-5 sky-submit sky-bg-2" data-bs-toggle="modal" data-bs-target={`#${targetModal}`}>
        Save
      </button>
    </div>
  );
};
