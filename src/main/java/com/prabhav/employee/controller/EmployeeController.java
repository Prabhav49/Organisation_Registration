package com.prabhav.employee.controller;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.dto.EmployeeResponse;
import com.prabhav.employee.dto.EmployeeUpdateRequest;
import com.prabhav.employee.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // Create a new employee
    @PostMapping("/create")
    public ResponseEntity<String> createEmployee(@RequestBody EmployeeRequest request) {
        String response = employeeService.createEmployee(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getEmployeeInfo/{email}")
    public ResponseEntity<EmployeeResponse> getEmployeeByEmail(@PathVariable String email) {
        EmployeeResponse response = employeeService.getEmployeeInfo(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/update/{employeeId}")
    public ResponseEntity<String> updateEmployeeProfile(
            @PathVariable Long employeeId,
            @RequestBody EmployeeUpdateRequest updateRequest) {
        String response = employeeService.updateEmployeeProfile(employeeId, updateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{employeeId}")
    public String deleteEmployee(@PathVariable Long employeeId) {
        return (employeeService.deleteEmployee(employeeId));
    }

    @GetMapping("/getAllEmployee")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        return new ResponseEntity<>(employees, HttpStatus.OK);
    }
}
