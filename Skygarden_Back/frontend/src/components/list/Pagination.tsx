import React from "react";

type Pagination = {
  pagerOutput: string;
};

export const Pagination: React.FC<Pagination> = ({ pagerOutput }) => {
  return (
    <nav aria-label="Page navigation example">
      <ul className="pagination justify-content-center align-items-center mt-5" dangerouslySetInnerHTML={{ __html: pagerOutput }}></ul>
    </nav>
  );
};
