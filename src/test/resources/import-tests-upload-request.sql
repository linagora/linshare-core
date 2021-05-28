INSERT INTO contact(id, mail) VALUES (2, 'yoda@linshare.org');
INSERT INTO contact(id, mail) VALUES (3, 'external2@linshare.org');

-- ENABLE UPLOAD REQUEST FUNCTIONALITY
UPDATE policy SET status = true where id = 63;
-- ENABLE UPLOAD REQUEST TEMPLATE FUNCTIONALITY
UPDATE policy SET status = true where id = 129;

UPDATE account set role_id = 6 where id = 3;