package com.prabhav.employee.service;

import com.prabhav.employee.dto.OrganizationHRRequest;
import com.prabhav.employee.dto.OrganizationHRResponse;
import com.prabhav.employee.dto.OrganizationHRUpdateRequest;
import com.prabhav.employee.entity.Organization;
import com.prabhav.employee.entity.OrganizationHR;
import com.prabhav.employee.mapper.OrganizationHRMapper;
import com.prabhav.employee.repo.OrganizationHRRepo;
import com.prabhav.employee.repo.OrganizationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationHRService {

    private final OrganizationHRRepo organizationHRRepo;
    private final OrganizationRepo organizationRepo;
    private final OrganizationHRMapper organizationHRMapper;

    public String addHrContact(Long organizationId, OrganizationHRRequest request) {
        Optional<Organization> organizationOpt = organizationRepo.findById(organizationId);
        if (organizationOpt.isEmpty()) {
            return "Organization not found";
        }
        Organization organization = organizationOpt.get();
        OrganizationHR hrContact = organizationHRMapper.toEntity(request, organization); // Pass organization here
        organizationHRRepo.save(hrContact);
        return "HR contact added successfully";
    }

    public OrganizationHRResponse getHrContactByEmail(String email, Long organizationId) {
        Optional<OrganizationHR> hrContactOpt = organizationHRRepo.findByEmail(email);

        if (hrContactOpt.isEmpty()) {
            System.out.println("HR contact not found for email: " + email + " and organization ID: " + organizationId);
        }
        return hrContactOpt.map(organizationHRMapper::toResponse).orElse(null);
    }

    public List<OrganizationHRResponse> getAllHrContacts() {
        List<OrganizationHR> hrContacts = organizationHRRepo.findAll();
        return organizationHRMapper.toResponse(hrContacts);
    }

    public String updateHrContact(Long hrId, Long organizationId, OrganizationHRUpdateRequest updateRequest) {
        Optional<OrganizationHR> hrContactOpt = organizationHRRepo.findByIdAndOrganization_Id(hrId, organizationId);
        if (hrContactOpt.isEmpty()) {
            return "HR contact not found for the given organization";
        }

        OrganizationHR hrContact = hrContactOpt.get();

        if (updateRequest.getFirstName() != null) {
            hrContact.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            hrContact.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            hrContact.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getContactNumber() != null) {
            hrContact.setContactNumber(updateRequest.getContactNumber());
        }

        organizationHRRepo.save(hrContact);
        return "HR contact updated successfully";
    }

    public String deleteHrContact(Long hrId, Long organizationId) {
        Optional<OrganizationHR> hrContactOpt = organizationHRRepo.findByIdAndOrganization_Id(hrId, organizationId);
        if (hrContactOpt.isEmpty()) {
            return "HR contact not found for the given organization";
        }
        organizationHRRepo.delete(hrContactOpt.get());
        return "HR contact deleted successfully";
    }
}
