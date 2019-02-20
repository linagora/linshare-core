-- Functionality : UPLOAD_MAIL_ATTACHMENT
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (306, true, true, 1, false);
INSERT INTO policy(id, status, default_status, policy, system)
	VALUES (307, true, true, 1, false);
INSERT INTO functionality(id, system, identifier, policy_activation_id, policy_configuration_id, domain_id, creation_date, modification_date)
	VALUES (66, true, 'UPLOAD_MAIL_ATTACHMENT', 305, 306, 1, now(), now());