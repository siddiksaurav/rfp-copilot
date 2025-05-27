package com.rfp.copilot.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "employees")
@Data
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String position;

    private String name;
    private String phone;
    private String email;
    private String location;

    @Column(name = "academic_qualification")
    private String academicQualification;

    private String subject;

    private Integer experience;

    private String skills;

    private String certification;

    @Column(name = "experience_details", columnDefinition = "TEXT")
    private String experienceDetails;
}
