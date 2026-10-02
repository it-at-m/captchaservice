-- Speed up expired-row cleanup (DELETE WHERE expires_at < ?)
CREATE INDEX idx_captcha_request_expires_at ON captcha_request (expires_at);
CREATE INDEX idx_invalidated_payload_expires_at ON invalidated_payload (expires_at);
