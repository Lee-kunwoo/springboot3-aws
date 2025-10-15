CREATE TABLE IF NOT EXISTS article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    author VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);