CREATE TABLE captcha_request
(
    id                  UUID                     NOT NULL,
    request_at          TIMESTAMP WITH TIME ZONE,
    source_address_hash VARCHAR(64)              NOT NULL,
    is_whitelisted      BOOLEAN                  NOT NULL,
    expires_at          TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_captcharequest PRIMARY KEY (id)
);

CREATE TABLE invalidated_payload
(
    id           UUID                     NOT NULL,
    payload_hash VARCHAR(64)              NOT NULL,
    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_invalidatedpayload PRIMARY KEY (id)
);

CREATE INDEX idx_captcha_request_source_address_hash ON captcha_request (source_address_hash);

CREATE INDEX idx_captcha_request_source_address_hash_expires_at ON captcha_request (source_address_hash, expires_at);

CREATE INDEX idx_invalidated_payload_payload_hash ON invalidated_payload (payload_hash);

CREATE INDEX idx_invalidated_payload_payload_hash_expires_at ON invalidated_payload (payload_hash, expires_at);
