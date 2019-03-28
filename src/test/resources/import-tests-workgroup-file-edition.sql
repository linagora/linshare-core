	-- Functionality : WORK_GROUP__FILE_EDITION
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (303, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (304, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system) 
	VALUES (305, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, policy_delegation_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (65, false, 'WORK_GROUP__FILE_EDITION', 303, 304, 305, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO functionality_string(functionality_id, string_value) 
	VALUES (65, 'http://editor.linshare.local');