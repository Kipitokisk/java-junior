package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Log
public class UserServiceImpl implements UserService{
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findByUsername(String username) {
        log.info("Finding user with username: " + username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warning("User not found with username: " + username);
            throw new ResourceNotFoundException("User not found with username: " + username);
        }
        return user;
    }

    public User findByRole(String role) {
        log.info("Finding users with role: " + role);
        User user = userMapper.findByRole(role);
        if (user == null) {
            log.warning("User not found with role: " + role);
            throw new ResourceNotFoundException("User not found with role: " + role);
        }
        return user;
    }

    public User findByEmail(String email) {
        log.info("Finding users with email: " + email);
        User user = userMapper.findByEmail(email);
        if (user == null) {
            log.warning("User not found with email: " + email);
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    @Override
    public void updatePasswordByEmail(String email, String newPassword) {
        userMapper.updatePasswordByEmail(email, newPassword);
    }

    public ResponseEntity<Response> save(UserDTO userDTO) {
        log.info("Registration called with: " + userDTO);

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole("USER");

        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new ResourceAlreadyExistsException("Username '" + user.getUsername() + "' is already taken");
        }
        userMapper.save(user);
        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("User registered successfully",
                        Collections.singletonMap("username", user.getUsername())));
    }

    public void delete(Long id) {
        userMapper.delete(id);
    }
}
