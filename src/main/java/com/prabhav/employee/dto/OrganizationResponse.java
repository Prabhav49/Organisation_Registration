package com.prabhav.employee.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrganizationResponse {
    private Long id;
    private String name;
    private String address;
    private List<OrganizationHRResponse> hrContacts; // Nested HR details
}
