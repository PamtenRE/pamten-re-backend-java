package com.careermatch.pamtenproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    @NotBlank(message = "User ID is required")
    @Size(min = 3, max = 64, message = "User ID must be between 3 and 64 characters")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "Role_id", referencedColumnName = "role_id")
    @NotNull(message = "Role is required")
    private Role role;

    @Column(name = "Email", nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Password hash is required")
    @Size(min = 60, max = 255, message = "Password hash must be between 60 and 255 characters")
    private String password;

    @Column(name = "Phone", nullable = false)
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Lob
    @Column(name = "QR_code")
    private byte[] qrCode;

    @Lob
    @Column(name = "Authenticator")
    private byte[] authenticator;

    @Column(name = "full_name")
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Full name must be between 2 and 255 characters")
    private String fullName;

    @Column(name = "profile_completed")
    @NotNull(message = "Profile completed status is required")
    private Boolean profileCompleted = false;

    @Column(name = "is_active")
    @NotNull(message = "Active status is required")
    private Boolean isActive = true;

    @Column(name = "created_at")
    @NotNull(message = "Created at timestamp is required")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @NotNull(message = "Updated at timestamp is required")
    private LocalDateTime updatedAt;

    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;
}