package com.prabhav.employee.repo;

import com.prabhav.employee.entity.OrganizationHR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationHRRepo extends JpaRepository<OrganizationHR, Long> {

    // Find HR contact by email
    Optional<OrganizationHR> findByEmail(String email);
    Optional<OrganizationHR> findByEmailAndOrganization_Id(String email, Long organizationId);
    Optional<OrganizationHR> findByIdAndOrganization_Id(Long hrId, Long organizationId);

}
