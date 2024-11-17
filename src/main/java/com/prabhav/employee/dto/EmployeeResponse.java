package com.prabhav.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {

    @JsonProperty("employee_id")
    private Long employeeId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("title")
    private String title;

    @JsonProperty("photograph_path")
    private String photographPath;

    @JsonProperty("department")
    private String departmentName;

    @JsonProperty("organization")
    private String organizationName;

    @JsonProperty("organization_id")
    private Long organizationId;
}
