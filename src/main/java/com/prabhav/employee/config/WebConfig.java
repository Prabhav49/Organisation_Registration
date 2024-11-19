package com.prabhav.employee.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all origins for simplicity (you can restrict to specific origins later)
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")  // React frontend origin
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")  // Allow the methods you need
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
