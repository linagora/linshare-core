UPDATE functionality_unit SET integer_max_value = 0, unlimited_value = TRUE WHERE functionality_id = 32;

UPDATE functionality_unit SET integer_max_value = 7 WHERE functionality_id = 33;

UPDATE policy SET status = false, default_status = false, system = false  WHERE id = 80;