package com.java.test.junior.service;

import com.java.test.junior.mapper.UserProductMapper;
import com.java.test.junior.model.Product;
import com.java.test.junior.model.User;
import com.java.test.junior.model.UserProduct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserProductServiceImpl implements UserProductService{
    private static final Logger logger = LoggerFactory.getLogger(UserProductServiceImpl.class);
    private final UserServiceImpl userService;
    private final ProductServiceImpl productService;
    private final UserProductMapper userProductMapper;

    @Override
    public void save(String username, Long productId) {
        logger.info("User {} liking product: {}", username, productId);

        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = new UserProduct(user.getId(), productId);
        userProductMapper.save(userProduct);
        logger.info("User {} successfully liked product: {}", username, productId);
    }

    @Override
    public void delete(String username, Long productId) {
        logger.info("User {} disliking product: {}", username, productId);
        User user = userService.findByUsername(username);
        productService.findProduct(productId);
        UserProduct userProduct = new UserProduct(user.getId(), productId);
        userProductMapper.delete(userProduct);
        logger.info("User {} successfully disliked product: {}", username, productId);
    }
}
