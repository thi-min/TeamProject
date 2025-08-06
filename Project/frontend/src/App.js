import { BrowserRouter, Routes, Route } from "react-router-dom";

// 레이아웃
import Layout from './layout/Layout';
// 놀이터예약
import LandReserveDatePage from "./program/reserve/land/pages/LandReserveDatePage";
import LandReserveFormPage from "./program/reserve/land/pages/LandReserveFormPage";


function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/reserve/land/date" element={<LandReserveDatePage />} />
          <Route path="/reserve/land/form" element={<LandReserveFormPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;