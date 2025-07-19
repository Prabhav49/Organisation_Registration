import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';

const OAuth2RedirectHandler = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  useEffect(() => {
    const token = searchParams.get('token');
    const user = searchParams.get('user');
    const error = searchParams.get('error');

    console.log('OAuth2 Redirect Handler - token:', token ? token.substring(0, 20) + '...' : 'null');
    console.log('OAuth2 Redirect Handler - user:', user);
    console.log('OAuth2 Redirect Handler - error:', error);

    if (token && user) {
      // Store token and redirect to dashboard
      localStorage.setItem('user', token);
      localStorage.setItem('userEmail', user);
      console.log('Token stored successfully, redirecting to dashboard');
      navigate('/dashboard');
    } else if (error) {
      // Handle error
      console.error('OAuth2 login failed:', error);
      navigate('/login?error=oauth2_failed');
    } else {
      // No token or error, redirect to login
      console.log('No token or user found, redirecting to login');
      navigate('/login');
    }
  }, [navigate, searchParams]);

  return (
    <div style={{ 
      display: 'flex', 
      justifyContent: 'center', 
      alignItems: 'center', 
      height: '100vh',
      fontSize: '18px' 
    }}>
      <div>
        <div style={{ marginBottom: '20px' }}>Processing Google login...</div>
        <div className="spinner" style={{
          border: '4px solid #f3f3f3',
          borderTop: '4px solid #3498db',
          borderRadius: '50%',
          width: '40px',
          height: '40px',
          animation: 'spin 1s linear infinite',
          margin: '0 auto'
        }} />
      </div>
    </div>
  );
};

export default OAuth2RedirectHandler;
