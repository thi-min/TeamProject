import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./program/login/pages/LoginPage";
//import MyPage from "./program/login/pages/MyPage";

import Layout from './layout/Layout';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}




export default App;
