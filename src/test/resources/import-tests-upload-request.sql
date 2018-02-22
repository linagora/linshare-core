INSERT INTO contact(id, mail) VALUES (2, 'yoda@linshare.org');

-- ENABLE UPLOAD REQUEST FUNCTIONALITY
UPDATE policy SET status = true where id = 63;
-- ENABLE UPLOAD REQUEST TEMPLATE FUNCTIONALITY
UPDATE policy SET status = true where id = 129;

-- ADD DELAY BEFORE ACTIVATION UPLOAD REQUEST
-- UPDATE functionality_unit SET integer_value = 7 where functionality_id = 32;

UPDATE account set role_id = 6 where id = 3;