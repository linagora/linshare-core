--INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,current_value, last_value, domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
--VALUES (2, 'aebe1b64-39c0-11e5-9fa7-080027b8274b', NOW(), NOW(), NOW(), null, null,1096,500,2,null,null, 99991900, 1800, 9999999999, null, 'DOMAIN_QUOTA'),
--(3, 'aebe1b64-39c0-11e5-9fa7-080027b8274c', NOW(), NOW(), NOW(), 2, null,496,0,2,null,1, 99991900, 1300, 9999999999, 'USER', 'ENSEMBLE_QUOTA'),
--(6, 'aebe1b64-39c0-11e5-9fa7-080027b8274e', NOW(), NOW(), NOW(), 2, null,900,200,2,null,1, 99992000, 1500, 9999999999, 'THREAD', 'ENSEMBLE_QUOTA'),
--(1, 'aebe1b64-39c0-11e5-9fa7-080027b8274a', NOW(), NOW(), NOW(), null, 3,800,0,2,11,null, 9999000, 1480, 9999999999, null, 'ACCOUNT_QUOTA'),
--(5, 'aebe1b64-39c0-11e5-9fa7-080027b8274f', NOW(), NOW(), NOW(), null, 3,900,100,2,12,null, 99991500, 1000, 6, null, 'ACCOUNT_QUOTA'),
--(4, 'aebe1b64-39c0-11e5-9fa7-080027b8274d', NOW(), NOW(), NOW(), null, null, 1096,100, null, null, null, 99992300, 2000, 9999999999, null, 'PLATFORM_QUOTA');


--INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,
--	current_value, last_value, domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
--	VALUES (2, 'd273f30c-825c-4379-bd21-470b0cb40da6', NOW(), NOW(), NOW(), null, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, null, 'DOMAIN_QUOTA');
--INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,
--	current_value, last_value, domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
--	VALUES (5, 'daa310bc-6b94-4ddc-855b-f9ef1a653f15', NOW(), NOW(), NOW(), 2, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, 'USER', 'ENSEMBLE_QUOTA');
--INSERT INTO quota(id, uuid, creation_date, modification_date, batch_modification_date, domain_quota, ensemble_quota,
--	current_value, last_value, domain_id, account_id, parent_domain_id, quota, quota_warning, file_size_max, ensemble_type, quota_type)
--	VALUES (6, 'd91c2675-460f-4f31-b0cd-1eb82e2900ee', NOW(), NOW(), NOW(), 2, null, 0, 0, 2, null, 1, 1099511627776, 1045824536576, 10737418240, 'THREAD', 'ENSEMBLE_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - Jane (11)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	101, 'aebe1b64-39c0-11e5-9fa8-080027b8274a', NOW(), NOW(), NOW(),
	null, 5, 0, 0,
	2, 11, null, 1099511627776,
	1045824536576, 10737418240, null, 'ACCOUNT_QUOTA');

-- topdomain 2, ACCOUNT_QUOTA - John (12)
INSERT INTO quota (
	id, uuid, creation_date, modification_date, batch_modification_date,
	domain_quota, ensemble_quota, current_value, last_value,
	domain_id, account_id, parent_domain_id, quota,
	quota_warning, file_size_max, ensemble_type, quota_type)
VALUES (
	102, 'aebe1b64-39c0-11e5-9fa8-080027b8274b', NOW(), NOW(), NOW(),
	null, 5, 0, 0,
	2, 12, null, 1099511627776,
	1045824536576, 10737418240, null, 'ACCOUNT_QUOTA');
