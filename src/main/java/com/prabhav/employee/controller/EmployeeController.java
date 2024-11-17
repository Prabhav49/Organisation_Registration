package com.prabhav.employee.controller;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // Endpoint to create a new employee
    @PostMapping("/create")
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequest employeeRequest) {
        String response = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.ok(response);
    }
}
