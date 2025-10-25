-- Seed a default lecturer account so manual logins and automated tests always have a lecturer user available.
-- The ON DUPLICATE KEY clause makes the insert idempotent by updating the email if the user already exists.
INSERT INTO users (user_name, password, email, role)
VALUES ('test-lecturer', 'password123', 'lecturer@test.edu', 'LECTURER')
    ON DUPLICATE KEY UPDATE email = VALUES(email);