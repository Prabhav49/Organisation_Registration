package com.prabhav.employee.service;

import com.prabhav.employee.dto.OrganizationRequest;
import com.prabhav.employee.dto.OrganizationResponse;
import com.prabhav.employee.entity.Organization;
import com.prabhav.employee.mapper.OrganizationMapper;
import com.prabhav.employee.repo.OrganizationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final OrganizationMapper organizationMapper;

    public String createOrganization(OrganizationRequest request) {
        Organization organization = organizationMapper.toEntity(request);
        organizationRepo.save(organization);
        return "Organization Created Successfully";
    }

    public OrganizationResponse getOrganizationByName(String name) {
        Optional<Organization> organization = organizationRepo.findByName(name);
        return organization.map(organizationMapper::toResponse).orElse(null);
    }

    public OrganizationResponse getOrganizationByHrEmail(String hrContactEmail) {
        Optional<Organization> organization = organizationRepo.findByHrContacts_Email(hrContactEmail);
        return organization.map(organizationMapper::toResponse).orElse(null);
    }

    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepo.findAll();
        return organizationMapper.toResponse(organizations);
    }
}
