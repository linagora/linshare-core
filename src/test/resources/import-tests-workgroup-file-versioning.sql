	-- Functionality : WORK_GROUP__FILE_VERSIONING
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1297, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (1298, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (1299, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (163, false, 'WORK_GROUP__FILE_VERSIONING', 1297, 1298, 1299, 100001, 'WORK_GROUP', true, now(), now());