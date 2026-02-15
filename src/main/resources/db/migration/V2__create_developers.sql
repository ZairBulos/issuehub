CREATE TYPE developer_status AS ENUM ('active', 'blocked', 'deleted');

CREATE TABLE IF NOT EXISTS developers (
    id                          UUID NOT NULL,
    email                       VARCHAR(320) NOT NULL UNIQUE,
    is_verified                 BOOLEAN NOT NULL DEFAULT FALSE,
    status                      developer_status NOT NULL DEFAULT 'active',
    name                        VARCHAR(255) NULL,
    language                    VARCHAR(10) NOT NULL,
    timezone                    VARCHAR(50) NOT NULL,
    notification_preferences    JSONB NOT NULL,
    created_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMPTZ NOT NULL DEFAULT now(),

    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS id_developers_email
    ON developers(email);
