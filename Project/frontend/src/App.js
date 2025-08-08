import { BrowserRouter, Routes, Route } from "react-router-dom";
import { AuthProvider } from './common/context/AuthContext'; //로그인 상태 Provider import

import Layout from './layout/Layout';
import React from 'react';

import layoutRoutes from './common/routes/layoutRoutes';
function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>{layoutRoutes}</Routes>
      </Layout>
    </BrowserRouter>
  );
}
export default App;

