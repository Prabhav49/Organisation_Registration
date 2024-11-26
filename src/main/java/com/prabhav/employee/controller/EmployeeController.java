package com.prabhav.employee.controller;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.dto.EmployeeResponse;
import com.prabhav.employee.dto.EmployeeUpdateRequest;
import com.prabhav.employee.dto.PasswordChangeRequest;
import com.prabhav.employee.service.EmployeeService;
import com.prabhav.employee.auth.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<String> createEmployee(
            @RequestBody @Valid EmployeeRequest request,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = employeeService.createEmployee(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/getEmployeeInfo/{email}")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmail(
            @PathVariable String email,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null);
        }
        EmployeeResponse response = employeeService.getEmployeeInfo(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{employeeId}")
    public ResponseEntity<String> updateEmployeeProfile(
            @PathVariable Long employeeId,
            @RequestBody @Valid EmployeeUpdateRequest updateRequest,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = employeeService.updateEmployeeProfile(employeeId, updateRequest);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/changePassword/{employeeId}")
    public ResponseEntity<String> changePassword(
            @PathVariable Long employeeId,
            @RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
            HttpServletRequest httpRequest) {

        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }

        String response = employeeService.changePassword(employeeId, passwordChangeRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{employeeId}")
    public ResponseEntity<String> deleteEmployee(
            @PathVariable Long employeeId,
            HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }
        String response = employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/getAllEmployee")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(HttpServletRequest httpRequest) {
        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
    @PostMapping("/uploadImage/{employeeId}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long employeeId,
            @RequestParam("file") MultipartFile file,
            HttpServletRequest httpRequest) {

        if (!jwtUtil.validateToken(getTokenFromRequest(httpRequest))) {
            return ResponseEntity.status(401).body("Unauthorized: Invalid or missing token");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty. Please upload a valid image.");
        }

        try {
            String photographPath = employeeService.uploadEmployeeImage(employeeId, file);
            return ResponseEntity.ok(photographPath);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
