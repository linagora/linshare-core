	-- Functionality : WORK_GROUP__FILE_VERSIONING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (297, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (298, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (299, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (63, false, 'WORK_GROUP__FILE_VERSIONING', 297, 298, 299, 1, 'WORK_GROUP', true, now(), now());

	-- Functionality : WORK_GROUP__FILE_VERSIONING_EXPIRATION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (300, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (301, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (302, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (64, false, 'WORK_GROUP__FILE_VERSIONING_EXPIRATION', 300, 301, 302, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (12, 0, 2);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (64, 3, 12);