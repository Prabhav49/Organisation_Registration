package com.prabhav.employee.auth.oauth2;

import com.prabhav.employee.entity.Employee;
import com.prabhav.employee.repo.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final EmployeeRepo employeeRepo;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException("Error processing OAuth2 user");
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        OAuth2UserInfo oauth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            userRequest.getClientRegistration().getRegistrationId(),
            oauth2User.getAttributes()
        );

        if (oauth2UserInfo.getEmail() == null || oauth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<Employee> employeeOptional = employeeRepo.findByEmailAndProviderOrLocal(
            oauth2UserInfo.getEmail(),
            userRequest.getClientRegistration().getRegistrationId().toUpperCase()
        );

        Employee employee;
        if (employeeOptional.isPresent()) {
            employee = employeeOptional.get();
            employee = updateExistingEmployee(employee, oauth2UserInfo, userRequest.getClientRegistration().getRegistrationId());
        } else {
            employee = registerNewEmployee(oauth2UserInfo, userRequest.getClientRegistration().getRegistrationId());
        }

        return UserPrincipal.create(employee, oauth2User.getAttributes());
    }

    private Employee registerNewEmployee(OAuth2UserInfo oauth2UserInfo, String provider) {
        Employee employee = Employee.builder()
            .firstName(oauth2UserInfo.getFirstName())
            .lastName(oauth2UserInfo.getLastName())
            .email(oauth2UserInfo.getEmail())
            .password(null) // No password for OAuth users
            .role(com.prabhav.employee.entity.Role.EMPLOYEE) // Explicitly set role to EMPLOYEE
            .oauthProvider(provider.toUpperCase())
            .oauthId(oauth2UserInfo.getId())
            .isEmailVerified(true) // Trust Google email verification
            .isTwoFactorEnabled(false) // OAuth users start with 2FA disabled
            .isAccountLocked(false) // New users are not locked
            .failedLoginAttempts(0) // No failed attempts for new users
            .build();

        return employeeRepo.save(employee);
    }

    private Employee updateExistingEmployee(Employee existingEmployee, OAuth2UserInfo oauth2UserInfo, String provider) {
        // Update OAuth info if it's a local account being converted to OAuth
        if ("LOCAL".equals(existingEmployee.getOauthProvider())) {
            existingEmployee.setOauthProvider(provider.toUpperCase());
            existingEmployee.setOauthId(oauth2UserInfo.getId());
            existingEmployee.setIsEmailVerified(true);
        }

        return employeeRepo.save(existingEmployee);
    }
}
