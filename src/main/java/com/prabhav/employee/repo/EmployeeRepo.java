package com.prabhav.employee.repo;

import com.prabhav.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
}
