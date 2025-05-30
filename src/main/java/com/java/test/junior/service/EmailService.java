package com.java.test.junior.service;

import com.java.test.junior.model.Response;
import org.springframework.http.ResponseEntity;

public interface EmailService {
    ResponseEntity<Response> send(String to, String subject, String content);
}
