// src/pages/Dashboard.js

import React from 'react';
import { useNavigate } from 'react-router-dom'; // Use useNavigate in react-router-dom v6

const Dashboard = () => {
  const navigate = useNavigate(); // For navigation

  const handleLogout = () => {
    // Remove token from localStorage
    localStorage.removeItem('user');
    
    // Redirect to login page
    navigate('/login');
  };

  return (
    <div className="dashboard">
      <h1>Welcome to the Dashboard!</h1>
      <button onClick={handleLogout}>Logout</button>
    </div>
  );
};

export default Dashboard;
