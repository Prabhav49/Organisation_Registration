package com.prabhav.employee.mapper;

import com.prabhav.employee.dto.OrganizationRequest;
import com.prabhav.employee.dto.OrganizationResponse;
import com.prabhav.employee.dto.OrganizationHRResponse;
import com.prabhav.employee.entity.Organization;
import com.prabhav.employee.entity.OrganizationHR;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrganizationMapper {

    public Organization toEntity(OrganizationRequest request) {
        if (request == null) {
            return null;
        }

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setAddress(request.getAddress());
        return organization;
    }

    public OrganizationResponse toResponse(Organization organization) {
        if (organization == null) {
            return null;
        }

        OrganizationResponse response = new OrganizationResponse();
        response.setId(organization.getId());
        response.setName(organization.getName());
        response.setAddress(organization.getAddress());

        if (organization.getHrContacts() != null) {
            List<OrganizationHRResponse> hrResponses = organization.getHrContacts()
                    .stream()
                    .map(this::toHRResponse)
                    .collect(Collectors.toList());
            response.setHrContacts(hrResponses);
        }

        return response;
    }

    public List<OrganizationResponse> toResponse(List<Organization> organizations) {
        return organizations.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private OrganizationHRResponse toHRResponse(OrganizationHR hr) {
        if (hr == null) {
            return null;
        }

        OrganizationHRResponse hrResponse = new OrganizationHRResponse();
        hrResponse.setId(hr.getId());
        hrResponse.setFirstName(hr.getFirstName());
        hrResponse.setLastName(hr.getLastName());
        hrResponse.setEmail(hr.getEmail());
        hrResponse.setContactNumber(hr.getContactNumber());
        return hrResponse;
    }
}
