/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.ProductMapper;
import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.*;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;
import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper;
    private final UserService userService;
    private final UserProductMapper userProductMapper;

    @Override
    public ResponseEntity<Response> createProduct(ProductDTO productDTO) {
        logger.info("Creating product: {}", productDTO);
        Product product = mapDTOToProduct(productDTO);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.save(product);
        logger.info("Product created with ID: {}", product.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(buildSuccessResponse("Product created successfully", productDTO));
    }

    private Product mapDTOToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new ResourceNotFoundException("Authenticated user not found in database");
        }
        product.setUserId(user.getId());

        return product;
    }

    @Override
    public ResponseEntity<Response> findProduct(Long id) {
        logger.info("Finding product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse("Product not found with id: " + id));
        }
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
    }

    @Override
    public ResponseEntity<Response> updateProduct(Long id, ProductDTO productDTO, String authentication) {
        String username = getUsername(authentication);
        Long userId = userService.findByUsername(username).getId();
        logger.info("Updating product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(getErrorResponse("Product not found with id: " + id));
        } else if (!product.getUserId().equals(userId)) {
            logger.warn("User with id {} doesn't have access to product with ID: {}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(getErrorResponse("User is not authorized to access this product."));
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        logger.info("Product updated with ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product updated successfully", product));
    }

    @Override
    public ResponseEntity<Response> deleteProduct(Long id, String authentication) {
        String username = getUsername(authentication);
        Long userId = userService.findByUsername(username).getId();
        logger.info("Deleting product with ID: {}", id);
        Product product = productMapper.findById(id);
        if (product == null) {
            logger.warn("Product not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse("Product not found with id: " + id));
        } else if (!product.getUserId().equals(userId)) {
            logger.warn("User with id {} doesn't have access to product with ID: {}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(getErrorResponse("User is not authorized to access this product."));
        }
        userProductMapper.deleteByProductId(id);
        productMapper.delete(id);
        logger.info("Product deleted with ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product deleted successfully", null));

    }

    @Override
    public ResponseEntity<?> findAll(int page, int pageSize) {
        logger.info("Fetching products, page: {}, pageSize: {}", page, pageSize);
        if (page < 1 || pageSize < 1) {
            logger.warn("Invalid pagination parameters: page={}, pageSize={}", page, pageSize);
            throw new IllegalArgumentException("Page and page size must be equal or greater than 1");
        }
        if (pageSize > 100) {
            logger.warn("Page size too large: {}", pageSize);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getErrorResponse("Page size cannot exceed 100"));
        }
        int offset = (page - 1) * pageSize;
        List<Product> list = productMapper.findAll(offset, pageSize);
        PaginatedResponse paginatedResponse = new PaginatedResponse();
        paginatedResponse.setSuccess(true);
        paginatedResponse.setMessage("Products retrieved successfully");
        paginatedResponse.setData(list);
        paginatedResponse.setPage(page);
        paginatedResponse.setPageSize(pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(paginatedResponse);

    }

    @Override
    public ResponseEntity<Response> findByName(String name) {
        logger.info("Searching for product with name: {}", name);
        Product product = productMapper.findByName(name);
        if (product == null) {
            logger.warn("Product not found with name: {}", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getErrorResponse("Product not found with name: " + name));
        }
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
    }

    private static String getUsername(String authentication) {
        String pair = new String(Base64.decodeBase64(authentication.substring(6)));
        return pair.split(":")[0];
    }
}