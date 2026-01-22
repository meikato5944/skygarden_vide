import React from "react";
import { sortList } from "../../utils/commonPrc";

export const Sort: React.FC<{ sortOutput: string }> = ({ sortOutput }) => {
  return (
    <div className="col-xs-12 col-sm-3 mb-2 sky-input-pulldown">
      <select className="form-select sky-bg-4 sky-list-sort sky-fc-1" id="sort" name="sort" onChange={sortList} dangerouslySetInnerHTML={{ __html: sortOutput }}></select>
    </div>
  );
};
