package com.java.test.junior.service;

public interface UserProductService {
    void save(String username, Long productId);
    void delete(String username, Long productId);
}
