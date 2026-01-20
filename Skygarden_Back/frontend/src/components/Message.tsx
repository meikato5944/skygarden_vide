import React from "react";

export const Message: React.FC<{ message: string }> = ({ message }) => {
  return <div className="ms-2">{message}</div>;
};
