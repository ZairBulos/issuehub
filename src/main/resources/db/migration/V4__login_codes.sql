CREATE TABLE IF NOT EXISTS login_verifications (
    id              UUID NOT NULL,
    developer_id    UUID NOT NULL,
    hashed_code     VARCHAR(255) NOT NULL,
    used_at         TIMESTAMP NULL,
    expires_at      TIMESTAMP NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    CONSTRAINT fk_login_verifications_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_login_verifications_one_active_per_developer
    ON login_verifications (developer_id)
    WHERE used_at IS NULL;
