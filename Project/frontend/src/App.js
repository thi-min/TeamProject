import { BrowserRouter, Routes } from "react-router-dom";

import Layout from "./layout/Layout";
import React from "react";

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
