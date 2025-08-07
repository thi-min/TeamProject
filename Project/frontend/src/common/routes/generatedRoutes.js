// src/routes/generatedRoutes.js
import { Route } from 'react-router-dom';
import { routeAccessMap } from './router';

// ✅ 여기에 연결할 컴포넌트들을 import
// import MainPage from '../pages/MainPage';
import LoginPage from '../../program/login/pages/LoginPage';
// ... 기타 필요한 컴포넌트

// ✅ path → 컴포넌트 매핑
const componentMap = {
//   '/': <MainPage />,
  '/login': <LoginPage />,
  // 나머지도 계속 추가
};

// ✅ routes 생성 유틸
function generateRoutes(map) {
  const routes = [];

  map.forEach((item) => {
    if (componentMap[item.path]) {
      routes.push(<Route key={item.path} path={item.path} element={componentMap[item.path]} />);
    }

    if (item.children) {
      item.children.forEach((child) => {
        if (componentMap[child.path]) {
          routes.push(<Route key={child.path} path={child.path} element={componentMap[child.path]} />);
        }
      });
    }
  });

  return routes;
}

export const layoutRoutes = generateRoutes(routeAccessMap);
