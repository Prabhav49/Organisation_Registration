import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { BASEURL } from '../helper/helper';
import TwoFactorSetup from '../components/TwoFactorSetup';
import AuditLogs from '../components/AuditLogs';
import '../css/SecuritySettings.css';

const SecuritySettings = () => {
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [sessions, setSessions] = useState([]);
  const [isTwoFactorEnabled, setIsTwoFactorEnabled] = useState(false);
  const [showTwoFactorSetup, setShowTwoFactorSetup] = useState(false);
  const [showAuditLogs, setShowAuditLogs] = useState(false);
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    setUserRole(localStorage.getItem('role') || '');
    fetchUserSessions();
    checkTwoFactorStatus();
  }, []);

  const fetchUserSessions = async () => {
    try {
      const token = localStorage.getItem('user');
      if (!token) {
        console.warn('No authentication token found');
        return;
      }
      
      let email;
      try {
        email = JSON.parse(atob(token.split('.')[1])).sub;
      } catch (tokenError) {
        console.error('Invalid token format:', tokenError);
        return;
      }
      
      const response = await axios.get(
        `${BASEURL}/api/v1/admin/sessions/active?userEmail=${email}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setSessions(response.data);
    } catch (error) {
      console.error('Failed to fetch sessions:', error);
      if (error.response?.status === 401) {
        setMessage('Session expired. Please log in again.');
      }
    }
  };

  const checkTwoFactorStatus = async () => {
    try {
      const token = localStorage.getItem('user');
      if (!token) {
        console.warn('No authentication token found');
        setIsTwoFactorEnabled(false);
        return;
      }
      
      const response = await axios.get(
        `${BASEURL}/api/v1/security/2fa/status`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setIsTwoFactorEnabled(response.data.isEnabled);
    } catch (error) {
      console.error('Failed to check 2FA status:', error);
      setIsTwoFactorEnabled(false);
      if (error.response?.status === 401) {
        setMessage('Session expired. Please log in again.');
      }
    }
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();
    
    // Clear previous messages
    setMessage('');
    
    // Validate passwords match
    if (passwordData.newPassword !== passwordData.confirmPassword) {
      setMessage('New password and confirm password do not match.');
      return;
    }

    // Validate password strength
    if (passwordData.newPassword.length < 8) {
      setMessage('Password must be at least 8 characters long.');
      return;
    }

    setIsLoading(true);
    try {
      const token = localStorage.getItem('user');
      if (!token) {
        setMessage('Authentication required. Please log in again.');
        return;
      }
      
      let email;
      try {
        email = JSON.parse(atob(token.split('.')[1])).sub;
      } catch (tokenError) {
        setMessage('Invalid authentication token. Please log in again.');
        return;
      }
      
      // Get employee info to get the employee ID
      const employeeResponse = await axios.get(
        `${BASEURL}/api/v1/employees/getEmployeeInfo/${email}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      
      const employeeId = employeeResponse.data.employee_id;
      
      const response = await axios.put(
        `${BASEURL}/api/v1/employees/changePassword/${employeeId}`,
        {
          oldPassword: passwordData.currentPassword,
          newPassword: passwordData.newPassword,
          confirmPassword: passwordData.confirmPassword
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setMessage('Password changed successfully!');
      setPasswordData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
      
      // Auto-clear success message after 5 seconds
      setTimeout(() => setMessage(''), 5000);
      
    } catch (error) {
      let errorMessage = 'Failed to change password. Please try again.';
      
      if (error.response?.status === 401) {
        errorMessage = 'Session expired. Please log in again.';
      } else if (error.response?.data) {
        if (typeof error.response.data === 'string') {
          if (error.response.data.includes('Old password is incorrect')) {
            errorMessage = 'Current password is incorrect.';
          } else if (error.response.data.includes('not match')) {
            errorMessage = 'New password and confirm password do not match.';
          } else {
            errorMessage = error.response.data;
          }
        } else if (error.response.data.error) {
          errorMessage = error.response.data.error;
        } else if (error.response.data.message) {
          errorMessage = error.response.data.message;
        }
      }
      
      setMessage(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const disableTwoFactor = async () => {
    const code = prompt('Enter your 6-digit verification code to disable 2FA:');
    if (!code) return;
    
    if (code.length !== 6 || !/^\d{6}$/.test(code)) {
      setMessage('Please enter a valid 6-digit verification code.');
      return;
    }

    setIsLoading(true);
    try {
      const token = localStorage.getItem('user');
      const response = await axios.post(
        `${BASEURL}/api/v1/security/2fa/disable`,
        {
          verificationCode: code,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.data.isEnabled) {
        setIsTwoFactorEnabled(false);
        setMessage('Two-factor authentication has been disabled successfully.');
        setTimeout(() => setMessage(''), 5000);
      } else {
        setMessage(response.data.message || 'Failed to disable two-factor authentication.');
      }
    } catch (error) {
      if (error.response?.status === 400) {
        setMessage('Invalid verification code. Please check your authenticator app and try again.');
      } else if (error.response?.status === 401) {
        setMessage('Session expired. Please log in again.');
      } else {
        setMessage('Failed to disable 2FA. Please try again.');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const terminateSession = async (sessionId) => {
    try {
      const token = localStorage.getItem('user');
      await axios.post(
        `${BASEURL}/api/v1/admin/sessions/${sessionId}/terminate`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      
      setMessage('Session terminated successfully.');
      fetchUserSessions();
    } catch (error) {
      setMessage('Failed to terminate session.');
    }
  };

  const handleInputChange = (e) => {
    setPasswordData({
      ...passwordData,
      [e.target.name]: e.target.value
    });
  };

  const getPasswordStrength = (password) => {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/\d/.test(password)) strength++;
    if (/[@$!%*?&]/.test(password)) strength++;
    
    const levels = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong'];
    return { score: strength, level: levels[strength] || 'Very Weak' };
  };

  const passwordStrength = getPasswordStrength(passwordData.newPassword);

  return (
    <div className="security-settings">
      <h2>Security Settings</h2>

      {message && (
        <div className={`security-message ${message.includes('successfully') ? 'success' : 'error'}`}>
          {message}
        </div>
      )}
      
      {/* Password Change Section */}
      <div className="security-section">
        <h3>Change Password</h3>
        <form onSubmit={handlePasswordChange}>
          <div className="form-group">
            <label>Current Password:</label>
            <input
              type="password"
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <div className="form-group">
            <label>New Password:</label>
            <input
              type="password"
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handleInputChange}
              required
            />
            {passwordData.newPassword && (
              <div className={`password-strength strength-${passwordStrength.score}`}>
                Password Strength: {passwordStrength.level}
              </div>
            )}
          </div>
          
          <div className="form-group">
            <label>Confirm New Password:</label>
            <input
              type="password"
              name="confirmPassword"
              value={passwordData.confirmPassword}
              onChange={handleInputChange}
              required
            />
          </div>
          
          <button type="submit" disabled={isLoading} className="change-password-btn">
            {isLoading && <div className="spinner"></div>}
            {isLoading ? 'Changing...' : 'Change Password'}
          </button>
        </form>
      </div>

      {/* Two-Factor Authentication Section */}
      <div className="security-section">
        <div className="section-header">
          <h3>Two-Factor Authentication</h3>
          <div className={`status-badge ${isTwoFactorEnabled ? 'enabled' : 'disabled'}`}>
            {isTwoFactorEnabled ? 'ENABLED' : 'DISABLED'}
          </div>
        </div>
        <p>
          {isTwoFactorEnabled 
            ? 'Your account is protected with two-factor authentication. You will need to enter a code from your authenticator app when signing in.'
            : 'Add an extra layer of security to your account with two-factor authentication. This helps protect your account even if someone knows your password.'
          }
        </p>
        <div className="two-factor-actions">
          {isTwoFactorEnabled ? (
            <button 
              className="security-button danger" 
              onClick={disableTwoFactor}
              disabled={isLoading}
            >
              {isLoading && <div className="spinner"></div>}
              Disable Two-Factor Authentication
            </button>
          ) : (
            <button 
              className="security-button success" 
              onClick={() => setShowTwoFactorSetup(true)}
              disabled={isLoading}
            >
              {isLoading && <div className="spinner"></div>}
              Enable Two-Factor Authentication
            </button>
          )}
        </div>
      </div>

      {/* Active Sessions Section */}
      <div className="security-section">
        <h3>Active Sessions</h3>
        <p>These are the devices that are currently signed in to your account.</p>
        <div className="sessions-list">
          {sessions.length === 0 ? (
            <p>No active sessions found.</p>
          ) : (
            sessions.map((session) => (
              <div key={session.sessionId} className="session-item">
                <div className="session-info">
                  <div className="session-device">
                    <strong>{session.deviceInfo || 'Unknown Device'}</strong>
                  </div>
                  <div className="session-details">
                    <span>IP: {session.ipAddress}</span>
                    <span>Last Active: {new Date(session.lastActivity).toLocaleString()}</span>
                  </div>
                </div>
                <button 
                  className="terminate-btn"
                  onClick={() => terminateSession(session.sessionId)}
                >
                  Terminate
                </button>
              </div>
            ))
          )}
        </div>
      </div>

      {/* Admin Actions Section */}
      {(userRole === 'SUPER_ADMIN' || userRole === 'ADMIN') && (
        <div className="security-section">
          <h3>Administrative Actions</h3>
          <p>Access advanced security and monitoring features.</p>
          <div className="admin-actions">
            <button 
              className="security-button info" 
              onClick={() => setShowAuditLogs(true)}
            >
              View Audit Logs
            </button>
          </div>
        </div>
      )}

      {showTwoFactorSetup && (
        <TwoFactorSetup 
          onClose={() => {
            setShowTwoFactorSetup(false);
            setIsTwoFactorEnabled(true);
            checkTwoFactorStatus();
          }}
        />
      )}

      {showAuditLogs && (
        <AuditLogs 
          onClose={() => setShowAuditLogs(false)}
        />
      )}
    </div>
  );
};

export default SecuritySettings;
