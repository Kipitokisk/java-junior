CREATE TABLE password_reset_token (
    token VARCHAR(255) PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    expiry TIMESTAMP NOT NULL,
    CONSTRAINT fk_user_email FOREIGN KEY (email) REFERENCES "user"(email) ON DELETE CASCADE
);
