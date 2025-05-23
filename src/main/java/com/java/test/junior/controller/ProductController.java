/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.controller;

import com.java.test.junior.exception.ProductNotFoundException;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.service.ProductService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        logger.info("POST /api/products called with: {}", productDTO);
        Product product = productService.createProduct(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse("Product created successfully", product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductById(@PathVariable("id") @Min(1) Long id) {
        logger.info("GET /api/products/{} called", id);
        try {
            Product product = productService.findProduct(id);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
        } catch (ProductNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyProductById(@PathVariable("id") @Min(1) Long id, @Valid @RequestBody ProductDTO productDTO) {
        logger.info("PUT /api/products/{} called with: {}", id, productDTO);
        try {
            Product product = productService.updateProduct(id, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product updated successfully", product));
        } catch (ProductNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") @Min(1) Long id) {
        logger.info("DELETE /api/products/{} called", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product deleted successfully", null));
        } catch (ProductNotFoundException p) {
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
            Map<String, Object> response = buildSuccessResponse("Products retrieved successfully", products);
            response.put("page", page);
            response.put("pageSize", pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (IllegalArgumentException i) {
            logger.warn("Invalid pagination parameters: {}", i.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse(i.getMessage()));
        }
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<?> findProductByName(@PathVariable("name") @NotBlank String name) {
        logger.info("GET /api/products/{} called", name);
        try {
            Product product = productService.findByName(name);
            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
        } catch (ProductNotFoundException p) {
            logger.warn("Product not found: {}", p.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse(p.getMessage()));
        }
    }


}