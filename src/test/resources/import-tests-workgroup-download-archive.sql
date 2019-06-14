INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (306, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (307, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, parent_identifier, param, creation_date, modification_date)
	VALUES (66, false, 'WORK_GROUP__DOWNLOAD_ARCHIVE', 306, 307, 1, 'WORK_GROUP', true, now(), now());
INSERT INTO unit(id, unit_type, unit_value)
	VALUES (12, 1, 1);
INSERT INTO functionality_unit(functionality_id, integer_value, unit_id) 
	VALUES (66, 900, 12);