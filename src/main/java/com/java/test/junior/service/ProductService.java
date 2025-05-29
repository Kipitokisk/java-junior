/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.model.PaginatedResponse;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.Response;
import org.springframework.http.ResponseEntity;

import java.sql.SQLException;


public interface ProductService {
    ResponseEntity<Response> createProduct(ProductDTO productDTO);

    ResponseEntity<Response> findProduct(Long id);

    ResponseEntity<Response> updateProduct(Long id, ProductDTO productDTO);

    ResponseEntity<Response> deleteProduct(Long id);

    ResponseEntity<PaginatedResponse> findAll(int page, int pageSize);

    ResponseEntity<Response> findByName(String name);

    ResponseEntity<Response> loadProductsFromCsv(String fileLocation) throws SQLException;

    void deleteAllByUserId(Long id);
}