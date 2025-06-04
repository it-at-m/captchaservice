CREATE TABLE captcha_request
(
    id                  UUID                        NOT NULL,
    source_address_hash VARCHAR(64)                 NOT NULL,
    valid_until         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_captcharequest PRIMARY KEY (id)
);

CREATE TABLE invalidated_payload
(
    id           UUID                        NOT NULL,
    payload_hash VARCHAR(64)                 NOT NULL,
    valid_until  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_invalidatedpayload PRIMARY KEY (id)
);

CREATE INDEX idx_captcha_request_source_address_hash ON captcha_request (source_address_hash);

CREATE INDEX idx_captcha_request_valid_until ON captcha_request (valid_until);

CREATE INDEX idx_invalidated_payload_payload_hash ON invalidated_payload (payload_hash);

CREATE INDEX idx_invalidated_payload_valid_until ON invalidated_payload (valid_until);