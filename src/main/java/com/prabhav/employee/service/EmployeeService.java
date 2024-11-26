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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public String uploadEmployeeImage(Long employeeId, MultipartFile file) {
        // Fetch the employee from the repository
        Employee employee = employeeRepo.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Get the employee's first name and sanitize it for a valid filename
        String firstName = employee.getFirstName();
        String cleanFirstName = firstName != null ? firstName.replaceAll("[^a-zA-Z0-9]", "_") : "no_firstname"; // Clean first name

        // Create the file name in the format: id_firstname.extension
        String fileName = employeeId + "_" + cleanFirstName + "." + StringUtils.getFilenameExtension(file.getOriginalFilename());

        try {
            // Define the directory to save images
            Path uploadPath = Paths.get("uploads/images");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);  // Ensure the directory exists
            }

            // Define the full path where the file will be saved
            Path filePath = uploadPath.resolve(fileName);

            // If a file with the same name exists, delete it to overwrite
            if (Files.exists(filePath)) {
                Files.delete(filePath);  // Delete the old file
                System.out.println("Existing file deleted: " + filePath);  // Optional: Log the deletion
            }

            // Copy the new file to the target location
            Files.copy(file.getInputStream(), filePath);
            System.out.println("New file uploaded: " + filePath);  // Optional: Log the upload

            // Update the employee record with the new photograph path
            String photographPath = "/uploads/images/" + fileName;
            employee.setPhotographPath(photographPath); // Save the path
            employeeRepo.save(employee);  // Save the updated employee record

            // Return the photograph path
            return photographPath;
        } catch (IOException e) {
            // Handle file IO exceptions (e.g., file permission issues)
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        } catch (Exception e) {
            // Handle other exceptions
            throw new RuntimeException("An error occurred while uploading the image: " + e.getMessage(), e);
        }
    }




    public List<EmployeeResponse> getAllEmployees() {
        List<Employee> employees = employeeRepo.findAll();
        return employeeMapper.toResponse(employees);
    }
}
