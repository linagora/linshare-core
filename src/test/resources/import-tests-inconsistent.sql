INSERT INTO domain_abstract(
	id, type , uuid, label, 
	enable, template, description, default_role, 
	default_locale, purge_step, user_provider_id, 
	domain_policy_id, parent_id, auth_show_order, mailconfig_id, 
	welcome_messages_id, creation_date, modification_date) 
VALUES
-- Top domain (MyDomain)
	(3, 1, 'MyDomain', 'MyDomain', 
	true, false, 'a simple description', 0, 
	'en','IN_USE', null, 1, 
	1, 2, null, 1,
	 now(), now());

INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
	VALUES (15, 'upper@echellon.com', 2, 'aebe1b64-39c0-11e5-9fa8-080027b827rt', now(), now(), 0, 'en', 'en', 'en', true, null, false, 3, 'IN_USE', 'Upper', 'Echelon', true, '', false, true, false, 0);
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale, cmis_locale, enable, password, destroyed, domain_id, purge_step, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent, authentication_failure_count)
	VALUES (16, 'fresh@prince.com', 2, 'd896140a-39c0-11e5-b7f9-080027b827eu', now(), now(), 0, 'en', 'en', 'en', true, null, false, 3, 'IN_USE', 'Fresh', 'Prince', true, '', false, true, false, 0);
-- system account :
INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,cmis_locale, enable, destroyed, domain_id, purge_step, authentication_failure_count)
	VALUES (17, 'system-test', 7, 'system-test', now(),now(), 3, 'en', 'en','en', true, false, 1, 'IN USE', 0);
