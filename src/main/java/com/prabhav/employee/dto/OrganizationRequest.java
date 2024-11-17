package com.prabhav.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequest {

    @NotNull(message = "Organization name is required")
    @NotBlank(message = "Organization name should not be blank")
    @JsonProperty("name")
    private String name;

    @JsonProperty("hr_contact_name")
    private String hrContactName;

    @NotNull(message = "HR Contact email is required")
    @Email(message = "HR Contact email must be in correct format")
    @JsonProperty("hr_contact_email")
    private String hrContactEmail;

    @JsonProperty("address")
    private String address;

    @JsonProperty("city")
    private String city;

    @JsonProperty("pincode")
    private int pincode;

    @JsonProperty("phone")
    private String phone;
}
