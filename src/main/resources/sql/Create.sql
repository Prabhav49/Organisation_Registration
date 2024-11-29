-- Create table for `Organization`
CREATE TABLE organization (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL
);

-- Create the department table
CREATE TABLE department (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            capacity BIGINT NOT NULL
);

-- Create the employee table
CREATE TABLE employee (
                          employee_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(50) NOT NULL,
                          last_name VARCHAR(50),
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          title VARCHAR(100),
                          photograph_path VARCHAR(255) DEFAULT '/uploads/images/default.png',
                          department_id BIGINT NOT NULL,
                          CONSTRAINT fk_department FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE CASCADE
);


-- Create table for `OrganizationHR`
CREATE TABLE organization_hr (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    contact_number CHAR(10) NOT NULL,
    organization_id BIGINT NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organization(id) ON DELETE CASCADE
);

