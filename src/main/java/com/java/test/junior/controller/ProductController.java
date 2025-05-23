/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.controller;

import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.Response;
import com.java.test.junior.service.ProductService;
import com.java.test.junior.service.UserProductService;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;
import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

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
        Product product = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse("Product created successfully", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> findProductById(@PathVariable("id") @Min(1) Long id) {
        logger.info("GET /api/products/{} called", id);
        try {
            Product product = productService.findProduct(id);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
        } catch (ResourceNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> modifyProductById(@PathVariable("id") @Min(1) Long id, @Valid @RequestBody ProductDTO productDTO) {
        logger.info("PUT /api/products/{} called with: {}", id, productDTO);
        try {
            Product product = productService.updateProduct(id, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product updated successfully", product));
        } catch (ResourceNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable("id") @Min(1) Long id) {
        logger.info("DELETE /api/products/{} called", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product deleted successfully", null));
        } catch (ResourceNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int pageSize) {
        logger.info("GET /api/products called with page={}, pageSize={}", page, pageSize);
        try {
            List<Product> products = productService.findAll(page, pageSize);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Products retrieved successfully");
            response.put("data", products);
            response.put("page", page);
            response.put("pageSize", pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException i) {
            logger.warn("Invalid pagination parameters: {}", i.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(i.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Response> findProductByName(@PathVariable("name") String name) {
        logger.info("GET /api/products/name/{} called", name);
        try {
            Product product = productService.findByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
        } catch (ResourceNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Response> likeProductById(@PathVariable("id") Long productId, @RequestHeader("Authorization") String authentication) {
        try {
            logger.info("POST /api/products/like/{} called", productId);
            String pair = new String(Base64.decodeBase64(authentication.substring(6)));
            String username = pair.split(":")[0];
            userProductService.save(username, productId);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product liked successfully", null));
        } catch (ResourceNotFoundException p) {
        logger.warn("Product not found: {}", p.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @DeleteMapping("/dislike/{id}")
    public ResponseEntity<Response> dislikeProductById(@PathVariable("id") Long productId, @RequestHeader("Authorization") String authentication) {
        try {
            logger.info("DELETE /api/products/dislike/{} called", productId);
            String pair = new String(Base64.decodeBase64(authentication.substring(6)));
            String username = pair.split(":")[0];
            userProductService.delete(username, productId);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product disliked successfully", null));
        } catch (ResourceNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }
}