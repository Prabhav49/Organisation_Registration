package com.prabhav.employee.mapper;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .departmentId(request.getDepartmentId())  // Set the departmentId directly
                .title(request.getTitle())  // Assuming title is also part of the request, based on your Employee entity
                .photographPath(request.getPhotographPath())  // Assuming photographPath is part of the request
                .build();
    }
}
