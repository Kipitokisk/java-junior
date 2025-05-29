package com.java.test.junior.service;

import com.java.test.junior.model.Response;
import org.springframework.http.ResponseEntity;

public interface UserProductService {
    ResponseEntity<Response> like(Long productId);
}
