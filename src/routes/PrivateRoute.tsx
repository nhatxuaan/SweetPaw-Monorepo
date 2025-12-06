import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';

interface PrivateRouteProps {
  children: ReactNode;
}

export default function PrivateRoute({ children }: PrivateRouteProps) {
  const token = localStorage.getItem("adminToken");
  

  if (!token) {
    return <Navigate to="/sign-in" replace />;
  }

  return <>{children}</>;
}
