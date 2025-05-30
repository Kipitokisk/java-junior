package com.java.test.junior.mapper;

import com.java.test.junior.model.PasswordResetToken;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PasswordResetTokenMapper {
    void save(PasswordResetToken token);
    PasswordResetToken findByToken(String token);
    void deleteByToken(String token);
}
