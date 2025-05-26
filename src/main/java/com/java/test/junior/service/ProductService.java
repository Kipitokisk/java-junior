/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.Response;
import org.springframework.http.ResponseEntity;


public interface ProductService {
    ResponseEntity<Response> createProduct(ProductDTO productDTO);
    ResponseEntity<Response> findProduct(Long id);
    ResponseEntity<Response> updateProduct(Long id, ProductDTO productDTO, String authentication);
    ResponseEntity<Response> deleteProduct(Long id, String authentication);
    ResponseEntity<?> findAll(int page, int pageSize);
    ResponseEntity<Response> findByName(String name);
}