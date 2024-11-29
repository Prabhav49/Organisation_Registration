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
