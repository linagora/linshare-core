
-- topdomain 2, ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	101, 'a2b4ed6e-4039-4f45-9a33-d906a3fd3ae8', NOW(), NOW(), null,
	null, 3, 0, 0,
	2, 11, 1, 1000000000,
	false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - John (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	quota_domain_id, quota_container_id, current_value, last_value,
	domain_id, account_id, domain_parent_id, quota,
	maintenance,
	quota_warning, max_file_size, container_type, quota_type)
VALUES (
	102, '072b06d3-8f05-48fa-8aed-bf86fce7170a', NOW(), NOW(), null,
	null, 3, 0, 0,
	2, 12, 1, 1000000000,
	false,
	1000000000, 1000000000, null, 'ACCOUNT_QUOTA');
