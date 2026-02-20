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
        '00000000-0000-0000-0000-000000000000',
        'dummy@example.com',
        FALSE,
        'active',
        'Dummy',
        'EN',
        'UTC',
        '{"notify_on_ticket_creation": true, "notify_on_ticket_status_change": true}'::jsonb,
        NOW(),
        NOW()
);
