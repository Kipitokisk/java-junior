package com.java.test.junior.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserProduct {
    private Long userId;
    private Long productId;
}
