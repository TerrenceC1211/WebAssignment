-- Seed a default lecturer account so manual logins and automated tests always have a lecturer user available.
-- The ON DUPLICATE KEY clause makes the insert idempotent by updating the email if the user already exists.
INSERT IGNORE INTO users (user_name, password, email, role)
VALUES ('test-teacher', 'password123', 'teacher@test.edu', 'LECTURER');