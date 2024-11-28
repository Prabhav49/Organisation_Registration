USE ESDProject

ALTER TABLE organization_hr
ADD CONSTRAINT fk_organization_hr_organization
FOREIGN KEY (organization_id)
REFERENCES organization(id)
ON DELETE CASCADE;