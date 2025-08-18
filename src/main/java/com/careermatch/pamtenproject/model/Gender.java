package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Gender")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "gender_name", nullable = false, unique = true)
    @NotBlank(message = "Gender name is required")
    @Size(min = 2, max = 50, message = "Gender name must be between 2 and 50 characters")
    private String genderName;
}