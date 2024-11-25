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

const App = () => {
  return (
    <Router>
      <Navbar />
      <Routes>
        {/* Redirect root (/) to /dashboard */}
        <Route path="/" element={<Navigate to="/dashboard" />} />

        {/* Private routes with protected access */}
        <Route
          path="/login"
          element={<Login />}
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
              </Routes>
            </>
          }
        />
      </Routes>
    </Router>
  );
};

export default App;
