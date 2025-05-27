package com.java.test.junior.mapper;

import com.java.test.junior.model.User;

public interface UserMapper {
    User findByUsername(String username);
    void save(User user);
    void delete(Long id);
    int countByRole(String role);
    User findByRole(String role);
}
