INSERT INTO users (user_name, password, email, role)
SELECT 'test-teacher', 'password123', 'teacher@test.edu', 'LECTURER'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE user_name = 'test-teacher'
);