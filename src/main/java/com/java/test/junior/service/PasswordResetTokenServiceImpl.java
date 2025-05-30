package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.exception.TokenExpired;
import com.java.test.junior.mapper.PasswordResetTokenMapper;
import com.java.test.junior.model.PasswordResetToken;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;

@Service
@Log
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements  PasswordResetTokenService{
    private final PasswordResetTokenMapper passwordResetTokenMapper;
    private final UserService userService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<Response> save(PasswordResetToken token) {
        log.info("Saving password reset token");
        passwordResetTokenMapper.save(token);
        log.info("Password reset token saved");
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Password reset token saved", token));
    }

    @Override
    public ResponseEntity<Response> findByToken(String token) {
        log.info("Finding password reset token with token: " + token);
        PasswordResetToken passwordResetToken = passwordResetTokenMapper.findByToken(token);
        if (passwordResetToken == null) {
            log.warning("Password reset token with token {" + token + "} not found");
            throw new ResourceNotFoundException("Password reset token with token {" + token + "} not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Password reset token found successfully", passwordResetToken));
    }

    @Override
    public ResponseEntity<Response> deleteByToken(String token) {
        log.info("Deleting passsword reset token with token: " + token);
        passwordResetTokenMapper.deleteByToken(token);
        log.info("Password reset token deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Password reset token deleted successfully", null));
    }

    @Override
    public ResponseEntity<Response> forgotPassword(String email) {
        log.info("Initiating forgot password logic");
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, email, LocalDateTime.now().plusHours(3).plusMinutes(5));
        passwordResetTokenMapper.save(resetToken);

        String message = "Use this token to reset your password: " + token;
        emailService.send(email, "Password Reset Request", message);

        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("If email exists, password has been sent.", null));
    }

    public ResponseEntity<Response> resetPassword(String token, String newPassword) {
        PasswordResetToken foundToken = passwordResetTokenMapper.findByToken(token);
        if (foundToken == null) {
            throw new ResourceNotFoundException("No token found");
        }

        if (foundToken.getExpiry().isBefore(LocalDateTime.now())) {
            passwordResetTokenMapper.deleteByToken(token);
            throw new TokenExpired("Token has expired");
        }

        String email = foundToken.getEmail();
        passwordResetTokenMapper.deleteByToken(token);

        userService.updatePasswordByEmail(email, passwordEncoder.encode(newPassword));
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Password reset successfully", null));
    }
}
