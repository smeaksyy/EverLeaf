USE everleaf_db;

-- Assuming a test user will be inserted with ID 1
INSERT INTO categories (user_id, category_name) VALUES 
(1, 'Personal'),
(1, 'Work'),
(1, 'Finance'),
(1, 'Health'),
(1, 'Education');
