package com.prabhav.employee.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorRequest {
    
    @NotBlank(message = "Verification code is required")
    private String verificationCode;
    
    private String email;
    private String action; // ENABLE, DISABLE, VERIFY
}
