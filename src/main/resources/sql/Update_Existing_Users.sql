-- Update all existing employees with NULL security values to have defaults
UPDATE employee 
SET 
    role = 'EMPLOYEE',
    is_email_verified = TRUE,
    failed_login_attempts = 0,
    is_account_locked = FALSE,
    is_two_factor_enabled = FALSE,
    created_at = NOW(),
    updated_at = NOW()
WHERE role IS NULL;

-- Update admin user to have ADMIN role
UPDATE employee 
SET 
    role = 'ADMIN',
    is_email_verified = TRUE,
    failed_login_attempts = 0,
    is_account_locked = FALSE,
    is_two_factor_enabled = FALSE,
    created_at = NOW(),
    updated_at = NOW()
WHERE email = 'admin@organization.com';

-- Verify the updates
SELECT id, first_name, email, role, is_account_locked, is_two_factor_enabled, failed_login_attempts 
FROM employee 
ORDER BY id;
