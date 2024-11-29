package com.prabhav.employee.mapper;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.dto.EmployeeResponse;
import com.prabhav.employee.dto.EmployeeUpdateRequest;
import com.prabhav.employee.entity.Employee;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
//                .departmentId(request.getDepartmentId())
                .title(request.getTitle())
                .photographPath(request.getPhotographPath())
                .build();
    }

    public Employee toEntity(EmployeeUpdateRequest updateRequest, Employee existingEmployee) {
        if (updateRequest.getFirstName() != null) {
            existingEmployee.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            existingEmployee.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            existingEmployee.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            existingEmployee.setPassword(updateRequest.getPassword());
        }
        if (updateRequest.getTitle() != null) {
            existingEmployee.setTitle(updateRequest.getTitle());
        }
        if (updateRequest.getPhotographPath() != null) {
            existingEmployee.setPhotographPath(updateRequest.getPhotographPath());
        }
//        if (updateRequest.getDepartmentId() != null) {
//            existingEmployee.setDepartmentId(updateRequest.getDepartmentId());
//        }
        return existingEmployee;
    }

    // Map Employee entity to EmployeeResponse DTO
    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .title(employee.getTitle())
                .photographPath(employee.getPhotographPath())
//                .departmentName(getDepartmentName(employee.getDepartmentId()))
                .build();
    }

    public List<EmployeeResponse> toResponse(List<Employee> employees) {
        return employees.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private String getDepartmentName(Long departmentId) {
        return "Sample Department";
    }

}
