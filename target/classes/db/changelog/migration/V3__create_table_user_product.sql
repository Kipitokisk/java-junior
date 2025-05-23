CREATE TABLE user_product (
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_user_product PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_user_product_user FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_user_product_product FOREIGN KEY (product_id) REFERENCES product(id)
);
