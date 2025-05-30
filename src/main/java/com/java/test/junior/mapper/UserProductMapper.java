package com.java.test.junior.mapper;

import com.java.test.junior.model.UserProduct;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserProductMapper {
    void save(UserProduct userProduct);

    void delete(UserProduct userProduct);

    UserProduct findById(Long userId, Long productId);

    void deleteByProductId(Long productId);
}
