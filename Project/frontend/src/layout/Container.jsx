
import React from "react";
import { useLocation } from "react-router-dom";
import { Link } from 'react-router-dom'; // 페이지 이동용

const Container = ({ children }) => {
  const location = useLocation();

  // "/" (메인페이지)면 wrap 제거
  const isMainPage = location.pathname === "/";

  return (
    <div id="container">
      {isMainPage ? (
        // 메인페이지 → wrap 제거
        children
      ) : (
        // 나머지 페이지 → wrap 포함
        <div className="wrap">{children}</div>
      )}
    </div>
  );
};

export default Container;
