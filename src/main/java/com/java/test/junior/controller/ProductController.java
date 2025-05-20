/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */

package com.java.test.junior.controller;

import com.java.test.junior.exception.ProductNotFoundException;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author dumitru.beselea
 * @version java-test-junior
 * @apiNote 08.12.2022
 */

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductDTO productDTO) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating product: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findProductById(@PathVariable("id") Long id) {
        try {
            Product product = productService.findProduct(id);
            return ResponseEntity.status(HttpStatus.FOUND).body(product);
        } catch (ProductNotFoundException p) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(p.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyProductById(@PathVariable("id") Long id, @RequestBody ProductDTO productDTO) {
        try {
            Product product = productService.updateProduct(id, productDTO);
            return ResponseEntity.status(HttpStatus.OK).body(product);
        } catch (ProductNotFoundException p) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(p.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (ProductNotFoundException p) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(p.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        try {
            List<Product> products = productService.findAll(page, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(products);
        } catch (IllegalArgumentException i) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(i.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}