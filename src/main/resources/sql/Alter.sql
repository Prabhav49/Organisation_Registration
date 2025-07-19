USE ESDProject

ALTER TABLE organization_hr
ADD CONSTRAINT fk_organization_hr_organization
FOREIGN KEY (organization_id)
REFERENCES organization(id)
ON DELETE CASCADE;

ALTER TABLE employee
    ADD CONSTRAINT fk_employee_department
        FOREIGN KEY (department_id)
            REFERENCES department(id)
            ON DELETE CASCADE;

-- OAuth2 Support: Make password nullable and add OAuth2 fields
ALTER TABLE employee 
MODIFY COLUMN password VARCHAR(255) NULL;

ALTER TABLE employee 
ADD COLUMN oauth_provider VARCHAR(50) DEFAULT 'LOCAL';

ALTER TABLE employee 
ADD COLUMN oauth_id VARCHAR(255);

ALTER TABLE employee 
ADD COLUMN is_email_verified BOOLEAN DEFAULT FALSE;

-- Make department_id nullable for OAuth users who might not have departments initially
ALTER TABLE employee 
MODIFY COLUMN department_id BIGINT NULL;
