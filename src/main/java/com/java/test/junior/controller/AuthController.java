package com.java.test.junior.controller;

import com.java.test.junior.model.Response;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO) {
        return userService.save(userDTO);
    }
}