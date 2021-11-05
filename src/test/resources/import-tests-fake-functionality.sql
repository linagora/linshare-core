UPDATE functionality_unit SET integer_max_value = 3, unlimited_value = FALSE WHERE functionality_id = 32;

UPDATE functionality_unit SET integer_max_value = 10 WHERE functionality_id = 33;

UPDATE policy SET status = true, default_status = true, system = true  WHERE id = 80;