package com.prabhav.employee.controller;

import com.prabhav.employee.dto.OrganizationRequest;
import com.prabhav.employee.dto.OrganizationResponse;
import com.prabhav.employee.service.OrganizationService;
import com.prabhav.employee.auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createOrganization(
            @RequestBody @Valid OrganizationRequest request,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = organizationService.createOrganization(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/getByName/{name}")
    public ResponseEntity<OrganizationResponse> getOrganizationByName(
            @PathVariable String name,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        OrganizationResponse response = organizationService.getOrganizationByName(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getByHrEmail/{hrContactEmail}")
    public ResponseEntity<OrganizationResponse> getOrganizationByHrEmail(
            @PathVariable String hrContactEmail,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        OrganizationResponse response = organizationService.getOrganizationByHrEmail(hrContactEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<OrganizationResponse>> getAllOrganizations(HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        List<OrganizationResponse> organizations = organizationService.getAllOrganizations();
        return ResponseEntity.ok(organizations);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
