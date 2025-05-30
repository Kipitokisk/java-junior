package com.java.test.junior.service;

import com.java.test.junior.model.PasswordResetToken;
import com.java.test.junior.model.Response;
import org.springframework.http.ResponseEntity;

public interface PasswordResetTokenService {
    ResponseEntity<Response> save(PasswordResetToken token);

    ResponseEntity<Response> findByToken(String token);

    ResponseEntity<Response> deleteByToken(String token);

    ResponseEntity<Response> forgotPassword(String email);

    ResponseEntity<Response> resetPassword(String token, String newPassword);
}
