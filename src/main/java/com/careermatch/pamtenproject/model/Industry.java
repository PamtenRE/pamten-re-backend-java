package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "Industries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "industry_id")
    private Integer industryId;

    @Column(name = "industry_name", nullable = false, unique = true)
    @NotBlank(message = "Industry name is required")
    @Size(min = 2, max = 100, message = "Industry name must be between 2 and 100 characters")
    private String industryName;

    @Column(name = "description", columnDefinition = "TEXT")
    @Size(max = 1000, message = "Industry description cannot exceed 1000 characters")
    private String description;
}