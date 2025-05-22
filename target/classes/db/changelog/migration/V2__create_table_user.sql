CREATE TABLE IF NOT EXISTS "user"
(
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(255)   NOT NULL UNIQUE,
    password    VARCHAR(255)   NOT NULL,
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

ALTER TABLE "product"
    ADD CONSTRAINT fk_product_user
        FOREIGN KEY (user_id)
            REFERENCES "user" (id);
