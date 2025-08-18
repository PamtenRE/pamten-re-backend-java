package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "region")
    @Size(max = 100, message = "Region cannot exceed 100 characters")
    private String region;

    @Column(name = "street_address")
    @Size(max = 255, message = "Street address cannot exceed 255 characters")
    private String streetAddress;

    @Column(name = "city", nullable = false)
    @NotBlank(message = "City is required")
    @Size(min = 1, max = 100, message = "City must be between 1 and 100 characters")
    private String city;

    @Column(name = "state", nullable = false)
    @NotBlank(message = "State is required")
    @Size(min = 2, max = 100, message = "State must be between 2 and 100 characters")
    private String state;

    @Column(name = "zip_code", nullable = false)
    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid zip code format")
    @Size(max = 10, message = "Zip code cannot exceed 10 characters")
    private String zipCode;

    @Column(name = "country")
    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country = "USA";

    @Column(name = "created_at")
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    // Remove the timeZones field and related annotations
}