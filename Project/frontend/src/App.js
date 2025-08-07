import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from './common/context/AuthContext'; //로그인 상태 Provider import
import LoginPage from "./program/login/pages/LoginPage";
//import MyPage from "./program/login/pages/MyPage";
import QnaBbs from './bbs/questionbbs/qnabbs';

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
          </Routes>
        </Layout>
      </BrowserRouter>
    </AuthProvider>
  );
}




export default App;
