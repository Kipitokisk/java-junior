package com.java.test.junior.controller;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;
import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO) {
        logger.info("POST /api/auth/register called with: {}", userDTO);
        try {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userService.save(user);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("User registered successfully", user));
        } catch (IllegalArgumentException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(e.getMessage()));
        } catch (ResourceAlreadyExistsException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorResponse(e.getMessage()));
        }

    }
}