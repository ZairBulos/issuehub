CREATE TABLE IF NOT EXISTS email_verifications (
    id              UUID NOT NULL,
    developer_id    UUID NOT NULL,
    hashed_code     VARCHAR(255) NOT NULL,
    used_at         TIMESTAMP NULL,
    expires_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    CONSTRAINT fk_email_verifications_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_email_verifications_hashed_code
    ON email_verifications (hashed_code);

CREATE INDEX IF NOT EXISTS idx_email_verifications_developer_id
    ON email_verifications (developer_id);
