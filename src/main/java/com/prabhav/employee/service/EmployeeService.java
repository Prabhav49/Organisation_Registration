package com.prabhav.employee.service;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.dto.EmployeeUpdateRequest;
import com.prabhav.employee.dto.EmployeeResponse;
import com.prabhav.employee.dto.PasswordChangeRequest;
import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.mapper.EmployeeMapper;
import com.prabhav.employee.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepo employeeRepo;
    private final EmployeeMapper employeeMapper;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String createEmployee(EmployeeRequest request) {
        Employee employee = employeeMapper.toEntity(request);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRepo.save(employee);
        return "Employee Created Successfully";
    }

    public EmployeeResponse getEmployeeInfo(String email) {
        Employee employee = employeeRepo.findByEmail(email);
        if(employee == null) {
            return null;
        }
        return employeeMapper.toResponse(employee);
    }

    public String updateEmployeeProfile(Long employeeId, EmployeeUpdateRequest updateRequest) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        employee = employeeMapper.toEntity(updateRequest, employee);

        employeeRepo.save(employee);
        return "Employee's profile updated successfully";
    }
    public String changePassword(Long employeeId, PasswordChangeRequest passwordChangeRequest) {
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Validate old password
        if (!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), employee.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Check if new password and confirm password match
        if (!passwordChangeRequest.getNewPassword().equals(passwordChangeRequest.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }

        // Update password
        employee.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        employeeRepo.save(employee);

        return "Password changed successfully";
    }

    public String deleteEmployee(Long employeeId) {
        try {
            if (employeeRepo.existsById(employeeId)) {
                employeeRepo.deleteById(employeeId);
                return "Employee deleted successfully";
            } else {
                return "Employee not found"; // Return a message if the employee doesn't exist
            }
        } catch (Exception e) {
            return "Failed to delete employee: " + e.getMessage(); // Handle any exceptions and return an error message
        }
    }

    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        return employeeMapper.toResponse(employees);
    }
}
