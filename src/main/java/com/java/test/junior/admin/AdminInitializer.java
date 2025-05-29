package com.java.test.junior.admin;

import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Log
@Component
@RequiredArgsConstructor
public class AdminInitializer implements ApplicationRunner {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    @Value("${admin.default.password}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        int adminCount = userMapper.countByRole("ADMIN");

        if (adminCount == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole("ADMIN");

            userMapper.save(admin);
            log.info("Default admin created.");
        } else {
            log.warning("Admin already exists.");
        }
    }
}

