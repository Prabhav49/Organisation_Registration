package com.prabhav.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Column(name = "last_name")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Column(name = "title")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Column(name = "photograph_path")
    private String photographPath;

    @Column(name = "department_id")
    private Long departmentId;

    @PrePersist
    public void prePersist() {
        if (this.photographPath == null || this.photographPath.isEmpty()) {
            this.photographPath = "/uploads/images/default.png"; // Default profile picture
        }

        if (this.employeeId == null) {
            this.employeeId = 1L;
        }
    }
}
