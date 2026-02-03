import React, { lazy, Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Layout from './components/layout/Layout';
import PrivateRoute from './components/PrivateRoute';
import LoadingSpinner from './components/common/LoadingSpinner';

// Lazy loading des pages
const Login = lazy(() => import('./pages/Login'));
const Register = lazy(() => import('./pages/Register'));
const Dashboard = lazy(() => import('./pages/Dashboard'));
const Learning = lazy(() => import('./pages/Learning'));
const LessonDetail = lazy(() => import('./pages/LessonDetail'));
const PlantationHistory = lazy(() => import('./pages/PlantationHistory'));
const CarbonMetrics = lazy(() => import('./pages/CarbonMetrics'));
const UserProfile = lazy(() => import('./pages/UserProfile'));
const NotFound = lazy(() => import('./pages/NotFound'));

// Composant de chargement
const PageLoader = () => (
  <div className="flex justify-center items-center h-screen">
    <LoadingSpinner size="lg" />
  </div>
);

function App() {
  return (
    <Router>
      <AuthProvider>
        <Suspense fallback={<PageLoader />}>
          <Routes>
            {/* Routes publiques */}
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />

            {/* Routes protégées */}
            <Route
              path="/dashboard"
              element={
                <PrivateRoute>
                  <Layout>
                    <Dashboard />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/learning"
              element={
                <PrivateRoute>
                  <Layout>
                    <Learning />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/learning/:id"
              element={
                <PrivateRoute>
                  <Layout>
                    <LessonDetail />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/plantations"
              element={
                <PrivateRoute>
                  <Layout>
                    <PlantationHistory />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/impact"
              element={
                <PrivateRoute>
                  <Layout>
                    <CarbonMetrics />
                  </Layout>
                </PrivateRoute>
              }
            />
            <Route
              path="/profile"
              element={
                <PrivateRoute>
                  <Layout>
                    <UserProfile />
                  </Layout>
                </PrivateRoute>
              }
            />

            {/* Redirection */}
            <Route path="/" element={<Navigate to="/dashboard" />} />
            
            {/* 404 */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </Suspense>
      </AuthProvider>
    </Router>
  );
}

export default App;