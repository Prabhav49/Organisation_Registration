-- Security Enhancement Tables

-- Add new columns to employee table
ALTER TABLE employee 
ADD COLUMN role VARCHAR(50) DEFAULT 'EMPLOYEE',
ADD COLUMN is_account_locked BOOLEAN DEFAULT FALSE,
ADD COLUMN failed_login_attempts INT DEFAULT 0,
ADD COLUMN last_login DATETIME,
ADD COLUMN password_changed_at DATETIME,
ADD COLUMN is_two_factor_enabled BOOLEAN DEFAULT FALSE,
ADD COLUMN created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Create audit_log table
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id VARCHAR(255),
    old_values TEXT,
    new_values TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    error_message TEXT,
    INDEX idx_user_email (user_email),
    INDEX idx_timestamp (timestamp),
    INDEX idx_entity (entity_type, entity_id)
);

-- Create user_session table
CREATE TABLE user_session (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) UNIQUE NOT NULL,
    user_email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    login_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_activity DATETIME DEFAULT CURRENT_TIMESTAMP,
    logout_time DATETIME,
    is_active BOOLEAN DEFAULT TRUE,
    device_info VARCHAR(50),
    INDEX idx_session_id (session_id),
    INDEX idx_user_email (user_email),
    INDEX idx_is_active (is_active)
);

-- Create two_factor_auth table
CREATE TABLE two_factor_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) UNIQUE NOT NULL,
    secret_key VARCHAR(255),
    backup_codes TEXT,
    is_enabled BOOLEAN DEFAULT FALSE,
    verification_code VARCHAR(10),
    code_expiry DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_email (user_email)
);

-- Create password_history table
CREATE TABLE password_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_email (user_email),
    INDEX idx_created_at (created_at)
);

-- Create initial super admin user (password: Admin@123)
INSERT INTO employee (
    first_name, 
    last_name, 
    email, 
    password, 
    title, 
    role, 
    is_email_verified,
    created_at,
    updated_at
) VALUES (
    'Super', 
    'Admin', 
    'admin@organization.com', 
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- Admin@123
    'Super Administrator', 
    'SUPER_ADMIN',
    TRUE,
    NOW(),
    NOW()
) ON DUPLICATE KEY UPDATE role = 'SUPER_ADMIN';
