/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.exception.ProductNotFoundException;
import com.java.test.junior.mapper.ProductMapper;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.ProductDTO;
import com.java.test.junior.model.User;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper;
    private final UserService userService;

    @Override
    public Product createProduct(ProductDTO productDTO) {
        logger.info("Creating product: {}", productDTO);
        Product product = mapDTOToProduct(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.save(product);
        logger.info("Product created with ID: {}", product.getId());
        return product;
    }

    private Product mapDTOToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("Authenticated user not found in database");
        }
        product.setUserId(user.getId());

        return product;
    }

    @Override
    public Product findProduct(Long id) {
        logger.info("Finding product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        return product;
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        logger.info("Updating product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        logger.info("Product updated with ID: {}", id);
        return product;
    }

    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productMapper.delete(id);
        logger.info("Product deleted with ID: {}", id);
    }

    @Override
    public List<Product> findAll(int page, int pageSize) {
        logger.info("Fetching products, page: {}, pageSize: {}", page, pageSize);
        if (page < 1 || pageSize < 1) {
            logger.warn("Invalid pagination parameters: page={}, pageSize={}", page, pageSize);
            throw new IllegalArgumentException("Page and page size must be equal or greater than 1");
        }
        if (pageSize > 100) {
            logger.warn("Page size too large: {}", pageSize);
            throw new IllegalArgumentException("Page size cannot exceed 100");
        }
        int offset = (page - 1) * pageSize;
        return productMapper.findAll(offset, pageSize);
    }

    public Product findByName(String name) {
        logger.info("Searching for product with name: {}", name);
        Product product = productMapper.findByName(name);
        if (product == null) {
            logger.warn("Product not found with name: {}", name);
            throw new ProductNotFoundException("Product not found with name: " + name);
        }
        return product;
    }
}