/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */

package com.java.test.junior.service;

import com.java.test.junior.exception.ProductNotFoundException;
import com.java.test.junior.mapper.ProductMapper;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dumitru.beselea
 * @version java-test-junior
 * @apiNote 08.12.2022
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    @Override
    public Product createProduct(ProductDTO productDTO) {
        Product product = mapDTOToProduct(productDTO);
        productMapper.save(product);
        return product;
    }

    private static Product mapDTOToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setUserId(1L);
        return product;
    }

    @Override
    public Product findProduct(Long id){
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        productMapper.update(product);
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productMapper.delete(id);
    }

    @Override
    public List<Product> findAll(int page, int pageSize) {
        if (page < 1 || pageSize < 1) {
            throw new IllegalArgumentException("Page and page size must be equal or greater than 1");
        }
        int offset = (page - 1) * pageSize;
        return productMapper.findAll(offset, pageSize);
    }
}