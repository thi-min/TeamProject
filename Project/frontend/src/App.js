import React from 'react';
import Layout from './layout/Layout';
import { BrowserRouter, Routes } from "react-router-dom";
import { AuthProvider } from './common/context/AuthContext'; //로그인 상태 Provider import

import { layoutRoutes } from './common/routes/generatedRoutes';
function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Layout>
          <Routes>{layoutRoutes}</Routes>
        </Layout>
      </BrowserRouter>
    </AuthProvider>
  );
}




export default App;
