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
    '11111111-1111-1111-1111-111111111111',
    'verify@example.com',
    FALSE,
    'active',
    'Verify',
    'EN',
    'UTC',
    '{"notify_on_ticket_creation": true, "notify_on_ticket_status_change": true}'::jsonb,
    NOW(),
    NOW()
);

-- email_verifications
INSERT INTO email_verifications (
    id, developer_id, hashed_code, used_at, expires_at, created_at
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    '11111111-1111-1111-1111-111111111111',
    '$2a$10$N1L8JS65rxXkKgaNCSf1POXqUodqo6s78YN06ajgy1zO8r7Q0LDJ.', -- BCrypt hash 'VALID_CODE_123'
    NULL,
    NOW() + INTERVAL '1 day',
    NOW()
);