package com.prabhav.employee.repo;

import com.prabhav.employee.entity.OrganizationHR;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationHRRepo extends JpaRepository<OrganizationHR, Long> {

    // Updated method to search by both organization name and organization ID
    List<OrganizationHR> findByOrganization_NameAndOrganization_Id(String name, Long organizationId);

    // Other existing methods
    Optional<OrganizationHR> findByIdAndOrganization_Id(Long hrId, Long organizationId);

    List<OrganizationHR> findByOrganization_Id(Long organizationId);
}
