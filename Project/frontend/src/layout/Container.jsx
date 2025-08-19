import { Link } from 'react-router-dom';

import React from "react";


const Container = ({ children }) => {
  return (
    <div id="container">
      <div className="wrap">{children}</div>
    </div>
  );
};

export default Container;
