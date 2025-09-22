CREATE TABLE IF NOT EXISTS delivery (
    idx BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    departure_location VARCHAR(255) NOT NULL,
    arrival_location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at DATETIME,
    modified_at DATETIME
);

CREATE TABLE IF NOT EXISTS delivery_item (
    idx BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    amount INT NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    delivery_id BIGINT,
    CONSTRAINT fk_delivery_item_delivery FOREIGN KEY (delivery_id)
        REFERENCES delivery(idx) ON DELETE CASCADE
);
