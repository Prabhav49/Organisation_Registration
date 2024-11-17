package com.prabhav.employee.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "organization")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long organizationId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "hr_contact_name")
    private String hrContactName;

    @Column(name = "hr_contact_email", unique = true)
    private String hrContactEmail;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "pincode")
    private int pincode;

    @Column(name = "phone")
    private String phone;

    // No relationship to Employee here, just the fields for Organization.
}
