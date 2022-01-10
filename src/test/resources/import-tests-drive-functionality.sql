	 -- Functionality : DRIVE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (317, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (318, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, param, creation_date, modification_date)
	VALUES (67, false, 'DRIVE', 317, 318, 1, false, now(), now());

-- Functionality : WORK_SPACE__CREATION_RIGHT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (295, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (296, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (62, false, 'WORK_SPACE__CREATION_RIGHT', 295, 296, 1, 'DRIVE', true, now(), now());
	