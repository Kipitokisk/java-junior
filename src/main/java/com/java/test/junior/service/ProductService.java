/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;

import java.util.List;

public interface ProductService {
    Product createProduct(ProductDTO productDTO);
    Product findProduct(Long id);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<Product> findAll(int page, int pageSize);
}