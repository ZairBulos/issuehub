CREATE TABLE IF NOT EXISTS refresh_tokens (
    id              UUID NOT NULL,
    developer_id    UUID NOT NULL,
    hashed_token    VARCHAR(255) NOT NULL,
    expires_at      TIMESTAMPTZ NOT NULL,
    revoked         BOOLEAN NOT NULL DEFAULT FALSE,
    ip_address      INET NOT NULL,
    user_agent      TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_developer_id
    ON refresh_tokens(developer_id);

CREATE UNIQUE INDEX IF NOT EXISTS idx_refresh_tokens_hashed_token
    ON refresh_tokens (hashed_token);
