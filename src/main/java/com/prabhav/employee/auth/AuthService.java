package com.prabhav.employee.auth;

import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepo employeeRepo;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String email, String password) {
        Employee employee = employeeRepo.findByEmail(email);

        if (employee != null && passwordEncoder.matches(password, employee.getPassword())) {
            return jwtUtil.generateToken(email);
        } else {
            return "Invalid email or password";
        }
    }

    public String processOAuth2Login(String email, String provider) {
        Optional<Employee> employeeOptional = employeeRepo.findByEmailAndProviderOrLocal(email, provider);
        
        if (employeeOptional.isPresent()) {
            return jwtUtil.generateToken(email);
        } else {
            return "User not found or not authorized";
        }
    }

    public void initiateGoogleLogout(String email) {
        // Log the logout event
        System.out.println("User logged out via Google OAuth: " + email);
    }
}
