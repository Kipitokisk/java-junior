/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */
package com.java.test.junior.service;

import com.java.test.junior.exception.ForbiddenException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.ProductMapper;
import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.*;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;
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
import static com.java.test.junior.util.ResponseUtil.getErrorResponse;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper;
    private final UserService userService;
    private final UserProductMapper userProductMapper;
    private final DataSource dataSource;

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
            logger.warn("User not found with username: {}", username);
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
            productNotFound(id);
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product retrieved successfully", product));
    }

    @Override
    public ResponseEntity<Response> updateProduct(Long id, ProductDTO productDTO, String authentication) {
        logger.info("Updating product with ID: {}", id);
        String username = getUsername(authentication);
        Long userId = userService.findByUsername(username).getId();
        Product product = productMapper.findById(id);
        if (product == null) {
            productNotFound(id);
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        } else if (!product.getUserId().equals(userId)) {
            noAccess(id, userId);
            throw new ForbiddenException("User with id " + userId + "doesn't have access to product with ID: " + id);
        }
        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        logger.info("Product updated with ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product updated successfully", product));
    }

    private void noAccess(Long id, Long userId) {
        logger.warn("User with id {} doesn't have access to product with ID: {}", userId, id);
    }

    private void productNotFound(Long id) {
        logger.warn("Product not found with ID: {}", id);
    }

    @Override
    public ResponseEntity<Response> deleteProduct(Long id, String authentication) {
        logger.info("Deleting product with ID: {}", id);
        String username = getUsername(authentication);
        Long userId = userService.findByUsername(username).getId();
        Product product = productMapper.findById(id);
        if (product == null) {
            productNotFound(id);
            throw new ResourceNotFoundException("Product not found with ID: " + id);
        } else if (!product.getUserId().equals(userId)) {
            noAccess(id, userId);
            throw new ForbiddenException("User with id " + userId + "doesn't have access to product with ID: " + id);
        }
        userProductMapper.deleteByProductId(id);
        productMapper.delete(id);
        logger.info("Product deleted with ID: {}", id);

        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product deleted successfully", null));
    }

    @Override
    public ResponseEntity<PaginatedResponse> findAll(int page, int pageSize) {
        logger.info("Fetching products, page: {}, pageSize: {}", page, pageSize);
        int offset = (page - 1) * pageSize;
        List<Product> list = productMapper.findAll(offset, pageSize);
        String message = "Products retrieved successfully";
        PaginatedResponse paginatedResponse = new PaginatedResponse(true, message, list, page, pageSize);

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

    private String getUsername(String authentication) {
        String pair = new String(Base64.decodeBase64(authentication.substring(6)));

        return pair.split(":")[0];
    }

    @Override
    public ResponseEntity<Response> loadProductsFromCsv(String fileLocation) throws SQLException{
        logger.info("Loading products from CSV with path: {}", fileLocation);
        try {
            long adminUserId = userService.findByRole("ADMIN").getId();

            InputStream inputStream = getInputStream(fileLocation);

            File tempFile = getTempFile(inputStream, adminUserId);

            copy(tempFile);

            deleteTempFile(tempFile);

            return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("CSV file copied successfully", null));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getErrorResponse("Failed to read the CSV file"));
        } catch (CannotGetJdbcConnectionException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(getErrorResponse("Database connection failed"));
        }
    }

    private void deleteTempFile(File tempFile) {
        try {
            Files.delete(tempFile.toPath());
            logger.info("Temporary file deleted.");
        } catch (IOException e) {
            logger.warn("Failed to delete temporary file: {}", e.getMessage(), e);
        }
    }

    private void copy(File tempFile) throws SQLException, IOException {
        Connection conn = DataSourceUtils.getConnection(dataSource);
        CopyManager copyManager = new CopyManager(conn.unwrap(BaseConnection.class));

        try (Reader processedReader = new FileReader(tempFile)) {
            copyManager.copyIn(
                    "COPY product(name, price, description, user_id, created_at, updated_at) FROM STDIN WITH (FORMAT csv, HEADER true)",
                    processedReader
            );
        }
    }


    private File getTempFile(InputStream inputStream, long adminUserId) throws IOException {
        File tempFile = Files.createTempFile("processed-products", ".csv").toFile();
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
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