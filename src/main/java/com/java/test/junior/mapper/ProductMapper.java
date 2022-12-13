/*
 * Copyright (c) 2013-2022 Global Database Ltd, All rights reserved.
 */

package com.java.test.junior.mapper;

import com.java.test.junior.model.Product;

/**
 * @author dumitru.beselea
 * @version java-test-junior
 * @apiNote 08.12.2022
 */
public interface ProductMapper {
    Product findById(Long id);
}