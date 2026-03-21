CREATE TYPE oauth_provider AS ENUM ('github', 'gitlab');

CREATE TABLE IF NOT EXISTS oauth_connections (
    id                          UUID NOT NULL,
    developer_id                UUID NOT NULL,
    provider                    oauth_provider NOT NULL,
    provider_user_id            VARCHAR(255) NOT NULL,
    provider_username           VARCHAR(255) NOT NULL,
    encrypted_access_token      TEXT NOT NULL,
    encrypted_refresh_token     TEXT NOT NULL,
    access_token_expires_at     TIMESTAMPTZ NOT NULL,
    refresh_token_expires_at    TIMESTAMPTZ NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id),
    CONSTRAINT fk_oauth_connections_developer
        FOREIGN KEY (developer_id)
        REFERENCES developers(id)
        ON DELETE CASCADE,
    CONSTRAINT uq_oauth_connections_developer_provider_user
        UNIQUE (developer_id, provider, provider_user_id)
);

CREATE INDEX IF NOT EXISTS idx_oauth_connections_developer_id
    ON oauth_connections(developer_id);

CREATE INDEX IF NOT EXISTS idx_oauth_connections_developer_provider
    ON oauth_connections(developer_id, provider);
