 -- Functionality : DRIVE
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (295, true, true, 2, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (296, true, true, 2, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (62, false, 'DRIVE__CAN_CREATE', 295, 296, 1, 'WORK_GROUP', true, now(), now());