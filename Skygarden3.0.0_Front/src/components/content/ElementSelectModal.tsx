import React, { MouseEventHandler } from "react";

type Props = {
  results: Array<{
    id: string;
    created: string;
    updated: string;
    created_by: string;
    updated_by: string;
    schedule_published: string;
    schedule_unpublished: string;
    title: string;
    content: string;
    head: string;
    url: string;
    type: string;
    elementcolor: string;
    template: string;
  }>;
  elementSelect: (index: number) => void;
  eleCancelDelete: MouseEventHandler<HTMLButtonElement>;
  elementSelecedComp: MouseEventHandler<HTMLButtonElement>;
};

export const ElementSelectModal: React.FC<Props> = ({ results, elementSelect, eleCancelDelete, elementSelecedComp }) => {
  return (
    <>
      <div className="modal fade" id="element-selectModal" tabIndex={-1} aria-labelledby="element-selectModalLabel" aria-hidden="true" data-bs-backdrop="static" data-bs-keyboard="false">
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">Element Select</div>
            <section className="sky-list-container">
              {results.map((result: any, index: number) => (
                <>
                  <a
                    href="#"
                    onClick={() => {
                      elementSelect(index);
                    }}
                    key={index}
                  >
                    <div className={`sky-list-card entry element-${index}`} style={{ backgroundColor: "elementcolor" }}>
                      <p className="mb-0">ID: {result.id}</p>
                      <h5 className="mb-1">{result.title}</h5>
                    </div>
                  </a>
                  <input type="hidden" id={`element-${index}-id`} value={result.id} />
                  <input type="hidden" id={`element-${index}-title`} value={result.title} />
                  <input type="hidden" id={`element-${index}-code`} value={result.elementcolor} />
                </>
              ))}
              <input type="hidden" id="selected-index" value="" />
              <input type="hidden" id="selected-value" value="" />
            </section>
            <div className="modal-footer d-flex justify-content-center align-items-center">
              <button type="button" className="btn mx-5" data-bs-dismiss="modal" onClick={eleCancelDelete}>
                キャンセル
              </button>
              <button type="button" className="btn btn-warning px-4 mx-5" data-bs-dismiss="modal" onClick={elementSelecedComp}>
                選択
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
