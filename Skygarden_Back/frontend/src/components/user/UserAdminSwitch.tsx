import React, { ChangeEventHandler } from "react";

type Props = {
  onChange: ChangeEventHandler<HTMLInputElement>;
  admin: boolean;
};

export const UserAdminSwitch: React.FC<Props> = ({ onChange, admin }) => {
  return (
    <div className="form-check form-switch mb-1 sky-User-adminSwitch">
      <input className="form-check-input sky-input-switch" type="checkbox" role="switch" id="admin" name="admin" value="1" onChange={onChange} checked={admin} />
      <label className="form-check-label ms-2" htmlFor="admin">
        admin
      </label>
    </div>
  );
};
