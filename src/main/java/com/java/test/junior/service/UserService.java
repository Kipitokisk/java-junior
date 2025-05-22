package com.java.test.junior.service;

import com.java.test.junior.model.User;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    void save(User user);
    void update(User user);
    void delete(Long id);
}
