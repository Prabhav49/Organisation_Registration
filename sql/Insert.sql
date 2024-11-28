-- Insert into `Organization`
INSERT INTO organization (name, address) 
VALUES ('TechCorp', '123 Tech Street, Silicon Valley'),
       ('InnoSoft', '456 Innovation Drive, New York');

-- Insert into `OrganizationHR`
INSERT INTO organization_hr (first_name, last_name, email, contact_number, organization_id) 
VALUES ('John', 'Doe', 'john.doe@techcorp.com', '9876543210', 1),
       ('Jane', 'Smith', 'jane.smith@innosoft.com', '8765432109', 2);

-- Insert into `Employee`
INSERT INTO employee (first_name, last_name, email, password, title, photograph_path, department_id) 
VALUES ('Alice', 'Johnson', 'alice.johnson@techcorp.com', 'password123', 'Software Engineer', '/uploads/images/default.png', 101),
       ('Bob', 'Brown', 'bob.brown@innosoft.com', 'password456', 'Data Analyst', '/uploads/images/default.png', 102);
