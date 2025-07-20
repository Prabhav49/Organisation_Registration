package com.prabhav.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorResponse {
    
    private String secretKey;
    private String qrCodeUrl;
    private List<String> backupCodes;
    private Boolean isEnabled;
    private String message;
}
