package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;
import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public User findByUsername(String username) {
        logger.info("Finding user with username: {}", username);
        User user = userMapper.findByUsername(username);
        if (user == null) {
            logger.warn("Product not found with username: {}", username);
            throw new ResourceNotFoundException("Product not found with username: " + username);
        }
        return user;
    }

    public ResponseEntity<Response> save(UserDTO userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        if (!StringUtils.hasText(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Username is required"));
        }
        if (!StringUtils.hasText(user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Password is required"));
        }
        if (user.getPassword().length() < 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Password must be at least 6 characters"));
        }

        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(getErrorResponse("Username '" + user.getUsername() + "' is already taken"));
        }
        userMapper.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("User registered successfully", Collections.singletonMap("username", user.getUsername())));
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void delete(Long id) {
        userMapper.delete(id);
    }
}
