
-- topdomain 2, ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	101, 'a2b4ed6e-4039-4f45-9a33-d906a3fd3ae8', NOW(), NOW(), null,
	null, 3, 0, 0,
	2, 11, 1, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - John (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	102, '072b06d3-8f05-48fa-8aed-bf86fce7170a', NOW(), NOW(), null,
	null, 3, 0, 0,
	2, 12, 1, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');

INSERT INTO account(id, mail, account_type, ls_uuid, creation_date, modification_date, role_id, locale, external_mail_locale,
	cmis_locale, enable, password, destroyed, domain_id, purge_step)
	VALUES (14, 'inconsistent-user1@linshare.org', 2, '412da435-1860-439c-b753-b91a2b13af70', now(), now(), 0, 'en', 'en', 'en', true, null, 0, 2, 'IN_USE');
INSERT INTO users(account_id, first_name, last_name, can_upload, comment, restricted, CAN_CREATE_GUEST, inconsistent)
	VALUES (14, 'inconsistent', 'User1', true, '', false, true, false);

-- subdomain 2, ACCOUNT_QUOTA - inconsistent-user1@linshare.org (14)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	quota_override, max_file_size_override, maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	103, 'd17b876d-d9b3-4f17-ab37-1ceff4cc59bc', NOW(), NOW(), null,
	null, 6, 0, 0,
	3, 14, 2, 1000000000,
	false, false, false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');