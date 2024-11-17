package com.prabhav.employee.service;

import com.prabhav.employee.dto.EmployeeRequest;
import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.mapper.EmployeeMapper;
import com.prabhav.employee.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
