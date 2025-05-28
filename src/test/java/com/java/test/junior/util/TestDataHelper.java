package com.java.test.junior.util;

import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.ProductService;
import com.java.test.junior.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TestDataHelper {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createTestUser(String username, String password) {
        UserDTO user = new UserDTO(username, password);
        userService.save(user);
        userService.findByUsername(username);
    }

    public Product createTestProduct(String name, Double price, String description, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, "testpass", userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        try {
            ProductDTO productDTO = new ProductDTO(name, price, description);
            productService.createProduct(productDTO);
            return (Product) Objects.requireNonNull(productService.findByName(name).getBody()).getData();
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    public void cleanupAllTables() {
        try {
            jdbcTemplate.update("DELETE FROM user_product");
            jdbcTemplate.update("DELETE FROM product");
            jdbcTemplate.update("DELETE FROM \"user\"");
        } catch (Exception e) {
            System.err.println("Failed to clean up database: " + e.getMessage());
        }
    }

    public void createAdminUser() {
        String password = passwordEncoder.encode("123456");
        jdbcTemplate.update(
                "INSERT INTO \"user\" (id, username, password, created_at, updated_at, role) VALUES (?, ?, ?, current_timestamp, current_timestamp, ?)",
                1, "admin", password, "ADMIN"
        );
    }
}