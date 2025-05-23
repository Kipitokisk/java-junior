package com.java.test.junior.model;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponse {
    private boolean success;
    private String message;
    private Object data;
    private int page;
    private int pageSize;
}
