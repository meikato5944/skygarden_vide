import React, { MouseEventHandler } from "react";
import elementAddImg from "../../common/image/plus-lg.svg";
import elementDelImg from "../../common/image/trash.svg";
import { EleResultData } from "../../types";

type Props = {
  eleResults: EleResultData[];
  upButton: MouseEventHandler<HTMLDivElement>;
  downButton: MouseEventHandler<HTMLDivElement>;
  eleDelete: MouseEventHandler<HTMLDivElement>;
  elementAdd: MouseEventHandler<HTMLAnchorElement>;
  id: string;
  content: string;
};

export const ContentElementInput: React.FC<Props> = ({ eleResults, upButton, downButton, eleDelete, elementAdd, id, content }) => {
  return (
    <>
      <div className="mb-3">
        <label htmlFor="url" className="sky-form-label fw-bold ms-1">
          content
        </label>
        <div className="form-control border-warning" id="elements" data-name="content">
          {eleResults.map((eleResult, index: number) =>
            eleResult.id == null || eleResult.id == "" || eleResult.id == "content" ? (
              <div className="sky-Content-element-card justify-content-between" key={eleResult.id} id={`element-${index}`} data-name="element-content" data-element-id="content" data-element-title="" data-element-code="">
                <div>
                  <div className="btn btn-warning mx-1 sky-Content-element-arrow" onClick={upButton}>
                    ▲
                  </div>
                  <div className="btn btn-warning mx-1 sky-Content-element-arrow" onClick={downButton}>
                    ▼
                  </div>
                </div>
                <h5 className="d-flex justify-content-center align-items-center">コンテンツ部分</h5>
                <div className="d-flex justify-content-center align-items-center">
                  <div className="btn btn-warning sky-Content-element-delete" onClick={eleDelete}>
                    <img src={elementDelImg} alt="elementDelImg" />
                  </div>
                </div>
              </div>
            ) : (
              <div className="sky-Content-element-card justify-content-between" key={eleResult.id} id={`element-${index}`} data-name="element-content" data-element-id={eleResult.id} data-element-title={eleResult.title} data-element-code={eleResult.code} style={{ backgroundColor: eleResult.code }}>
                <div>
                  <div className="btn btn-warning mx-1 sky-Content-element-arrow" onClick={upButton}>
                    ▲
                  </div>
                  <div className="btn btn-warning mx-1 sky-Content-element-arrow" onClick={downButton}>
                    ▼
                  </div>
                </div>
                <h5 className="d-flex justify-content-center align-items-center">{eleResult.title}</h5>
                <div>
                  <div className="btn btn-warning sky-Content-element-delete" onClick={eleDelete}>
                    <img src={elementDelImg} alt="elementDelImg" />
                  </div>
                </div>
              </div>
            )
          )}
        </div>
        {/*elementAdd */}
        <div>
          <a href="#" onClick={elementAdd} data-bs-toggle="modal" data-bs-target="#element-selectModal">
            <div className="d-flex justify-content-center align-items-center btn btn-warning p-3 my-2 sky-bg-2">
              <img className="sky-list-newCreate-img" src={elementAddImg} alt="elementAdd" />
            </div>
          </a>
        </div>
      </div>
      <input type="hidden" name="content" id="content" value={content} />
    </>
  );
};
