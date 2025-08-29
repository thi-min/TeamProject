import { BrowserRouter, Routes } from "react-router-dom";

<<<<<<< HEAD
import Layout from "./layout/Layout";
import React from "react";
=======
import Layout from './layout/Layout';
>>>>>>> refs/remotes/origin/youseungjupage

import Mainpage from "./main/pages/MainPage";
import layoutRoutes from "./common/routes/layoutRoutes";

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
