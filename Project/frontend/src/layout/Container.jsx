import React from "react";

import { Link } from 'react-router-dom'; // 페이지 이동용

const Container = ({ children }) => {
  return (
    <div id="container">
      <div className="wrap">{children}</div>
    </div>
  );
};

export default Container;
