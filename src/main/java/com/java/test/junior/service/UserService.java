package com.java.test.junior.service;

import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import org.springframework.http.ResponseEntity;

public interface UserService {
    User findById(Long id);
    User findByUsername(String username);
    ResponseEntity<Response> save(UserDTO userDTO);
    void update(User user);
    void delete(Long id);
}
