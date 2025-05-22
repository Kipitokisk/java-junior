package com.java.test.junior.service;

import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserMapper userMapper;

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public void save(User user) {
        userMapper.save(user);
    }

    public void update(User user) {
        userMapper.update(user);
    }

    public void delete(Long id) {
        userMapper.delete(id);
    }
}
