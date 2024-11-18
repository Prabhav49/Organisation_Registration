package com.prabhav.employee.mapper;

import com.prabhav.employee.dto.OrganizationHRRequest;
import com.prabhav.employee.dto.OrganizationHRResponse;
import com.prabhav.employee.entity.Organization;
import com.prabhav.employee.entity.OrganizationHR;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrganizationHRMapper {

    public OrganizationHR toEntity(OrganizationHRRequest request, Organization organization) {
        if (request == null) {
            return null;
        }

        OrganizationHR organizationHR = new OrganizationHR();
        organizationHR.setFirstName(request.getFirstName());
        organizationHR.setLastName(request.getLastName());
        organizationHR.setEmail(request.getEmail());
        organizationHR.setContactNumber(request.getContactNumber());
        organizationHR.setOrganization(organization);

        return organizationHR;
    }

    public OrganizationHRResponse toResponse(OrganizationHR organizationHR) {
        if (organizationHR == null) {
            return null;
        }

        OrganizationHRResponse response = new OrganizationHRResponse();
        response.setId(organizationHR.getId());
        response.setFirstName(organizationHR.getFirstName());
        response.setLastName(organizationHR.getLastName());
        response.setEmail(organizationHR.getEmail());
        response.setContactNumber(organizationHR.getContactNumber());
        response.setOrganizationName(organizationHR.getOrganization().getName());

        return response;
    }

    public List<OrganizationHRResponse> toResponse(List<OrganizationHR> organizationHRList) {
        return organizationHRList.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
