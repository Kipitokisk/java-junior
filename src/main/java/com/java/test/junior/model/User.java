package com.java.test.junior.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Userame is required")
    private String username;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String role;
}