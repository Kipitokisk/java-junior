/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */

package com.java.test.junior.service;

import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;

import java.util.List;

/**
 * @author dumitru.beselea
 * @version java-test-junior
 * @apiNote 08.12.2022
 */
public interface ProductService {
    /**
     * @param productDTO this product to be created
     * @return the product created from the database
     */
    Product createProduct(ProductDTO productDTO);
    Product findProduct(Long id);
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<Product> findAll(int page, int pageSize);
}