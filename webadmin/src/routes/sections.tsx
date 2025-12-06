import type { RouteObject } from 'react-router';

import { lazy, Suspense } from 'react';
import { Outlet } from 'react-router-dom';
import { Navigate } from 'react-router-dom';
import { varAlpha } from 'minimal-shared/utils';

import Box from '@mui/material/Box';
import LinearProgress, { linearProgressClasses } from '@mui/material/LinearProgress';

import { AuthLayout } from 'src/layouts/auth';
import { DashboardLayout } from 'src/layouts/dashboard';

import PrivateRoute from './PrivateRoute';

// ----------------------------------------------------------------------

export const DashboardPage = lazy(() => import('src/pages/dashboard'));
export const BlogPage = lazy(() => import('src/pages/messages'));
export const UserPage = lazy(() => import('src/pages/user'));
export const SignInPage = lazy(() => import('src/pages/sign-in'));
export const ProductsPage = lazy(() => import('src/pages/products'));
export const Page404 = lazy(() => import('src/pages/page-not-found'));
export const ProductEditPage = lazy(() => import('src/pages/product-detail'));
export const UserEditPage = lazy(() => import('src/pages/user-detail'));
export const OrderPage = lazy(() => import('src/pages/order'));
export const ProductCreatePage = lazy(() => import('src/sections/product/product-create'));
export const UserCreatePage = lazy(() => import('src/sections/user/user-create'));

export const OrderDetailPage = lazy(() => import('src/sections/order/order-detail'));
export const OrderUpdatePage = lazy(() => import('src/sections/order/order-update'));
export const MessagesPage = lazy(() => import('src/pages/messages'));

export const DiscountPage = lazy(() => import('src/pages/discount'));
export const DiscountCreatePage = lazy(() => import('src/sections/discount/discount-create'));
export const DiscountEditPage = lazy(() => import('src/sections/discount/discount-edit'));

const renderFallback = () => (
  <Box
    sx={{
      display: 'flex',
      flex: '1 1 auto',
      alignItems: 'center',
      justifyContent: 'center',
    }}
  >
    <LinearProgress
      sx={{
        width: 1,
        maxWidth: 320,
        bgcolor: (theme) => varAlpha(theme.vars.palette.text.primaryChannel, 0.16),
        [`& .${linearProgressClasses.bar}`]: { bgcolor: 'text.primary' },
      }}
    />
  </Box>
);

export const routesSection: RouteObject[] = [
  {
    index: true,
    element: <Navigate to="/sign-in" replace />, // Mở web => sign-in
  },
  {
    path: 'sweetpaw',
    element: (
      <PrivateRoute>
        <DashboardLayout>
          <Suspense fallback={renderFallback()}>
            <Outlet />
          </Suspense>
        </DashboardLayout>
      </PrivateRoute>
    ),
    children: [
      { index: true, element: <DashboardPage /> },
      {
        path: 'user',
        children: [
          { index: true, element: <UserPage /> },
          { path: ':id/edit', element: <UserEditPage /> },
          { path: 'new/edit', element: <UserCreatePage /> },
        ],
      },
      { path: 'discount',
        children: [
          { index: true, element: <DiscountPage /> },
          { path: ':id/edit', element: <DiscountEditPage /> },
          { path: 'new/edit', element: <DiscountCreatePage /> },
        ],
      },

      { path: 'messages', element: <MessagesPage /> },

      // Trang Đơn hàng

      {
        path: 'order', // Đường dẫn cha: /order
        children: [
          { index: true, element: <OrderPage /> },
          { path: ':id/view', element: <OrderDetailPage /> },
          { path: ':id/update', element: <OrderUpdatePage /> },
        ],
      },
      

      { path: 'order', element: <OrderPage /> },
    
      {
        path: 'products', // Đường dẫn cha: /products
        children: [
          // Route con: /products (Hiển thị danh sách sản phẩm)
          { index: true, element: <ProductsPage /> },
          // Route con: /products/:id/edit (Hiển thị trang chỉnh sửa sản phẩm)
          { path: ':id/edit', element: <ProductEditPage /> },
          { path: 'new/edit', element: <ProductCreatePage /> },
        ],
      },
    ],
  },
  {
    path: 'sign-in',
    element: (
      <AuthLayout>
        <SignInPage />
      </AuthLayout>
    ),
  },
  // {
  //   path: '404',
  //   element: <Page404 />,
  // },
  { path: '*', element: <Page404 /> },
];
