package com.java.test.junior.admin;

import com.java.test.junior.mapper.UserMapper;
import com.java.test.junior.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    private static final String ADMIN_PASSWORD = System.getenv("ADMIN_PASSWORD");


    public AdminInitializer(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        int adminCount = userMapper.countByRole("ADMIN");

        if (adminCount == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
            admin.setRole("ADMIN");

            userMapper.save(admin);
            logger.info("Default admin created.");
        } else {
            logger.warn("Admin already exists.");
        }
    }
}

