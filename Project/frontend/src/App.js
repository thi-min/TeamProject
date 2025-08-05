import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LandReservePage from "./program/reserve/land/pages/LandReservePage.jsx";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<div>메인 페이지</div>} />
        <Route path="/reserve/land" element={<LandReservePage />} />
      </Routes>
    </Router>
  );
}

export default App;