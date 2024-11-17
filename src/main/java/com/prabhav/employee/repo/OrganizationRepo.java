package com.prabhav.employee.repo;

import com.prabhav.employee.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    // Find organization by name
    Optional<Organization> findByName(String name);

    // Find organization by HR contact email (unique)
    Optional<Organization> findByHrContactEmail(String hrContactEmail);

    // Additional custom queries as needed
}
