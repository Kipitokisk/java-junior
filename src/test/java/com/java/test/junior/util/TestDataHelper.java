package com.java.test.junior.util;

import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserDTO;
import com.java.test.junior.service.ProductService;
import com.java.test.junior.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDataHelper {
    private final ProductService productService;
    private final UserDetailsService userDetailsService;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

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
                "INSERT INTO \"user\" (id, username, password, created_at, updated_at, role, email) " +
                        "VALUES (?, ?, ?, current_timestamp, current_timestamp, ?, ?)", 1, "admin", password,
                "ADMIN", "testadmin@gmail.com");
    }

    public void createTestUser(int id, String username) {
        String password = passwordEncoder.encode("123456");
        jdbcTemplate.update("INSERT INTO \"user\" (id, username, password, created_at, updated_at, role, email) " +
                "VALUES (?, ?, ?, current_timestamp, current_timestamp, ?, ?)", id, username, password, "USER",
                username + "@gmail.com");
    }

    public String createPasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusHours(3).plusMinutes(30);

        jdbcTemplate.update("INSERT INTO password_reset_token (token, email, expiry) VALUES (?, ?, ?)",
                token, email, expiry);

        return token;
    }

    public String createExpiredPasswordResetToken(String email) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().minusHours(1);

        jdbcTemplate.update("INSERT INTO password_reset_token (token, email, expiry) VALUES (?, ?, ?)",
                token, email, expiry);

        return token;
    }
}