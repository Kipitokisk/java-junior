/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.exception.ForbiddenException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.ProductMapper;
import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Log
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final UserService userService;
    private final UserProductMapper userProductMapper;
    private final DataSource dataSource;


    @Override
    public ResponseEntity<Response> createProduct(ProductDTO productDTO) {
        log.info("Creating product: " + productDTO);
        Product product = mapDTOToProduct(productDTO);
        productMapper.save(product);
        log.info("Product created with ID: " + product.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(buildSuccessResponse("Product created successfully", productDTO));
    }

    private Product mapDTOToProduct(ProductDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());

        String username = ((UserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getUsername();

        User user = userService.findByUsername(username);
        if (user == null) {
            log.warning("User not found with username: " + username);
            throw new ResourceNotFoundException("Authenticated user not found in database");
        }
        product.setUserId(user.getId());

        return product;
    }

    @Override
    public ResponseEntity<Response> findProduct(Long id) {
        log.info("Finding product with ID: " + id);
        Product product = productMapper.findById(id);
        if (product == null) {
            productNotFound(id);
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product retrieved successfully", product));
    }

    @Override
    public ResponseEntity<Response> updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product with ID: " + id);
        Product product = checkPermission(id);
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        log.info("Product updated with ID: " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product updated successfully", product));
    }

    private void noAccess(Long id, Long userId) {
        log.warning("User with id " + userId + " doesn't have access to product with ID: " + id);
    }

    private void productNotFound(Long id) {
        log.warning("Product not found with ID: " + id);
    }

    @Override
    public ResponseEntity<Response> deleteProduct(Long id) {
        log.info("Deleting product with ID: " + id);
        checkPermission(id);
        userProductMapper.deleteByProductId(id);
        productMapper.delete(id);
        log.info("Product deleted with ID: " + id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product deleted successfully", null));
    }

    private Product checkPermission(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long userId = userService.findByUsername(username).getId();
        Product product = productMapper.findById(id);
        if (product == null) {
            productNotFound(id);
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        } else if (!product.getUserId().equals(userId)) {
            noAccess(id, userId);
            throw new ForbiddenException("User with id " + userId + " doesn't have access to product with ID: " + id);
        }
        return product;
    }

    @Override
    public ResponseEntity<PaginatedResponse> findAll(int page, int pageSize) {
        log.info("Fetching products, page: " + page + ", pageSize: " + pageSize);
        int offset = (page - 1) * pageSize;
        List<Product> list = productMapper.findAll(offset, pageSize);
        String message = "Products retrieved successfully";
        PaginatedResponse paginatedResponse = new PaginatedResponse(true, message, list, page, pageSize);

        return ResponseEntity.status(HttpStatus.OK).body(paginatedResponse);
    }

    @Override
    public ResponseEntity<Response> findByName(String name) {
        log.info("Searching for product with name: " + name);
        Product product = productMapper.findByName(name);
        if (product == null) {
            log.warning("Product not found with name: " + name);
            throw new ResourceNotFoundException("Product not found with name: " + name);
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product retrieved successfully", product));
    }

    @Override
    public ResponseEntity<Response> loadProductsFromCsv(String fileLocation) throws SQLException, IOException {
        log.info("Loading products from CSV with path: " + fileLocation);
        long adminUserId = userService.findByRole("ADMIN").getId();

        InputStream inputStream = getInputStream(fileLocation);

        File tempFile = getTempFile(inputStream, adminUserId);

        copy(tempFile);

        deleteTempFile(tempFile);

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("CSV file copied successfully", null));
    }

    @Override
    public void deleteAllByUserId(Long id) {
        productMapper.deleteAllByUserId(id);
    }

    private void deleteTempFile(File tempFile) throws IOException{
        Files.delete(tempFile.toPath());
        log.info("Temporary file deleted.");
    }

    private void copy(File tempFile) throws SQLException, IOException {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        CopyManager copyManager = new CopyManager(conn.unwrap(BaseConnection.class));

        try (Reader processedReader = new FileReader(tempFile)) {
            copyManager.copyIn(
                    "COPY product(name, price, description, user_id, created_at, updated_at) " +
                            "FROM STDIN WITH (FORMAT csv, HEADER true)",
                    processedReader
            );
        }
    }


    private File getTempFile(InputStream inputStream, long adminUserId) throws IOException {
        File tempFile = Files.createTempFile("processed-products", ".csv").toFile();
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            reader.readLine();
            writer.write("name,price,description,user_id,created_at,updated_at\n");

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line + "," + adminUserId + "," + nowStr + "," + nowStr + "\n");
            }
        }
        return tempFile;
    }

    private InputStream getInputStream(String fileLocation) throws IOException {
        InputStream inputStream;
        if (fileLocation.startsWith("http://") || fileLocation.startsWith("https://")) {
            inputStream = new URL(fileLocation).openStream();
        } else {
            inputStream = new FileInputStream(fileLocation);
        }
        return inputStream;
    }
}