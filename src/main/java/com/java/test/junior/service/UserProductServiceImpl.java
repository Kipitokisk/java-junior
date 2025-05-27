package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserProduct;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;

@Service
@AllArgsConstructor
public class UserProductServiceImpl implements UserProductService{
    private static final Logger logger = LoggerFactory.getLogger(UserProductServiceImpl.class);
    private final UserServiceImpl userService;
    private final ProductServiceImpl productService;
    private final UserProductMapper userProductMapper;

    @Override
    public ResponseEntity<Response> save(String authentication, Long productId) {
        String username = getUsername(authentication);
        logger.info("User {} liking product: {}", username, productId);
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = userProductMapper.findById(user.getId(), productId);
        if (userProduct != null) {
            logger.warn("User already liked product with ID {}", productId);
            throw new ResourceAlreadyExistsException("User already liked product with ID " + productId);
        }
        UserProduct userProductFinal = new UserProduct(user.getId(), productId);
        userProductMapper.save(userProductFinal);
        logger.info("User {} successfully liked product: {}", username, productId);

        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product liked successfully", null));
    }

    @Override
    public ResponseEntity<Response> delete(String authentication, Long productId) {
        String username = getUsername(authentication);
        logger.info("User {} disliking product: {}", username, productId);
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = userProductMapper.findById(user.getId(), productId);
        if (userProduct == null) {
            logger.warn("User doesn't have product with ID {} liked", productId);
            throw new ResourceNotFoundException("User doesn't have product with ID " + productId + " liked");
        }
        userProductMapper.delete(userProduct);
        logger.info("User {} successfully disliked product: {}", username, productId);

        return ResponseEntity.status(HttpStatus.OK).body(buildSuccessResponse("Product disliked successfully", null));
    }

    private static String getUsername(String authentication) {
        String pair = new String(Base64.decodeBase64(authentication.substring(6)));
        return pair.split(":")[0];
    }
}
