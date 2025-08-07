import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from './common/context/AuthContext'; //로그인 상태 Provider import
import LoginPage from "./program/login/pages/LoginPage";
//import MyPage from "./program/login/pages/MyPage";
import QnaBbs from './bbs/questionbbs/qnabbs';
import LandReserveDatePage from "./program/reserve/land/pages/LandReserveDatePage";
import LandReserveFormPage from "./program/reserve/land/pages/LandReserveFormPage";
import LandReserveConfirmPage from "./program/reserve/land/pages/LandReserveConfirmPage";
import LandReserveSuccessPage from "./program/reserve/land/pages/LandReserveSuccessPage";

import VolunteerReserveDatePage from './program/reserve/volunteer/pages/VolunteerReserveDatePage';
import VolunteerReserveFormPage from './program/reserve/volunteer/pages/VolunteerReserveFormPage';
import VolunteerReserveConfirmPage from './program/reserve/volunteer/pages/VolunteerReserveConfirmPage';
import VolunteerReserveSuccessPage from './program/reserve/volunteer/pages/VolunteerReserveFormPage';

import Layout from './layout/Layout';

import React from 'react';
import NormalBbs from './bbs/normalbbs/NormalBbs';
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Layout>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/normalbbs" element={<NormalBbs />} />
            <Route path="/qnabbs" element={<QnaBbs />} />
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
    </AuthProvider>
  );
}
export default App;

