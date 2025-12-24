-- Insert sample users for testing
INSERT INTO users (username, email, first_name, last_name, created_at, last_access_time) VALUES
('john_doe', 'john.doe@example.com', 'John', 'Doe', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane_smith', 'jane.smith@example.com', 'Jane', 'Smith', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob_johnson', 'bob.johnson@example.com', 'Bob', 'Johnson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('alice_brown', 'alice.brown@example.com', 'Alice', 'Brown', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('charlie_wilson', 'charlie.wilson@example.com', 'Charlie', 'Wilson', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);