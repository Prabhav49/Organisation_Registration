import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import AuthService from '../services/AuthService';
import '../css/Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  
  // Safely decode JWT token with error handling
  const getEmailFromToken = () => {
    try {
      const token = localStorage.getItem('user');
      if (token && token.includes('.')) {
        const payload = JSON.parse(atob(token.split('.')[1]));
        return payload.sub;
      }
      return null;
    } catch (error) {
      console.warn('Error decoding token:', error);
      return null;
    }
  };
  
  const email = getEmailFromToken();
  const userRole = localStorage.getItem('role');

  const handleLogout = () => {
    if (email) {
      AuthService.logout(email);
    }
    navigate('/login');
  };

  const handleProfileNavigation = () => {
    navigate('/profile');
  };

  // Don't show navbar if user is not logged in
  if (!email) {
    return null;
  }

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="navbar-logo">
        Outreach Management
      </Link>
      <div className="navbar-actions">
        <Link to="/dashboard" className="navbar-link">
          Dashboard
        </Link>
        {userRole === 'SUPER_ADMIN' && (
          <Link to="/admin/users" className="navbar-link">
            Admin Users
          </Link>
        )}
        <button className="navbar-profile" onClick={handleProfileNavigation}>
          Profile
        </button>
        <Link to="/security" className="navbar-link">
          Security
        </Link>
        <button className="navbar-logout" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
