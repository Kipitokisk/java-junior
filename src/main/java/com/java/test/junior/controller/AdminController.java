package com.java.test.junior.controller;

import com.java.test.junior.model.Response;
import com.java.test.junior.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;

    @PostMapping("/loading/products")
    public ResponseEntity<Response> loadProducts(@RequestParam String path) throws SQLException, IOException {
        return productService.loadProductsFromCsv(path);
    }
}
