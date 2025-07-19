package com.prabhav.employee.repo;

import com.prabhav.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee, Long> {
    Employee findByEmail(String email);
    
    // OAuth2 related methods
    Optional<Employee> findByEmailAndOauthProvider(String email, String oauthProvider);
    Optional<Employee> findByOauthIdAndOauthProvider(String oauthId, String oauthProvider);
    
    @Query("SELECT e FROM Employee e WHERE e.email = :email AND (e.oauthProvider = :provider OR e.oauthProvider = 'LOCAL')")
    Optional<Employee> findByEmailAndProviderOrLocal(@Param("email") String email, @Param("provider") String provider);
}
