CREATE TABLE outbox_events (
                               id UUID PRIMARY KEY,
                               aggregate_id UUID NOT NULL,
                               aggregate_type VARCHAR(100) NOT NULL,
                               event_type VARCHAR(100) NOT NULL,
                               payload TEXT NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               processed_at TIMESTAMP,
                               error_message VARCHAR(1000)
);

CREATE INDEX idx_outbox_status_created_at
    ON outbox_events(status, created_at);