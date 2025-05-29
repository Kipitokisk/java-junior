package com.java.test.junior.controller;

import com.java.test.junior.model.Response;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO) {
        return userService.save(userDTO);
    }
}