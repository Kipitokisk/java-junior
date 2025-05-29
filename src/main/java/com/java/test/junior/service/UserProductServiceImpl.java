package com.java.test.junior.service;

import com.java.test.junior.exception.ResourceAlreadyExistsException;
import com.java.test.junior.exception.ResourceNotFoundException;
import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.Response;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static com.java.test.junior.util.ResponseUtil.buildSuccessResponse;

@Service
@RequiredArgsConstructor
@Log
public class UserProductServiceImpl implements UserProductService{
    private final UserServiceImpl userService;
    private final ProductServiceImpl productService;
    private final UserProductMapper userProductMapper;

    @Override
    public ResponseEntity<Response> save(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User " + username + " liking product: " + productId);
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = userProductMapper.findById(user.getId(), productId);
        if (userProduct != null) {
            log.warning("User already liked product with ID " + productId);
            throw new ResourceAlreadyExistsException("User already liked product with ID " + productId);
        }
        UserProduct userProductFinal = new UserProduct(user.getId(), productId);
        userProductMapper.save(userProductFinal);
        log.info("User " + username + " successfully liked product: " + productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product liked successfully", null));
    }

    @Override
    public ResponseEntity<Response> delete(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("User " + username + " disliking product: " + productId);
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = userProductMapper.findById(user.getId(), productId);
        if (userProduct == null) {
            log.warning("User doesn't have product with ID  liked" + productId);
            throw new ResourceNotFoundException("User doesn't have product with ID " + productId + " liked");
        }
        userProductMapper.delete(userProduct);
        log.info("User " + username + " successfully disliked product: " + productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product disliked successfully", null));
    }
}
