package com.prabhav.employee.controller;

import com.prabhav.employee.dto.OrganizationHRRequest;
import com.prabhav.employee.dto.OrganizationHRResponse;
import com.prabhav.employee.dto.OrganizationHRUpdateRequest;
import com.prabhav.employee.service.OrganizationHRService;
import com.prabhav.employee.auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/hr")
@RequiredArgsConstructor
public class OrganizationHRController {

    private final OrganizationHRService organizationHRService;
    private final JwtUtil jwtUtil;

    @PostMapping("/add")
    public ResponseEntity<String> addHrContact(
            @PathVariable Long organizationId,
            @RequestBody @Valid OrganizationHRRequest request,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = organizationHRService.addHrContact(organizationId, request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<OrganizationHRResponse> getHrContactByEmail(
            @PathVariable String email,
            @PathVariable Long organizationId,
            HttpServletRequest httpRequest) {

        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        OrganizationHRResponse response = organizationHRService.getHrContactByEmail(email, organizationId);
        if (response == null) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<OrganizationHRResponse>> getAllHrContacts(
            @PathVariable Long organizationId,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        List<OrganizationHRResponse> hrContacts = organizationHRService.getAllHrContacts(organizationId);
        if (hrContacts.isEmpty()) {
            return ResponseEntity.status(404).body(null);
        }
        return ResponseEntity.ok(hrContacts);
    }


    @PutMapping("/update/{hrId}")
    public ResponseEntity<String> updateHrContact(
            @PathVariable Long hrId,
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganizationHRUpdateRequest updateRequest,
            HttpServletRequest httpRequest) {

        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized access");
        }

        String response = organizationHRService.updateHrContact(hrId, organizationId, updateRequest);
        if ("HR contact not found for the given organization".equals(response)) {
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{hrId}")
    public ResponseEntity<String> deleteHrContact(
            @PathVariable Long organizationId,
            @PathVariable Long hrId,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = organizationHRService.deleteHrContact(hrId, organizationId);
        return ResponseEntity.ok(response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
