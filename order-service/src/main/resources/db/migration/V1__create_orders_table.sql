CREATE TABLE orders (
                        id UUID PRIMARY KEY,
                        product VARCHAR(255) NOT NULL,
                        amount DECIMAL(19,2) NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        created_at TIMESTAMP NOT NULL
);