import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom'; 
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import PrivateRoute from './components/PrivateRoute';
import Profile from './pages/Profile'; 
import AddOrganization from './pages/AddOrganization'; 
import AddHr from './pages/AddHR'; 
import ViewHRs from './pages/ViewHRs';

const App = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
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
    </Router>
  );
};

export default App;
