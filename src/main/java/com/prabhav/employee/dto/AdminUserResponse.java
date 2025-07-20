package com.prabhav.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prabhav.employee.entity.Role;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private Role role;

    @JsonProperty("isAccountLocked")
    private boolean isAccountLocked;

    @JsonProperty("isTwoFactorEnabled")
    private boolean isTwoFactorEnabled;

    @JsonProperty("title")
    private String title;

    @JsonProperty("departmentName")
    private String departmentName;
}
