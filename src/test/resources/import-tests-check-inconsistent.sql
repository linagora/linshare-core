INSERT INTO account(id, mail, account_type, ls_uuid, 
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, ldap_uid, can_upload, comment, 
	restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES (500, 'user5@linshare.org', 2, 'aebe1b64-39c0-11e5-9fa8-080027b8274y', 
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 3, 'IN_USE', 'Peter', 
	'parker', 'user5', true, '', 
	false, true, true, 0);
INSERT INTO account(id, mail, account_type, ls_uuid,
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, ldap_uid, can_upload, comment,
	restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES (501, 'user6@linshare.org', 2, 'd896140a-39c0-11e5-b7f9-080027b8274u', 
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 3, 'IN_USE', 'Bruce', 
	'Wane', 'user6', true, '', 
	false, true, true, 0);
INSERT INTO account(id, mail, account_type, ls_uuid,
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, ldap_uid, can_upload, comment, 
	restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
VALUES (502, 'user7@linshare.org', 2, 'e524e1ba-39c0-11e5-b704-080027b8274i', 
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 3, 'IN_USE', 'Oliver', 
	'Twist', 'user7', true, '', 
	false, true, true, 0);
INSERT INTO account(id, mail, account_type, ls_uuid,
	creation_date, modification_date, role_id,
	mail_locale, external_mail_locale,
	cmis_locale, enable, password,
	destroyed, domain_id, purge_step, first_name,
	last_name, can_upload, comment, 
	restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
	
-- User that does not exist in the LDAP
VALUES (503, 'clark@kent.org', 2, 'e524e1ba-39c0-11e5-b704-080027b8274o', 
	now(), now(), 0,
	'en', 'en', 'en', true, null,
	0, 3, 'IN_USE', 'Clark', 
	'Kent', true, '', false, 
	true, true, 0);
