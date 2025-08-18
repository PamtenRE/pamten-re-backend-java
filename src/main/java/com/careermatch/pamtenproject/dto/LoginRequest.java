package com.careermatch.pamtenproject.dto;

import lombok.Data;
import jakarta.validation.constraints.*;

@Data
public class LoginRequest {
    @NotBlank(message = "User ID is required")
    private String userId;
    
    @NotBlank(message = "Password is required")
    private String password;
}