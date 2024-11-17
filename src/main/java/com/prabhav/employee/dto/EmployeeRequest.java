package com.prabhav.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmployeeRequest {

    @NotNull(message = "Employee first name is required")
    @NotEmpty(message = "Employee first name should not be empty")
    @NotBlank(message = "Employee first name should not be blank")
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @NotNull(message = "Employee email is required")
    @Email(message = "Email must be in correct format")
    @JsonProperty("email")
    private String email;

    @NotNull(message = "Password should be present")
    @NotEmpty(message = "Password should be present")
    @NotBlank(message = "Password should be present")
    @Size(min = 6, max = 12)
    @JsonProperty("password")
    String password;

    @JsonProperty("title")
    private String title;

    @JsonProperty("photograph_path")
    private String photographPath;

    @NotNull(message = "Department ID is required")
    @JsonProperty("department_id")
    private Long departmentId;
}
