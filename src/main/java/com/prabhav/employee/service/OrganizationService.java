package com.prabhav.employee.service;

import com.prabhav.employee.dto.OrganizationHRResponse;
import com.prabhav.employee.dto.OrganizationRequest;
import com.prabhav.employee.dto.OrganizationResponse;
import com.prabhav.employee.entity.Organization;
import com.prabhav.employee.entity.OrganizationHR;
import com.prabhav.employee.mapper.OrganizationHRMapper;
import com.prabhav.employee.mapper.OrganizationMapper;
import com.prabhav.employee.repo.OrganizationHRRepo;
import com.prabhav.employee.repo.OrganizationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final OrganizationMapper organizationMapper;
    private final OrganizationHRRepo organizationHRRepo;
    private final OrganizationHRMapper organizationHRMapper;

    public String createOrganization(OrganizationRequest request) {
        Organization organization = organizationMapper.toEntity(request);
        organizationRepo.save(organization);
        return "Organization Created Successfully";
    }
    public OrganizationHRResponse getHRById(Long id) {
        Optional<OrganizationHR> hr = organizationHRRepo.findById(id);
        return hr.map(organizationHRMapper::toResponse).orElse(null);
    }
    public List<OrganizationResponse> getOrganizationsByName(String name) {
        List<Organization> organizations = organizationRepo.findByNameContainingIgnoreCase(name);
        return organizations.stream()
                .map(organizationMapper::toResponse)
                .collect(Collectors.toList());
    }


    public OrganizationResponse getOrganizationByHrEmail(String hrContactEmail) {
        Optional<Organization> organization = organizationRepo.findByHrContacts_Email(hrContactEmail);
        return organization.map(organizationMapper::toResponse).orElse(null);
    }

    public List<OrganizationResponse> getAllOrganizations() {
        List<Organization> organizations = organizationRepo.findAll();
        return organizationMapper.toResponse(organizations);
    }
    public String deleteOrganization(Long id) {
        Optional<Organization> organization = organizationRepo.findById(id);
        if (organization.isPresent()) {
            organizationRepo.deleteById(id);
            return "Organization deleted successfully";
        } else {
            return "Organization not found";
        }
    }
    public List<OrganizationHRResponse> getHRsByOrganizationId(Long organizationId) {
        List<OrganizationHR> hrList = organizationHRRepo.findByOrganization_Id(organizationId);
        return organizationHRMapper.toResponse(hrList);
    }
}
