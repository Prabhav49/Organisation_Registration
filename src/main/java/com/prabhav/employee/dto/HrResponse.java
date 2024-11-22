package com.prabhav.employee.dto;

import lombok.Data;

@Data
public class HrResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
}
