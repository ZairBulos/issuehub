-- developers
INSERT INTO developers (
    id,
    email,
    is_verified,
    status,
    name,
    language,
    timezone,
    notification_preferences,
    created_at,
    updated_at
)
VALUES (
    '6b6cd56c-d3d4-447b-86b1-8c287e45031b',
    'callback@example.com',
    TRUE,
    'active',
    'Callback',
    'EN',
    'UTC',
    '{"notify_on_ticket_creation": true, "notify_on_ticket_status_change": true}'::jsonb,
    NOW(),
    NOW()
);

INSERT INTO developers (
    id,
    email,
    is_verified,
    status,
    name,
    language,
    timezone,
    notification_preferences,
    created_at,
    updated_at
)
VALUES (
    'd12333cc-1292-47b2-abe7-193478f93e17',
    'connections@example.com',
    TRUE,
    'active',
    'Connection',
    'EN',
    'UTC',
    '{"notify_on_ticket_creation": true, "notify_on_ticket_status_change": true}'::jsonb,
    NOW(),
    NOW()
);

-- oauth_connections
INSERT INTO oauth_connections (
    id,
    developer_id,
    provider,
    provider_user_id,
    provider_username,
    encrypted_access_token,
    encrypted_refresh_token,
    access_token_expires_at,
    refresh_token_expires_at,
    created_at,
    updated_at
)
VALUES (
    '8ed6228c-8080-4b3a-9731-501b25008447',
    'd12333cc-1292-47b2-abe7-193478f93e17',
    'github',
    '987654321',
    'it_test',
    '8tmAY5Qc2miPXXL55yN03wvdUWMJiU0/YhW9zGwmiRd1b6P2CyFTPYMmWXqUmp0=',
    'dhOItPeXTYw/SSolSKqHXuM0DgWEl3RZM6cZyrKa3AMQngqweec0zZCEpl5MZUA8',
    NOW() + INTERVAL '8 hours',
    NOW() + INTERVAL '6 months',
    NOW(),
    NOW()
);