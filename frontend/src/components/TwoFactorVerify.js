import React, { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import { BASEURL } from '../helper/helper';
import '../css/TwoFactorVerify.css';

const TwoFactorVerify = () => {
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  
  const email = location.state?.email;
  const password = location.state?.password;

  const handleVerify = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (code.length !== 6) {
      setError('Please enter a valid 6-digit code.');
      setIsLoading(false);
      return;
    }

    try {
      const response = await axios.post(`${BASEURL}/api/v1/auth/login/2fa`, {
        email,
        password,
        twoFactorCode: code
      });

      if (response.data.token) {
        localStorage.setItem('user', response.data.token);
        localStorage.setItem('role', response.data.role);
        localStorage.setItem('sessionId', response.data.sessionId);
        
        // Success feedback
        setError('Login successful! Redirecting...');
        // Add success class to the error message element in JSX
        setTimeout(() => {
          navigate('/dashboard');
        }, 1000);
      }
    } catch (error) {
      if (error.response?.status === 400) {
        setError('Invalid verification code. Please check your authenticator app and try again.');
      } else if (error.response?.status === 401) {
        setError('Session expired. Please log in again.');
        setTimeout(() => {
          navigate('/login');
        }, 2000);
      } else if (error.response?.status === 429) {
        setError('Too many attempts. Please wait a moment before trying again.');
      } else {
        setError('Verification failed. Please try again or contact support.');
      }
    }
    setIsLoading(false);
  };

  const handleBackToLogin = () => {
    navigate('/login');
  };

  // Check if the error message is a success message
  const isSuccessMessage = error && error.includes('successful');

  return (
    <div className="two-factor-verify-page">
      <div className="verify-container">
        <div className="verify-card">
          <div className="verify-header">
            <h2>Two-Factor Authentication</h2>
            <p>Enter the 6-digit code from your authenticator app</p>
          </div>
          
          <form onSubmit={handleVerify} className="verify-form">
            {error && (
              <div className={`error-message ${isSuccessMessage ? 'success' : ''}`}>
                {error}
              </div>
            )}
            
            <div className="code-input-group">
              <input
                type="text"
                value={code}
                onChange={(e) => setCode(e.target.value.replace(/\D/g, '').slice(0, 6))}
                placeholder="Enter 6-digit code"
                className="code-input"
                maxLength="6"
                autoFocus
              />
            </div>
            
            <div className="verify-actions">
              <button 
                type="submit" 
                disabled={isLoading || code.length !== 6}
                className="verify-btn"
              >
                {isLoading && <div className="spinner"></div>}
                {isLoading ? 'Verifying...' : 'Verify'}
              </button>
              
              <button 
                type="button" 
                onClick={handleBackToLogin}
                className="back-btn"
              >
                Back to Login
              </button>
            </div>
          </form>
          
          <div className="help-text">
            <p>Can't access your authenticator app?</p>
            <button className="help-link">Use backup code</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TwoFactorVerify;
