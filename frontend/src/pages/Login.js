import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import AuthService from '../services/AuthService';
import GoogleLoginButton from '../components/GoogleLoginButton';
import validations from '../utils/validation'; 
import '../css/Login.css';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const [errors, setErrors] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const { validateLoginForm } = validations.Login; 

  useEffect(() => {
    const error = searchParams.get('error');
    if (error) {
      switch (error) {
        case 'oauth2_failed':
          setErrorMessage('Google login failed. Please try again.');
          break;
        case 'oauth2_authentication_failed':
          setErrorMessage('Google authentication failed. Please try again.');
          break;
        default:
          setErrorMessage('Login failed. Please try again.');
      }
    }
  }, [searchParams]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setErrorMessage('');
    setErrors({});

    const formErrors = validateLoginForm(email, password);
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      return;
    }
    
    setIsLoading(true);
    
    try {
      const response = await AuthService.login(email, password);
      
      if (response.requiresTwoFactor || response.message === "2FA required") {
        // User has 2FA enabled, redirect to 2FA verification
        setErrorMessage('Two-factor authentication required. Redirecting...');
        setTimeout(() => {
          navigate('/2fa-verify', { 
            state: { 
              email: email, 
              password: password 
            } 
          });
        }, 1500);
      } else if (response.token) {
        // Normal login successful
        setErrorMessage('Login successful! Redirecting...');
        setTimeout(() => {
          navigate('/dashboard');
        }, 1000);
      } else {
        setErrorMessage('Login failed. Please check your credentials.');
      }
    } catch (error) {
      if (error.response?.status === 401) {
        setErrorMessage('Invalid email or password. Please try again.');
      } else if (error.response?.status === 423) {
        setErrorMessage('Account is locked due to too many failed attempts. Please contact support.');
      } else if (error.response?.status === 429) {
        setErrorMessage('Too many login attempts. Please wait before trying again.');
      } else {
        setErrorMessage(error.message || 'Login failed. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleSuccess = (response) => {
    console.log('Google login successful:', response);
    // This will be handled by the OAuth2RedirectHandler
  };

  const handleGoogleFailure = (error) => {
    console.error('Google login failed:', error);
    setErrorMessage('Google login failed. Please try again.');
  };

  // Check if the error message is a success message
  const isSuccessMessage = errorMessage && (
    errorMessage.includes('successful') || 
    errorMessage.includes('Redirecting') ||
    errorMessage.includes('required. Redirecting')
  );

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-card">
          <div className="login-header">
            <h1>Welcome Back</h1>
            <p>Sign in to your account</p>
          </div>
          
          <form onSubmit={handleLogin} className="login-form">
            {errorMessage && (
              <div className={`error-message ${isSuccessMessage ? 'success' : ''}`}>
                {errorMessage}
              </div>
            )}
            
            <div className="form-group">
              <label htmlFor="email">Email Address</label>
              <input
                id="email"
                type="email"
                placeholder="Enter your email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
              {errors.email && <div className="form-error">{errors.email}</div>}
            </div>

            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                type="password"
                placeholder="Enter your password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
              {errors.password && <div className="form-error">{errors.password}</div>}
            </div>

            <button type="submit" className="login-button" disabled={isLoading}>
              {isLoading && <div className="spinner"></div>}
              {isLoading ? 'Signing In...' : 'Sign In'}
            </button>
          </form>
          
          <div className="google-login-section">
            <h3>Or continue with</h3>
            <GoogleLoginButton
              onSuccess={handleGoogleSuccess}
              onFailure={handleGoogleFailure}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
