import React from 'react';
import { useNavigate, Link } from 'react-router-dom';
import AuthService from '../services/AuthService';
import '../css/Navbar.css';

const Navbar = () => {
  const navigate = useNavigate();
  const email = localStorage.getItem('user') ? JSON.parse(atob(localStorage.getItem('user').split('.')[1])).sub : null;

  const handleLogout = () => {
    if (email) {
      AuthService.logout(email);
    }
    navigate('/login');
  };

  const handleProfileNavigation = () => {
    navigate('/profile');
  };

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="navbar-logo">
        Outreach Management
      </Link>
      <div className="navbar-actions">
        <button className="navbar-profile" onClick={handleProfileNavigation}>
          Profile
        </button>
        <button className="navbar-logout" onClick={handleLogout}>
          Logout
        </button>
      </div>
    </nav>
  );
};

export default Navbar;
