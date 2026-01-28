import { createBrowserRouter, Navigate } from 'react-router-dom';
import { AdminLayout } from '@/layouts/AdminLayout';
import { LoginPage } from '@/features/auth/LoginPage';
import { DashboardPage } from '@/features/dashboard/DashboardPage';
import { CookiesPage } from '@/features/cookies/CookiesPage';
import { PointUrlsPage } from '@/features/point-urls/PointUrlsPage';
import { PointLogsPage } from '@/features/point-logs/PointLogsPage';
import { SitesPage } from '@/features/sites/SitesPage';

export const router = createBrowserRouter([
  {
    path: '/login',
    element: <LoginPage />,
  },
  {
    path: '/',
    element: <AdminLayout />,
    children: [
      {
        index: true,
        element: <Navigate to="/dashboard" replace />,
      },
      {
        path: 'dashboard',
        element: <DashboardPage />,
      },
      {
        path: 'cookies',
        element: <CookiesPage />,
      },
      {
        path: 'point-urls',
        element: <PointUrlsPage />,
      },
      {
        path: 'point-logs',
        element: <PointLogsPage />,
      },
      {
        path: 'sites',
        element: <SitesPage />,
      },
    ],
  },
]);
