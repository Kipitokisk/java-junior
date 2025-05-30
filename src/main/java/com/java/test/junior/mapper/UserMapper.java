package com.java.test.junior.mapper;

import com.java.test.junior.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User findByUsername(String username);

    void save(User user);

    void delete(Long id);

    int countByRole(String role);

    User findByRole(String role);

    User findByEmail(String email);

    void updatePasswordByEmail(String email, String encodedPassword);
}
