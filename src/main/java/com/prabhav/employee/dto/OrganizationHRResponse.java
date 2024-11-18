package com.prabhav.employee.dto;

import lombok.Data;

@Data
public class OrganizationHRResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String organizationName;
}
