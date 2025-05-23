package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserMapper userMapper;

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

    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!StringUtils.hasText(user.getUsername())) {
            throw new IllegalArgumentException("Username is required");
        }
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        User existingUser = userMapper.findByUsername(user.getUsername());
        if (existingUser != null) {
            throw new ResourceAlreadyExistsException("Username '" + user.getUsername() + "' is already taken");
        }

        userMapper.save(user);
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void delete(Long id) {
        userMapper.delete(id);
    }
}
