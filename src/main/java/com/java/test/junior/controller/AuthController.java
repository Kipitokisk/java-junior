package com.java.test.junior.controller;

import com.java.test.junior.model.Response;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO) {
        logger.info("POST /api/auth/register called with: {}", userDTO);
        return userService.save(userDTO);

    }
}