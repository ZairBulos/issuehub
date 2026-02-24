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
    '22222222-2222-2222-2222-222222222222',
    'request_login@example.com',
    TRUE,
    'active',
    'Request Login',
    'EN',
    'UTC',
    '{"notify_on_ticket_creation": true, "notify_on_ticket_status_change": true}'::jsonb,
    NOW(),
    NOW()
);

-- login_verifications
INSERT INTO login_verifications (
    id, developer_id, hashed_code, used_at, expires_at, created_at
) VALUES (
    '00000000-0000-0000-0000-000000000000',
    '22222222-2222-2222-2222-222222222222',
    '$2a$10$lFHF8I/im4T14ihQGGpSaO7O..8djUzORaYRc5M0dvInz3d6xqT7G', -- BCrypt hash '123456'
    NULL,
    NOW() + INTERVAL '15 minutes',
    NOW()
);
