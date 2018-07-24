-- Functionality : JWT_PERMANENT_TOKEN
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1290, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1291, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (160, true, 'JWT_PERMANENT_TOKEN', 1290, 1291, 100001, now(), now());

-- Functionality : JWT_PERMANENT_TOKEN__USER_MANAGEMENT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1292, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1293, false, false, 1, true);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1294, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (161, false, 'JWT_PERMANENT_TOKEN__USER_MANAGEMENT', 1292, 1293, 1294, 100001, 'JWT_PERMANENT_TOKEN', true, now(), now());