package com.java.test.junior.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PasswordResetToken {
    private String token;
    private String email;
    private LocalDateTime expiry;
}
