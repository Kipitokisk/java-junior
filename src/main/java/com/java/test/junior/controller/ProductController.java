/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.controller;

import com.java.test.junior.model.PaginatedResponse;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.Response;
import com.java.test.junior.service.ProductService;
import com.java.test.junior.service.UserProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/products")
@Validated
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;
    private final UserProductService userProductService;

    @PostMapping
    public ResponseEntity<Response> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        return productService.createProduct(productDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> findProductById(@PathVariable("id") @Min(1) Long id) {
        return productService.findProduct(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response> modifyProductById(@PathVariable("id") @Min(1) Long id,
                                                      @Valid @RequestBody ProductDTO productDTO) {
        return productService.updateProduct(id, productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProduct(@PathVariable("id") @Min(1) Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse> findAll(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int pageSize) {
        return productService.findAll(page, pageSize);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Response> findProductByName(@PathVariable("name") String name) {
        return productService.findByName(name);
    }

    @PostMapping("/like/{id}")
    public ResponseEntity<Response> likeProductById(@PathVariable("id") Long productId) {
        return userProductService.like(productId);
    }
}