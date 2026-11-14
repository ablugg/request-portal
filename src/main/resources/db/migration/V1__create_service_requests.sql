CREATE TABLE service_requests (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reference       VARCHAR(16)   NOT NULL UNIQUE,
    category        VARCHAR(32)   NOT NULL,
    title           VARCHAR(120)  NOT NULL,
    description     VARCHAR(2000) NOT NULL,
    contact_email   VARCHAR(254)  NOT NULL,
    status          VARCHAR(32)   NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_service_requests_status ON service_requests (status);
