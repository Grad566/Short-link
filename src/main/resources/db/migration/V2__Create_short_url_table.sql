CREATE TABLE short_urls (
    id SERIAL PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    origin_url_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (origin_url_id) REFERENCES urls(id) ON DELETE CASCADE
);