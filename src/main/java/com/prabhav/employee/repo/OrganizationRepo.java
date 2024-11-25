package com.prabhav.employee.repo;

import com.prabhav.employee.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepo extends JpaRepository<Organization, Long> {

    Optional<Organization> findByName(String name);
    List<Organization> findByNameContainingIgnoreCase(String name);

    Optional<Organization> findByHrContacts_Email(String email);

    Optional<Organization> findByAddress(String address);
}
