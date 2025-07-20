import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from "./components/Navbar";
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import PrivateRoute from "./components/PrivateRoute";
import Profile from "./pages/Profile";
import AddOrganization from "./pages/AddOrganization";
import AddHr from "./pages/AddHR";
import ViewHRs from "./pages/ViewHRs";
import OAuth2RedirectHandler from "./components/OAuth2RedirectHandler";
import SecuritySettings from "./components/SecuritySettings";
import TwoFactorSetup from "./components/TwoFactorSetup";
import TwoFactorVerify from "./components/TwoFactorVerify";
import AdminUsers from "./pages/AdminUsers";

const App = () => {
  return (
    <Router>
      <Navbar />
      <Routes>

        <Route path="/" element={<Navigate to="/login" />} />


        <Route
          path="/login"
          element={<Login />}
        />
        
        <Route
          path="/2fa-verify"
          element={<TwoFactorVerify />}
        />
        
        <Route
          path="/oauth2/redirect"
          element={<OAuth2RedirectHandler />}
        />
        
        <Route
          path="/*"
          element={
            <>
              <Routes>
                <Route
                  path="/dashboard"
                  element={
                    <PrivateRoute>
                      <Dashboard />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/profile"
                  element={
                    <PrivateRoute>
                      <Profile />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/add-organization"
                  element={
                    <PrivateRoute>
                      <AddOrganization />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/add-hr/:organizationId"
                  element={
                    <PrivateRoute>
                      <AddHr />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/view-hrs/:organizationId"
                  element={
                    <PrivateRoute>
                      <ViewHRs />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/security"
                  element={
                    <PrivateRoute>
                      <SecuritySettings />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/2fa-setup"
                  element={
                    <PrivateRoute>
                      <TwoFactorSetup />
                    </PrivateRoute>
                  }
                />
                <Route
                  path="/admin/users"
                  element={
                    <PrivateRoute>
                      <AdminUsers />
                    </PrivateRoute>
                  }
                />
              </Routes>
            </>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
