CREATE TABLE payments (
                          id UUID PRIMARY KEY,
                          order_id UUID UNIQUE NOT NULL,
                          amount DECIMAL(19,2) NOT NULL,
                          status VARCHAR(50) NOT NULL,
                          created_at TIMESTAMP NOT NULL
);