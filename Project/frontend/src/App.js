import { BrowserRouter, Routes, Route } from "react-router-dom";

// 레이아웃
import Layout from './layout/Layout';
// 놀이터예약
import LandReserveDatePage from "./program/reserve/land/pages/LandReserveDatePage";
import LandReserveFormPage from "./program/reserve/land/pages/LandReserveFormPage";
import LandReserveConfirmPage from "./program/reserve/land/pages/LandReserveConfirmPage";
import LandReserveSuccessPage from "./program/reserve/land/pages/LandReserveSuccessPage";
// 봉사예약
import VolunteerReserveDatePage from './program/reserve/volunteer/pages/VolunteerReserveDatePage';
import VolunteerReserveFormPage from './program/reserve/volunteer/pages/VolunteerReserveFormPage';
import VolunteerReserveConfirmPage from './program/reserve/volunteer/pages/VolunteerReserveConfirmPage';
import VolunteerReserveSuccessPage from './program/reserve/volunteer/pages/VolunteerReserveFormPage';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/reserve/land/date" element={<LandReserveDatePage />} />
          <Route path="/reserve/land/form" element={<LandReserveFormPage />} />
          <Route path="/reserve/land/confirm" element={<LandReserveConfirmPage />} />
          <Route path="/reserve/land/success" element={<LandReserveSuccessPage />} />
          <Route path="/reserve/volunteer/date" element={<VolunteerReserveDatePage />} />
          <Route path="/reserve/volunteer/form" element={<VolunteerReserveFormPage />} />
          <Route path="/reserve/volunteer/confirm" element={<VolunteerReserveConfirmPage />} />
          <Route path="/reserve/volunteer/success" element={<VolunteerReserveSuccessPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;