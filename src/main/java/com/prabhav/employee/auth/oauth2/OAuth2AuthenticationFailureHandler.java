package com.prabhav.employee.auth.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Unknown OAuth2 error";
        String localizedMessage = exception.getLocalizedMessage() != null ? exception.getLocalizedMessage() : errorMessage;
        
        String targetUrl = UriComponentsBuilder.fromUriString(frontendUrl + "/login")
            .queryParam("error", "oauth2_authentication_failed")
            .queryParam("message", localizedMessage)
            .build().toUriString();

        log.error("OAuth2 authentication failed: {}", errorMessage, exception);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
