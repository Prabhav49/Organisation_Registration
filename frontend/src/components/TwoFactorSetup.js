import React, { useState } from 'react';
import axios from 'axios';
import { BASEURL } from '../helper/helper';
import '../css/TwoFactorSetup.css';

const TwoFactorSetup = ({ onClose }) => {
  const [setupData, setSetupData] = useState(null);
  const [verificationCode, setVerificationCode] = useState('');
  const [isVerifying, setIsVerifying] = useState(false);
  const [message, setMessage] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const setupTwoFactor = async () => {
    setIsLoading(true);
    setMessage('Setting up two-factor authentication...');
    
    try {
      const token = localStorage.getItem('user');
      const response = await axios.post(
        `${BASEURL}/api/v1/security/2fa/setup`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setSetupData(response.data);
      setMessage('QR code generated! Scan the QR code with your authenticator app (Google Authenticator, Authy, etc.) and enter the 6-digit verification code below.');
    } catch (error) {
      if (error.response?.status === 401) {
        setMessage('Session expired. Please log in again and try to set up 2FA.');
      } else {
        setMessage('Failed to setup two-factor authentication. Please try again or contact support.');
      }
      console.error('2FA setup error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const enableTwoFactor = async () => {
    if (!verificationCode || verificationCode.length !== 6) {
      setMessage('Please enter a valid 6-digit verification code.');
      return;
    }
    
    if (!/^\d{6}$/.test(verificationCode)) {
      setMessage('Verification code must be 6 digits only.');
      return;
    }

    setIsVerifying(true);
    setMessage('🔄 Verifying code...');
    
    try {
      const token = localStorage.getItem('user');
      const response = await axios.post(
        `${BASEURL}/api/v1/security/2fa/enable`,
        {
          verificationCode: verificationCode,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.data.isEnabled) {
        setMessage('Two-factor authentication has been enabled successfully! Your account is now more secure.');
        setTimeout(() => {
          onClose();
        }, 3000);
      } else {
        setMessage(response.data.message || 'Failed to enable two-factor authentication.');
      }
    } catch (error) {
      if (error.response?.status === 400) {
        setMessage('Invalid verification code. Please check your authenticator app and ensure the code is current.');
      } else if (error.response?.status === 401) {
        setMessage('Session expired. Please log in again and try to set up 2FA.');
      } else {
        setMessage('Failed to enable 2FA. Please try again or contact support if the problem persists.');
      }
      console.error('2FA enable error:', error);
    } finally {
      setIsVerifying(false);
    }
  };

  const copyBackupCode = (code) => {
    navigator.clipboard.writeText(code);
    setMessage('Backup code copied to clipboard!');
  };

  return (
    <div className="two-factor-setup-overlay">
      <div className="two-factor-setup-modal">
        <div className="two-factor-header">
          <h2>Two-Factor Authentication Setup</h2>
          <button className="close-btn" onClick={onClose}>
            ×
          </button>
        </div>

        <div className="two-factor-content">
          {message && (
            <div className={`two-factor-message ${message.includes('✅') ? 'success' : message.includes('🔄') ? 'info' : 'error'}`}>
              {message}
            </div>
          )}
          
          {!setupData ? (
            <div className="setup-intro">
              <p>
                Two-factor authentication adds an extra layer of security to your account.
                You'll need an authenticator app like Google Authenticator or Authy.
              </p>
              <button
                className="setup-btn"
                onClick={setupTwoFactor}
                disabled={isLoading}
              >
                {isLoading && <div className="spinner"></div>}
                {isLoading ? 'Setting up...' : 'Setup Two-Factor Authentication'}
              </button>
            </div>
          ) : (
            <div className="setup-steps">
              <div className="step">
                <h3>Step 1: Scan QR Code</h3>
                <p>Open your authenticator app and scan this QR code:</p>
                <div className="qr-code-container">
                  <img src={setupData.qrCodeUrl} alt="QR Code" className="qr-code" />
                </div>
                <p className="secret-key">
                  Manual entry key: <code>{setupData.secretKey}</code>
                </p>
              </div>

              <div className="step">
                <h3>Step 2: Enter Verification Code</h3>
                <p>Enter the 6-digit code from your authenticator app:</p>
                <input
                  type="text"
                  value={verificationCode}
                  onChange={(e) => setVerificationCode(e.target.value)}
                  placeholder="000000"
                  maxLength="6"
                  className="verification-input"
                />
                <button
                  className="verify-btn"
                  onClick={enableTwoFactor}
                  disabled={isVerifying || verificationCode.length !== 6}
                >
                  {isVerifying && <div className="spinner"></div>}
                  {isVerifying ? 'Verifying...' : 'Enable Two-Factor Authentication'}
                </button>
              </div>

              {setupData.backupCodes && setupData.backupCodes.length > 0 && (
                <div className="step">
                  <h3>Step 3: Save Backup Codes</h3>
                  <p>
                    Save these backup codes in a safe place. You can use them to access
                    your account if you lose your authenticator device:
                  </p>
                  <div className="backup-codes">
                    {setupData.backupCodes.map((code, index) => (
                      <div key={index} className="backup-code">
                        <span>{code}</span>
                        <button
                          onClick={() => copyBackupCode(code)}
                          className="copy-btn"
                        >
                          Copy
                        </button>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          {message && (
            <div className={`message ${
              message.includes('successfully') || message.includes('generated') || message.includes('copied') 
                ? 'success' 
                : message.includes('expired') || message.includes('Failed') || message.includes('Invalid')
                ? 'error'
                : 'info'
            }`}>
              {message}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default TwoFactorSetup;
