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

    const formErrors = validateLoginForm(email, password);
    if (Object.keys(formErrors).length > 0) {
      setErrors(formErrors);
      return;
    }
    try {
      const response = await AuthService.login(email, password);
      if (response.token) {
        navigate('/dashboard');
      }
    } catch (error) {
      setErrorMessage(error.message || 'Login failed. Please try again.');
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

  return (
    <div className="login-page">
      <div className="login-container">
        <div className="login-form">
          <h2>Login</h2>
          <form onSubmit={handleLogin}>
            <input
              type="email"
              placeholder="Enter your email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            {errors.email && <p className="error-message">{errors.email}</p>}

            <input
              type="password"
              placeholder="Enter your password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            {errors.password && <p className="error-message">{errors.password}</p>}

            {errorMessage && <p className="error-message">{errorMessage}</p>}

            <button type="submit" className="login-button">Login</button>
          </form>
          
          <div className="divider">
            <span>or</span>
          </div>
          
          <GoogleLoginButton
            onSuccess={handleGoogleSuccess}
            onFailure={handleGoogleFailure}
          />
        </div>
      </div>
    </div>
  );
};

export default Login;
