/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.controller;

import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.Response;
import com.java.test.junior.service.ProductService;
import com.java.test.junior.service.UserProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
@AllArgsConstructor
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final UserProductService userProductService;

    @PostMapping
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("POST /api/products called with: {}", productDTO);
        return productService.createProduct(productDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> findProductById(@PathVariable("id") @Min(1) Long id) {
        logger.info("GET /api/products/{} called", id);
        return productService.findProduct(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> modifyProductById(@PathVariable("id") @Min(1) Long id, @Valid @RequestBody ProductDTO productDTO) {
        logger.info("PUT /api/products/{} called with: {}", id, productDTO);
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable("id") @Min(1) Long id) {
        logger.info("DELETE /api/products/{} called", id);
        return productService.deleteProduct(id);
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int pageSize) {
        logger.info("GET /api/products called with page={}, pageSize={}", page, pageSize);
        return productService.findAll(page, pageSize);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Response> findProductByName(@PathVariable("name") String name) {
        logger.info("GET /api/products/name/{} called", name);
        return productService.findByName(name);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Response> likeProductById(@PathVariable("id") Long productId, @RequestHeader("Authorization") String authentication) {
        logger.info("POST /api/products/like/{} called", productId);
        return userProductService.save(authentication, productId);
    }

    @DeleteMapping("/dislike/{id}")
    public ResponseEntity<Response> dislikeProductById(@PathVariable("id") Long productId, @RequestHeader("Authorization") String authentication) {
        logger.info("DELETE /api/products/dislike/{} called", productId);
        return userProductService.delete(authentication, productId);
    }
}