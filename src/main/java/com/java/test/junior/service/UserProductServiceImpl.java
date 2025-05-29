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
    public ResponseEntity<Response> like(Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = userProductMapper.findById(user.getId(), productId);
        if (userProduct != null) {
            userProductMapper.delete(userProduct);
            log.info("User " + username + " successfully disliked product: " + productId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(buildSuccessResponse("Product disliked successfully", null));
        }
        UserProduct userProductFinal = new UserProduct(user.getId(), productId);
        userProductMapper.save(userProductFinal);
        log.info("User " + username + " successfully liked product: " + productId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(buildSuccessResponse("Product liked successfully", null));
    }
}
