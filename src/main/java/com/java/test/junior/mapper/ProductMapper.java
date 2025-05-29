package com.java.test.junior.mapper;

import com.java.test.junior.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    Product findById(@Param("id") Long id);

    void save(Product product);

    void update(Product product);

    void delete(@Param("id") Long id);

    Product findByName(@Param("name") String name);

    List<Product> findAll(@Param("offset") int offset, @Param("pageSize") int pageSize);

    void deleteAllByUserId(Long id);
}